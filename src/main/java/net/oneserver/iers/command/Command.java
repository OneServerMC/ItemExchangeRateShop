package net.oneserver.iers.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public class Command
{
    private final String name;
    private final CommandExecutor executor;
    private final TabCompleter tabCompleter;

    public Command(String name, CommandExecutor executor)
    {
        this.name = name;
        this.executor = executor;
        this.tabCompleter = null;
    }

    public Command(String name, CommandExecutor executor, TabCompleter tabCompleter)
    {
        this.name = name;
        this.executor = executor;
        this.tabCompleter = tabCompleter;
    }

    public String getName()
    {
        return name;
    }

    public CommandExecutor getExecutor()
    {
        return executor;
    }

    public TabCompleter getTabCompleter()
    {
        return tabCompleter;
    }
}