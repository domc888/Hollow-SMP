package com.domc888.heartsmp;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class HeartSMP extends JavaPlugin {

    private LivesManager livesManager;
    private HollowAdminManager adminManager;
    private TokenItems tokenItems;
    private ShrineManager shrineManager;
    private NamespacedKey toolKey;
    private NamespacedKey shrineKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int maxLives = getConfig().getInt("max-lives", 3);
        int startingLives = getConfig().getInt("starting-lives", 3);

        this.toolKey = new NamespacedKey(this, "heartsmp_item");
        this.shrineKey = new NamespacedKey(this, "shrine_item");
        this.livesManager = new LivesManager(this, maxLives, startingLives);
        this.tokenItems = new TokenItems(this, toolKey);
        this.shrineManager = new ShrineManager(this);
        this.adminManager = new HollowAdminManager(this);

        // Register Commands
        HeartCommand heartCommand = new HeartCommand(this);
        if (getCommand("hollowsmp") != null) {
            getCommand("hollowsmp").setExecutor(heartCommand);
            getCommand("hollowsmp").setTabCompleter(heartCommand);
        }

        if (getCommand("voicechatmute") != null) {
            VoiceChatMuteCommand muteCmd = new VoiceChatMuteCommand(adminManager);
            getCommand("voicechatmute").setExecutor(muteCmd);
            getCommand("voicechatmute").setTabCompleter(muteCmd);
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new HollowAdminListener(this), this);
        getServer().getPluginManager().registerEvents(new LifeListener(this, livesManager, tokenItems), this);
        getServer().getPluginManager().registerEvents(new ShrineListener(this, livesManager, shrineManager, shrineKey), this);
    }

    @Override
    public void onDisable() {
        if (adminManager != null) {
            adminManager.shutdown();
        }
    }

    public LivesManager getLivesManager() {
        return livesManager;
    }

    public HollowAdminManager getAdminManager() {
        return adminManager;
    }

    public TokenItems getTokenItems() {
        return tokenItems;
    }

    public ShrineManager getShrineManager() {
        return shrineManager;
    }

    public NamespacedKey getToolKey() {
        return toolKey;
    }

    public NamespacedKey getShrineKey() {
        return shrineKey;
    }
}
