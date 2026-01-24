package me.tetshow.engine;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class PreviewEngine {

    public static void preview(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(
            FireworkEffect.builder()
                .with(ShowData.pattern)
                .withColor(ShowData.color)
                .flicker(true)
                .trail(true)
                .build()
        );

        meta.setPower(2);
        fw.setFireworkMeta(meta);
    }
}
