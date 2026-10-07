package com.domc888.heartsmp;

import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HeartSMP extends JavaPlugin {

    private LivesManager livesManager;
    private TokenItems tokenItems;
    private ShrineManager shrineManager;

    private NamespacedKey shrineItemKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int max = Math.max(1, getConfig().getInt("max-lives", 3));
        int start = Math.min(max, Math.max(1, getConfig().getInt("starting-lives", max)));

        this.livesManager = new LivesManager(this, max, start);
        this.tokenItems = new TokenItems(this);
        this.shrineManager = new ShrineManager(this);

        this.shrineItemKey = new NamespacedKey(this, "revival_shrine");

        ShrineItems.registerRecipe(this, shrineItemKey);

        getServer().getPluginManager().registerEvents(
                new LifeListener(this, livesManager, tokenItems),
                this
        );

        getServer().getPluginManager().registerEvents(
                new ShrineListener(this, livesManager, shrineManager, shrineItemKey),
                this
        );

        HeartCommand heartCommand = new HeartCommand(livesManager, tokenItems);

        PluginCommand heart = Objects.requireNonNull(getCommand("heartsmp"));
        heart.setExecutor(heartCommand);
        heart.setTabCompleter(heartCommand);

        LivesCommand livesCommand = new LivesCommand(livesManager);

        PluginCommand livesCommandPlugin = Objects.requireNonNull(getCommand("lives"));
        livesCommandPlugin.setExecutor(livesCommand);
        livesCommandPlugin.setTabCompleter(livesCommand);

        getLogger().info("HeartSMP enabled.");
    }

    public LivesManager getLivesManager() {
        return livesManager;
    }

    public TokenItems getTokenItems() {
        return tokenItems;
    }

    public ShrineManager getShrineManager() {
        return shrineManager;
    }
}
