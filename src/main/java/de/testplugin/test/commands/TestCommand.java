package de.testplugin.test.commands;

import de.testplugin.test.SpeedRunPlugin;
import jdk.incubator.vector.VectorOperators;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements TabExecutor {
    private List<String> permittedPlayers = new ArrayList<>();

    public TestCommand(){
        permittedPlayers.add("Flodreey");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) || !allowedToExecute(commandSender, "test"))
            return false;

        Player player = (Player) commandSender;

        if (strings.length != 1)
            return false;

        String arg = strings[0];
        if (arg.equals("tp")) {
            player.teleport(new Location(Bukkit.getWorld("world_the_end"), 0, 100, 0));
            return true;
        }

        else if (arg.startsWith("place")){
            Location loc = player.getLocation().add(new Vector(0, -1, 0));
            loc.getBlock().setType(Material.STRIPPED_OAK_LOG);
            BlockData blockData = loc.getBlock().getBlockData();
            BlockState blockState = loc.getBlock().getState();
            if (arg.endsWith("East")){
                loc.getBlock().setBlockData(blockData);
            } else if (arg.endsWith("North")){
                ((Directional) blockData).setFacing(BlockFace.NORTH);
                loc.getBlock().setBlockData(blockData);
            }
            return true;
        }

        else if (arg.equals("spawn")){
            player.getWorld().setSpawnLocation(player.getLocation());
            return true;
        }

        else if (arg.equals("bedspawn")){
            player.setBedSpawnLocation(player.getLocation(), true);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length != 1 || !(commandSender instanceof Player) || !allowedToExecute(commandSender, null))
            return new ArrayList<>();

        return Arrays.asList("tp", "placeEast", "placeNorth", "spawn", "bedspawn");
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
