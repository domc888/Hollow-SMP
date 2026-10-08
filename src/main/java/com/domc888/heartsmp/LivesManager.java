package com.domc888.heartsmp;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public int getStartingLives() {
        return startingLives;
    }

    public int getLives(UUID id) {
        return Math.min(
                maxLives,
                Math.max(
                        0,
                        data.getInt(id + ".lives", startingLives)
                )
        );
    }

    public boolean isEliminated(UUID id) {
        return data.getBoolean(id + ".eliminated", false);
    }

    public void setLives(UUID id, int lives) {
        int clamped = Math.min(maxLives, Math.max(0, lives));

        data.set(id + ".lives", clamped);

        if (clamped <= 0) {
            data.set(id + ".eliminated", true);
            data.set(id + ".restore", false);
        } else {
            data.set(id + ".eliminated", false);
            data.set(id + ".restore", false);
        }

        save();

        if (clamped <= 0) {
            Player player = Bukkit.getPlayer(id);

            if (player != null) {
                deathBan(player);
            }
        }
    }

    public int loseLife(UUID id) {
        int newLives = Math.max(0, getLives(id) - 1);
        setLives(id, newLives);
        return newLives;
    }

    public void revive(UUID id, int lives) {
        int restoredLives = Math.min(maxLives, Math.max(1, lives));

        data.set(id + ".lives", restoredLives);
        data.set(id + ".eliminated", false);
        data.set(id + ".restore", true);

        save();

        OfflinePlayerData.unban(plugin, id);
    }

    /**
     * Revives the given offline player back to their starting
     * number of lives. Returns false if they were not eliminated.
     */
    public boolean revivePlayer(OfflinePlayer target) {
        UUID id = target.getUniqueId();

        if (!isEliminated(id)) {
            return false;
        }

        revive(id, startingLives);
        return true;
    }

    public void applyState(Player player) {
        UUID id = player.getUniqueId();

        if (isEliminated(id)) {
            deathBan(player);
            return;
        }

        if (data.getBoolean(id + ".restore", false)) {
            data.set(id + ".restore", false);
            save();

            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            player.setHealth(Math.min(
                    player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue(),
                    20.0
            ));
        }
    }

    private void deathBan(Player player) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(
                player.getName(),
                "You are out of lives.",
                null,
                "HeartSMP"
        );

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                player.kickPlayer("You are out of lives.");
            }
        });
    }

    private void save() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Could not save lives.yml",
                    e
            );
        }
    }

    private static final class OfflinePlayerData {

        private static void unban(HeartSMP plugin, UUID id) {
            Player player = Bukkit.getPlayer(id);

            if (player != null) {
                Bukkit.getBanList(BanList.Type.NAME).pardon(player.getName());
                return;
            }

            String name = plugin
                    .getServer()
                    .getOfflinePlayer(id)
                    .getName();

            if (name != null) {
                Bukkit.getBanList(BanList.Type.NAME).pardon(name);
            }
        }
    }
}
