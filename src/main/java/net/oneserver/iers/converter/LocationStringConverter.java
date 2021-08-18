package net.oneserver.iers.converter;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationStringConverter
{
    private static final String format = "%world%><%x%><%y%><%z%";

    public static String toString(Location loc)
    {
        return format
                .replaceAll("%world%", loc.getWorld().getName())
                .replaceAll("%x%", String.valueOf(loc.getX()))
                .replaceAll("%y%", String.valueOf(loc.getY()))
                .replaceAll("%z%", String.valueOf(loc.getZ()));

    }

    public static Location fromString(String loc)
    {
        final String[] parts = loc.split("><");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
