package com.domc888.heartsmp;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class HeartSMP extends JavaPlugin {

    private LivesManager livesManager;
    private HollowAdminManager adminManager;
    private TokenItems tokenItems;
    private NamespacedKey toolKey;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.toolKey = new NamespacedKey(this, "heartsmp_item");
        this.livesManager = new LivesManager(this);
        this.tokenItems = new TokenItems(this, toolKey);
        this.adminManager = new HollowAdminManager(this);

        // Register Commands
        HeartCommand heartCommand = new HeartCommand(this);
        if (getCommand("hollowsmp") != null) {
            getCommand("hollowsmp").setExecutor(heartCommand);
            getCommand("hollowsmp").setTabCompleter(heartCommand);
        }

        if (getCommand("voicechatmute") != null) {
            VoiceChatMuteCommand muteCmd = new VoiceChatMuteCommand(this);
            getCommand("voicechatmute").setExecutor(muteCmd);
            getCommand("voicechatmute").setTabCompleter(muteCmd);
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new HollowAdminListener(this), this);
        getServer().getPluginManager().registerEvents(new LifeListener(this), this);
        getServer().getPluginManager().registerEvents(new ShrineListener(this), this);
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

    public NamespacedKey getToolKey() {
        return toolKey;
    }
}
