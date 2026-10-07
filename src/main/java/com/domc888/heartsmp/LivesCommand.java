package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class LivesCommand implements TabExecutor {

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
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                        Component.text(
                                "Specify a player: /lives <player>"
                        )
                );
                return true;
            }

            sender.sendMessage(
                    Component.text(
                            "You have "
                                    + describe(player.getUniqueId())
                                    + "."
                    )
            );

            return true;
        }

        if (!sender.hasPermission("heartsmp.lives.others")) {
            sender.sendMessage(
                    Component.text(
                            "You do not have permission to check other players."
                    )
            );
            return true;
        }

        OfflinePlayer target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            target = Bukkit.getOfflinePlayerIfCached(args[0]);
        }

        if (target == null) {
            sender.sendMessage(Component.text("Player not found."));
            return true;
        }

        String name =
                target.getName() != null
                        ? target.getName()
                        : args[0];

        sender.sendMessage(
                Component.text(
                        name
                                + " has "
                                + describe(target.getUniqueId())
                                + "."
                )
        );

        return true;
    }

    private String describe(UUID id) {
        String text =
                lives.getLives(id)
                        + "/"
                        + lives.getMaxLives()
                        + " lives";

        if (lives.isEliminated(id)) {
            text += " (death-banned)";
        }

        return text;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (
                args.length != 1
                        || !sender.hasPermission(
                        "heartsmp.lives.others"
                )
        ) {
            return List.of();
        }

        String lower =
                args[0].toLowerCase(Locale.ROOT);

        List<String> out = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (
                    player.getName()
                            .toLowerCase(Locale.ROOT)
                            .startsWith(lower)
            ) {
                out.add(player.getName());
            }
        }

        return out;
    }
}
