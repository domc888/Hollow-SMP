package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
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
    private final TokenItems tokens;
    private final NamespacedKey customItemKey;

    private final File file;
    private final YamlConfiguration data;

    private final Map<UUID, PermissionAttachment> voiceAttachments =
            new HashMap<>();

    public HollowAdminManager(
            HeartSMP plugin,
            TokenItems tokens
    ) {
        this.plugin = plugin;
        this.tokens = tokens;

        this.customItemKey = new NamespacedKey(
                plugin,
                "hollow_admin_item"
        );

        this.file = new File(
                plugin.getDataFolder(),
                "hollowsmp.yml"
        );

        this.data = YamlConfiguration.loadConfiguration(file);
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

    public void setImmortality(UUID uuid, boolean value) {
        setPlayerBoolean(
                uuid,
                "immortality",
                value
        );
    }

    public void setSaturation(UUID uuid, boolean value) {
        setPlayerBoolean(
                uuid,
                "saturation",
                value
        );

        Player player = Bukkit.getPlayer(uuid);

        if (player != null && value) {
            player.setFoodLevel(20);
        }
    }

    public void setInfiniteArmor(UUID uuid, boolean value) {
        setPlayerBoolean(
                uuid,
                "infinite-armor",
                value
        );
    }

    public void setVoiceMuted(UUID uuid, boolean value) {
        setPlayerBoolean(
                uuid,
                "voice-muted",
                value
        );

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            applyVoicePermissions(player);
        }
    }

    public boolean toggleImmortality(UUID uuid) {
        boolean value = !isImmortality(uuid);

        setImmortality(uuid, value);

        return value;
    }

    public boolean toggleSaturation(UUID uuid) {
        boolean value = !isSaturation(uuid);

        setSaturation(uuid, value);

        return value;
    }

    public boolean toggleInfiniteArmor(UUID uuid) {
        boolean value = !isInfiniteArmor(uuid);

        setInfiniteArmor(uuid, value);

        return value;
    }

    public boolean toggleProximityChat() {
        boolean value = !isProximityChat();

        data.set(
                "global.proximity-chat",
                value
        );

        save();

        return value;
    }

    public boolean toggleGlobalVoiceMute() {
        boolean value = !isGlobalVoiceMute();

        data.set(
                "global.voice-mute",
                value
        );

        save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            applyVoicePermissions(player);
        }

        return value;
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
                new ItemStack(Material.GOAT_HORN);

        ItemMeta rawMeta =
                item.getItemMeta();

        if (rawMeta == null) {
            return item;
        }

        rawMeta.displayName(
                Component.text(
                        "Revival",
                        NamedTextColor.DARK_PURPLE,
                        TextDecoration.BOLD
                ).decoration(
                        TextDecoration.ITALIC,
                        false
                )
        );

        rawMeta.getPersistentDataContainer().set(
                customItemKey,
                PersistentDataType.STRING,
                "revival_horn"
        );

        if (rawMeta instanceof MusicInstrumentMeta instrumentMeta) {
            instrumentMeta.setInstrument(
                    org.bukkit.MusicInstrument.PONDER_GOAT_HORN
            );

            rawMeta = instrumentMeta;
        }

        item.setItemMeta(rawMeta);

        return item;
    }

    public boolean isRevivalHorn(ItemStack item) {
        if (item == null
                || item.getType() != Material.GOAT_HORN
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

    public void sendToggleMessage(
            Player admin,
            String feature,
            boolean enabled,
            String targetName
    ) {
        Component message =
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
                );

        admin.sendMessage(message);
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
        return "players." + uuid + "." + key;
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

    public ItemStack createGuiItem(
            Material material,
            String name,
            NamedTextColor color
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

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createGuiItem(
            Material material,
            String name,
            NamedTextColor color,
            String lore
    ) {
        ItemStack item =
                createGuiItem(
                        material,
                        name,
                        color
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.lore(
                java.util.List.of(
                        Component.text(
                                lore,
                                NamedTextColor.GRAY
                        )
                )
        );

        item.setItemMeta(meta);

        return item;
    }

    public void playRevivalHorn(Player player) {
        player.playSound(
                player.getLocation(),
                Sound.ENTITY_WARDEN_HEARTBEAT,
                1.0f,
                1.0f
        );

        for (Player nearby :
                player.getWorld().getPlayers()) {

            if (!nearby.getWorld().equals(
                    player.getWorld()
            )) {
                continue;
            }

            if (nearby.getLocation()
                    .distanceSquared(
                            player.getLocation()
                    ) <= 96.0 * 96.0) {

                nearby.playSound(
                        player.getLocation(),
                        Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                        1.0f,
                        1.0f
                );
            }
        }
    }
}
