package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
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

        ItemStack item = event.getPlayer()
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
                            "That player head could not be identified.",
                            NamedTextColor.RED
                    )
            );
            return;
        }

        Player target = Bukkit.getPlayerExact(playerName);

        if (target != null
                && !lives.isEliminated(
                target.getUniqueId()
        )) {
            event.getPlayer().sendMessage(
                    Component.text(
                            target.getName() + " is not death banned.",
                            NamedTextColor.RED
                    )
            );
            return;
        }

        org.bukkit.OfflinePlayer offline =
                Bukkit.getOfflinePlayer(playerName);

        UUID uuid = offline.getUniqueId();

        if (!lives.isEliminated(uuid)) {
            event.getPlayer().sendMessage(
                    Component.text(
                            playerName + " is not death banned.",
                            NamedTextColor.RED
                    )
            );
            return;
        }

        event.setCancelled(true);

        /*
         * Consume exactly one player head.
         */
        item.setAmount(item.getAmount() - 1);

        event.getPlayer()
                .getInventory()
                .setItemInMainHand(item);

        revive(
                playerName,
                uuid,
                clicked.getLocation()
        );
    }

    private void revive(
            String playerName,
            UUID uuid,
            Location shrine
    ) {
        /*
         * Revived players receive exactly 1 life.
         */
        lives.revive(uuid, 1);

        /*
         * Remove the death ban.
         */
        Bukkit.getBanList(BanList.Type.NAME)
                .pardon(playerName);

        /*
         * Permanently consume the revival shrine.
         *
         * Remove it from the ShrineManager first so
         * nothing else can treat this block as a shrine.
         */
        Block shrineBlock = shrine.getBlock();

        shrines.remove(shrineBlock);

        /*
         * Remove the actual shrine block from the world.
         */
        shrineBlock.setType(Material.AIR, false);

        /*
         * Play the End Portal opening sound to every
         * online player.
         */
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_END_PORTAL_SPAWN,
                    1.0f,
                    1.0f
            );
        }

        /*
         * A LOT of lightning around the shrine.
         *
         * strikeLightningEffect() is used so this is
         * visual lightning without normal lightning damage/fire.
         */
        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        for (int i = 0; i < 32; i++) {
            double angle =
                    random.nextDouble(0.0, Math.PI * 2.0);

            double radius =
                    random.nextDouble(1.0, 4.5);

            double x =
                    Math.cos(angle) * radius;

            double z =
                    Math.sin(angle) * radius;

            Location strike = shrine.clone().add(
                    x,
                    0,
                    z
            );

            int highestY =
                    shrine.getWorld()
                            .getHighestBlockYAt(
                                    strike.getBlockX(),
                                    strike.getBlockZ()
                            );

            strike.setY(highestY + 1);

            shrine.getWorld()
                    .strikeLightningEffect(strike);
        }

        /*
         * Broadcast the revival with the revived
         * player's name clearly coloured.
         */
        Bukkit.broadcast(
                Component.text(
                        playerName,
                        NamedTextColor.GREEN
                ).append(
                        Component.text(
                                " has been revived!",
                                NamedTextColor.GOLD
                        )
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
