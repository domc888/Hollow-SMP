package com.domc888.heartsmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum RevivalToken {

    EMERALD,
    DIAMOND,
    GOLD,
    IRON,
    COPPER,
    NETHERITE,
    AMETHYST,
    ECHO,
    PRISMARINE_SHARD,
    PRISMARINE_CRYSTAL,
    QUARTZ,
    LAPIS,
    GLOWSTONE,
    BLAZE,
    GHAST,
    NAUTILUS,
    HEART_OF_THE_SEA,
    NETHER_STAR,
    PHANTOM,
    RABBIT,
    FEATHER,
    SLIME,
    MAGMA,
    BREEZE,
    DISC_FRAGMENT;

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String displayName() {
        return "Revival token";
    }

    public int customModelData() {
        return 1001 + ordinal();
    }

    public static RevivalToken byId(String id) {
        if (id == null) {
            return null;
        }

        try {
            return valueOf(id.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static List<String> ids() {
        List<String> ids = new ArrayList<>();

        for (RevivalToken token : values()) {
            ids.add(token.id());
        }

        return ids;
    }
}
