package com.ndellagrotte.aaqg.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ndellagrotte.aaqg.ModConfigs;
import com.ndellagrotte.aaqg.tiles.TileGrave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Draws a see-through-walls outline around the graves owned by the player at this client, while they are
 * within {@link ModConfigs#GRAVE_GLOW_RANGE} blocks of them.
 *
 * This runs from the world-last render pass rather than from the grave's tile entity renderer, because tile
 * entity renderers are skipped for chunk sections the game considers hidden - such as a grave sealed in a cave.
 */
public final class GraveGlowRenderer {

    /** The half slab a grave shows as when it is camouflaged as the ground. */
    private static final AxisAlignedBB GROUND_BOX = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1).grow(0.002);
    /** The space around the floating head a grave shows as when it has no ground to imitate. */
    private static final AxisAlignedBB HEAD_BOX = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75).grow(0.002);

    private static final float FILL_ALPHA = 0.12f;
    private static final float LINE_ALPHA = 0.9f;
    private static final float LINE_WIDTH = 2.5f;

    private static final List<TileGrave> ownGraves = new ArrayList<>();
    private static World scannedWorld;
    private static long scannedTime = Long.MIN_VALUE;

    private GraveGlowRenderer()
    {
    }

    public static void render(float partialTicks)
    {
        if (!ModConfigs.GRAVE_GLOW_ENABLED || !ModConfigs.GRAVE_RENDERING_ENABLED)
        {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        World world = mc.world;
        Entity camera = mc.getRenderViewEntity();
        if (player == null || world == null || camera == null)
        {
            return;
        }

        refresh(world, player.getUniqueID());
        if (ownGraves.isEmpty())
        {
            return;
        }

        double playerX = interpolate(player.lastTickPosX, player.posX, partialTicks);
        double playerY = interpolate(player.lastTickPosY, player.posY, partialTicks);
        double playerZ = interpolate(player.lastTickPosZ, player.posZ, partialTicks);

        double cameraX = interpolate(camera.lastTickPosX, camera.posX, partialTicks);
        double cameraY = interpolate(camera.lastTickPosY, camera.posY, partialTicks);
        double cameraZ = interpolate(camera.lastTickPosZ, camera.posZ, partialTicks);

        float pulse = GraveGlowMath.pulse(Minecraft.getSystemTime());
        float red = (ModConfigs.GRAVE_GLOW_COLOR >> 16 & 0xFF) / 255.0f;
        float green = (ModConfigs.GRAVE_GLOW_COLOR >> 8 & 0xFF) / 255.0f;
        float blue = (ModConfigs.GRAVE_GLOW_COLOR & 0xFF) / 255.0f;

        boolean drawing = false;
        for (TileGrave grave : ownGraves)
        {
            // a grave that was collected since the last scan
            if (grave.isInvalid())
            {
                continue;
            }

            BlockPos pos = grave.getPos();
            if (!GraveGlowMath.inRange(pos.getX() + 0.5 - playerX, pos.getY() + 0.5 - playerY,
                    pos.getZ() + 0.5 - playerZ, ModConfigs.GRAVE_GLOW_RANGE))
            {
                continue;
            }

            if (!drawing)
            {
                startDrawing();
                drawing = true;
            }

            AxisAlignedBB box = (grave.getShouldRenderGround() ? GROUND_BOX : HEAD_BOX)
                    .offset(pos.getX() - cameraX, pos.getY() - cameraY, pos.getZ() - cameraZ);
            RenderGlobal.renderFilledBox(box, red, green, blue, FILL_ALPHA * pulse);
            RenderGlobal.drawSelectionBoundingBox(box, red, green, blue, LINE_ALPHA * pulse);
        }

        if (drawing)
        {
            finishDrawing();
        }
    }

    /**
     * Forgets the graves found so far, so that a world which has been left is not kept in memory.
     */
    public static void clear()
    {
        ownGraves.clear();
        scannedWorld = null;
        scannedTime = Long.MIN_VALUE;
    }

    /**
     * Looks for the player's graves at most once per tick, however many frames are drawn in it.
     */
    private static void refresh(World world, UUID playerID)
    {
        long time = world.getTotalWorldTime();
        if (world == scannedWorld && time == scannedTime)
        {
            return;
        }
        scannedWorld = world;
        scannedTime = time;

        ownGraves.clear();
        for (TileEntity tile : world.loadedTileEntityList)
        {
            if (tile instanceof TileGrave && ((TileGrave) tile).isOwner(playerID))
            {
                ownGraves.add((TileGrave) tile);
            }
        }
    }

    private static void startDrawing()
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(LINE_WIDTH);
    }

    private static void finishDrawing()
    {
        GlStateManager.glLineWidth(1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private static double interpolate(double last, double now, float partialTicks)
    {
        return last + (now - last) * partialTicks;
    }
}
