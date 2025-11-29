package me.Fluxoah.homePlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class HomesPlusPlus extends JavaPlugin {
    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private LuckPermsHook luckPermsHook;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);

        File messagesFile = new File(getDataFolder(), "messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);

        this.homeManager = new HomeManager(this);
        this.teleportManager = new TeleportManager(this);
        this.luckPermsHook = new LuckPermsHook(this);

        // register commands
        if (getCommand("sethome") != null) getCommand("sethome").setExecutor(new SetHomeCommand(this));
        if (getCommand("delhome") != null) getCommand("delhome").setExecutor(new DelHomeCommand(this));
        if (getCommand("home") != null) getCommand("home").setExecutor(new HomeCommand(this));
        if (getCommand("homes") != null) getCommand("homes").setExecutor(new HomesCommand(this));
        if (getCommand("homesadmin") != null) getCommand("homesadmin").setExecutor(new HomesAdminCommand(this));

        // Tab completer: use our custom tab completer that respects permissions
        HomeTabCompleter tabCompleter = new HomeTabCompleter(this);
        if (getCommand("home") != null) getCommand("home").setTabCompleter(tabCompleter);
        if (getCommand("delhome") != null) getCommand("delhome").setTabCompleter(tabCompleter);
        if (getCommand("sethome") != null) getCommand("sethome").setTabCompleter(tabCompleter);
        if (getCommand("homes") != null) getCommand("homes").setTabCompleter(tabCompleter);
        if (getCommand("homesadmin") != null) getCommand("homesadmin").setTabCompleter(tabCompleter);

        getServer().getPluginManager().registerEvents(new AdminInventoryListener(this), this);

        homeManager.loadHomes();

        getLogger().info("Homes++ enabled");
    }

    @Override
    public void onDisable() {
        homeManager.saveHomes();
        getLogger().info("Homes++ disabled");
    }

    public HomeManager getHomeManager() { return homeManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public LuckPermsHook getLuckPermsHook() { return luckPermsHook; }

    /**
     * Get a translated message and apply replacements.
     * Usage: plugin.msg("home-set", "{home}", homeName)
     */
    public String msg(String key, Object... replacements) {
        String value = messages.getString(key, key);
        if (value == null) value = key;
        // replacements come as pairs: find, replace
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            String find = String.valueOf(replacements[i]);
            String rep = String.valueOf(replacements[i + 1]);
            value = value.replace(find, rep);
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', value);
    }

    /**
     * Return the configured prefix string (already colorized).
     */
    public String prefix() {
        return msg("prefix");
    }
}
