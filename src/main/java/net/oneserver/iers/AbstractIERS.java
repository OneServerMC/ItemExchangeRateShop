package net.oneserver.iers;

import net.oneserver.iers.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractIERS extends JavaPlugin
{
    protected void registerListeners(Listener... listeners)
    {
        for (Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
    }

    protected void registerCommands(Command... commands)
    {
        for (Command command : commands)
        {
            getCommand(command.getName()).setExecutor(command.getExecutor());
            if (command.getTabCompleter() != null) getCommand(command.getName()).setTabCompleter(command.getTabCompleter());
        }
    }
}