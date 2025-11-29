package me.Fluxoah.homePlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final HomesPlusPlus plugin;

    public HomeCommand(HomesPlusPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("only-players"));
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("homesplusplus.use")) {
            p.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(plugin.msg("usage-home"));
            return true;
        }

        String name = args[0].toLowerCase();
        Home homeObj = plugin.getHomeManager().getHome(p, name);

        if (homeObj == null) {
            p.sendMessage(plugin.msg("home-not-found", "{home}", name));
            return true;
        }

        // Start delayed teleport using the stored Location inside Home
        plugin.getTeleportManager().startDelayedTeleport(p, homeObj.getLocation(), name);
        return true;
    }
}
