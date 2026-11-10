package com.ndellagrotte.aaqg.invman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

class InventoryHolderTest {

    private static final int TAG_STRING = 8;

    @Test
    void reconcilingWithTheDropsKeepsThePlayerNameAndTimestamp() {
        NBTTagCompound stored = new NBTTagCompound();
        stored.setString(InventoryHolder.PLAYER_NAME, "Steve");
        stored.setString(InventoryHolder.TIMESTAMP, "11_09_2026_12_00_00");
        stored.setTag("vanilla", new NBTTagList());
        NBTTagCompound saved = new NBTTagCompound();
        saved.setTag(InventoryHolder.TAG_NAME, stored);

        InventoryHolder holder = new InventoryHolder();
        holder.readFromNBT(saved);
        holder.ensure(new ArrayList<>());

        // the save file, the death list and the grave are all written from this compound
        NBTTagCompound written = holder.writeToNBT(new NBTTagCompound()).getCompoundTag(InventoryHolder.TAG_NAME);
        assertTrue(written.hasKey(InventoryHolder.PLAYER_NAME, TAG_STRING));
        assertTrue(written.hasKey(InventoryHolder.TIMESTAMP, TAG_STRING));
        assertEquals("Steve", written.getString(InventoryHolder.PLAYER_NAME));
        assertEquals("11_09_2026_12_00_00", written.getString(InventoryHolder.TIMESTAMP));
    }

    @Test
    void aHolderSavedWithoutAGraveHasNoPosition() {
        NBTTagCompound stored = new NBTTagCompound();
        stored.setString(InventoryHolder.PLAYER_NAME, "Steve");
        NBTTagCompound saved = new NBTTagCompound();
        saved.setTag(InventoryHolder.TAG_NAME, stored);

        InventoryHolder holder = new InventoryHolder();
        holder.readFromNBT(saved);

        // a negative y is what the death list reads as "No grave exists."
        assertEquals(-1, holder.getPosition().getY());
    }

    @Test
    void aHolderSavedWithAGraveKeepsItsPosition() {
        NBTTagCompound stored = new NBTTagCompound();
        stored.setInteger(InventoryHolder.X, 12);
        stored.setInteger(InventoryHolder.Y, 64);
        stored.setInteger(InventoryHolder.Z, -7);
        NBTTagCompound saved = new NBTTagCompound();
        saved.setTag(InventoryHolder.TAG_NAME, stored);

        InventoryHolder holder = new InventoryHolder();
        holder.readFromNBT(saved);

        assertEquals(12, holder.getPosition().getX());
        assertEquals(64, holder.getPosition().getY());
        assertEquals(-7, holder.getPosition().getZ());
    }
}
