package net.oneserver.iers.shop;

import net.oneserver.iers.converter.ItemStringConverter;
import net.oneserver.iers.converter.LocationStringConverter;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.util.Utils;
import org.bukkit.block.Block;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopManager
{
    private static ShopManager instance;
    private Map<String, Shop> shops = new HashMap<>();

    public static synchronized ShopManager get()
    {
        return instance == null ? instance = new ShopManager() : instance;
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
            while (rs.next()) ptMap.put(rs.getString("shopId"), getShopFromResultSet(rs));
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
}