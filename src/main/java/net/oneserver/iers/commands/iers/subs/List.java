package net.oneserver.iers.commands.iers.subs;

import net.oneserver.iers.command.ISubCommand;
import net.oneserver.iers.menus.shops.ShopsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class List implements ISubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        ShopsMenu.INVENTORY().open((Player) sender);
    }
}