package me.tetshow.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class PreviewMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6Preview pháo hoa");

        inv.setItem(11, item(Material.FIREWORK_ROCKET, "§aXem preview"));
        inv.setItem(15, item(Material.LIME_WOOL, "§aChạy show"));

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
