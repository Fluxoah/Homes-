package me.Fluxoah.homePlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomesAdminCommand implements CommandExecutor, TabCompleter {

    private final HomesPlusPlus plugin;
    private final Map<Inventory, AdminInventoryData> inventoryMap = new HashMap<>();
    private final DateTimeFormatter dtf =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    public HomesAdminCommand(HomesPlusPlus plugin) {
        this.plugin = plugin;
    }

    // ============================
    // COMMAND EXECUTION
    // ============================
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("homes.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.msg("homesadmin-usage", "{usage}", "/homesadmin <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.msg("player-not-found"));
            return true;
        }

        Map<String, Home> homes = plugin.getHomeManager().getHomes(target);

        Inventory inv = Bukkit.createInventory(null, 9, "Homes: " + target.getName());
        int slot = 0;

        Map<Integer, String> slotMap = new HashMap<>();

        for (String name : homes.keySet()) {
            Home h = homes.get(name);

            ItemStack item = new ItemStack(Material.COMPASS);
            ItemMeta meta = item.getItemMeta();

            // Updated item name
            meta.setDisplayName(plugin.msg("admin-item-name", "{home}", name));

            String created = dtf.format(Instant.ofEpochMilli(h.getCreatedMillis()));
            String safety = h.isSafe()
                    ? plugin.msg("admin-safe", "{status}", "Safe")
                    : plugin.msg("admin-dangerous", "{status}", "Dangerous");

            meta.setLore(List.of(
                    plugin.msg("admin-lore-created", "{created}", created),
                    safety
            ));

            item.setItemMeta(meta);
            inv.setItem(slot, item);

            slotMap.put(slot, name);
            slot++;
            if (slot >= 9) break;
        }

        this.inventoryMap.put(inv, new AdminInventoryData(target.getUniqueId(), slotMap));

        if (sender instanceof Player) {
            ((Player) sender).openInventory(inv);
        } else {
            sender.sendMessage(plugin.msg("admin-open", "{player}", target.getName()));
        }

        return true;
    }

    // ============================
    // TAB COMPLETION
    // ============================
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

        if (!sender.hasPermission("homes.admin")) return null;

        // /homesadmin <player>
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                names.add(p.getName());
            }
            return names;
        }

        return Collections.emptyList();
    }

    // ============================
    // ADMIN INVENTORY DATA
    // ============================
    public AdminInventoryData getDataForInventory(Inventory inv) {
        return inventoryMap.get(inv);
    }

    public void removeInventory(Inventory inv) {
        inventoryMap.remove(inv);
    }

    public static class AdminInventoryData {
        public final UUID target;
        public final Map<Integer, String> slotToHome;

        public AdminInventoryData(UUID target, Map<Integer, String> slotToHome) {
            this.target = target;
            this.slotToHome = slotToHome;
        }
    }
}
