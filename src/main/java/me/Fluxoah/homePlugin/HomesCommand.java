package me.Fluxoah.homePlugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class HomesCommand implements CommandExecutor {
    private final HomesPlusPlus plugin;

    public HomesCommand(HomesPlusPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("only-players"));
            return true;
        }

        Player p = (Player) sender;

        Map<String, Home> homes = plugin.getHomeManager().getHomes(p);
        if (homes == null || homes.isEmpty()) {
            p.sendMessage(plugin.msg("no-homes"));
            return true;
        }

        p.sendMessage(plugin.msg("your-homes"));

        for (String name : homes.keySet()) {
            TextComponent line = new TextComponent(" - " + name + " ");
            line.setColor(net.md_5.bungee.api.ChatColor.AQUA);

            TextComponent click = new TextComponent("[Go]");
            click.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + name));

            // Hover: simple legacy text (no message key required)
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Click to teleport to " + name).create()));

            line.addExtra(click);
            p.spigot().sendMessage(line);
        }

        return true;
    }
}
