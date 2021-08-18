package net.oneserver.iers.commands.iers.subs;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import net.oneserver.iers.command.ISubCommand;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;
import net.oneserver.iers.worldedit.RegionManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Create implements ISubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length < 2)
        {
            sender.sendMessage(ChatColor.RED + "ShopIDを指定してください。");
            return;
        }

        final Player p = (Player) sender;
        final Region region = RegionManager.getRegion(p);

        if (region == null)
        {
            sender.sendMessage(ChatColor.RED + "範囲を指定してください。");
            return;
        }

        ItemStack item = null;
        Block info = null, buy = null, sell = null;

        for (BlockVector3 point : region)
        {
            final World world =  Bukkit.getWorld(region.getWorld().getName());
            final Block block = world.getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());
            if (block.getType().name().contains("SIGN"))
            {
                final Sign sign = (Sign) block.getState();
                switch (sign.getLine(0))
                {
                    case "info":
                        for (Entity entity : world.getNearbyEntities(new Location(world, block.getX(), block.getY(), block.getZ()), 1, 1, 1)) if (entity.getType() == EntityType.ITEM_FRAME) item = ((ItemFrame) entity).getItem();
                        sign.setLine(0, ChatColor.GOLD + ChatColor.BOLD.toString() + args[1]);
                        sign.setLine(1, "定価: 0円");
                        sign.setLine(2, "在庫: " + item.getAmount());
                        sign.update();
                        info = block;
                        break;
                    case "buy":
                        sign.setLine(0, ChatColor.GOLD + ChatColor.BOLD.toString() + args[1]);
                        sign.setLine(1, ChatColor.GREEN + "購入");
                        sign.setLine(2, "1個: 0円");
                        sign.update();
                        buy = block;
                        break;
                    case "sell":
                        sign.setLine(0, ChatColor.GOLD + ChatColor.BOLD.toString() + args[1]);
                        sign.setLine(1, ChatColor.RED + "売却");
                        sign.setLine(2, "1個: 0円");
                        sign.update();
                        sell = block;
                        break;
                }
            }
        }

        if (item == null)
        {
            sender.sendMessage(ChatColor.RED + "ショップの設定に失敗しました。");
            return;
        }

        ShopManager.get().getShops().put(args[1], Shop.create(args[1], item, info, buy, sell));
        sender.sendMessage(ChatColor.GREEN + "ショップ (" + args[1] + ") を設定しました。");
    }
}