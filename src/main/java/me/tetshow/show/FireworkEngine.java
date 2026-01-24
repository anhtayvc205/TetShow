package me.tetshow.engine;

import me.tetshow.data.ShowData;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkEngine {

    public static void shoot(Location loc) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(
            FireworkEffect.builder()
                .with(ShowData.pattern)
                .withColor(ShowData.colors)
                .trail(true)
                .flicker(true)
                .build()
        );

        meta.setPower(ShowData.power);
        fw.setFireworkMeta(meta);
    }
}
