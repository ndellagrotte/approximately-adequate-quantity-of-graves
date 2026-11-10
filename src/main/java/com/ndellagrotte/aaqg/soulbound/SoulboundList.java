package com.ndellagrotte.aaqg.soulbound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ndellagrotte.aaqg.util.LogHelper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

/**
 * The resolved form of the "soulbound" config list. Each entry is either an item id ("minecraft:diamond_sword") or an
 * ore dictionary name with an "ore:" prefix ("ore:ingotIron"), which is the 1.12 stand-in for item tags.
 */
public final class SoulboundList {

    public static final String ORE_PREFIX = "ore:";

    private static final Set<Item> ITEMS = new HashSet<>();
    private static final Set<Integer> ORE_IDS = new HashSet<>();

    private SoulboundList() {
    }

    /**
     * Resolves the raw config entries against the item registry and the ore dictionary. Must run after every mod has
     * registered its items and ore names. Invalid entries are reported in a single error and otherwise ignored.
     */
    public static void resolve(String[] entries) {
        ITEMS.clear();
        ORE_IDS.clear();

        List<String> invalid = new ArrayList<>();
        for (String raw : entries) {
            String entry = raw == null ? "" : raw.trim();
            if (entry.isEmpty()) {
                continue;
            }

            if (entry.startsWith(ORE_PREFIX)) {
                String oreName = entry.substring(ORE_PREFIX.length());
                if (!oreName.isEmpty() && OreDictionary.doesOreNameExist(oreName) && !OreDictionary.getOres(oreName, false).isEmpty()) {
                    ORE_IDS.add(OreDictionary.getOreID(oreName));
                } else {
                    invalid.add("\"" + entry + "\" (unknown or empty ore dictionary name)");
                }
            } else {
                ResourceLocation id = new ResourceLocation(entry);
                if (ForgeRegistries.ITEMS.containsKey(id)) {
                    ITEMS.add(ForgeRegistries.ITEMS.getValue(id));
                } else {
                    invalid.add("\"" + entry + "\" (unknown item)");
                }
            }
        }

        if (!invalid.isEmpty()) {
            LogHelper.error("Invalid \"soulbound\" entries in aaqg.cfg (ignored): " + String.join(", ", invalid)
                    + ". Use an item id such as minecraft:diamond_sword, or an ore dictionary name such as ore:ingotIron.");
        }
    }

    public static boolean isEmpty() {
        return ITEMS.isEmpty() && ORE_IDS.isEmpty();
    }

    public static boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (ITEMS.contains(stack.getItem())) {
            return true;
        }
        if (!ORE_IDS.isEmpty()) {
            for (int oreId : OreDictionary.getOreIDs(stack)) {
                if (ORE_IDS.contains(oreId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
