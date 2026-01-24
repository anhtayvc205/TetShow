package me.tetshow.show;

import me.tetshow.TetShow;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class PreviewEngine {

    public static void preview(TetShow plugin, Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(
            FireworkEffect.builder()
                .with(plugin.getData().pattern)
                .withColor(plugin.getData().color)
                .trail(true)
                .flicker(true)
                .build()
        );

        meta.setPower(plugin.getData().power);
        fw.setFireworkMeta(meta);
    }
}
