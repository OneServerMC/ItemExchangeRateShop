package net.oneserver.iers.converter;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemStringConverter
{
    public static ItemStack stringToItem(String dataSt)
    {
        String[] datas = dataSt.split("@@");

        Material mt = Material.getMaterial(datas[0]);
        int amount = 1;
        int damage = 0;

        try
        {
            if (datas.length >= 2) amount = Integer.parseInt(datas[1]);
            if (datas.length >= 3) damage = Integer.parseInt(datas[2]);
        }
        catch (NumberFormatException ex) { ex.printStackTrace(); }

        Map<Enchantment, Integer> enchants = new HashMap<>();

        if (datas.length >= 4 && !datas[3].equalsIgnoreCase(""))
        {
            String[] datass = datas[3].split("&&");
            boolean isValue = false;
            Enchantment cash = null;
            for (String data : datass)
            {
                if (!isValue)
                {
                    cash = Enchantment.getByKey(NamespacedKey.minecraft(data));
                    isValue = true;
                }
                else
                {
                    int value = Integer.parseInt(data);
                    enchants.put(cash,value);
                    isValue = false;
                    cash = null;
                }
            }
        }

        String itName = "";

        if (datas.length >= 5 && !datas[4].equalsIgnoreCase("")) itName = datas[4].replaceAll("_@_","@").replaceAll("_&_","&");

        List<String> lore = new ArrayList<>();

        if (datas.length >= 6 && !datas[5].equalsIgnoreCase(""))
        {
            String[] datass = datas[5].split("&&");
            for (String data : datass) lore.add(data.replaceAll("_@_","@").replaceAll("_&_","&"));
        }

        Set<ItemFlag> flags = new HashSet<>();

        if (datas.length >= 7 && !datas[6].equalsIgnoreCase(""))
        {
            String[] datass = datas[6].split("&&");
            for (String data : datass) flags.add(ItemFlag.valueOf(data));
        }

        if (mt==null) return null;

        ItemStack item = new ItemStack(mt,amount);
        ItemMeta meta = item.getItemMeta();

        if (meta==null) return null;
        if (meta instanceof Damageable) ((Damageable) meta).setDamage(damage);

        for (Enchantment en : enchants.keySet())
        {
            int data = enchants.get(en);
            meta.addEnchant(en,data,false);
        }

        if (!itName.equalsIgnoreCase("")) meta.setDisplayName(itName);

        meta.setLore(lore);

        for (ItemFlag flag : flags) meta.addItemFlags(flag);
        item.setItemMeta(meta);
        return item;
    }

    public static String itemToString(ItemStack item)
    {
        String mtName = "";
        int amount = 0;
        int damage = 0;
        Map<Enchantment, Integer> enchants = new HashMap<>();
        String itName = "";
        List<String> lore = new ArrayList<>();
        Set<ItemFlag> flags = null;


        ItemMeta meta = item.getItemMeta();

        Material mt = item.getType();
        mtName = mt.name();
        amount = item.getAmount();

        if (meta!=null)
        {
            if (meta instanceof Damageable) damage = ((Damageable) meta).getDamage();
            if (meta.hasEnchants()) enchants =  meta.getEnchants();
            if (meta.hasDisplayName()) itName = meta.getDisplayName();
            if (meta.hasLore()) lore = meta.getLore();
            flags = meta.getItemFlags();
        }


        StringBuilder resultSt = new StringBuilder(mtName + "@@" + amount + "@@" + damage + "@@");

        boolean first1 = true;
        for (Enchantment en : enchants.keySet())
        {
            if (!first1) resultSt.append("&&");
            else first1 = false;

            int data = enchants.get(en);
            resultSt.append(en.getKey().getKey()).append("&&").append(data);
        }

        resultSt.append("@@").append(itName.replaceAll("&","_&_").replaceAll("@","_@_")).append("@@");

        if (lore!=null)
        {
            boolean first2 = true;
            for (String lor : lore)
            {
                if (!first2) resultSt.append("&&");
                else first2 = false;
                resultSt.append(lor.replaceAll("&","_&_").replaceAll("@","_@_"));
            }
        }

        resultSt.append("@@");

        if (flags!=null)
        {
            boolean first3 = true;
            for (ItemFlag flag : flags)
            {
                if (!first3) resultSt.append("&&");
                else first3 = false;
                resultSt.append(flag.name());
            }
        }

        return resultSt.toString();
    }
}