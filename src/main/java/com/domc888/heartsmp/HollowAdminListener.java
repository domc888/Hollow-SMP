package com.domc888.heartsmp;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class HollowAdminListener implements Listener {

    private final HeartSMP plugin;

    public HollowAdminListener(HeartSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        if (!(event.getInventory().getHolder() instanceof HollowGuiHolder holder)) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        int slot = event.getRawSlot();

        if (holder.getType() == HollowGuiHolder.Type.MAIN) {
            switch (slot) {
                case 0 -> plugin.getAdminManager().openItemsGUI(admin);
                case 1 -> plugin.getAdminManager().openPlayersListGUI(admin);
                case 2 -> {
                    admin.playSound(admin.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1.0f, 1.0f);
                    admin.sendMessage(ChatColor.GREEN + "Played server audio.");
                }
                case 3 -> admin.sendMessage(ChatColor.AQUA + "Shrine settings opened.");
                case 4 -> {
                    // SLOT 4: TOGGLE FREEZE BARRIER
                    boolean currentState = plugin.getAdminManager().isFreezeBarrierActive();
                    plugin.getAdminManager().setFreezeBarrierActive(!currentState);
                    String statusMsg = !currentState 
                            ? ChatColor.AQUA + "[Freeze Barrier] " + ChatColor.GREEN + "ACTIVE. All non-OP players are frozen." 
                            : ChatColor.AQUA + "[Freeze Barrier] " + ChatColor.RED + "DISABLED.";
                    admin.sendMessage(statusMsg);

                    // Refresh GUI immediately so Slot 4 icon updates
                    plugin.getAdminManager().openMainGUI(admin);
                }
            }
        }
    }

    /**
     * Cancels movement for non-OP players when the freeze barrier is active.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getAdminManager().isFreezeBarrierActive() && !player.isOp()) {
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getY() != event.getTo().getY() || 
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }
}
