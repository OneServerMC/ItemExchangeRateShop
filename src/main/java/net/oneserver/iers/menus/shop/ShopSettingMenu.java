package net.oneserver.iers.menus.shop;

import dev.m1n1don.smartinvsr.inv.ClickableItem;
import dev.m1n1don.smartinvsr.inv.SmartInventory;
import dev.m1n1don.smartinvsr.inv.content.InventoryContents;
import dev.m1n1don.smartinvsr.inv.content.InventoryProvider;
import net.oneserver.iers.IERS;
import net.oneserver.iers.shop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ShopSettingMenu implements InventoryProvider
{
    private final Shop shop;

    public ShopSettingMenu(Shop shop)
    {
        this.shop = shop;
    }

    public static SmartInventory INVENTORY(Shop shop)
    {
        return SmartInventory.builder()
                .id("setting")
                .provider(new ShopSettingMenu(shop))
                .size(5, 9)
                .title(ChatColor.DARK_AQUA + "ショップ設定 " + ChatColor.GRAY + "(" + shop.getShopId() + ")")
                .closeable(true)
                .manager(IERS.getPlugin().getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents)
    {
        for (int i = 0; i < 5; i += 4)
        {
            contents.fillRow(i, ClickableItem.empty(icon -> {
                icon.material = Material.WHITE_STAINED_GLASS_PANE;
                icon.displayName = " ";
            }));
        }

        contents.set(2, 1, ClickableItem.of(i -> {
            i.material = Material.ANVIL;
            i.displayName = ChatColor.GOLD + "表示名を変更";
            i.lore = Collections.singletonList(ChatColor.GRAY + "ショップ名前を変更できます。");
        }, e -> new InputMenu(shop, InputMenu.Type.NAME).open(player)));

        contents.set(2, 4, ClickableItem.of(i -> {
            i.material = Material.GOLD_INGOT;
            i.displayName = ChatColor.GOLD + "定価を変更";
            i.lore = Collections.singletonList(ChatColor.GRAY + "アイテムの定価を変更できます。");
        }, e -> new InputMenu(shop, InputMenu.Type.PRICE).open(player)));

        contents.set(2, 7, ClickableItem.of(i -> {
            i.material = Material.CHEST;
            i.displayName = ChatColor.GOLD + "在庫数を変更";
            i.lore = Collections.singletonList(ChatColor.GRAY + "アイテムの在庫数を変更できます。");
        }, e -> new InputMenu(shop, InputMenu.Type.STOCK).open(player)));
    }
}