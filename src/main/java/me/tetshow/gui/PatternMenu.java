package me.tetshow.gui;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class PatternMenu {

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§bChọn kiểu pháo hoa");

        add(inv, 10, FireworkEffect.Type.BALL);
        add(inv, 11, FireworkEffect.Type.BALL_LARGE);
        add(inv, 12, FireworkEffect.Type.STAR);
        add(inv, 13, FireworkEffect.Type.BURST);
        add(inv, 14, FireworkEffect.Type.CREEPER);

        p.openInventory(inv);
    }

    private static void add(Inventory inv, int slot, FireworkEffect.Type type) {
        ItemStack i = new ItemStack(Material.FIREWORK_STAR);
        FireworkEffectMeta meta = (FireworkEffectMeta) i.getItemMeta();
        meta.setDisplayName("§e" + type.name());
        meta.setEffect(FireworkEffect.builder().with(type).build());
        i.setItemMeta(meta);
        inv.setItem(slot, i);
    }

    public static void select(Player p, FireworkEffect.Type type) {
        ShowData.pattern = type;
        p.sendMessage("§aĐã chọn kiểu: " + type.name());
    }
}
