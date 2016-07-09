package me.iamguus.skywars.game.mechanics;


import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import me.iamguus.skywars.game.util.WorldGenerator;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Guus on 5-7-2016.
 */
public class GameData {

    private Plugin plugin;
    private File schematicFolder;
    private List<File> maps;
    private File currentMap;

    public Map<Location, Boolean> spawnLocations;
    private List<Location> chestLocations;
    public boolean noFallDamage = false;

    public List<Player> players;
    public List<Player> spectators;

    private int timeLeft = 0;
    public GameState state;

    public GameData(Plugin plugin) {
        this.plugin = plugin;
    }

    public File initialize() {
        state = GameState.RESTARTING;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        schematicFolder = new File(plugin.getDataFolder(), "maps" + File.separator);
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }

        if (schematicFolder.listFiles().length > 0) {
            maps = Arrays.asList(schematicFolder.listFiles());
        } else {
            System.out.println(String.format("[%s] Could not find any schematic file, please upload one and restart the server!", plugin.getDescription().getName()));
            return null;
        }

        currentMap = maps.get(new Random().nextInt(maps.size()));

        return currentMap;
    }

    public void createMap(File schematic) {
        if (schematic == null) {
            return;
        }

        WorldCreator c = new WorldCreator("skywars");
        c.environment(World.Environment.NORMAL);
        c.generator(new WorldGenerator());
        World w = c.createWorld();

        Location pasteLocation = w.getBlockAt(0, 128, 0).getLocation();

        EditSession session = new EditSession(new BukkitWorld(w), 999999);
        session.enableQueue();

        try {
            SchematicFormat schematicFormat = SchematicFormat.getFormat(schematic);
            CuboidClipboard clipboard = schematicFormat.load(schematic);

            clipboard.paste(session, BukkitUtil.toVector(pasteLocation), true);
        } catch (MaxChangedBlocksException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (DataException ex) {
            ex.printStackTrace();
        }

        session.flushQueue();

        spawnLocations = new HashMap<>();
        chestLocations = new ArrayList<>();

        players = new ArrayList<>();
        spectators = new ArrayList<>();

        for (Chunk chunks : w.getLoadedChunks()) {
            int x = chunks.getX() << 4;
            int z = chunks.getZ() << 4;
            for(int xx = x; xx < x + 16; xx++) {
                for(int zz = z; zz < z + 16; zz++) {
                    for(int yy = 0; yy < 256; yy++) {
                        Block block = w.getBlockAt(xx, yy, zz);

                        if (block.getType() == Material.BEACON) {
                            spawnLocations.put(block.getLocation(), false);
                            createBox(block.getLocation());
                        }

                        if (block.getType() == Material.CHEST) {
                            chestLocations.add(block.getLocation());
                        }
                    }
                }
            }
        }

        setGameState(GameState.WAITING);
    }


    private void createBox(Location location) {
        location.getBlock().setType(Material.AIR);

        location.clone().add(0, -1, 0).getBlock().setType(Material.GLASS);
        location.clone().add(1, -1, 0).getBlock().setType(Material.GLASS);
        location.clone().add(-1, -1, 0).getBlock().setType(Material.GLASS);
        location.clone().add(0, -1, 1).getBlock().setType(Material.GLASS);
        location.clone().add(0, -1, -1).getBlock().setType(Material.GLASS);

        location.clone().add(1, 0, 0).getBlock().setType(Material.GLASS);
        location.clone().add(0, 0, 1).getBlock().setType(Material.GLASS);
        location.clone().add(0, 0, -1).getBlock().setType(Material.GLASS);
        location.clone().add(-1, 0, 0).getBlock().setType(Material.GLASS);

        location.clone().add(1, 1, 0).getBlock().setType(Material.GLASS);
        location.clone().add(0, 1, 1).getBlock().setType(Material.GLASS);
        location.clone().add(0, 1, -1).getBlock().setType(Material.GLASS);
        location.clone().add(-1, 1, 0).getBlock().setType(Material.GLASS);

        location.clone().add(1, 2, 0).getBlock().setType(Material.GLASS);
        location.clone().add(0, 2, 1).getBlock().setType(Material.GLASS);
        location.clone().add(0, 2, -1).getBlock().setType(Material.GLASS);
        location.clone().add(-1, 2, 0).getBlock().setType(Material.GLASS);
    }

    public Location findOpenSpot() {
        for (Map.Entry<Location, Boolean> entry : spawnLocations.entrySet()) {
            if (!entry.getValue()) {
                spawnLocations.put(entry.getKey(), true);
                return entry.getKey();
            }
        }
        return null;
    }

    public void setGameState(GameState state) {
        this.state = state;

        // TODO: send socket message
    }

    public void checkForStart() {
        if (Bukkit.getOnlinePlayers().size() == spawnLocations.size()) {
            setGameState(GameState.STARTING);
            timeLeft = 10;
            new BukkitRunnable() {
                public void run() {
                    String template = ChatColor.GRAY + "The game will begin in " + ChatColor.BLUE + "%s" + ChatColor.GRAY + " seconds!";
                    switch (timeLeft) {
                        case 10:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 5:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 4:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 3:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 2:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 1:
                            Bukkit.broadcastMessage(String.format(template, timeLeft));
                            break;
                        case 0:
                            start();
                            Bukkit.broadcastMessage(ChatColor.BLUE + "The game has begun!");
                            this.cancel();
                    }
                    timeLeft--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
        } else {
            Bukkit.broadcastMessage(ChatColor.BLUE + "The countdown will start when the arena is full!");
        }
    }

    public void start() {
        noFallDamage = true;

        for (Location location : spawnLocations.keySet()) {
            location.clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
        }

        //TODO: Give kit

        setGameState(GameState.INGAME);


        new BukkitRunnable() {
            public void run() {
                noFallDamage = false;
            }
        }.runTaskLater(plugin, 20L * 5L);
    }

    public void handleDeath(Player player, Player killer) {
        //TODO: Add MySQL stats (player/killer)

        if (players.size() == 1) {
            Firework fw = (Firework) killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwMeta = fw.getFireworkMeta();

            FireworkEffect fwe = FireworkEffect.builder().withColor(Color.WHITE).flicker(true).build();
            fwMeta.addEffect(fwe);


        }
    }

    public enum GameState {
        RESTARTING("Restarting"),
        WAITING("Waiting"),
        STARTING("Starting"),
        INGAME("In Game");

        String name;

        GameState(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
