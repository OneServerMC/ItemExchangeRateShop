package net.oneserver.iers.db;

public enum SQLQuery
{
    CREATE_TABLE_SHOP(
            "CREATE TABLE IF NOT EXISTS Shop (" +
                    "id INTEGER PRIMARY KEY," +
                    "shopId TEXT," +
                    "itemStack TEXT," +
                    "name VARCHAR(16)," +
                    "stock BIGINT," +
                    "price BIGINT," +
                    "info_sign TEXT," +
                    "buy_sign TEXT," +
                    "sell_sign TEXT)"
    ),
    INSERT_SHOP(
            "INSERT INTO Shop " +
                    "(shopId, itemStack, name, stock, price, info_sign, buy_sign, sell_sign) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    ),
    UPDATE_SHOP_FROM_NAME_BY_SHOPID(
            "UPDATE Shop SET name = ? WHERE shopId = ?"
    ),
    UPDATE_SHOP_FROM_PRICE_BY_SHOPID(
            "UPDATE Shop SET price = ? WHERE shopId = ?"
    ),
    UPDATE_SHOP_FROM_STOCK_BY_SHOPID(
            "UPDATE Shop SET stock = ? WHERE shopId = ?"
    ),
    SELECT_SHOP_ALL(
            "SELECT * FROM Shop"
    ),
    SELECT_SHOP_BY_NAME(
            "SELECT * FROM Shop WHERE name = ?"
    ),
    SELECT_SHOP_BY_BUY_SIGN(
            "SELECT * FROM Shop WHERE buy_sign = ?"
    ),
    SELECT_SHOP_BY_SELL_SIGN(
            "SELECT * FROM Shop WHERE sell_sign = ?"
    ),
    SELECT_SHOP_BY_SHOPID(
            "SELECT * FROM Shop WHERE shopId = ?"
    ),
    DELETE_SHOP_BY_SHOPID(
            "DELETE FROM Shop WHERE shopId = ?"
    );

    private final String sqlite;

    SQLQuery(String sqlite)
    {
        this.sqlite = sqlite;
    }

    @Override
    public String toString()
    {
        return sqlite;
    }
}