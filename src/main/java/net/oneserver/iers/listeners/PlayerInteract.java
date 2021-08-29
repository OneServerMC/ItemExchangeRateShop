package net.oneserver.iers.listeners;

import net.oneserver.iers.IERS;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class PlayerInteract implements Listener
{

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        final Player p = e.getPlayer();

        if (e.getClickedBlock() == null) return;
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

                if (p.isSneaking() && shop.getStock() < 64)
                {
                    p.sendMessage(ChatColor.RED + "在庫が足りません。");
                    return;
                }

                if (shop.getStock() <= 0)
                {
                    p.sendMessage(ChatColor.RED + "在庫切れです。");
                    return;
                }

                if (p.isSneaking()) item.setAmount(64);

                p.getInventory().addItem(item);
                IERS.getPlugin().getOconomyAPI().getMoney().withdraw(p.getUniqueId(), Math.round((shop.getPrice() * 1.05) * (p.isSneaking() ? 64 : 1)));
                Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID, (shop.getStock() - (p.isSneaking() ? 64 : 1)), shop.getShopId());
                updateSign(shop.getInfoSign(), 2, "在庫: " + (shop.getStock() - (p.isSneaking() ? 64 : 1)));
                ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                p.sendMessage(ChatColor.GOLD + String.valueOf(Math.round((shop.getPrice() * 1.05) * (p.isSneaking() ? 64 : 1))) + "円でアイテムを購入しました。");

                if (!ShopManager.get().getExchanges().containsKey(shop.getShopId()))
                {
                    ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(0, 0));
                    return;
                }

                ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(ShopManager.get().getExchanges().get(shop.getShopId()).get(0), ShopManager.get().getExchanges().get(shop.getShopId()).get(1) + (p.isSneaking() ? 64 : 1)));

                if (ShopManager.get().getExchanges().get(shop.getShopId()).get(1) >= 192)
                {
                    final int difference = ShopManager.get().getExchanges().get(shop.getShopId()).get(1) - 192;
                    final long price = Math.round(shop.getPrice() + shop.getPrice() * 0.01);

                    updateSign(shop.getInfoSign(), 1, "定価: " + price + "円");
                    updateSign(shop.getBuySign(), 2, "1個: " + Math.round(price * 1.05) + "円");
                    updateSign(shop.getSellSign(), 2, "1個: " + Math.round(price / 1.05) + "円");
                    Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_PRICE_BY_SHOPID, price, shop.getShopId());

                    ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                    ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(ShopManager.get().getExchanges().get(shop.getShopId()).get(0), difference));
                }

                break;

            case SELL:

                ItemStack itemStack = null;

                for (ItemStack i : p.getInventory().getContents())
                {
                    if (i != null
                            && i.getType() == item.getType()
                            && i.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())
                            && (i.getItemMeta().getLore() == null ? "null" : i.getItemMeta().getLore()).equals(item.getItemMeta().getLore() == null ? "null" : item.getItemMeta().getLore())
                            && i.getItemMeta().getEnchants() == item.getItemMeta().getEnchants()
                            && ((Damageable) i.getItemMeta()).getDamage() == ((Damageable) item.getItemMeta()).getDamage()
                    )
                    {
                        if (p.isSneaking())
                        {
                            if (i.getAmount() >= 64)
                            {
                                itemStack = i;
                                break;
                            }
                        }
                        else
                        {
                            itemStack = i;
                            break;
                        }
                    }
                }

                if (itemStack == null)
                {
                    String error = ChatColor.RED + "売却可能なアイテムを所持していません。";
                    if (p.isSneaking()) error = ChatColor.RED + "売却するアイテムの数が足りません。";
                    p.sendMessage(error);
                    return;
                }

                if (itemStack.getType() == Material.PLAYER_HEAD && !((SkullMeta) itemStack.getItemMeta()).getOwner().equals(((SkullMeta) item.getItemMeta()).getOwner()))
                {
                    p.sendMessage(ChatColor.RED + "");
                    return;
                }

                itemStack.setAmount(itemStack.getAmount() - (p.isSneaking() ? 64 : 1));
                IERS.getPlugin().getOconomyAPI().getMoney().deposit(p.getUniqueId(), Math.round((p.isSneaking() ? 64 : 1) * shop.getPrice() / 1.05));
                Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID,  (shop.getStock() + (p.isSneaking() ? 64 : 1)), shop.getShopId());
                updateSign(shop.getInfoSign(), 2, "在庫: " + (shop.getStock() + (p.isSneaking() ? 64 : 1)));
                ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                p.sendMessage(ChatColor.YELLOW + String.valueOf(Math.round((p.isSneaking() ? 64 : 1) * shop.getPrice() / 1.05) + "円でアイテムを売却しました。"));

                if (!ShopManager.get().getExchanges().containsKey(shop.getShopId()))
                {
                    ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(0, 0));
                    return;
                }

                ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(ShopManager.get().getExchanges().get(shop.getShopId()).get(0) + (p.isSneaking() ? 64 : 1), ShopManager.get().getExchanges().get(shop.getShopId()).get(1)));

                if (ShopManager.get().getExchanges().get(shop.getShopId()).get(0) >= 192)
                {
                    final int difference = ShopManager.get().getExchanges().get(shop.getShopId()).get(0) - 192;

                    final long price = Math.round(shop.getPrice() - shop.getPrice() * 0.01);

                    updateSign(shop.getInfoSign(), 1, "定価: " + price + "円");
                    updateSign(shop.getBuySign(), 2, "1個: " + Math.round(price * 1.05) + "円");
                    updateSign(shop.getSellSign(), 2, "1個: " + Math.round(price / 1.05) + "円");
                    Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_PRICE_BY_SHOPID, price, shop.getShopId());

                    ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                    ShopManager.get().getExchanges().put(shop.getShopId(), Arrays.asList(difference, ShopManager.get().getExchanges().get(shop.getShopId()).get(1)));
                }

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