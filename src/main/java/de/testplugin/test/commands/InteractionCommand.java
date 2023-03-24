package de.testplugin.test.commands;

import de.testplugin.test.SpeedRunPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InteractionCommand implements TabExecutor {
    private SpeedRunPlugin plugin;
    private List<String> permittedPlayers = new ArrayList<>();
    public InteractionCommand(SpeedRunPlugin plugin){
        this.plugin = plugin;
        permittedPlayers.add("Flodreey");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) || !allowedToExecute(commandSender, "interaction"))
            return false;

        Player player = (Player) commandSender;

        if (strings.length == 1){
            if (strings[0].equals("true") || strings[0].equals("t")){
                plugin.setInteractionEnabled(true);
                SpeedRunPlugin.sendMessageToAll(ChatColor.BLUE + "Interaction is now enabled");
            } else if (strings[0].equals("false") || strings[0].equals("f")){
                plugin.setInteractionEnabled(false);
                SpeedRunPlugin.sendMessageToAll(ChatColor.BLUE + "Interaction is now disabled");
            } else {
                return false;
            }
            return true;
        } else {
            plugin.setInteractionEnabled(true);
            SpeedRunPlugin.sendMessageToAll(ChatColor.BLUE + "Interction is now enabled");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) || !allowedToExecute(commandSender, null))
            return new ArrayList<>();

        if (strings.length == 1){
            return Arrays.asList("true", "false");
        }
        return new ArrayList<>();
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
