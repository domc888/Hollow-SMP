package com.domc888.heartsmp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public final class ShrineManager {

    private final HeartSMP plugin;
    private final File file;
    private final YamlConfiguration data;

    private final Set<String> shrines = new HashSet<>();

    public ShrineManager(HeartSMP plugin) {
        this.plugin = plugin;

        this.file = new File(
                plugin.getDataFolder(),
                "shrines.yml"
        );

        this.data =
                YamlConfiguration.loadConfiguration(file);

        load();
    }

    public void add(Block block) {
        shrines.add(key(block.getLocation()));
        save();
    }

    public void remove(Block block) {
        shrines.remove(key(block.getLocation()));
        save();
    }

    public boolean isShrine(Block block) {
        return shrines.contains(
                key(block.getLocation())
        );
    }

    private void load() {
        for (String key : data.getStringList("shrines")) {
            shrines.add(key);
        }
    }

    private void save() {
        data.set(
                "shrines",
                shrines.stream().toList()
        );

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Could not save shrines.yml",
                    e
            );
        }
    }

    private String key(Location location) {
        World world = location.getWorld();

        if (world == null) {
            return "";
        }

        return world.getUID()
                + ":"
                + location.getBlockX()
                + ":"
                + location.getBlockY()
                + ":"
                + location.getBlockZ();
    }
}
