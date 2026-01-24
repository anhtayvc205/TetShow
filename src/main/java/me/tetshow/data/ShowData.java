package me.tetshow.data;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import java.util.ArrayList;
import java.util.List;

public class ShowData {

    // màu pháo
    public static List<Color> colors = new ArrayList<>();

    // kiểu pháo
    public static FireworkEffect.Type pattern = FireworkEffect.Type.BALL;

    // độ cao pháo
    public static int power = 2;

    static {
        colors.add(Color.RED);
    }
}
