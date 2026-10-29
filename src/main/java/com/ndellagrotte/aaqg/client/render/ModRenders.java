package com.ndellagrotte.aaqg.client.render;

import com.ndellagrotte.aaqg.client.render.tiles.TileGraveRenderer;
import com.ndellagrotte.aaqg.tiles.TileGrave;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModRenders {

    public static void init()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileGrave.class, new TileGraveRenderer());
    }
}
