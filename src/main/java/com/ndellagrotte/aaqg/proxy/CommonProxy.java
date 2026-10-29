package com.ndellagrotte.aaqg.proxy;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.ndellagrotte.aaqg.ModConfigs;
import com.ndellagrotte.aaqg.AAQG;
import com.ndellagrotte.aaqg.blocks.ModBlocks;
import com.ndellagrotte.aaqg.events.CommonEvents;
import com.ndellagrotte.aaqg.friends.FriendHandler;
import com.ndellagrotte.aaqg.gui.ModGuiHandler;
import com.ndellagrotte.aaqg.invman.DeathInventoryHandler;
import com.ndellagrotte.aaqg.items.ModItems;
import com.ndellagrotte.aaqg.network.AAQGNetwork;
import com.ndellagrotte.aaqg.tiles.ModTiles;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

    public void preinit(FMLPreInitializationEvent e)
    {
        FriendHandler.importFriendsList();
        ModConfigs.preInit(e);
        AAQGNetwork.setup();
        ModItems.createItems();
        ModBlocks.preInit();
    }

    public void init(FMLInitializationEvent e)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(AAQG.INSTANCE, new ModGuiHandler());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        MinecraftForge.EVENT_BUS.register(new FriendHandler());

        ModTiles.init();
    }

    public void postinit(FMLPostInitializationEvent e)
    {

    }

    public List<String> probeForFiles(BlockPos pos)
    {
//        LogHelper.info("Probing for files in Common!");
        return DeathInventoryHandler.getSavedInventories();
    }

    public void particleStream(Vector3f start, Vector3f end)
    {

    }

    public void pathFX(double x, double y, double z, float r, float g, float b, float size,
                       float motionX, float motionY, float motionZ, float maxAge)
    {

    }

    public void toggleGraveRendering()
    {
    }

    public void toggleGravePositionRendering()
    {

    }
}
