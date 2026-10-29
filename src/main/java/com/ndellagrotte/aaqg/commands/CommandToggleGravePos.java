package com.ndellagrotte.aaqg.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.ndellagrotte.aaqg.network.GravePosTogglePacket;
import com.ndellagrotte.aaqg.network.AAQGNetwork;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandToggleGravePos extends CommandBase {

    public CommandToggleGravePos()
    {
        super("aaqg_toggle_grave_pos", 0, true);
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return COMMAND_NAME;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP))
        {
            return;
        }
        AAQGNetwork.sendTo(new GravePosTogglePacket(), (EntityPlayerMP)sender);
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList<>();
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
