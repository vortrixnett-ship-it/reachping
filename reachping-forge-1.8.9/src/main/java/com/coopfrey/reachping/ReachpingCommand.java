package com.coopfrey.reachping;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class ReachpingCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "reachping";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/reachping";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        ReachpingHandler.openConfig = true;
    }
}
