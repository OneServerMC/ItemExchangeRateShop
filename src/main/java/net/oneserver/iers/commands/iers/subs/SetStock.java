package net.oneserver.iers.commands.iers.subs;

import net.oneserver.iers.command.ISubCommand;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

public class SetStock implements ISubCommand
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

        if (!StringUtils.isNumeric(args[2]))
        {
            sender.sendMessage(ChatColor.RED + "文字列は適応できません。");
            return;
        }

        if (Integer.parseInt(args[2]) < 0)
        {
            sender.sendMessage(ChatColor.RED + "0以下の値は使用できません。");
            return;
        }

        Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID, args[2], args[1]);
        sender.sendMessage(ChatColor.GREEN + "ショップ (" + args[1] + ") の在庫を" + args[2] + "個に変更しました。");

        final Shop shop = ShopManager.get().getShopByShopId(args[1]);

        updateSign(shop.getInfoSign(), 2, "在庫: " + args[2]);

        ShopManager.get().getShops().put(args[1], shop);
    }

    private void updateSign(Sign sign, int index, String content)
    {
        sign.setLine(index, content);
        sign.update();
    }
}