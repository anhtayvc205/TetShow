package me.tetshow;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FireworkShow {

    /* ================= CONFIG ================= */
    private static final List<Color> COLORS = List.of(
            Color.RED, Color.ORANGE, Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.PURPLE,
            Color.FUCHSIA
    );

    private static final FireworkEffect.Type[] TYPES = {
            FireworkEffect.Type.BALL,
            FireworkEffect.Type.BALL_LARGE,
            FireworkEffect.Type.BURST,
            FireworkEffect.Type.STAR,
            FireworkEffect.Type.CREEPER
    };

    /* ================= START SHOW ================= */
    public static void start(Location center) {

        new BukkitRunnable() {
            int tick = 0;
            int step = 0;

            @Override
            public void run() {

                // 1. TA GIAO
                if (step == 0) {
                    drawText(center, "TA GIAO", 0);
                    step++;
                    return;
                }

                // 2. HAPPY NEW YEAR
                if (step == 1) {
                    drawText(center, "HAPPY NEW YEAR", 10);
                    step++;
                    return;
                }

                // 3. SHOW THƯỜNG
                if (tick < 20 * 300) {
                    spawnCircle(center, 3);
                }

                // 4. CAO TRÀO 5 PHÚT CUỐI
                else {
                    spawnCircle(center, 10);
                    spawnSkyRain(center);
                    spawnHelix(center, 3, 8, 20);
                }

                if (tick > 20 * 600) {
                    cancel();
                }

                tick += 20;
            }

        }.runTaskTimer(TetShowProvider.plugin(), 0, 20);
    }

    /* ================= CHỮ PIXEL ================= */
    private static void drawText(Location center, String text, int heightOffset) {

        new BukkitRunnable() {
            int row = 0;

            @Override
            public void run() {
                if (row > 7) {
                    cancel();
                    return;
                }

                int x = -text.length() * 3;

                for (char c : text.toCharArray()) {
                    for (int[] p : FONT.getOrDefault(c, new int[][]{})) {
                        if (p[1] == row) {
                            Location loc = center.clone().add(
                                    x + p[0],
                                    heightOffset + row + 5,
                                    0
                            );
                            spawnFirework(loc, 3);
                        }
                    }
                    x += 6;
                }
                row++;
            }
        }.runTaskTimer(TetShowProvider.plugin(), 0, 10);
    }

    /* ================= VÒNG TRÒN ================= */
    private static void spawnCircle(Location c, int amount) {
        Random r = new Random();

        for (int i = 0; i < amount; i++) {
            double a = r.nextDouble() * Math.PI * 2;
            double rds = 8 + r.nextDouble() * 10;

            Location loc = c.clone().add(
                    Math.cos(a) * rds,
                    3 + r.nextDouble() * 8,
                    Math.sin(a) * rds
            );
            spawnFirework(loc, 3 + r.nextInt(2));
        }
    }

    /* ================= MƯA PHÁO ================= */
    private static void spawnSkyRain(Location c) {
        Random r = new Random();

        for (int i = 0; i < 6; i++) {
            Location loc = c.clone().add(
                    r.nextInt(20) - 10,
                    15 + r.nextInt(10),
                    r.nextInt(20) - 10
            );
            spawnFirework(loc, 4);
        }
    }

    /* ================= XOẮN 3D ================= */
    private static void spawnHelix(Location center, int turns, double radius, double height) {

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                if (t > Math.PI * 2 * turns) {
                    cancel();
                    return;
                }

                double x = Math.cos(t) * radius;
                double z = Math.sin(t) * radius;
                double y = (t / (Math.PI * 2 * turns)) * height;

                Location loc = center.clone().add(x, y + 2, z);
                spawnFirework(loc, 3);
                t += 0.25;
            }
        }.runTaskTimer(TetShowProvider.plugin(), 0, 2);
    }

    /* ================= FIREWORK ================= */
    private static void spawnFirework(Location loc, int power) {
        Random r = new Random();
        Firework fw = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(
                FireworkEffect.builder()
                        .with(TYPES[r.nextInt(TYPES.length)])
                        .withColor(COLORS.get(r.nextInt(COLORS.size())))
                        .withFade(COLORS.get(r.nextInt(COLORS.size())))
                        .trail(true)
                        .flicker(true)
                        .build()
        );
        meta.setPower(power);
        fw.setFireworkMeta(meta);
    }

    /* ================= FONT PIXEL (GỘP) ================= */
    private static final Map<Character, int[][]> FONT = new HashMap<>();

    static {
        FONT.put('A', new int[][]{{1,0},{2,0},{3,0},{0,1},{4,1},{0,2},{4,2},{0,3},{4,3},{1,4},{2,4},{3,4},{0,5},{4,5},{0,6},{4,6}});
        FONT.put('T', new int[][]{{0,6},{1,6},{2,6},{3,6},{4,6},{2,0},{2,1},{2,2},{2,3},{2,4},{2,5}});
        FONT.put('G', new int[][]{{1,0},{2,0},{3,0},{0,1},{0,2},{2,2},{3,2},{4,2},{0,3},{4,3},{0,4},{4,4},{1,5},{2,5},{3,5}});
        FONT.put('O', new int[][]{{1,0},{2,0},{3,0},{0,1},{4,1},{0,2},{4,2},{0,3},{4,3},{0,4},{4,4},{0,5},{4,5},{1,6},{2,6},{3,6}});
        FONT.put('H', new int[][]{{0,0},{4,0},{0,1},{4,1},{0,2},{4,2},{0,3},{1,3},{2,3},{3,3},{4,3},{0,4},{4,4},{0,5},{4,5},{0,6},{4,6}});
        FONT.put('E', new int[][]{{0,0},{1,0},{2,0},{3,0},{4,0},{0,1},{0,2},{0,3},{0,4},{1,4},{2,4},{3,4},{0,5},{0,6},{1,6},{2,6},{3,6},{4,6}});
        FONT.put('Y', new int[][]{{0,6},{4,6},{1,5},{3,5},{2,4},{2,3},{2,2},{2,1},{2,0}});
        FONT.put('N', new int[][]{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{1,5},{2,4},{3,3},{4,2},{4,1},{4,0},{4,3},{4,4},{4,5},{4,6}});
        FONT.put('R', new int[][]{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{1,6},{2,6},{3,6},{4,5},{4,4},{1,3},{2,3},{3,3},{4,2},{4,1},{4,0}});
        FONT.put('W', new int[][]{{0,0},{0,1},{0,2},{0,3},{0,4},{0,5},{0,6},{2,0},{2,1},{2,2},{4,0},{4,1},{4,2},{4,3},{4,4},{4,5},{4,6}});
        FONT.put(' ', new int[][]{});
    }
                                  }
