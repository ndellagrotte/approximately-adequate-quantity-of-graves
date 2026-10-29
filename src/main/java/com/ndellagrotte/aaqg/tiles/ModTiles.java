package com.ndellagrotte.aaqg.tiles;

import com.ndellagrotte.aaqg.Names;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTiles {
	public static void init() {
		GameRegistry.registerTileEntity(TileGrave.class, new ResourceLocation(Names.MODID, Names.GRAVE_BLOCK));
	}
}
