package me.tetshow.data;

import me.tetshow.TetShow;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

import java.util.HashSet;
import java.util.Set;

public class ShowData {
    public Set<Color> colors = new HashSet<>();
    public FireworkEffect.Type pattern = FireworkEffect.Type.BALL;
    public int power = 2;

    public ShowData(TetShow plugin) {
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
    }
}
