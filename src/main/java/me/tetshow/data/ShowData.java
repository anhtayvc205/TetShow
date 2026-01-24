package me.tetshow.data;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import java.util.*;

public class ShowData {

    public static List<Color> colors = new ArrayList<>();
    public static FireworkEffect.Type pattern = FireworkEffect.Type.BALL;
    public static int power = 2;

    static {
        colors.add(Color.RED);
    }
}
