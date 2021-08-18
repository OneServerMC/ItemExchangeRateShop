package net.oneserver.iers.util;

import net.oneserver.iers.IERS;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;

import java.io.*;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
    public static void debug(Object msg)
    {
        Utils.log("§cDebug: §7" + msg.toString());
        debugToFile(msg);
    }

    public static void debugException(Exception exc)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace(pw);
        debug(sw.toString());
    }

    /**
     * Debug.
     *
     * @param ex the ex
     */
    public static void debugSqlException(SQLException ex)
    {
        debug("§7An error has occurred with the database, the error code is: '" + ex.getErrorCode() + "'");
        debug("§7The state of the sql is: " + ex.getSQLState());
        debug("§7Error message: " + ex.getMessage());
        debugException(ex);
    }

    private static void debugToFile(Object msg)
    {
        File debugFile = new File(IERS.getPlugin().getDataFolder(), "logs/latest.log");
        if (!debugFile.exists())
        {
            System.out.print("Seems that a problem has occurred while creating the latest.log file in the startup.");
            try { debugFile.createNewFile(); }
            catch (IOException ex)
            {
                System.out.print("An error has occurred creating the 'latest.log' file again, check your server.");
                System.out.print("Error message" + ex.getMessage());
            }
        }
        try
        {
            FileUtils.writeStringToFile(debugFile, "[" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + "] " + ChatColor.stripColor(msg.toString()) + "\n", Charsets.UTF_8, true);
        }
        catch (IOException ex)
        {
            System.out.print("An error has occurred writing to 'latest.log' file.");
            System.out.print(ex.getMessage());
        }
    }

    public static void log(String message, int severity)
    {
        switch (severity)
        {
            case 1:
                IERS.getPlugin().getLogger().warning(message);
                break;
            case 2:
                IERS.getPlugin().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                IERS.getPlugin().getLogger().info(message);
                break;
        }
    }

    public static void logToFile(String message)
    {
        try
        {
            File saveTo = new File(IERS.getPlugin().getDataFolder(), "debug.log");
            if (!saveTo.exists()) saveTo.createNewFile();

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(getDateTime() + " " + message);
            pw.flush();
            pw.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public static void log(String message)
    {
        log(message, 0);
    }

    public static String getDateTime()
    {
        Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
        return formatter.format(new Date());
    }
}