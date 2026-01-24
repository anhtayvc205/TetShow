package me.tetshow.gui;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class ColorMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§cChọn màu");

        add(inv, 11, Material.RED_DYE, Color.RED);
        add(inv, 13, Material.YELLOW_DYE, Color.YELLOW);
        add(inv, 15, Material.BLUE_DYE, Color.BLUE);

        p.openInventory(inv);
    }

    private static void add(Inventory inv, int slot, Material m, Color c) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("§eChọn");
        i.setItemMeta(im);
        inv.setItem(slot, i);
    }

    public static void select(Color c) {
        ShowData.colors.clear();
        ShowData.colors.add(c);
    }
}
