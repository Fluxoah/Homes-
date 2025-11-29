package me.Fluxoah.homePlugin;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;

import java.lang.reflect.Method;

public class TeleportManager {
    private final HomesPlusPlus plugin;
    private static final double MOVE_THRESHOLD_SQUARED = 0.04; // 0.2 block tolerance

    public TeleportManager(HomesPlusPlus plugin) { this.plugin = plugin; }

    public void startDelayedTeleport(final Player player, final Location target, final String homeName) {
        int delaySeconds = plugin.getConfig().getInt("teleport-delay", 5);
        final long totalMillis = delaySeconds * 1000L;
        final long startTime = System.currentTimeMillis();
        final Location startLoc = player.getLocation().clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) { cancel(); return; }

                // movement check
                Location now = player.getLocation();
                if (now.distanceSquared(startLoc) > MOVE_THRESHOLD_SQUARED) {
                    // send red actionbar cancel and chat message
                    String action = plugin.msg("teleport-cancelled-actionbar");
                    boolean sent = trySpigotActionBar(player, action);
                    if (!sent) tryPlayerSendActionBar(player, action);

                    player.sendMessage(plugin.msg("teleport-cancelled"));
                    cancel();
                    return;
                }

                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = totalMillis - elapsed;
                if (remaining <= 0) {
                    player.teleport(target);
                    player.sendMessage(plugin.msg("home-teleported", "{home}", homeName));
                    cancel();
                    return;
                }

                // Format one decimal place
                double secondsDouble = Math.max(0.0, remaining / 1000.0);
                String timeStr = String.format("%.1f", secondsDouble);

                String actionbar = plugin.msg("home-teleport-actionbar", "{time}", timeStr);
                boolean sent = trySpigotActionBar(player, actionbar);
                if (!sent) tryPlayerSendActionBar(player, actionbar);
            }
        }.runTaskTimer(plugin, 0L, 1L); // run every tick
    }

    private boolean trySpigotActionBar(Player player, String message) {
        try {
            Method spigotMethod = player.getClass().getMethod("spigot");
            Object spigot = spigotMethod.invoke(player);
            Class<?> chatMsgTypeClass = Class.forName("net.md_5.bungee.api.ChatMessageType");
            Object actionBar = java.lang.Enum.valueOf((Class<Enum>)chatMsgTypeClass, "ACTION_BAR");
            Method sendMsg = spigot.getClass().getMethod("sendMessage", chatMsgTypeClass, net.md_5.bungee.api.chat.BaseComponent[].class);
            net.md_5.bungee.api.chat.BaseComponent[] comps = TextComponent.fromLegacyText(message);
            sendMsg.invoke(spigot, actionBar, (Object) comps);
            return true;
        } catch (Throwable ignored) { return false; }
    }

    private boolean tryPlayerSendActionBar(Player player, String message) {
        try {
            Method m = player.getClass().getMethod("sendActionBar", String.class);
            m.invoke(player, message);
            return true;
        } catch (Throwable ignored) { return false; }
    }
}
