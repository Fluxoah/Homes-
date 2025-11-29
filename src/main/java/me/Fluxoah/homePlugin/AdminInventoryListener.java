package me.Fluxoah.homePlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;

public class AdminInventoryListener implements Listener {
    private final HomesPlusPlus plugin;

    public AdminInventoryListener(HomesPlusPlus plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        HomesAdminCommand cmd = (HomesAdminCommand) plugin.getCommand("homesadmin").getExecutor();
        if (cmd == null) return;
        HomesAdminCommand.AdminInventoryData data = cmd.getDataForInventory(inv);
        if (data == null) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player clicker = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        String homeName = data.slotToHome.get(slot);
        if (homeName == null) return;
        java.util.Map<String, Home> homes = plugin.getHomeManager().getHomes(data.target);
        Home h = homes.get(homeName);
        if (h == null) {
            clicker.sendMessage(plugin.msg("home-not-found", "{home}", homeName));
            return;
        }
        clicker.closeInventory();
        plugin.getTeleportManager().startDelayedTeleport(clicker, h.getLocation(), homeName);
        cmd.removeInventory(inv);
    }
}
