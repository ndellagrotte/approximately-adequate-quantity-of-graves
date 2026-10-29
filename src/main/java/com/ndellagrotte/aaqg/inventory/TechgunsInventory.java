package com.ndellagrotte.aaqg.inventory;

import com.ndellagrotte.aaqg.api.GraveInventoryHelper;
import com.ndellagrotte.aaqg.api.GraveRegistry;
import com.ndellagrotte.aaqg.api.IGraveInventory;
import com.ndellagrotte.aaqg.api.TempInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import techguns.capabilities.TGExtendedPlayer;

@GraveRegistry(id = "techguns", name = "Techguns", overridable = true, reqMod = "techguns")
public class TechgunsInventory implements IGraveInventory {

	@Override
	public boolean pregrabLogic(EntityPlayer player) {
		return true;
	}

	@Override
	public TempInventory getItems(EntityPlayer player) {
		return GraveInventoryHelper.storeInventory(TGExtendedPlayer.get(player).getTGInventory());
	}

	@Override
	public void insertInventory(EntityPlayer player, TempInventory graveItems, boolean shouldForce) {
		IInventory currentItems = TGExtendedPlayer.get(player).getTGInventory();

		for (int i = 0; i < graveItems.getSizeInventory(); i++) {
			ItemStack graveItem = graveItems.getStackInSlot(i);
			if (!graveItem.isEmpty()) {
				ItemStack playerItem = currentItems.getStackInSlot(i).copy();

				if (playerItem.isEmpty()) {
					currentItems.setInventorySlotContents(i, graveItem);
				} else if (shouldForce) {
					currentItems.setInventorySlotContents(i, graveItem);
					GraveInventoryHelper.dropItem(player, playerItem);
				} else {
					GraveInventoryHelper.dropItem(player, graveItem);
				}
			}
		}
	}
}