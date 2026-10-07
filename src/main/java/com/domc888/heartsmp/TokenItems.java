package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
        ItemStack item = new ItemStack(token.material());
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(token.displayName()).decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, token.id());

        item.setItemMeta(meta);
        return item;
    }

    public RevivalToken read(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }
        String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return RevivalToken.byId(id);
    }
}
