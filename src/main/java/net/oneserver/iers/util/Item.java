package net.oneserver.iers.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Item
{
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public Item(ItemStack itemStack)
    {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public Item(Material material)
    {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public Item(Material material, int id)
    {
        this.itemStack = new ItemStack(material, 1, (short) id);
        this.itemMeta = itemStack.getItemMeta();
    }

    public Item setDisplayName(String name)
    {
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    @SuppressWarnings("deprecation")
    public Item setMetaID(byte metaID)
    {
        itemStack.getData().setData(metaID);
        return this;
    }

    public Item setOwner(String name)
    {
        SkullMeta m = (SkullMeta) itemMeta;
        m.setOwner(name);
        itemStack.setItemMeta(m);
        return this;
    }

    public Item setAmount(int amount)
    {
        this.itemStack.setAmount(amount);
        return this;
    }

    public Item setDurability(short durability)
    {
        this.itemStack.setDurability(durability);
        return this;
    }

    public Item addEnchantment(Enchantment enchantment, int lvl)
    {
        this.itemMeta.addEnchant(enchantment, lvl, false);
        return this;
    }

    public Item addEnchantments(Map<Enchantment, Integer> enchantments)
    {
        for (Enchantment enchantment : enchantments.keySet()) this.itemMeta.addEnchant(enchantment, enchantments.get(enchantment), false);
        return this;
    }

    public Item clearEnchantments()
    {
        this.itemMeta.getEnchants().forEach((enchantment, integer) -> this.itemMeta.removeEnchant(enchantment));
        return this;
    }

    public Item removeEnchantment(Enchantment enchantment)
    {
        if (this.itemMeta.getEnchants().containsKey(enchantment)) this.itemMeta.removeEnchant(enchantment);
        return this;
    }

    public Item setLore(List<String> lines)
    {
        this.itemMeta.setLore(lines);
        return this;
    }

    public Item setLore(String... lines) {
        this.itemMeta.setLore(Arrays.asList(lines));
        return this;
    }

    public Item resetLore()
    {
        this.itemMeta.getLore().clear();
        return this;
    }

    public ItemStack build()
    {
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }
}

