package me.tetshow.show;

import me.tetshow.TetShow;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkEngine {

    public static void spawn(Location l) {
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .with(TetShow.get().getData().pattern)
                .withColor(TetShow.get().getData().colors)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(TetShow.get().getData().power);
        fw.setFireworkMeta(meta);
    }
}
