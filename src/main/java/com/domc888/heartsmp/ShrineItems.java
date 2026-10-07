package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShrineItems {

    private ShrineItems() {
    }

    public static ItemStack create(NamespacedKey shrineKey) {
        ItemStack shrine = new ItemStack(Material.IRON_BLOCK);

        ItemMeta meta = shrine.getItemMeta();

        meta.displayName(
                Component.text("revival shrine")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false)
        );

        meta.getPersistentDataContainer().set(
                shrineKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        shrine.setItemMeta(meta);

        return shrine;
    }

    public static void registerRecipe(
            JavaPlugin plugin,
            NamespacedKey shrineKey
    ) {
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "revival_shrine"),
                create(shrineKey)
        );

        recipe.shape(
                "DGD",
                "THA",
                "DGD"
        );

        recipe.setIngredient(
                'D',
                Material.DIAMOND_BLOCK
        );

        recipe.setIngredient(
                'G',
                Material.GOLD_BLOCK
        );

        recipe.setIngredient(
                'T',
                Material.TOTEM_OF_UNDYING
        );

        recipe.setIngredient(
                'H',
                Material.PLAYER_HEAD
        );

        recipe.setIngredient(
                'A',
                Material.GOLDEN_APPLE
        );

        plugin.getServer()
                .addRecipe(recipe);
    }

    public static boolean isShrineItem(
            ItemStack item,
            NamespacedKey shrineKey
    ) {
        if (item == null || item.getType() != Material.IRON_BLOCK) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return false;
        }

        Byte value = item.getItemMeta()
                .getPersistentDataContainer()
                .get(
                        shrineKey,
                        PersistentDataType.BYTE
                );

        return value != null && value == (byte) 1;
    }
}
