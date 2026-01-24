package me.tetshow.gui;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public class ColorMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§cChọn màu pháo hoa");

        add(inv, 10, Material.RED_DYE, Color.RED);
        add(inv, 11, Material.YELLOW_DYE, Color.YELLOW);
        add(inv, 12, Material.BLUE_DYE, Color.BLUE);
        add(inv, 13, Material.GREEN_DYE, Color.GREEN);
        add(inv, 14, Material.PURPLE_DYE, Color.PURPLE);
        add(inv, 15, Material.WHITE_DYE, Color.WHITE);

        p.openInventory(inv);
    }

    private static void add(Inventory inv, int slot, Material m, Color c) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("§e" + c.name());
        i.setItemMeta(im);
        inv.setItem(slot, i);
    }

    public static void select(Player p, Color c) {
        ShowData.color = c;
        p.sendMessage("§aĐã chọn màu: " + c.name());
    }
}
