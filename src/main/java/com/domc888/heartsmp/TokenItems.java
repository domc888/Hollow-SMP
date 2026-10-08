package com.domc888.heartsmp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TokenItems {

    private final HeartSMP plugin;
    private final NamespacedKey key;

    public TokenItems(HeartSMP plugin, NamespacedKey key) {
        this.plugin = plugin;
        this.key = key;
    }

    public ItemStack createRevivalToken() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Revival Token");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "revival_token");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createHeartContainer() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Heart Container");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "heart_container");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createShrineCore() {
        ItemStack item = new ItemStack(Material.GOAT_HORN);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Shrine Core");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "shrine_core");
            item.setItemMeta(meta);
        }
        return item;
    }
}
