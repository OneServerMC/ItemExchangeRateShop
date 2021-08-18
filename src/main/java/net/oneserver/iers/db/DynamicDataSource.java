package net.oneserver.iers.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.oneserver.iers.IERS;

import java.io.File;
import java.io.IOException;

public class DynamicDataSource
{
    private HikariConfig config = new HikariConfig();

    public DynamicDataSource() throws ClassNotFoundException
    {
        String path = IERS.getPlugin().getDataFolder().getPath() + "/database/";

        File dataFolder = new File(path);
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File databaseFile = new File(dataFolder, "sqlite.db");
        if (!databaseFile.exists())
        {
            try { databaseFile.createNewFile(); }
            catch (IOException e) { }
        }
        String driverClassName = "org.sqlite.JDBC";
        Class.forName(driverClassName);
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl("jdbc:sqlite:" + path + "sqlite.db");
    }

    public HikariDataSource generateDataSource()
    {
        return new HikariDataSource(config);
    }
}