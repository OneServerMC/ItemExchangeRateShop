package net.oneserver.iers.shop;

import net.oneserver.iers.converter.ItemStringConverter;
import net.oneserver.iers.converter.LocationStringConverter;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShopManager
{
    private static ShopManager instance;
    private final Map<String, List<Integer>> exchanges = new HashMap<>();
    private Map<String, Shop> shops = new HashMap<>();

    public static synchronized ShopManager get()
    {
        return instance == null ? instance = new ShopManager() : instance;
    }

    public String setName(Shop shop, String name)
    {
        Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_NAME_BY_SHOPID, name, shop.getShopId());

        updateSign(shop.getInfoSign(), 0, name);
        updateSign(shop.getBuySign(), 0, name);
        updateSign(shop.getSellSign(), 0, name);

        shops.put(shop.getShopId(), getShopByShopId(shop.getName()));
        return name;
    }

    public Optional<Shop> loadShop(String id)
    {
        Shop shop = getShopByShopId(id);

        if (shop == null) return Optional.empty();

        return Optional.of(shop);
    }

    public Optional<Shop> loadShopByBuySign(Block block)
    {
        Shop shop = getShopByBuySign(block);

        if (shop == null) return Optional.empty();

        return Optional.of(shop);
    }

    public Optional<Shop> loadShopBySellSign(Block block)
    {
        Shop shop = getShopBySellSign(block);

        if (shop == null) return Optional.empty();

        return Optional.of(shop);
    }

    public void loadShops()
    {
        final Map<String, Shop> ptMap = new HashMap<>();

        try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_ALL))
        {
            while (rs.next())
                if (Bukkit.getWorlds().contains(getShopFromResultSet(rs).getInfoSign().getWorld()))
                    ptMap.put(rs.getString("shopId"), getShopFromResultSet(rs));
        }
        catch (SQLException ex)
        {
            Utils.debugSqlException(ex);
        }

        shops = ptMap;
    }

    public Shop getShopByShopId(String id)
    {
        Shop shop = null;

        try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_BY_SHOPID, id))
        {
            while (rs.next()) shop = getShopFromResultSet(rs);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return shop;
    }

    public Shop getShopByName(String name)
    {
        Shop shop = null;

        try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_BY_NAME, name))
        {
            while (rs.next()) shop = getShopFromResultSet(rs);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return shop;
    }

    public Shop getShopByBuySign(Block block)
    {
        Shop shop = null;

        try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_BY_BUY_SIGN, LocationStringConverter.toString(block.getLocation())))
        {
            while (rs.next()) shop = getShopFromResultSet(rs);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return shop;
    }

    public Shop getShopBySellSign(Block block)
    {
        Shop shop = null;

        try (ResultSet rs = Database.get().executeResultStatement(SQLQuery.SELECT_SHOP_BY_SELL_SIGN, LocationStringConverter.toString(block.getLocation())))
        {
            while (rs.next()) shop = getShopFromResultSet(rs);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }

        return shop;
    }

    public Map<String, Shop> getShops()
    {
        return shops;
    }

    public Map<String, List<Integer>> getExchanges()
    {
        return exchanges;
    }

    private Shop getShopFromResultSet(ResultSet rs) throws SQLException
    {
        return new Shop(
                rs.getString("shopId"),
                ItemStringConverter.stringToItem(rs.getString("itemStack")),
                rs.getString("name"),
                rs.getInt("stock"),
                rs.getInt("price"),
                LocationStringConverter.fromString(rs.getString("info_sign")).getBlock(),
                LocationStringConverter.fromString(rs.getString("buy_sign")).getBlock(),
                LocationStringConverter.fromString(rs.getString("sell_sign")).getBlock(),
                rs.getInt("id")
        );
    }

    private void updateSign(Sign sign, int index, String content)
    {
        sign.setLine(index, content);
        sign.update();
    }
}