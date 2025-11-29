package me.Fluxoah.homePlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeTabCompleter implements TabCompleter {

    private final HomesPlusPlus plugin;

    public HomeTabCompleter(HomesPlusPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return List.of();

        Player p = (Player) sender;
        String cmd = command.getName().toLowerCase();

        // /home -> only suggest if player has homesplusplus.use
        if (cmd.equals("home")) {
            if (!p.hasPermission("homesplusplus.use")) return List.of();
            Map<String, Home> homes = plugin.getHomeManager().getHomes(p);
            List<String> names = new ArrayList<>(homes.keySet());
            if (args.length == 0 || args[0].isEmpty()) return names;
            String partial = args[0].toLowerCase();
            return names.stream()
                    .filter(n -> n.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }

        // /delhome -> only suggest if player has sethome permission
        if (cmd.equals("delhome")) {
            if (!p.hasPermission("homesplusplus.sethome")) return List.of();
            Map<String, Home> homes = plugin.getHomeManager().getHomes(p);
            List<String> names = new ArrayList<>(homes.keySet());
            if (args.length == 0 || args[0].isEmpty()) return names;
            String partial = args[0].toLowerCase();
            return names.stream()
                    .filter(n -> n.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }

        // /sethome and /homes -> don't show player name suggestions (return empty list)
        if (cmd.equals("sethome") || cmd.equals("homes")) {
            // Allow these only if player has permission; otherwise nothing (prevents showing commands)
            if (!p.hasPermission("homesplusplus.sethome") && cmd.equals("sethome")) return List.of();
            if (!p.hasPermission("homesplusplus.use") && cmd.equals("homes")) return List.of();
            return List.of();
        }

        // /homesadmin -> only allow suggestions if player has admin permission (no suggestions anyway)
        if (cmd.equals("homesadmin")) {
            if (!p.hasPermission("homesplusplus.admin")) return List.of();
            return List.of();
        }

        return List.of();
    }
}
