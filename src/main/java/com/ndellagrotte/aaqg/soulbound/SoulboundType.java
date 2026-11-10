package com.ndellagrotte.aaqg.soulbound;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

/**
 * Whole kinds of item that can be named in the "soulbound" config list with a "type:" prefix ("type:armor").
 * <p>
 * Minecraft 1.12 has no item tags, and the ore dictionary does not list tools or armor, so these are worked out from how
 * an item behaves rather than from a list of ids. That makes them cover modded items too, including ones that do not
 * extend the vanilla classes, as long as they use the Forge hooks every tool and armor piece needs to function.
 */
enum SoulboundType {

    /** Anything worn in an armor slot: armor pieces, elytra, and modded equivalents. */
    ARMOR("armor") {
        @Override
        boolean matches(ItemStack stack) {
            if (stack.getItem() instanceof ItemArmor) {
                return true;
            }
            // pumpkins and mob heads can be worn, but stack; real equipment does not
            if (stack.getMaxStackSize() != 1) {
                return false;
            }
            // respects Item#getEquipmentSlot, the Forge hook for armor that is not an ItemArmor
            if (EntityLiving.getSlotForItemStack(stack).getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                return true;
            }
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR
                        && stack.getAttributeModifiers(slot).containsKey(SharedMonsterAttributes.ARMOR.getName())) {
                    return true;
                }
            }
            return false;
        }
    },

    /** Anything held and used: digging tools, weapons, bows, shields, shears, fishing rods and the like. */
    TOOL("tool") {
        @Override
        boolean matches(ItemStack stack) {
            Item item = stack.getItem();
            if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemHoe || item instanceof ItemBow
                    || item instanceof ItemShears || item instanceof ItemFishingRod || item instanceof ItemFlintAndSteel) {
                return true;
            }
            if (stack.getMaxStackSize() != 1 || ARMOR.matches(stack)) {
                return false;
            }
            // Forge harvest tools (pickaxe, axe, shovel, and whatever classes mods add)
            if (!item.getToolClasses(stack).isEmpty()) {
                return true;
            }
            if (item.isShield(stack, null)) {
                return true;
            }
            // anything that deals its own damage in the main hand is a weapon, whatever class it is
            return stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).containsKey(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        }
    };

    private final String configName;

    SoulboundType(String configName) {
        this.configName = configName;
    }

    String configName() {
        return configName;
    }

    abstract boolean matches(ItemStack stack);

    /** @return the type with this config name, or null if there is none */
    static SoulboundType byConfigName(String name) {
        for (SoulboundType type : values()) {
            if (type.configName.equals(name)) {
                return type;
            }
        }
        return null;
    }

    static String configNames() {
        StringBuilder names = new StringBuilder();
        for (SoulboundType type : values()) {
            if (names.length() > 0) {
                names.append(", ");
            }
            names.append(SoulboundList.TYPE_PREFIX).append(type.configName);
        }
        return names.toString();
    }
}
