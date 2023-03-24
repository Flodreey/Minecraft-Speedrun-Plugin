package de.testplugin.test.commands;

import de.testplugin.test.InteractionEvents;
import de.testplugin.test.SpeedRunPlugin;
import de.testplugin.test.SpeedRunStatus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FreezeCommand implements TabExecutor {
    private SpeedRunPlugin plugin;
    private Map<World, Long> savedWorldTime = new HashMap<>();
    private List<String> permittedPlayers = new ArrayList<>();

    public FreezeCommand(SpeedRunPlugin plugin){
        this.plugin = plugin;
        for (World world : Bukkit.getWorlds()){
            savedWorldTime.put(world, -1L);
        }
        permittedPlayers.add("Flodreey");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) || strings.length != 1 || !allowedToExecute(commandSender, "freeze")){
            return false;
        }

        if (strings[0].equals("on")){
            plugin.setStatus(SpeedRunStatus.PAUSED);
            SpeedRunPlugin.sendMessageToAll(ChatColor.BLUE + "The world is freezed now!");
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                savedWorldTime.replace(world, world.getTime());
                for (Entity entity : world.getEntities()) {
                    entity.setVelocity(new Vector(0, 0, 0));
                    if (entity instanceof LivingEntity)
                        ((LivingEntity) entity).setAI(false);
                }
            }
            return true;
        } else if (strings[0].equals("off")) {
            plugin.setStatus(SpeedRunStatus.RUNNING);
            SpeedRunPlugin.sendMessageToAll(ChatColor.BLUE + "The world is unfreezed now!");
            for (World world : Bukkit.getWorlds()){
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                world.setTime(savedWorldTime.get(world));
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof LivingEntity)
                        ((LivingEntity) entity).setAI(true);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) || !allowedToExecute(commandSender, null) || strings.length != 1) {
            return new ArrayList<>();
        }

        return Arrays.asList("on", "off");
    }

    private boolean allowedToExecute(CommandSender sender, String commandName) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        if (!permittedPlayers.contains(player.getName())) {
            if (commandName != null)
                player.sendMessage(ChatColor.RED + "You don't have the permission to execute the " + commandName + " command!");
            return false;
        }
        return true;
    }
}
