package me.tetshow.gui;

import me.tetshow.engine.FireworkEngine;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class PreviewMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§aPreview");

        ItemStack i = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("§eBắn thử");
        i.setItemMeta(im);

        inv.setItem(13, i);
        p.openInventory(inv);

        FireworkEngine.shoot(p.getLocation());
    }
}
