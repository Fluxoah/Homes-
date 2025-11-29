package me.Fluxoah.homePlugin;

import org.bukkit.entity.Player;

public class LuckPermsHook {

    private final HomesPlusPlus plugin;
    private final int scanLimit;

    public LuckPermsHook(HomesPlusPlus plugin) {
        this.plugin = plugin;
        this.scanLimit = Math.max(10, plugin.getConfig().getInt("permission-scan-limit", 50));
        plugin.getLogger().info("Permissions bridge enabled (no LuckPerms API required). scanLimit=" + scanLimit);
    }
    public int getMaxHomes(Player player, int baseMax) {
        if (player == null) return baseMax;

        int best = baseMax;

        // Highest permission node wins.
        for (int i = 1; i <= scanLimit; i++) {
            String node = "homesplusplus.homes." + i;
            try {
                if (player.hasPermission(node)) {
                    if (i > best) best = i;
                }
            } catch (Throwable ignored) {
                // defensive: ignore any provider quirks
            }
        }

        Integer metaMax = tryReadLuckPermsMetaMaxHomes(player);
        if (metaMax != null && metaMax > best) best = metaMax;

        return best;
    }

    private Integer tryReadLuckPermsMetaMaxHomes(Player player) {
        try {
            // detect LuckPerms provider class
            Class<?> lpProvider = Class.forName("net.luckperms.api.LuckPermsProvider");
            Class<?> luckPermsClass = Class.forName("net.luckperms.api.LuckPerms");
            Class<?> userManagerClass = Class.forName("net.luckperms.api.model.user.UserManager");
            Class<?> userClass = Class.forName("net.luckperms.api.model.user.User");
            Class<?> queryOptionsClass = Class.forName("net.luckperms.api.query.QueryOptions");
            Class<?> cachedDataClass = Class.forName("net.luckperms.api.cacheddata.CachedData");

            // LuckPermsProvider.get()
            Object provider = lpProvider.getMethod("get").invoke(null);
            Object luckPerms = lpProvider.getMethod("get").invoke(null); // same

            // get user manager & user
            Object userManager = luckPermsClass.getMethod("getUserManager").invoke(luckPerms);
            Object user = userManager.getClass().getMethod("getUser", java.util.UUID.class).invoke(userManager, player.getUniqueId());
            if (user == null) return null;

            // get query options (context)
            Object contextManager = luckPermsClass.getMethod("getContextManager").invoke(luckPerms);
            Object queryOptions = contextManager.getClass().getMethod("getStaticQueryOptions").invoke(contextManager);

            // get meta value
            Object cachedData = user.getClass().getMethod("getCachedData").invoke(user);
            Object metaData = cachedData.getClass().getMethod("getMetaData", queryOptionsClass).invoke(cachedData, queryOptions);
            Object metaVal = metaData.getClass().getMethod("getMetaValue", String.class).invoke(metaData, "maxhomes");
            if (metaVal == null) return null;
            try {
                return Integer.parseInt(String.valueOf(metaVal));
            } catch (NumberFormatException ignored) {
                return null;
            }
        } catch (ClassNotFoundException cnf) {
            // LuckPerms not present — normal situation
            return null;
        } catch (Throwable t) {
            // any failure -> ignore (don't break functionality)
            plugin.getLogger().finest("LuckPerms meta read failed: " + t.getClass().getSimpleName() + " " + t.getMessage());
            return null;
        }
    }
}
