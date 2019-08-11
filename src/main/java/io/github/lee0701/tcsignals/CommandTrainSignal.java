package io.github.lee0701.tcsignals;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTrainSignal implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 1) {
            if(sender.isOp() && args[0].equals("repopulate")) {
                sender.sendMessage(ChatColor.GRAY + "Repopulating all signal signs...");
                BlockSignal.repopulateAll();
                return true;
            }
        }
        return false;
    }

}
