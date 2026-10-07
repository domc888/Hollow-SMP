package com.domc888.heartsmp;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HeartSMP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int max = Math.max(1, getConfig().getInt("max-lives", 3));
        int start = Math.min(
                max,
                Math.max(1, getConfig().getInt("starting-lives", max))
        );

        LivesManager lives = new LivesManager(this, max, start);
        TokenItems tokens = new TokenItems(this);

        getServer().getPluginManager().registerEvents(
                new LifeListener(this, lives, tokens),
                this
        );

        HeartCommand heartCommand = new HeartCommand(lives, tokens);

        PluginCommand heart = Objects.requireNonNull(
                getCommand("heartsmp"),
                "heartsmp command is missing from plugin.yml"
        );

        heart.setExecutor(heartCommand);
        heart.setTabCompleter(heartCommand);

        LivesCommand livesCommand = new LivesCommand(lives);

        PluginCommand livesCmd = Objects.requireNonNull(
                getCommand("lives"),
                "lives command is missing from plugin.yml"
        );

        livesCmd.setExecutor(livesCommand);
        livesCmd.setTabCompleter(livesCommand);

        getLogger().info("HeartSMP enabled.");
    }
}
