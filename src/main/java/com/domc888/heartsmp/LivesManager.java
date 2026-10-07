package com.domc888.heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public final class LivesManager {

    private final HeartSMP plugin;
    private final File file;
    private final YamlConfiguration data;
    private final int maxLives;
    private final int startingLives;

    public LivesManager(HeartSMP plugin, int maxLives, int startingLives) {
        this.plugin = plugin;
        this.maxLives = maxLives;
        this.startingLives = startingLives;
        this.file = new File(plugin.getDataFolder(), "lives.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public int getMaxLives() {
        return maxLives;
    }

    public int getLives(UUID id) {
        return Math.min(maxLives, Math.max(0, data.getInt(id + ".lives", startingLives)));
    }

    public boolean isEliminated(UUID id) {
        return data.getBoolean(id + ".eliminated", false);
    }

    private boolean needsRestore(UUID id) {
        return data.getBoolean(id + ".restore", false);
    }

    public void setLives(UUID id, int lives) {
        int clamped = Math.min(maxLives, Math.max(0, lives));
        boolean wasEliminated = isEliminated(id);

        data.set(id + ".lives", clamped);
        if (clamped == 0) {
            data.set(id + ".eliminated", true);
            data.set(id + ".restore", false);
        } else if (wasEliminated) {
            data.set(id + ".eliminated", false);
            data.set(id + ".restore", true);
        }
        save();
    }

    public int loseLife(UUID id) {
        setLives(id, getLives(id) - 1);
        return getLives(id);
    }

    private void clearRestore(UUID id) {
        data.set(id + ".restore", false);
        save();
    }

    public void applyState(Player player) {
        UUID id = player.getUniqueId();

        if (isEliminated(id)) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        } else if (needsRestore(id)) {
            clearRestore(id);
            Location spawn = player.getRespawnLocation();
            if (spawn == null) {
                spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
            }
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawn);
        }
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save lives.yml", e);
        }
    }
}
