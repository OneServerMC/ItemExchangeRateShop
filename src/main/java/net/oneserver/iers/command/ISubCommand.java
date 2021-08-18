package net.oneserver.iers.command;

import org.bukkit.command.CommandSender;

public interface ISubCommand
{
    void execute(CommandSender sender, String[] args);
}