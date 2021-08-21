package net.oneserver.iers;

import net.oneserver.iers.command.Command;
import net.oneserver.iers.commands.iers.IERSCommand;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.listeners.PlayerInteract;

import net.oneserver.iers.shop.ShopManager;
import net.oneserver.oconomy.Oconomy;
import net.oneserver.oconomy.api.OconomyAPI;

public class IERS extends AbstractIERS
{
    private static IERS plugin;

    private OconomyAPI oconomy;

    @Override
    public void onEnable()
    {
        plugin = this;
        oconomy = Oconomy.getOconomyAPI();

        Database.get().setup();

        ShopManager.get().loadShops();

        registerListeners(
                new PlayerInteract()
        );

        registerCommands(
                new Command("iers", new IERSCommand())
        );
    }

    @Override
    public void onDisable()
    {
        Database.get().shutdown();
    }

    public static net.oneserver.iers.IERS getPlugin()
    {
        return plugin;
    }

    public OconomyAPI getOconomyAPI()
    {
        return oconomy;
    }

}