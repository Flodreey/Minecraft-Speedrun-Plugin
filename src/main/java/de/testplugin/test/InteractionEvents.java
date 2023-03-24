package de.testplugin.test;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class InteractionEvents implements Listener {
    SpeedRunPlugin plugin;
    public InteractionEvents(SpeedRunPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if (!plugin.getInteractionEnabled()){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't destroy blocks at the moment!");
        } else if (plugin.getUndestroyableBlocks().contains(block.getLocation().toCenterLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't destroy blocks of your spawn area!");
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        if (!plugin.getInteractionEnabled()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks at the moment!");
        } else if (plugin.getUnplaceableBlocks().contains(block.getLocation().toCenterLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks in your spawn area!");
        } else {
            event.setCancelled(false);
        }
    }

    /*
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.RED + "Block is burning!"));
        Block block = event.getBlock();
        if (plugin.getUndestroyableBlocks().contains(block.getLocation().toCenterLocation())) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event){
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.RED + "Block is damaged!"));
        Block block = event.getBlock();
        if (plugin.getUndestroyableBlocks().contains(block.getLocation().toCenterLocation())) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }
    }
     */

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        if (!plugin.getInteractionEnabled()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't interact with any entities at the moment!");
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        //Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.RED + "An Entity was damaged!"));
        if (!plugin.getInteractionEnabled() && event.getDamager().getType() == EntityType.PLAYER){
            event.setCancelled(true);
            Player player = (Player) event.getDamager();
            player.sendMessage(ChatColor.RED + "You can't harm any entities at the moment!");
        } else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        //Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.RED + "An Entity died!"));
        Entity entity = event.getEntity();
        if (entity instanceof EnderDragon){
            plugin.winSpeedRun();
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
        if (plugin.getStatus() == SpeedRunStatus.PAUSED){
            event.setCancelled(true);
        } else {
            //SpeedRunPlugin.sendMessageToAll(ChatColor.RED + "Ein Entity is gespawnt");
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (plugin.getStatus() == SpeedRunStatus.PAUSED && (from.getX() != to.getX() || from.getZ() != to.getZ())){
            player.teleport(from);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (plugin.getStatus() == SpeedRunStatus.WON || plugin.checkIfWorldAlreadyWon()){
            plugin.setStatus(SpeedRunStatus.WON);
            player.sendMessage("==============================");
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "YOU ALREADY WON IN THIS WORLD!");
            player.sendMessage(ChatColor.GREEN + "Generate a new world and set a new speedrun record");
            player.sendMessage("==============================");
        } else if (plugin.getStatus() == SpeedRunStatus.STOPPED){
            player.sendMessage("==============================");
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "WELCOME TO FLODREEYS SPEEDRUN WORLD!");
            player.sendMessage(ChatColor.GREEN + "Type \"/speedrun start\" to start the speedrun");
            player.sendMessage("==============================");
        } else if (plugin.getStatus() == SpeedRunStatus.PAUSED) {
            player.sendMessage("==============================");
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "WELCOME TO FLODREEYS SPEEDRUN WORLD!");
            player.sendMessage(ChatColor.GREEN + "The speedrun is currently paused. Type \"/speedrun continue\" to continue");
            player.sendMessage("==============================");
        } else if (plugin.getStatus() == SpeedRunStatus.RUNNING) {
            player.sendMessage("==============================");
            player.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + "WELCOME TO FLODREEYS SPEEDRUN WORLD!");
            player.sendMessage(ChatColor.GREEN + "The speedrun is currently running");
            player.sendMessage("==============================");
        }
    }
}
