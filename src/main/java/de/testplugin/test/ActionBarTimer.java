package de.testplugin.test;

import de.testplugin.test.commands.SpeedRunCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ActionBarTimer {
    SpeedRunPlugin plugin;
    LocalTime time;
    boolean paused = false;
    boolean stopped = true;
    boolean shown = true;

    ActionBarTimer(SpeedRunPlugin plugin){
        this.plugin = plugin;
        time = LocalTime.of(0, 0, 0);
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public void start() {
        paused = false;
        stopped = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (stopped){
                    this.cancel();
                    return;
                }

                if (!paused)
                    time = time.plusSeconds(1);

                if (shown)
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(ChatColor.BOLD +  time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void pause(){
        paused = true;
    }

    public void unpause(){
        paused = false;
    }

    public void stop(){
        stopped = true;
        reset();
    }

    public void reset(){
        time = LocalTime.of(0, 0, 0);
    }

    public boolean getShown(){
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
