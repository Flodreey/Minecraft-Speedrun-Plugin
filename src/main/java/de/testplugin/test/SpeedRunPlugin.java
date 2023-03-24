package de.testplugin.test;

import de.testplugin.test.commands.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SpeedRunPlugin extends JavaPlugin implements Listener {
    private boolean interactionEnabled = false;
    private List<Location> undestroyableBlocks = new ArrayList<>();
    private List<Location> unplaceableBlocks = new ArrayList<>();
    ActionBarTimer timer = new ActionBarTimer(this);
    SpeedRunStatus status = SpeedRunStatus.STOPPED;
    ScoreManager scoreManager = new ScoreManager();
    World overworld;
    Map<Player, Location> playerSpawnMap;
    @Override
    public void onEnable() {
        getLogger().info("Speedrun Plugin has been enabled!");

        getCommand("interaction").setExecutor(new InteractionCommand(this));
        getCommand("speedrun").setExecutor(new SpeedRunCommand(this));
        getCommand("position").setExecutor(new PositionCommand(this));
        getCommand("test").setExecutor(new TestCommand());
        getCommand("freeze").setExecutor(new FreezeCommand(this));

        getServer().getPluginManager().registerEvents(new InteractionEvents(this), this);

        for (World world : Bukkit.getWorlds()){
            if (world.getEnvironment() == World.Environment.NORMAL)
                overworld = world;
        }
        if (overworld == null) overworld = Bukkit.getWorld("world");
        assert overworld != null;

        undestroyableBlocks.clear();
        unplaceableBlocks.clear();

        playerSpawnMap = buildBigSpawnPlatform();

        freezeWorldsAndTime(false);
    }

    @Override
    public void onDisable() {
        getLogger().info("Speedrun Plugin has been disabled!");
    }

    public boolean getInteractionEnabled(){
        return interactionEnabled;
    }

    public void setInteractionEnabled(boolean interactionEnabled){
        this.interactionEnabled = interactionEnabled;
    }

    public List<Location> getUndestroyableBlocks() {
        return undestroyableBlocks;
    }

    public void addUndestroyableBlock(Location loc){
        undestroyableBlocks.add(loc);
    }

    public List<Location> getUnplaceableBlocks() {
        return unplaceableBlocks;
    }

    public void addUnplaceableBlock(Location loc){
        unplaceableBlocks.add(loc);
    }

    public ActionBarTimer getTimer() {
        return timer;
    }

    public SpeedRunStatus getStatus() {
        return status;
    }

    public void setStatus(SpeedRunStatus status) {
        this.status = status;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public World getOverworld() {
        return overworld;
    }

    public Map<Player, Location> getPlayerSpawnMap() {
        return playerSpawnMap;
    }

    public static void sendMessageToAll(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public void startCountdown(int start_seconds){
        final int countdown = start_seconds;
        new BukkitRunnable() {
            int count = countdown;
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendTitle(Integer.toString(count), "The speed run is about to start", 0, 25, 0);
                    //player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
                    player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 1.0f);
                });

                if (count == 0){
                    startSpeedRun();
                    this.cancel();
                    return;
                }

                count--;
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    public void startSpeedRun(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
            player.sendTitle(ChatColor.BOLD + "GO!", "");
            removeBlockUnderPlayer(player);
        });

        timer.start();

        interactionEnabled = true;
    }

    public void removeBlockUnderPlayer(Player player){
        Location loc = player.getLocation();
        loc.getBlock().setType(Material.AIR);
        loc.subtract(0, 1, 0);
        loc.getBlock().setType(Material.AIR);
        loc.subtract(0, 1, 0);
        loc.getBlock().setType(Material.AIR);
    }

    // Called when enderdragon is defeated
    public void winSpeedRun(){
        setStatus(SpeedRunStatus.WON);
        List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        int ranking = scoreManager.addEntry(playerNames, timer.getTime(), Bukkit.getWorld("world").getSeed());
        sendMessageToAll("==============================");
        sendMessageToAll(ChatColor.BOLD + "" + ChatColor.GOLD + "YOU WON!");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < playerNames.size(); i++) {
            if (i == playerNames.size() - 1) {
                builder.append(playerNames.get(i));
            } else {
                builder.append(playerNames.get(i)).append(", ");
            }
        }
        sendMessageToAll(ChatColor.GREEN + "Players: " + builder.toString());
        sendMessageToAll(ChatColor.GREEN + "Your time: " + timer);
        sendMessageToAll(ChatColor.GREEN + "Ranking: " + ranking);
        sendMessageToAll("==============================");
        timer.stop();
    }

    public void freezeWorldsAndTime(boolean enable){
        for (World world : Bukkit.getWorlds()){
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !enable);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, !enable);
            for (Entity entity : world.getEntities()){
                if (enable)
                    entity.setVelocity(new Vector(0, 0, 0));
                if (entity instanceof LivingEntity){
                    ((LivingEntity) entity).setAI(!enable);
                }
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()){
            new BukkitRunnable() {
                final int savedFoodeLevel = player.getFoodLevel();
                final double savedHealthLevel = player.getHealth();
                @Override
                public void run() {
                    if (status != SpeedRunStatus.PAUSED){
                        this.cancel();
                        return;
                    }
                    player.setFoodLevel(savedFoodeLevel);
                    player.setHealth(savedHealthLevel);
                }
            }.runTaskTimer(this, 0L, 10L);
        }
    }

    public boolean checkIfWorldAlreadyWon(){
        long worldSeed = Bukkit.getWorld("world").getSeed();
        for (int i = 0; i < scoreManager.size(); i++){
            ScoreManager.ScoreEntry current = scoreManager.getEntryAtIndex(i);
            if (current.getWorldSeed() == worldSeed){
                return true;
            }
        }
        return false;
    }

    private Block placeBlock(double x, double y, double z, Material material){
        Location blockLocation = new Location(overworld, x, y, z);
        Block block = blockLocation.getBlock();
        block.setType(material);

        addUndestroyableBlock(blockLocation.toCenterLocation());
        addUnplaceableBlock(blockLocation.toCenterLocation());

        return block;
    }

    public Block placeUpperSlab(double x, double y, double z){
        Block block = placeBlock(x, y, z, Material.OAK_SLAB);
        Location loc = new Location(overworld, x, y, z);
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Slab){
            Slab slab = (Slab) blockData;
            slab.setType(Slab.Type.TOP);
            loc.getBlock().setBlockData(slab);
        }
        return block;
    }

    public void buildSpawnCage(Location loc){
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        placeBlock(x, y, z, Material.AIR);
        placeBlock(x, y + 1, z, Material.AIR);

        placeBlock(x, y - 1, z, Material.OAK_SLAB);
        placeUpperSlab(x + 1, y - 1, z);
        placeUpperSlab(x - 1, y - 1, z);
        placeUpperSlab(x, y - 1, z + 1);
        placeUpperSlab(x, y - 1, z - 1);

        placeBlock(x - 1, y, z, Material.GLASS);
        placeBlock(x - 1, y + 1, z, Material.GLASS);
        placeBlock(x + 1, y, z, Material.GLASS);
        placeBlock(x + 1, y + 1, z, Material.GLASS);
        placeBlock(x, y, z - 1, Material.GLASS);
        placeBlock(x, y + 1, z - 1, Material.GLASS);
        placeBlock(x, y, z + 1, Material.GLASS);
        placeBlock(x, y + 1, z + 1, Material.GLASS);

        placeUpperSlab(x, y + 2, z);
        placeBlock(x + 1, y + 2, z, Material.OAK_SLAB);
        placeBlock(x - 1, y + 2, z, Material.OAK_SLAB);
        placeBlock(x, y + 2, z + 1, Material.OAK_SLAB);
        placeBlock(x, y + 2, z - 1, Material.OAK_SLAB);
    }

    public void buildLittleSpawnPlatform(double x, double y, double z){
        // 3x3x1 wood platform and 3x3x6 air above wood -> 3x3x7
        x -= 1;
        z -= 1;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 7; j ++){
                for (int k = 0; k < 3; k++){
                    Material material;
                    if (j == 0) material = Material.STRIPPED_OAK_LOG;
                    else material = Material.AIR;

                    placeBlock(x + i, y + j, z + k, material);
                }
            }
        }
    }

    public Map<Player, Location> buildBigSpawnPlatform(){
        Location spawn = overworld.getSpawnLocation();
        double x = spawn.getX() - 4;
        double y = spawn.getY();
        double z = spawn.getZ() - 4;
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 20; j++){
                for (int k = 0; k < 9; k++){
                    Material material;
                    if (j == 0) material = Material.STRIPPED_OAK_LOG;
                    else material = Material.AIR;
                    placeBlock(x + i, y + j, z + k, material);
                }
            }
        }

        x += 4;
        z += 4;
        Location[] littlePlatforms = {
                new Location(overworld, x, y + 1, z + 6),
                new Location(overworld, x + 4, y + 1, z + 4),
                new Location(overworld, x + 6, y + 1, z),
                new Location(overworld, x + 4, y + 1, z - 4),
                new Location(overworld, x, y + 1, z - 6),
                new Location(overworld, x - 4, y + 1, z - 4),
                new Location(overworld, x - 6, y + 1, z),
                new Location(overworld, x - 4, y + 1, z + 4)
        };
        for (Location p : littlePlatforms){
            buildLittleSpawnPlatform(p.getX(), p.getY(), p.getZ());
            Location current = p.clone();
            while (current.getBlock().getType() == Material.AIR || current.getBlock().getType() == Material.STRIPPED_OAK_LOG || current.getBlock().getType() == Material.GRASS || current.getBlock().getType() == Material.SNOW){
                placeBlock(current.getX(), current.getY(), current.getZ(), Material.STRIPPED_OAK_LOG);
                current.add(new Vector(0, -1, 0));
            }
        }

        int playerAmount = Bukkit.getOnlinePlayers().size();
        int index = 0;
        Map<Player, Location> result = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()){
            result.put(p, littlePlatforms[index]);
            index += 8 / playerAmount;
        }
        return result;
    }
}
