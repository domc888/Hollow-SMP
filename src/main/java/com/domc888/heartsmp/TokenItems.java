package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
                Component.text("Revival Token")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        );

        /*
         * Hidden identifier used by the plugin to determine which
         * Revival Token type this is.
         */
        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                token.id()
        );

        /*
         * Each token gets a different model value so a resource pack
         * can give each token its own texture while the actual Minecraft
         * item remains a Netherite Star.
         */
        meta.setCustomModelData(token.customModelData());

        item.setItemMeta(meta);

        return item;
    }

    public RevivalToken read(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        String id = meta.getPersistentDataContainer().get(
                key,
                PersistentDataType.STRING
        );

        return RevivalToken.byId(id);
    }
}
