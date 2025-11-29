package me.Fluxoah.homePlugin;

import org.bukkit.Location;

public class Home {
    private final Location location;
    private final long createdMillis;
    private final boolean safe;

    public Home(Location location, long createdMillis, boolean safe) {
        this.location = location;
        this.createdMillis = createdMillis;
        this.safe = safe;
    }

    public Location getLocation() { return location; }
    public long getCreatedMillis() { return createdMillis; }
    public boolean isSafe() { return safe; }
}
