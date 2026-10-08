package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HeartSMP extends JavaPlugin {

    private HollowAdminManager hollowAdminManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int max =
                Math.max(
                        1,
                        getConfig().getInt(
                                "max-lives",
                                3
                        )
                );

        int start =
                Math.min(
                        max,
                        Math.max(
                                1,
                                getConfig().getInt(
                                        "starting-lives",
                                        max
                                )
                        )
                );

        LivesManager lives =
                new LivesManager(
                        this,
                        max,
                        start
                );

        TokenItems tokens =
                new TokenItems(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new LifeListener(
                                this,
                                lives,
                                tokens
                        ),
                        this
                );

        HeartCommand heartCommand =
                new HeartCommand(
                        lives,
                        tokens
                );

        NamespacedKey shrineKey =
                new NamespacedKey(
                        this,
                        "revival_shrine"
                );

        ShrineManager shrineManager =
                new ShrineManager(this);

        getServer()
                .getPluginManager()
                .registerEvents(
                        new ShrineListener(
                                this,
                                lives,
                                shrineManager,
                                shrineKey
                        ),
                        this
                );

        registerShrineRecipe(
                shrineKey,
                tokens
        );

        hollowAdminManager =
                new HollowAdminManager(this);

        HollowAdminListener hollowListener =
                new HollowAdminListener(
                        this,
                        hollowAdminManager,
                        tokens,
                        shrineKey
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        hollowListener,
                        this
                );

        /*
         * /hollowsmp
         *
         * No arguments:
         * open the admin GUI.
         *
         * Arguments:
         * pass through to the existing HeartCommand,
         * preserving:
         *
         * /hollowsmp setlives ...
         * /hollowsmp give ...
         */
        PluginCommand hollowCommand =
                Objects.requireNonNull(
                        getCommand("hollowsmp")
                );

        hollowCommand.setExecutor(
                (sender, command, label, args) -> {

                    if (!sender.isOp()) {
                        sender.sendMessage(
                                Component.text(
                                        "Only server operators can use /hollowsmp.",
                                        NamedTextColor.RED
                                )
                        );

                        return true;
                    }

                    if (args.length == 0) {
                        if (!(sender
                                instanceof org.bukkit.entity.Player player)) {

                            sender.sendMessage(
                                    Component.text(
                                            "The GUI can only be opened by a player.",
                                            NamedTextColor.RED
                                    )
                            );

                            return true;
                        }

                        hollowListener.openMain(player);

                        return true;
                    }

                    return heartCommand.onCommand(
                            sender,
                            command,
                            label,
                            args
                    );
                }
        );

        hollowCommand.setTabCompleter(
                (sender, command, alias, args) ->
                        heartCommand.onTabComplete(
                                sender,
                                command,
                                alias,
                                args
                        )
        );

        LivesCommand livesCommand =
                new LivesCommand(lives);

        PluginCommand livesPluginCommand =
                Objects.requireNonNull(
                        getCommand("lives")
                );

        livesPluginCommand.setExecutor(
                livesCommand
        );

        livesPluginCommand.setTabCompleter(
                livesCommand
        );

        VoiceChatMuteCommand voiceChatMuteCommand =
                new VoiceChatMuteCommand(
                        hollowAdminManager
                );

        PluginCommand voiceMute =
                Objects.requireNonNull(
                        getCommand(
                                "voicechatmute"
                        )
                );

        voiceMute.setExecutor(
                voiceChatMuteCommand
        );

        voiceMute.setTabCompleter(
                voiceChatMuteCommand
        );
    }

    @Override
    public void onDisable() {
        if (hollowAdminManager != null) {
            hollowAdminManager.shutdown();
        }
    }

    private void registerShrineRecipe(
            NamespacedKey shrineKey,
            TokenItems tokens
    ) {
        ItemStack result =
                ShrineItems.create(
                        shrineKey
                );

        ShapedRecipe recipe =
                new ShapedRecipe(
                        new NamespacedKey(
                                this,
                                "revival_shrine"
                        ),
                        result
                );

        /*
         * D G D
         * G T G
         * D R D
         *
         * D = Diamond Block
         * G = Gold Block
         * T = Revival Token
         * R = Totem of Undying
         */

        recipe.shape(
                "DGD",
                "GTG",
                "DRD"
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
                'R',
                Material.TOTEM_OF_UNDYING
        );

        List<ItemStack> tokenChoices =
                new ArrayList<>();

        for (RevivalToken token :
                RevivalToken.values()) {

            tokenChoices.add(
                    tokens.create(token)
            );
        }

        recipe.setIngredient(
                'T',
                new RecipeChoice.ExactChoice(
                        tokenChoices
                )
        );

        getServer().addRecipe(recipe);
    }
}
