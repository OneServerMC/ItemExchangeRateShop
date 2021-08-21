package net.oneserver.iers.commands.iers.subs;

import net.oneserver.iers.command.ISubCommand;
import net.oneserver.iers.shop.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Delete implements ISubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 3)
        {
            sender.sendMessage(ChatColor.RED + "引数が足りません。");
            return;
        }

        if (!ShopManager.get().getShops().containsKey(args[1]))
        {
            sender.sendMessage(ChatColor.RED + args[1] + " は存在しません。");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "ショップ (" + args[1] + ") を削除しました。");

        ShopManager.get().getShopByShopId(args[1]).delete();

        ShopManager.get().getShops().remove(args[1]);
    }
}