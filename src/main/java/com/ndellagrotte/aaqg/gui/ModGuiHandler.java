package com.ndellagrotte.aaqg.gui;

import com.ndellagrotte.aaqg.client.gui.GuiDeathItems;
import com.ndellagrotte.aaqg.items.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler{

    public static final int DEATH_ITEMS_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//        if (ID == DEATH_INVENTORIES)
//        {
////            LogHelper.info("creating new container");
//            return new ContainerInventoryFileManager(player);
//        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == DEATH_ITEMS_GUI)
        {
            ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
            if (held == null || held.getItem() != ModItems.itemDeathList || !held.hasTagCompound())
            {
                return null;
            }
            return new GuiDeathItems(player, held);
        }
        return null;
    }
}
