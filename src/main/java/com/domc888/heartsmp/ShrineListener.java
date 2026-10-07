package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.ThreadLocalRandom;

public final class ShrineListener implements Listener {

    private final HeartSMP plugin;
    private final LivesManager lives;
    private final ShrineManager shrines;
    private final NamespacedKey shrineKey;

    public ShrineListener(
            HeartSMP plugin,
            LivesManager lives,
            ShrineManager shrines,
            NamespacedKey shrineKey
    ) {
        this.plugin = plugin;
        this.lives = lives;
        this.shrines = shrines;
        this.shrineKey = shrineKey;
    }

    @EventHandler
    public void onShrinePlace(BlockPlaceEvent event) {
        if (!ShrineItems.isShrineItem(
                event.getItemInHand(),
                shrineKey
        )) {
            return;
        }

        shrines.add(event.getBlockPlaced());
    }

    @EventHandler
    public void onShrineBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (!shrines.isShrine(block)) {
            return;
        }

        shrines.remove(block);

        /*
         * Drop the custom shrine item again.
         */
        event.setDropItems(false);

        block.getWorld().dropItemNaturally(
                block.getLocation(),
                ShrineItems.create(shrineKey)
        );
    }

    @EventHandler
    public void onHeadPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block clicked = event.getClickedBlock();

        if (clicked == null) {
            return;
        }

        if (!shrines.isShrine(clicked)) {
            return;
        }

        ItemStack item =
                event.getPlayer()
                        .getInventory()
                        .getItemInMainHand();

        if (item.getType() != Material.PLAYER_HEAD) {
            return;
        }

        if (!(item.getItemMeta() instanceof SkullMeta skullMeta)) {
            return;
        }

        String playerName = getHeadPlayerName(skullMeta);

        if (playerName == null || playerName.isBlank()) {
            event.getPlayer().sendMessage(
                    Component.text(
                            "That player head could not be identified."
                    )
            );
            return;
        }

        Player target =
                Bukkit.getPlayerExact(playerName);

        if (target != null
                && !lives.isEliminated(
                target.getUniqueId()
        )) {
            event.getPlayer().sendMessage(
                    Component.text(
                            target.getName()
                                    + " is not death banned."
                    )
            );
            return;
        }

        org.bukkit.OfflinePlayer offline =
                Bukkit.getOfflinePlayer(playerName);

        if (!lives.isEliminated(
                offline.getUniqueId()
        )) {
            event.getPlayer().sendMessage(
                    Component.text(
                            playerName
                                    + " is not death banned."
                    )
            );
            return;
        }

        event.setCancelled(true);

        /*
         * Consume the player head.
         */
        item.setAmount(item.getAmount() - 1);

        event.getPlayer()
                .getInventory()
                .setItemInMainHand(item);

        revive(
                playerName,
                offline.getUniqueId(),
                clicked.getLocation()
        );
    }

    private void revive(
            String playerName,
            java.util.UUID uuid,
            Location shrine
    ) {
        /*
         * Set exactly 1 life and remove elimination.
         */
        lives.revive(uuid, 1);

        /*
         * Remove the death ban.
         */
        Bukkit.getBanList(BanList.Type.NAME)
                .pardon(playerName);

        /*
         * Lightning at random positions within radius 4.
         */
        for (int i = 0; i < 6; i++) {
            double x =
                    ThreadLocalRandom.current()
                            .nextDouble(-4.0, 4.0);

            double z =
                    ThreadLocalRandom.current()
                            .nextDouble(-4.0, 4.0);

            Location strike =
                    shrine.clone().add(
                            x,
                            1,
                            z
                    );

            strike.setY(
                    shrine.getWorld()
                            .getHighestBlockYAt(
                                    strike
                            ) + 1
            );

            shrine.getWorld()
                    .strikeLightningEffect(strike);
        }

        /*
         * Exact requested revival message.
         */
        Bukkit.broadcast(
                Component.text(
                        playerName
                                + " has been revived"
                )
        );
    }

    private String getHeadPlayerName(
            SkullMeta meta
    ) {
        if (meta.getOwnerProfile() != null) {
            if (meta.getOwnerProfile().getName() != null) {
                return meta.getOwnerProfile().getName();
            }
        }

        if (meta.getOwningPlayer() != null) {
            return meta.getOwningPlayer().getName();
        }

        return null;
    }
}
