package net.justinthetrucker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class SkeletonReducer extends JavaPlugin implements Listener {

    private double spawnReductionChance;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadConfig();

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("SkeletonReducer plugin enabled! Skeleton spawn reduction: " +
                (spawnReductionChance * 100) + "%");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkeletonReducer plugin disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        spawnReductionChance = config.getDouble("spawn-reduction-percentage", 50.0) / 100.0;

        if (spawnReductionChance < 0) spawnReductionChance = 0;
        if (spawnReductionChance > 1) spawnReductionChance = 1;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("skeletonreducer")) {
            if (!sender.hasPermission("skeletonreducer.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.GOLD + "SkeletonReducer v" + getDescription().getVersion());
                sender.sendMessage(ChatColor.YELLOW + "Current spawn reduction: " +
                        (spawnReductionChance * 100) + "%");
                sender.sendMessage(ChatColor.GRAY + "Use /skeletonreducer reload to reload config");
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                loadConfig();
                sender.sendMessage(ChatColor.GREEN + "SkeletonReducer config reloaded!");
                sender.sendMessage(ChatColor.YELLOW + "New spawn reduction: " +
                        (spawnReductionChance * 100) + "%");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Unknown command. Use /skeletonreducer reload");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.SKELETON) {
            CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

            if (reason == CreatureSpawnEvent.SpawnReason.NATURAL ||
                    reason == CreatureSpawnEvent.SpawnReason.SPAWNER ||
                    reason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {

                if (Math.random() < spawnReductionChance) {
                    event.setCancelled(true);
                }
            }
        }
    }
}