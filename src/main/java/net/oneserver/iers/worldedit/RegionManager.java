package net.oneserver.iers.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;

public class RegionManager
{
    public static Region getRegion(org.bukkit.entity.Player player)
    {
        final Player p = BukkitAdapter.adapt(player);
        final SessionManager manager = WorldEdit.getInstance().getSessionManager();
        final LocalSession session = manager.get(p);

        Region region;

        try
        {
            region = session.getSelection();
        }
        catch (IncompleteRegionException ex)
        {
            return null;
        }

        return region;
    }
}