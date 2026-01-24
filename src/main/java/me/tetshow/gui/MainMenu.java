package me.tetshow.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class MainMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§cTetShow PRO");

        inv.setItem(11, item(Material.FIREWORK_ROCKET, "§aSTART"));
        inv.setItem(13, item(Material.BLAZE_POWDER, "§eCOLOR"));
        inv.setItem(15, item(Material.CREEPER_HEAD, "§bPATTERN"));
        inv.setItem(22, item(Material.ENDER_EYE, "§dPREVIEW"));

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
