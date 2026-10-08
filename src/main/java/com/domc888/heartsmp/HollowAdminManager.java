package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class HollowAdminManager {

    private final HeartSMP plugin;

    private final NamespacedKey customItemKey;

    private final File file;
    private final YamlConfiguration data;

    private final Map<UUID, PermissionAttachment> voiceAttachments =
            new HashMap<>();

    public HollowAdminManager(HeartSMP plugin) {
        this.plugin = plugin;

        this.customItemKey = new NamespacedKey(
                plugin,
                "hollow_admin_item"
        );

        this.file = new File(
                plugin.getDataFolder(),
                "hollowsmp.yml"
        );

        this.data =
                YamlConfiguration.loadConfiguration(file);
    }

    public boolean isImmortality(UUID uuid) {
        return data.getBoolean(
                playerPath(uuid, "immortality"),
                false
        );
    }

    public boolean isSaturation(UUID uuid) {
        return data.getBoolean(
                playerPath(uuid, "saturation"),
                false
        );
    }

    public boolean isInfiniteArmor(UUID uuid) {
        return data.getBoolean(
                playerPath(uuid, "infinite-armor"),
                false
        );
    }

    public boolean isVoiceMuted(UUID uuid) {
        return data.getBoolean(
                playerPath(uuid, "voice-muted"),
                false
        );
    }

    public boolean isProximityChat() {
        return data.getBoolean(
                "global.proximity-chat",
                false
        );
    }

    public boolean isGlobalVoiceMute() {
        return data.getBoolean(
                "global.voice-mute",
                false
        );
    }

    public boolean toggleImmortality(UUID uuid) {
        boolean enabled = !isImmortality(uuid);

        setPlayerBoolean(
                uuid,
                "immortality",
                enabled
        );

        return enabled;
    }

    public boolean toggleSaturation(UUID uuid) {
        boolean enabled = !isSaturation(uuid);

        setPlayerBoolean(
                uuid,
                "saturation",
                enabled
        );

        Player player = Bukkit.getPlayer(uuid);

        if (player != null && enabled) {
            player.setFoodLevel(20);
        }

        return enabled;
    }

    public boolean toggleInfiniteArmor(UUID uuid) {
        boolean enabled = !isInfiniteArmor(uuid);

        setPlayerBoolean(
                uuid,
                "infinite-armor",
                enabled
        );

        return enabled;
    }

    public boolean toggleProximityChat() {
        boolean enabled = !isProximityChat();

        data.set(
                "global.proximity-chat",
                enabled
        );

        save();

        return enabled;
    }

    public boolean toggleGlobalVoiceMute() {
        boolean enabled = !isGlobalVoiceMute();

        data.set(
                "global.voice-mute",
                enabled
        );

        save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            applyVoicePermissions(player);
        }

        return enabled;
    }

    public boolean togglePersonalVoiceMute(UUID uuid) {
        boolean enabled = !isVoiceMuted(uuid);

        setPlayerBoolean(
                uuid,
                "voice-muted",
                enabled
        );

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            applyVoicePermissions(player);
        }

        return enabled;
    }

    public void applyVoicePermissions(Player player) {
        PermissionAttachment old =
                voiceAttachments.remove(
                        player.getUniqueId()
                );

        if (old != null) {
            player.removeAttachment(old);
        }

        boolean globalMute =
                isGlobalVoiceMute()
                        && !player.isOp();

        boolean personalMute =
                isVoiceMuted(
                        player.getUniqueId()
                );

        boolean muteSpeaking =
                globalMute || personalMute;

        boolean muteListening =
                globalMute;

        if (!muteSpeaking && !muteListening) {
            return;
        }

        PermissionAttachment attachment =
                player.addAttachment(plugin);

        attachment.setPermission(
                "voicechat.speak",
                !muteSpeaking
        );

        attachment.setPermission(
                "voicechat.listen",
                !muteListening
        );

        voiceAttachments.put(
                player.getUniqueId(),
                attachment
        );
    }

    public void shutdown() {
        for (Map.Entry<UUID, PermissionAttachment> entry :
                voiceAttachments.entrySet()) {

            Player player =
                    Bukkit.getPlayer(entry.getKey());

            if (player != null) {
                player.removeAttachment(
                        entry.getValue()
                );
            }
        }

        voiceAttachments.clear();
    }

    public ItemStack createRevivalHorn() {
        ItemStack item =
                new ItemStack(
                        org.bukkit.Material.GOAT_HORN
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.displayName(
                Component.text(
                        "Revival",
                        NamedTextColor.DARK_PURPLE,
                        TextDecoration.BOLD
                ).decoration(
                        TextDecoration.ITALIC,
                        false
                )
        );

        meta.getPersistentDataContainer().set(
                customItemKey,
                PersistentDataType.STRING,
                "revival_horn"
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isRevivalHorn(ItemStack item) {
        if (item == null
                || item.getType()
                != org.bukkit.Material.GOAT_HORN
                || !item.hasItemMeta()) {
            return false;
        }

        String value =
                item.getItemMeta()
                        .getPersistentDataContainer()
                        .get(
                                customItemKey,
                                PersistentDataType.STRING
                        );

        return "revival_horn".equals(value);
    }

    public void playRevivalHorn(Player player) {
        player.playSound(
                player.getLocation(),
                Sound.ENTITY_WARDEN_HEARTBEAT,
                1.0f,
                1.0f
        );

        double radius = 96.0;

        for (Player nearby :
                player.getWorld().getPlayers()) {

            if (nearby.getLocation()
                    .distanceSquared(
                            player.getLocation()
                    ) <= radius * radius) {

                nearby.playSound(
                        player.getLocation(),
                        Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                        1.0f,
                        1.0f
                );
            }
        }
    }

    public void sendToggleMessage(
            Player admin,
            String feature,
            boolean enabled,
            String targetName
    ) {
        admin.sendMessage(
                Component.text(
                        feature + " for ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                targetName,
                                NamedTextColor.GOLD
                        )
                ).append(
                        Component.text(
                                " is now ",
                                NamedTextColor.GRAY
                        )
                ).append(
                        Component.text(
                                enabled ? "ON" : "OFF",
                                enabled
                                        ? NamedTextColor.GREEN
                                        : NamedTextColor.RED,
                                TextDecoration.BOLD
                        )
                )
        );
    }

    public ItemStack createGuiItem(
            org.bukkit.Material material,
            String name,
            NamedTextColor color
    ) {
        return createGuiItem(
                material,
                name,
                color,
                null
        );
    }

    public ItemStack createGuiItem(
            org.bukkit.Material material,
            String name,
            NamedTextColor color,
            String lore
    ) {
        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.displayName(
                Component.text(
                        name,
                        color
                ).decoration(
                        TextDecoration.ITALIC,
                        false
                )
        );

        if (lore != null) {
            meta.lore(
                    java.util.List.of(
                            Component.text(
                                    lore,
                                    NamedTextColor.GRAY
                            )
                    )
            );
        }

        item.setItemMeta(meta);

        return item;
    }

    private void setPlayerBoolean(
            UUID uuid,
            String key,
            boolean value
    ) {
        data.set(
                playerPath(uuid, key),
                value
        );

        save();
    }

    private String playerPath(
            UUID uuid,
            String key
    ) {
        return "players."
                + uuid
                + "."
                + key;
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
                    "Could not save hollowsmp.yml",
                    e
            );
        }
    }
}
