package de.testplugin.test.commands;

import de.testplugin.test.ScoreManager;
import de.testplugin.test.SpeedRunPlugin;
import de.testplugin.test.SpeedRunStatus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class SpeedRunCommand implements TabExecutor {
    private SpeedRunPlugin plugin;
    public SpeedRunCommand(SpeedRunPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1){
            if (plugin.getStatus() == SpeedRunStatus.RUNNING)
                return Arrays.asList("stop", "pause", "list", "help");
            else if (plugin.getStatus() == SpeedRunStatus.PAUSED)
                return Arrays.asList("stop", "continue", "list", "help");
            else if (plugin.getStatus() == SpeedRunStatus.STOPPED)
                return Arrays.asList("start", "list", "help");
            else if (plugin.getStatus() == SpeedRunStatus.WON)
                return Arrays.asList("list", "help");
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            return false;
        }

        Player player = (Player) commandSender;
        if (strings.length == 1) {
            if ((plugin.getStatus() == SpeedRunStatus.STOPPED) && strings[0].equals("start")){
                plugin.setStatus(SpeedRunStatus.RUNNING);
                plugin.getTimer().stop();
                plugin.setInteractionEnabled(false);

                Map<Player, Location> playerSpawnMap = plugin.buildBigSpawnPlatform();
                for (Player p : Bukkit.getOnlinePlayers()){
                    Location teleportLocation = playerSpawnMap.get(player).toCenterLocation();
                    teleportLocation.add(0, 3, 0);
                    p.teleport(teleportLocation);

                    p.setBedSpawnLocation(teleportLocation, true);

                    plugin.buildSpawnCage(teleportLocation.add(0, 1, 0));

                    p.getInventory().clear();
                    p.getEquipment().setArmorContents(null);

                    p.setFoodLevel(20);
                    p.setHealth(20);

                    for (PotionEffect effect : p.getActivePotionEffects()){
                        p.removePotionEffect(effect.getType());
                    }
                }

                for (World world : Bukkit.getWorlds()){
                    world.setTime(0);
                }

                plugin.startCountdown(10);
                plugin.freezeWorldsAndTime(false);
            } else if ((plugin.getStatus() == SpeedRunStatus.RUNNING || plugin.getStatus() == SpeedRunStatus.PAUSED) && strings[0].equals("stop")) {
                plugin.setStatus(SpeedRunStatus.STOPPED);
                plugin.getTimer().stop();
                plugin.setInteractionEnabled(false);
                SpeedRunPlugin.sendMessageToAll(ChatColor.RED + "Speedrun stopped!");
            } else if (plugin.getStatus() == SpeedRunStatus.RUNNING && strings[0].equals("pause")) {
                plugin.setStatus(SpeedRunStatus.PAUSED);
                plugin.getTimer().pause();
                plugin.setInteractionEnabled(false);
                plugin.freezeWorldsAndTime(true);
            } else if (plugin.getStatus() == SpeedRunStatus.PAUSED && strings[0].equals("continue")) {
                plugin.setStatus(SpeedRunStatus.RUNNING);
                plugin.getTimer().unpause();
                plugin.setInteractionEnabled(true);
                plugin.freezeWorldsAndTime(false);
            } else if(strings[0].equals("list")){
                player.sendMessage("");
                player.sendMessage(ChatColor.GOLD +  "Speedrun Scores:");
                ScoreManager manager = plugin.getScoreManager();
                for (int i = 0; i < manager.size(); i++){
                    player.sendMessage("==============================");
                    ScoreManager.ScoreEntry current = manager.getEntryAtIndex(i);
                    player.sendMessage(ChatColor.GREEN + "Ranking: " + ChatColor.WHITE + "" +Integer.toString(i + 1));
                    List<String> playerNames = current.getPlayerNames();
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < playerNames.size(); j++){
                        if (j == playerNames.size() - 1){
                            builder.append(playerNames.get(j));
                        } else {
                            builder.append(playerNames.get(j)).append(", ");
                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "Players: " + ChatColor.WHITE + "" + builder.toString());
                    player.sendMessage(ChatColor.GREEN + "Speedrun time: " + ChatColor.WHITE + "" +current.getSpeedrunTimeString() + " h");
                    String time = current.getTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                    player.sendMessage(ChatColor.GREEN + "Date: " + ChatColor.WHITE + "" + current.getDateString() + " (" + time + ")");
                }
                player.sendMessage("==============================");
            } else if (strings[0].equals("help")) {
                player.sendMessage(ChatColor.GOLD + "----------" + ChatColor.WHITE + " Speedrun Help " + ChatColor.GOLD + "----------");
                player.sendMessage(ChatColor.GOLD + "/speedrun start: " + ChatColor.WHITE + "Starts the speedrun and the timer");
                player.sendMessage(ChatColor.GOLD + "/speedrun stop: " + ChatColor.WHITE + "Stops the speedrun and deletes timer");
                player.sendMessage(ChatColor.GOLD + "/speedrun pause: " + ChatColor.WHITE + "Pauses the speedrun and freezes the world");
                player.sendMessage(ChatColor.GOLD + "/speedrun continue: " + ChatColor.WHITE + "Continues the speedrun");
                player.sendMessage(ChatColor.GOLD + "/speedrun list: " + ChatColor.WHITE + "Shows a ranking of past speedruns");
                player.sendMessage(ChatColor.GOLD + "/speedrun help: " + ChatColor.WHITE + "Shows this help menu");
            } else
                return false;
        }
        return true;
    }
}
