package de.testplugin.test.commands;

import de.testplugin.test.SpeedRunPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PositionCommand implements TabExecutor {
    SpeedRunPlugin plugin;
    private boolean positionShown = false;

    public PositionCommand(SpeedRunPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        if (strings.length == 0){
            printPlayerPosition(player);
            return true;
        } else if (strings.length == 1){
            if (strings[0].equals("on")){
                positionShown = true;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!positionShown){
                            player.sendActionBar(Component.text(""));
                            this.cancel();
                            return;
                        }
                        int x = player.getLocation().getBlockX();
                        int y = player.getLocation().getBlockY();
                        int z = player.getLocation().getBlockZ();
                        String facing = player.getFacing().toString();
                        player.sendActionBar(Component.text("X: " + ChatColor.BOLD + "" + x +
                                ChatColor.WHITE + "  Y: " + ChatColor.BOLD + "" + y +
                                ChatColor.WHITE +  "  Z: " + ChatColor.BOLD + z +
                                ChatColor.WHITE + " (" +  facing + ")"));
                    }
                }.runTaskTimer(plugin, 0L, 5L);
                plugin.getTimer().setShown(false);
                return true;
            } else if (strings[0].equals("off")){
                positionShown = false;
                plugin.getTimer().setShown(true);
                return true;
            } else if (strings[0].equals("help")){
                player.sendMessage(ChatColor.GOLD + "----------" + ChatColor.WHITE + " Position Help " + ChatColor.GOLD + "----------");
                player.sendMessage(ChatColor.GOLD + "/position: " + ChatColor.WHITE + "Tells every player your position");
                player.sendMessage(ChatColor.GOLD + "/position <player name>: " + ChatColor.WHITE + "Shows position of the specified player");
                player.sendMessage(ChatColor.GOLD + "/position <on | off>: " + ChatColor.WHITE + "Toggle your position on action bar");
                player.sendMessage(ChatColor.GOLD + "/position help: " + ChatColor.WHITE + "Shows this help menu");
                return true;
            }

            List<String> onlinePlayerNames = Bukkit.getOnlinePlayers().stream().map(p -> p.getPlayer().getName()).collect(Collectors.toList());
            if (!onlinePlayerNames.contains(strings[0])){
                player.sendMessage(ChatColor.RED + "There is no online player with the name \"" + strings[0] + "\"");
                return false;
            }
            printPlayerPosition(Bukkit.getPlayer(strings[0]));
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1){
            List<String> result = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            result.add(0, "off");
            result.add(0, "on");
            result.add(0, "help");
            return result;
        }
        return new ArrayList<>();
    }

    private void printPlayerPosition(Player player){
        if (player == null){
            return;
        }
        Location loc = player.getLocation();
        String world;
        if (player.getWorld().getName().endsWith("_nether")){
            world = "Nether";
        } else if (player.getWorld().getName().endsWith("_end")){
            world = "End";
        } else {
            world = "Overworld";
        }
        SpeedRunPlugin.sendMessageToAll("Position of " + player.getName() + ": ");
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        SpeedRunPlugin.sendMessageToAll(ChatColor.GOLD + "(" + x + ", " + y + ", " + z + ") -> " + world);
    }
}
