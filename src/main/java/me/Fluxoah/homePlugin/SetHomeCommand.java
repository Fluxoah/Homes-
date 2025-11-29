package me.Fluxoah.homePlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final HomesPlusPlus plugin;

    public SetHomeCommand(HomesPlusPlus plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage(plugin.msg("only-players")); return true; }
        Player p = (Player) sender;

        if (!p.hasPermission("homesplusplus.sethome")) {
            p.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(plugin.msg("usage-sethome"));
            return true;
        }

        String name = args[0].toLowerCase();

        int baseMax = plugin.getConfig().getInt("max-homes", 2);
        int maxHomes = baseMax;
        if (plugin.getConfig().getBoolean("luckperms-integration", true)) {
            maxHomes = plugin.getLuckPermsHook().getMaxHomes(p, baseMax);
        }

        // Safety check
        boolean safe = plugin.getHomeManager().isLocationSafe(p.getLocation());
        if (!safe) {
            p.sendMessage(plugin.msg("home-forbidden"));
            return true;
        }

        int result = plugin.getHomeManager().setHome(p, name, p.getLocation(), maxHomes, safe);
        if (result == 1) {
            p.sendMessage(plugin.msg("home-exists", "{home}", name));
            return true;
        } else if (result == 2) {
            p.sendMessage(plugin.msg("home-max", "{max}", String.valueOf(maxHomes)));
            return true;
        }

        plugin.getHomeManager().saveHomes(); // persist immediately
        p.sendMessage(plugin.msg("home-set", "{home}", name));
        return true;
    }
}
