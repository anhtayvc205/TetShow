package me.tetshow.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class MainMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6TetShow Menu");

        inv.setItem(11, item(Material.RED_DYE, "§cChọn màu"));
        inv.setItem(13, item(Material.FIREWORK_STAR, "§bChọn pattern"));
        inv.setItem(15, item(Material.FIREWORK_ROCKET, "§aPreview"));

        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        return i;
    }
}
