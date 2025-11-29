package me.Fluxoah.homePlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {
    private final HomesPlusPlus plugin;
    private final File dataFile;
    private final FileConfiguration data;
    private final Map<UUID, Map<String, Home>> homes = new HashMap<>();

    public HomeManager(HomesPlusPlus plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "homes.yml");
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
                YamlConfiguration.loadConfiguration(dataFile).save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml");
            }
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void loadHomes() {
        homes.clear();
        for (String key : data.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            Map<String, Home> playerHomes = new HashMap<>();
            if (data.isConfigurationSection(key)) {
                for (String name : data.getConfigurationSection(key).getKeys(false)) {
                    String path = key + "." + name;
                    String world = data.getString(path + ".world", null);
                    double x = data.getDouble(path + ".x", 0);
                    double y = data.getDouble(path + ".y", 0);
                    double z = data.getDouble(path + ".z", 0);
                    float yaw = (float) data.getDouble(path + ".yaw", 0f);
                    float pitch = (float) data.getDouble(path + ".pitch", 0f);
                    long created = data.getLong(path + ".created", System.currentTimeMillis());
                    boolean safe = data.getBoolean(path + ".safe", true);
                    if (world != null && Bukkit.getWorld(world) != null) {
                        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                        playerHomes.put(name, new Home(loc, created, safe));
                    }
                }
            }
            homes.put(uuid, playerHomes);
        }
    }

    public void saveHomes() {
        // clear file
        for (String k : new HashSet<>(data.getKeys(false))) data.set(k, null);

        for (UUID uuid : homes.keySet()) {
            Map<String, Home> playerHomes = homes.get(uuid);
            for (String name : playerHomes.keySet()) {
                Home h = playerHomes.get(name);
                Location loc = h.getLocation();
                String base = uuid.toString() + "." + name;
                data.set(base + ".world", loc.getWorld().getName());
                data.set(base + ".x", loc.getX());
                data.set(base + ".y", loc.getY());
                data.set(base + ".z", loc.getZ());
                data.set(base + ".yaw", loc.getYaw());
                data.set(base + ".pitch", loc.getPitch());
                data.set(base + ".created", h.getCreatedMillis());
                data.set(base + ".safe", h.isSafe());
            }
        }

        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a mutable map name->Home (create if absent)
     */
    public Map<String, Home> getHomes(Player player) {
        return homes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
    }

    /**
     * Set a home. Returns:
     * 0 = success
     * 1 = duplicate name
     * 2 = max reached
     */
    public int setHome(Player player, String name, Location loc, int maxHomes, boolean safe) {
        Map<String, Home> playerHomes = getHomes(player);
        if (playerHomes.containsKey(name)) {
            return 1; // duplicate
        }
        if (playerHomes.size() >= maxHomes) {
            return 2; // max reached
        }
        playerHomes.put(name, new Home(loc.clone(), System.currentTimeMillis(), safe));
        return 0;
    }

    public boolean deleteHome(Player player, String name) {
        Map<String, Home> playerHomes = getHomes(player);
        if (!playerHomes.containsKey(name)) return false;
        playerHomes.remove(name);
        return true;
    }

    public Home getHome(Player player, String name) {
        return getHomes(player).get(name);
    }

    public Map<UUID, Map<String, Home>> getAllHomes() {
        Map<UUID, Map<String, Home>> all = new HashMap<>();
        for (UUID id : homes.keySet()) all.put(id, new HashMap<>(homes.get(id)));
        return all;
    }

    public Map<String, Home> getHomes(UUID uuid) {
        return homes.getOrDefault(uuid, Collections.emptyMap());
    }

    /**
     * Return true if the location is safe according to config "forbidden-blocks".
     * This checks the block the player is standing in (the feet block) ONLY.
     *
     * Config example:
     * forbidden-blocks:
     *   - "AIR"
     *   - "LAVA"
     *   - "POINTED_DRIPSTONE"
     *
     * Material names must match Bukkit's Material enum (uppercase).
     */
    public boolean isLocationSafe(Location loc) {
        if (loc == null) return false;

        List<String> forbidden = plugin.getConfig().getStringList("forbidden-blocks");
        if (forbidden == null || forbidden.isEmpty()) {
            // nothing forbidden -> everything is safe
            return true;
        }

        Set<String> forbidSet = new HashSet<>();
        for (String s : forbidden) {
            if (s == null) continue;
            forbidSet.add(s.trim().toUpperCase());
        }

        // the block the player's feet are in
        org.bukkit.block.Block feetBlock = loc.getBlock();
        String feetType = feetBlock.getType().name();

        // if the feet block is listed as forbidden -> unsafe
        if (forbidSet.contains(feetType)) {
            return false;
        }

        // otherwise safe
        return true;
    }
}
