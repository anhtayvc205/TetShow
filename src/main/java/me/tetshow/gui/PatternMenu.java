package me.tetshow.gui;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class PatternMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§bPattern");

        add(inv, 11, FireworkEffect.Type.BALL);
        add(inv, 13, FireworkEffect.Type.STAR);
        add(inv, 15, FireworkEffect.Type.CREEPER);

        p.openInventory(inv);
    }

    private static void add(Inventory inv, int slot, FireworkEffect.Type t) {
        ItemStack i = new ItemStack(Material.FIREWORK_STAR);
        FireworkEffectMeta meta = (FireworkEffectMeta) i.getItemMeta();
        meta.setEffect(FireworkEffect.builder().with(t).build());
        i.setItemMeta(meta);
        inv.setItem(slot, i);
    }

    public static void select(FireworkEffect.Type t) {
        ShowData.pattern = t;
    }
}
