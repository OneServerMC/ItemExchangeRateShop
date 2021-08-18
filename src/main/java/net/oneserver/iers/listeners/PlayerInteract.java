package net.oneserver.iers.listeners;

import net.oneserver.iers.IERS;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener
{

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        final Player p = e.getPlayer();

        if (!e.getClickedBlock().getType().name().contains("SIGN")) return;

        final Block block = e.getClickedBlock();
        final Sign sign = (Sign) block.getState();

        Type type = null;
        Shop shop = null;

        if (sign.getLine(1).contains(Type.BUY.toString()))
        {
            type = Type.BUY;
            shop = ShopManager.get().loadShopByBuySign(block).orElse(null);
        }
        else if (sign.getLine(1).contains(Type.SELL.toString()))
        {
            type = Type.SELL;
            shop = ShopManager.get().loadShopBySellSign(block).orElse(null);
        }

        if (type == null) return;

        ItemStack item = null;

        for (Entity entity : block.getWorld().getNearbyEntities(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), 2, 1, 2)) if (entity.getType() == EntityType.ITEM_FRAME) item = ((ItemFrame) entity).getItem();

        if (item == null)
        {
            p.sendMessage(ChatColor.RED + "アイテムが取得できませんでした。");
            return;
        }

        if (shop == null) p.sendMessage("null");

        switch (type)
        {
            case BUY:

                if (!IERS.getPlugin().getOconomyAPI().getMoney().has(p.getUniqueId(), Math.round(shop.getPrice() * 1.05)))
                {
                    p.sendMessage(ChatColor.RED + "必要な金額を所持していません。");
                    return;
                }

                if (shop.getStock() <= 0)
                {
                    p.sendMessage(ChatColor.RED + "在庫切れです。");
                    return;
                }

                p.getInventory().addItem(item);
                IERS.getPlugin().getOconomyAPI().getMoney().withdraw(p.getUniqueId(), Math.round(shop.getPrice() * 1.05));
                Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID, shop.getStock() - 1, shop.getShopId());
                updateSign(shop.getInfoSign(), 2, "在庫: " + (shop.getStock() - 1));
                ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                p.sendMessage(ChatColor.GOLD + String.valueOf(Math.round(shop.getPrice() * 1.05)) + "円でアイテムを購入しました。");
                break;

            case SELL:

                if (!p.getInventory().contains(item))
                {
                    p.sendMessage(ChatColor.RED + "売却可能なアイテムを所持していません。");
                    return;
                }

                p.getInventory().remove(item);
                IERS.getPlugin().getOconomyAPI().getMoney().deposit(p.getUniqueId(), Math.round(shop.getPrice() / 1.05));
                Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID, shop.getStock() + 1, shop.getShopId());
                updateSign(shop.getInfoSign(), 2, "在庫: " + (shop.getStock() + 1));
                ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                p.sendMessage(ChatColor.YELLOW + String.valueOf(Math.round(shop.getPrice() / 1.05)) + "円でアイテムを売却しました。");
                break;

        }
    }

    private enum Type
    {
        BUY("購入"),
        SELL("売却");

        private final String type;

        Type(String type)
        {
            this.type = type;
        }

        @Override
        public String toString()
        {
            return type;
        }
    }

    private void updateSign(Sign sign, int index, String content)
    {
        sign.setLine(index, content);
        sign.update();
    }
}