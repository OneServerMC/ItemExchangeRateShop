package net.oneserver.iers.menus.shops;

import dev.m1n1don.smartinvsr.inv.ClickableItem;
import dev.m1n1don.smartinvsr.inv.SmartInventory;
import dev.m1n1don.smartinvsr.inv.content.InventoryContents;
import dev.m1n1don.smartinvsr.inv.content.InventoryProvider;
import dev.m1n1don.smartinvsr.inv.content.Pagination;
import dev.m1n1don.smartinvsr.inv.content.SlotIterator;
import net.oneserver.iers.IERS;
import net.oneserver.iers.menus.shop.ShopSettingMenu;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;

public class ShopsMenu implements InventoryProvider
{
    public static SmartInventory INVENTORY()
    {
        return SmartInventory.builder()
                .id("shops")
                .provider(new ShopsMenu())
                .size(6, 9)
                .title(ChatColor.GOLD + "ショップ一覧")
                .closeable(true)
                .manager(IERS.getPlugin().getInvManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents)
    {
        final Pagination pagination = contents.pagination();
        final List<Shop> shops = new ArrayList<>(ShopManager.get().getShops().values());
        ClickableItem[] items = new ClickableItem[shops.size()];

        for (int i = 0; i < items.length; i++)
        {
            int finalI = i;
            items[i] = ClickableItem.of(createShopIcon(player, shops.get(i)), e -> {
                if (player.isOp()) ShopSettingMenu.INVENTORY(shops.get(finalI)).open(player);
            });
        }

        contents.fillRow(4, ClickableItem.empty(i -> {
            i.material = Material.WHITE_STAINED_GLASS_PANE;
            i.displayName = " ";
        }));

        pagination.setItems(items);
        pagination.setItemsPerPage(36);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0).allowOverride(false));

        contents.set(4, 4, ClickableItem.of(i -> {
            i.material = Material.BARRIER;
            i.displayName = ChatColor.YELLOW + "情報を更新";
        }, e -> {
            // 再取得
        }));

        if (!pagination.isFirst())
        {
            contents.set(5, 1, ClickableItem.of(i -> {
                i.material = Material.ARROW;
                i.displayName = ChatColor.GREEN + "前のページ";
                i.lore = Collections.singletonList(ChatColor.GRAY  + String.valueOf(pagination.previous().getPage() + 1) + " ページ目へ行く");
            }, e -> INVENTORY().open(player, pagination.previous().getPage())));
        }

        if (!pagination.isLast())
        {
            contents.set(5, 7, ClickableItem.of(i -> {
                i.material = Material.ARROW;
                i.displayName = ChatColor.GREEN + "次のページ";
                i.lore = Collections.singletonList(ChatColor.GRAY  + String.valueOf(pagination.previous().getPage() + 1) + " ページ目へ行く");
            }, e -> INVENTORY().open(player, pagination.next().getPage())));
        }
    }

    private Consumer<ClickableItem> createShopIcon(Player player, Shop shop)
    {
        final Block block = shop.getInfoSign().getBlock();
        ItemStack item = null;

        for (Entity entity : block.getWorld().getNearbyEntities(new Location(block.getWorld(), block.getX(), block.getY(), block.getZ()), 2, 2, 2)) if (entity.getType() == EntityType.ITEM_FRAME) item = ((ItemFrame) entity).getItem();

        if (item == null) return null;

        final ItemMeta meta = item.getItemMeta();
        final ItemStack finalItem = item;
        final List<String> lore = new ArrayList<>();

        if (Objects.nonNull(item.getItemMeta().getLore()))
        {
            lore.addAll(item.getItemMeta().getLore());
            lore.add(" ");
        }

        if (player.isOp()) lore.addAll(Arrays.asList(ChatColor.YELLOW + "左クリックで設定画面へ行く", " "));

        lore.addAll(
                Arrays.asList(
                        ChatColor.AQUA + "在庫" + ChatColor.DARK_GRAY + " >> " + ChatColor.YELLOW + shop.getStock() + "個",
                        ChatColor.AQUA + "定価" + ChatColor.DARK_GRAY + " >> " + ChatColor.YELLOW + shop.getPrice() + "円",
                        ChatColor.AQUA + "購入" + ChatColor.DARK_GRAY + " >> " + ChatColor.YELLOW + Math.round(shop.getPrice() * 1.05) + "円",
                        ChatColor.AQUA + "売却" + ChatColor.DARK_GRAY + " >> " + ChatColor.YELLOW + Math.round(shop.getPrice() / 1.05) + "円"
                )
        );

        if (player.isOp()) lore.addAll(Arrays.asList(" ", ChatColor.RED + "ID" + ChatColor.DARK_GRAY + " >> " + ChatColor.WHITE + shop.getShopId()));

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', shop.getName()));
        meta.setLore(lore);

        finalItem.setItemMeta(meta);

        return i -> i.basedItemStack = finalItem;
    }
}