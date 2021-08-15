package com.github.ozzymar.glowberriesreloaded;

import com.github.ozzymar.api.config.YamlConfig;
import com.github.ozzymar.api.jetbrains.NotNull;
import com.github.ozzymar.api.managers.CommandManager;
import com.github.ozzymar.api.misc.Formatter;
import com.github.ozzymar.api.model.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public final class GlowBerriesReloaded extends JavaPlugin implements Listener {

    // Create 'YamlConfig' object.
    private YamlConfig yamlConfiguration;

    @Override
    public void onEnable() {
        // Declare 'YamlConfig' object.
        yamlConfiguration = new YamlConfig(this, "config.yml");

        // Register this class as a listener since this is where the 'PlayerItemConsumeEvent' is.
        this.getServer().getPluginManager().registerEvents(this, this);

        // Instantiate 'CommandManager' and create the reload command.
        new CommandManager(this, new AbstractCommand("gbr-reload") {
            @Override
            public void executeAsPlayer(Player player, String[] strings) {
                if (!player.hasPermission("gbr.admin.reload")) {
                    player.sendMessage(Formatter.colorize(yamlConfiguration.getConfig().getString("noperms-message")));
                    return;
                }
                yamlConfiguration.reload();
                player.sendMessage(Formatter.colorize(yamlConfiguration.getConfig().getString("reload-message")));
            }

            @Override
            public @NotNull List<String> tabComplete(CommandSender commandSender, String s, String[] strings) throws IllegalArgumentException {
                return Collections.emptyList();
            }
        });
    }

    @Override
    public void onDisable() {
        // Un-declaring the 'yamlConfiguration' to prevent memory leaks.
        yamlConfiguration = null;
    }

    // Create the 'PlayerItemConsumeEvent' listener.
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        // If the item is not a 'minecraft:glow_berries' then do not do anything.
        if (event.getItem().getType() != Material.GLOW_BERRIES) return;

        // If player already has 'GLOWING' then do not do anything.
        if (event.getPlayer().hasPotionEffect(PotionEffectType.GLOWING)) return;

        // Effect the player with 'GLOWING' for a duration dependent on the 'config.yml' settings.
        event.getPlayer().addPotionEffect(new PotionEffect(
                PotionEffectType.GLOWING,
                yamlConfiguration.getConfig().getInt("glow-duration") * 20,
                1,
                false,
                false
        ));
    }
}
