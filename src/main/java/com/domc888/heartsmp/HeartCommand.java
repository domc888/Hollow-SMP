package com.domc888.heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HeartCommand implements CommandExecutor, TabCompleter {

    private final HeartSMP plugin;

    public HeartCommand(HeartSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("heartsmp.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <gui|revive> [player]");
            return true;
        }

        if (args[0].equalsIgnoreCase("gui")) {
            if (sender instanceof Player player) {
                plugin.getAdminManager().openMainGUI(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Only players can open the GUI.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("revive")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " revive <playername>");
                return true;
            }

            String targetName = args[1];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

            if (!target.hasPlayedBefore() && !target.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Player " + targetName + " has never played on this server.");
                return true;
            }

            boolean success = plugin.getLivesManager().revivePlayer(target);
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Successfully revived " + target.getName() + " and restored their life.");
                if (target.isOnline() && target.getPlayer() != null) {
                    target.getPlayer().sendMessage(ChatColor.GREEN + "You have been revived by an admin!");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + target.getName() + " is already alive or has max lives.");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Usage: /" + label + " <gui|revive> [player]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("heartsmp.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String sub : Arrays.asList("gui", "revive")) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
            return completions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("revive")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    playerNames.add(player.getName());
                }
            }
            return playerNames;
        }

        return Collections.emptyList();
    }
}
