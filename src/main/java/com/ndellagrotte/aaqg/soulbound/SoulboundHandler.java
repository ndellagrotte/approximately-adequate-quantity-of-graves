package com.ndellagrotte.aaqg.soulbound;

import java.util.ArrayList;
import java.util.List;

import com.ndellagrotte.aaqg.ModConfigs;
import com.ndellagrotte.aaqg.api.GraveInventoryHelper;
import com.ndellagrotte.aaqg.api.TempInventory;
import com.ndellagrotte.aaqg.inventory.VanillaInventory;
import com.ndellagrotte.aaqg.invman.GraveInventoryManager;
import com.ndellagrotte.aaqg.invman.InventoryHolder;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Keeps soulbound items on the player through death and applies the soulbound death penalties.
 * <p>
 * None of this runs while the keepInventory gamerule is on: vanilla keeps everything then, and never fires the drops
 * event that drives {@link #keepSoulboundItems}.
 */
public final class SoulboundHandler {

    /** Key under the player's persisted entity data holding the items waiting to be handed back on respawn. */
    public static final String KEPT_ITEMS_TAG = "aaqg_soulbound";

    private static final int TAG_LIST = 9;
    private static final int TAG_COMPOUND = 10;

    private SoulboundHandler() {
    }

    /**
     * Pulls the soulbound items out of a dead player's drops (and so out of the grave), applies the durability penalty,
     * and stashes them in the player's persisted data. That data is saved with the player and copied to the respawned
     * player by vanilla, so the items survive a logout or restart on the death screen.
     * <p>
     * Must be called before the grave snapshot is reconciled against the drops.
     */
    public static void keepSoulboundItems(EntityPlayer player, InventoryHolder snapshot, List<EntityItem> drops) {
        if (SoulboundList.isEmpty()) {
            return;
        }

        NBTTagCompound kept = snapshot.extractMatching(SoulboundList::matches, drops, SoulboundHandler::applyDurabilityPenalty);
        if (kept.isEmpty()) {
            return;
        }

        NBTTagCompound entityData = player.getEntityData();
        NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        persisted.setTag(KEPT_ITEMS_TAG, kept);
        entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    /**
     * Hands the stashed soulbound items back to a respawned player, in the slots they were in. Not gated on the
     * gamerule: if items were stashed they are always returned.
     */
    public static void restoreSoulboundItems(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG, TAG_COMPOUND)) {
            return;
        }
        NBTTagCompound persisted = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(KEPT_ITEMS_TAG, TAG_COMPOUND)) {
            return;
        }
        NBTTagCompound kept = persisted.getCompoundTag(KEPT_ITEMS_TAG);
        persisted.removeTag(KEPT_ITEMS_TAG);

        // Something may already sit in a slot a soulbound item is returning to (the death list is given the first free
        // slot). Lift those out so they can be re-added elsewhere instead of being thrown on the ground.
        List<ItemStack> displaced = new ArrayList<>();
        if (kept.hasKey(VanillaInventory.UNIQUE_IDENTIFIER, TAG_LIST)) {
            TempInventory vanilla = new TempInventory(kept.getTagList(VanillaInventory.UNIQUE_IDENTIFIER, TAG_COMPOUND));
            int slots = Math.min(vanilla.getSizeInventory(), player.inventory.getSizeInventory());
            for (int i = 0; i < slots; i++) {
                if (!vanilla.getStackInSlot(i).isEmpty()) {
                    ItemStack occupant = player.inventory.removeStackFromSlot(i);
                    if (!occupant.isEmpty()) {
                        displaced.add(occupant);
                    }
                }
            }
        }

        GraveInventoryManager.getInstance().insertInventory(player, kept, true);

        for (ItemStack stack : displaced) {
            if (!player.inventory.addItemStackToInventory(stack)) {
                GraveInventoryHelper.dropItem(player, stack);
            }
        }
    }

    /**
     * Whether a death of this player is subject to the experience penalty (rather than vanilla's handling). False when
     * keepInventory is on, for spectators, and when the penalty is 100%, which is exactly what vanilla does anyway.
     */
    public static boolean isExpPenaltyActive(EntityPlayer deadPlayer) {
        return ModConfigs.SOULBOUND_EXP_PENALTY < 100 && !deadPlayer.world.getGameRules().getBoolean("keepInventory") && !deadPlayer.isSpectator();
    }

    /**
     * Gives a respawned player the share of experience they keep after the penalty. Vanilla leaves them at zero.
     */
    public static void applyExpPenalty(EntityPlayer deadPlayer, EntityPlayer respawned) {
        if (!isExpPenaltyActive(deadPlayer)) {
            return;
        }
        int percent = ModConfigs.SOULBOUND_EXP_PENALTY;
        int level = SoulboundMath.keptLevel(deadPlayer.experienceLevel, deadPlayer.experience, percent);
        float progress = SoulboundMath.keptProgress(deadPlayer.experienceLevel, deadPlayer.experience, percent);

        respawned.experienceLevel = level;
        respawned.experience = progress;
        respawned.experienceTotal = SoulboundMath.totalExperience(level, progress);
    }

    private static void applyDurabilityPenalty(ItemStack stack) {
        if (ModConfigs.SOULBOUND_DURABILITY_PENALTY <= 0 || !stack.isItemStackDamageable()) {
            return;
        }
        stack.setItemDamage(SoulboundMath.penalizedDamage(stack.getItemDamage(), stack.getMaxDamage(), ModConfigs.SOULBOUND_DURABILITY_PENALTY));
    }
}
