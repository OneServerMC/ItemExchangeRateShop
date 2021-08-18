package net.oneserver.iers.commands.iers.subs;

import net.oneserver.iers.command.ISubCommand;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

public class SetName implements ISubCommand
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

        Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_NAME_BY_SHOPID, args[2], args[1]);
        sender.sendMessage(ChatColor.GREEN + "ショップ (" + args[1] + ") の名前を" + args[2] + "に変更しました。");

        final Shop shop = ShopManager.get().getShopByShopId(args[1]);
        final String name = ChatColor.translateAlternateColorCodes('&', args[2]);

        updateSign(shop.getInfoSign(), 0, name);
        updateSign(shop.getBuySign(), 0, name);
        updateSign(shop.getSellSign(), 0, name);

        ShopManager.get().getShops().put(args[1], shop);
    }

    private void updateSign(Sign sign, int index, String content)
    {
        sign.setLine(index, content);
        sign.update();
    }
}