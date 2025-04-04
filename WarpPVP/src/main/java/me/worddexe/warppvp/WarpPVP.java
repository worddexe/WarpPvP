package me.worddexe.warppvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class WarpPVP extends JavaPlugin {

    private Location pvpLocation;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadWarp();
        registerCommands();
    }

    private void registerCommands() {
        PluginCommand setPvpCommand = getCommand("setpvp");
        PluginCommand pvpCommand = getCommand("pvp");

        if (setPvpCommand != null) setPvpCommand.setExecutor(new SetPVPCommand());
        if (pvpCommand != null) pvpCommand.setExecutor(new PVPCommand());
    }

    private void loadWarp() {
        FileConfiguration config = getConfig();
        if (config.contains("pvp-warp.world")) {
            String worldName = config.getString("pvp-warp.world");
            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                getLogger().warning("PVP warp world is invalid or not found!");
                return;
            }

            pvpLocation = new Location(
                    Bukkit.getWorld(worldName),
                    config.getDouble("pvp-warp.x"),
                    config.getDouble("pvp-warp.y"),
                    config.getDouble("pvp-warp.z"),
                    (float) config.getDouble("pvp-warp.yaw"),
                    (float) config.getDouble("pvp-warp.pitch")
            );
        }
    }

    private void saveWarp(Location location) {
        FileConfiguration config = getConfig();
        config.set("pvp-warp.world", location.getWorld().getName());
        config.set("pvp-warp.x", location.getX());
        config.set("pvp-warp.y", location.getY());
        config.set("pvp-warp.z", location.getZ());
        config.set("pvp-warp.yaw", location.getYaw());
        config.set("pvp-warp.pitch", location.getPitch());
        saveConfig();
    }

    private class SetPVPCommand implements TabExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }
            pvpLocation = player.getLocation();
            saveWarp(pvpLocation);
            player.sendMessage(ChatColor.GREEN + "The PvP warp has now been set. Try running /pvp to use it.");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }

    private class PVPCommand implements TabExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            Player player = (Player) sender;
            if (pvpLocation == null) {
                player.sendMessage(ChatColor.RED + "The PVP warp has not been set.");
                return true;
            }
            player.teleport(pvpLocation);
            player.sendMessage(ChatColor.GREEN + "Sending you to PvP...");
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return Collections.emptyList();
        }
    }
}
