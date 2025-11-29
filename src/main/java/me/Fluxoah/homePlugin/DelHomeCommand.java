package me.Fluxoah.homePlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final HomesPlusPlus plugin;

    public DelHomeCommand(HomesPlusPlus plugin) { this.plugin = plugin; }

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
        boolean ok = plugin.getHomeManager().deleteHome(p, name);
        if (!ok) {
            p.sendMessage(plugin.msg("home-not-found", "{home}", name));
            return true;
        }

        plugin.getHomeManager().saveHomes();
        p.sendMessage(plugin.msg("home-deleted", "{home}", name));
        return true;
    }
}
