package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class LivesCommand implements CommandExecutor, TabCompleter {

    private final LivesManager lives;

    public LivesCommand(LivesManager lives) {
        this.lives = lives;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!sender.hasPermission("heartsmp.lives")) {
            sender.sendMessage(
                    Component.text(
                            "You do not have permission to use this command.",
                            NamedTextColor.RED
                    )
            );
            return true;
        }

        OfflinePlayer target;

        if (args.length == 0) {
            if (!(sender instanceof org.bukkit.entity.Player player)) {
                sender.sendMessage(
                        Component.text(
                                "Console must specify a player.",
                                NamedTextColor.RED
                        )
                );
                return true;
            }

            target = player;
        } else if (args.length == 1) {
            if (!sender.hasPermission(
                    "heartsmp.lives.others"
            )) {
                sender.sendMessage(
                        Component.text(
                                "You do not have permission to view another player's lives.",
                                NamedTextColor.RED
                        )
                );
                return true;
            }

            target = findPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(
                        Component.text(
                                "Player not found.",
                                NamedTextColor.RED
                        )
                );
                return true;
            }
        } else {
            sender.sendMessage(
                    Component.text(
                            "Usage: /lives [player]",
                            NamedTextColor.RED
                    )
            );
            return true;
        }

        int currentLives =
                lives.getLives(target.getUniqueId());

        int maxLives =
                lives.getMaxLives();

        boolean eliminated =
                lives.isEliminated(
                        target.getUniqueId()
                );

        String name =
                target.getName() == null
                        ? "Unknown"
                        : target.getName();

        Component message =
                Component.text(
                        "❤ ",
                        NamedTextColor.RED
                ).append(
                        Component.text(
                                name,
                                NamedTextColor.GOLD,
                                TextDecoration.BOLD
                        )
                ).append(
                        Component.text(
                                " has ",
                                NamedTextColor.GRAY
                        )
                ).append(
                        Component.text(
                                currentLives,
                                currentLives > 0
                                        ? NamedTextColor.GREEN
                                        : NamedTextColor.RED,
                                TextDecoration.BOLD
                        )
                ).append(
                        Component.text(
                                "/" + maxLives + " lives",
                                NamedTextColor.GRAY
                        )
                );

        if (eliminated) {
            message = message.append(
                    Component.text(
                            "  •  ELIMINATED",
                            NamedTextColor.RED,
                            TextDecoration.BOLD
                    )
            );
        }

        sender.sendMessage(message);

        return true;
    }

    private OfflinePlayer findPlayer(String name) {
        OfflinePlayer exact =
                Bukkit.getOfflinePlayerIfCached(name);

        if (exact != null) {
            return exact;
        }

        for (OfflinePlayer player :
                Bukkit.getOfflinePlayers()) {

            if (player.getName() != null
                    && player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length != 1
                || !sender.hasPermission(
                "heartsmp.lives.others"
        )) {
            return List.of();
        }

        String input = args[0].toLowerCase();

        List<String> completions = new ArrayList<>();

        for (org.bukkit.entity.Player player :
                Bukkit.getOnlinePlayers()) {

            if (player.getName()
                    .toLowerCase()
                    .startsWith(input)) {

                completions.add(player.getName());
            }
        }

        return completions;
    }
}
