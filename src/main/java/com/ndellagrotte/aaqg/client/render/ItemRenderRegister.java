package com.ndellagrotte.aaqg.client.render;

import com.ndellagrotte.aaqg.items.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ItemRenderRegister {

    /** Must be called during pre-init (before model baking). */
    public static void registerModels()
    {
        registerSingleModel(ModItems.itemDeathList);
    }

    private static void registerSingleModel(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
