package net.oneserver.iers.menus.shop;

import net.oneserver.iers.IERS;
import net.oneserver.iers.db.Database;
import net.oneserver.iers.db.SQLQuery;
import net.oneserver.iers.shop.Shop;
import net.oneserver.iers.shop.ShopManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class InputMenu
{
    private final Shop shop;
    private final Type type;

    public InputMenu(Shop shop, Type type)
    {
        this.shop = shop;
        this.type = type;
    }

    public void open(Player player)
    {
        new AnvilGUI.Builder()
                .title(type.toString() + "を入力してください。")
                .text("ここに入力")
                .onComplete((p, text) -> {
                    switch (type)
                    {
                        case NAME:
                            text = ChatColor.translateAlternateColorCodes('&', text);
                            ShopManager.get().setName(shop, text);
                            p.sendMessage(ChatColor.GREEN + "ショップ (" + shop.getShopId() + ") の名前を" + ChatColor.WHITE + text + ChatColor.GREEN + "に変更しました。");
                            break;

                        case PRICE:
                            if (!StringUtils.isNumeric(text))
                            {
                                p.sendMessage(ChatColor.RED + "文字列は適応できません。");
                                return AnvilGUI.Response.close();
                            }

                            if (Integer.parseInt(text) < 0)
                            {
                                p.sendMessage(ChatColor.RED + "0以下の値は使用できません。");
                                return AnvilGUI.Response.close();
                            }

                            Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_PRICE_BY_SHOPID, text, shop.getShopId());
                            p.sendMessage(ChatColor.GREEN + "ショップ (" + shop.getShopId() + ") の定価を" + text + "円に変更しました。");

                            updateSign(shop.getInfoSign(), 1, "定価: " + text + "円");
                            updateSign(shop.getBuySign(), 2, "1個: " + Math.round(Integer.parseInt(text) * 1.05) + "円");
                            updateSign(shop.getSellSign(), 2, "1個: " + Math.round(Integer.parseInt(text) / 1.05) + "円");
                            break;

                        case STOCK:
                            if (!StringUtils.isNumeric(text))
                            {
                                p.sendMessage(ChatColor.RED + "文字列は適応できません。");
                                return AnvilGUI.Response.close();
                            }

                            if (Integer.parseInt(text) < 0)
                            {
                                p.sendMessage(ChatColor.RED + "0以下の値は使用できません。");
                                return AnvilGUI.Response.close();
                            }

                            Database.get().executeStatement(SQLQuery.UPDATE_SHOP_FROM_STOCK_BY_SHOPID, text, shop.getShopId());
                            p.sendMessage(ChatColor.GREEN + "ショップ (" + shop.getShopId() + ") の在庫を" + text + "個に変更しました。");

                            updateSign(shop.getInfoSign(), 2, "在庫: " + text);
                            break;

                    }
                    ShopManager.get().getShops().put(shop.getShopId(), ShopManager.get().getShopByShopId(shop.getShopId()));
                    return AnvilGUI.Response.close();
                })
                .plugin(IERS.getPlugin())
                .open(player);
    }

    private void updateSign(Sign sign, int index, String content)
    {
        sign.setLine(index, content);
        sign.update();
    }

    public enum Type
    {
        NAME("名前"),
        PRICE("定価"),
        STOCK("在庫");

        private final String text;

        Type(String text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }
}