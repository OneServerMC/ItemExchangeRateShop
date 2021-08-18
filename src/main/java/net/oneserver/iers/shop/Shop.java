package net.oneserver.iers.shop;

import net.oneserver.iers.converter.ItemStringConverter;
import net.oneserver.iers.converter.LocationStringConverter;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.util.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Shop
{
    private final String shopId, name;
    private final ItemStack itemStack;
    private final int stock, price;
    private final Block info, buy, sell;

    private int id;

    Shop(String shopId, ItemStack itemStack, String name, int stock, int price, Block info, Block buy, Block sell, int id)
    {
        this.shopId = shopId;
        this.itemStack = itemStack;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.info = info;
        this.buy = buy;
        this.sell = sell;
        this.id = id;
    }

    public static Shop create(String shopId, ItemStack itemStack, Block info, Block buy, Block sell)
    {
        return new Shop(shopId, itemStack, shopId, 1, 0, info, buy, sell, -1).create();
    }

    public Shop create()
    {
        if (id != -1) return null;
        if (shopId == null) return null;

        try
        {
            Database.get().executeStatement(SQLQuery.INSERT_SHOP, shopId, ItemStringConverter.itemToString(itemStack), name, stock, price, LocationStringConverter.toString(info.getLocation()), LocationStringConverter.toString(buy.getLocation()), LocationStringConverter.toString(sell.getLocation()));

            try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_BY_SHOPID, shopId))
            {
                if (!rs.next())
                {
                    Utils.log("!! IDを更新できない! サーバーを再起動して解決してください");
                    Utils.log("!! Failed at: " + this);
                }
                else id = rs.getInt("id");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return this;
    }

    public Shop delete()
    {
        Database.get().executeResultStatement(SQLQuery.DELETE_SHOP_BY_SHOPID, shopId);
        return this;
    }

    public String getShopId()
    {
        return shopId;
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public String getName()
    {
        return name;
    }

    public int getStock()
    {
        return stock;
    }

    public int getPrice()
    {
        return price;
    }

    public Sign getInfoSign()
    {
        return (Sign) info.getState();
    }

    public Sign getBuySign()
    {
        return (Sign) buy.getState();
    }

    public Sign getSellSign()
    {
        return (Sign) sell.getState();
    }

    public int getId()
    {
        return id;
    }
}