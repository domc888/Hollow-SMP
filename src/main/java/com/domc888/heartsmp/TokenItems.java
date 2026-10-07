package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class TokenItems {

    private final NamespacedKey key;

    public TokenItems(HeartSMP plugin) {
        this.key = new NamespacedKey(plugin, "revival_token");
    }

    public ItemStack create(RevivalToken token) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text("Revival token")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        );

        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                token.id()
        );

        meta.setCustomModelData(token.customModelData());

        item.setItemMeta(meta);

        return item;
    }

    public RevivalToken read(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }

        if (item.getType() != Material.NETHER_STAR) {
            return null;
        }

        if (!item.hasItemMeta()) {
            return null;
        }

        String id = item.getItemMeta()
                .getPersistentDataContainer()
                .get(key, PersistentDataType.STRING);

        return RevivalToken.byId(id);
    }
}
