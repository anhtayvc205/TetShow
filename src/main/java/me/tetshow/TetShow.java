package me.tetshow;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TetShow extends JavaPlugin {

    private BukkitRunnable task;
    private Location center;
    private int time = 0;

    // dragon
    private double dragonAngle = 0;
    private double dragonScale = 0.4;

    // text
    private int textIndex = 0;
    private int textPhase = 0;

    private final Random r = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TetShow FINAL FULL ENABLED");
    }

    // ========================
    // COMMAND
    // ========================
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("tetset")) {
            Location l = p.getLocation();
            getConfig().set("show.world", l.getWorld().getName());
            getConfig().set("show.x", l.getX());
            getConfig().set("show.y", l.getY());
            getConfig().set("show.z", l.getZ());
            saveConfig();
            p.sendMessage("§aĐã set vị trí show");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tetshow")) {
            if (task != null) return true;
            center = loadLocation().add(0, 15, 0);
            startShow();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tetstop")) {
            stopShow();
            return true;
        }

        return true;
    }

    private Location loadLocation() {
        FileConfiguration c = getConfig();
        return new Location(
                Bukkit.getWorld(c.getString("show.world")),
                c.getDouble("show.x"),
                c.getDouble("show.y"),
                c.getDouble("show.z")
        );
    }

    // ========================
    // SHOW
    // ========================
    private void startShow() {
        World w = center.getWorld();
        time = 0;
        dragonAngle = 0;
        dragonScale = 0.4;
        textIndex = 0;
        textPhase = 0;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;

                // ======================
                // 🐲 0–600 RỒNG
                // ======================
                if (time <= 600) {
                    spawnDoubleDragon(w);
                }

                // ======================
                // 🔤 600–900 TÀ GIÁO
                // ======================
                else if (time <= 900) {
                    if (textPhase != 1) { textIndex = 0; textPhase = 1; }
                    drawSlowText(w, "TA GIAO");
                }

                // ======================
                // 🔤 900–1300 HAPPY NEW YEAR
                // ======================
                else if (time <= 1300) {
                    if (textPhase != 2) { textIndex = 0; textPhase = 2; }
                    drawSlowText(w, "HAPPY NEW YEAR 2026");
                }

                // ======================
                // 🦁 1300–1700 LÂN
                // ======================
                else if (time <= 1700) {
                    for (int i = 0; i < 12; i++) {
                        w.spawnParticle(
                                Particle.FLAME,
                                center.clone().add(-6 + i * 0.6, Math.sin(time * 0.4) * 2.8, 6),
                                10
                        );
                    }
                }

                // ======================
                // 🐎 1700–2100 NGỰA
                // ======================
                else if (time <= 2100) {
                    double r = 14;
                    double x = Math.cos(Math.toRadians(time * 5)) * r;
                    double z = Math.sin(Math.toRadians(time * 5)) * r;

                    for (int i = 0; i < 8; i++) {
                        w.spawnParticle(
                                Particle.CLOUD,
                                center.clone().add(x + i * 0.3, 2.5, z),
                                8
                        );
                    }
                }

                // ======================
                // 🎆 2100–10500 PHÁO HOA
                // ======================
                else {
                    finaleFireworks(w);
                }

                if (time >= 10500) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    // ========================
    // RỒNG 2 CON – TO DẦN – TAN
    // ========================
    private void spawnDoubleDragon(World w) {
        dragonAngle += 4;
        dragonScale = Math.min(2.0, dragonScale + 0.005);

        for (int d = 0; d < 2; d++) {
            double offset = d == 0 ? 0 : Math.PI;

            for (int i = 0; i < 24; i++) {
                double rad = Math.toRadians(dragonAngle) - i * 0.25 + offset;
                double r = 8 * dragonScale;
                double x = Math.cos(rad) * r;
                double z = Math.sin(rad) * r;
                double y = 6 + i * 0.28;

                Particle p = (i % 2 == 0) ? Particle.FLAME : Particle.LAVA;

                w.spawnParticle(p, center.clone().add(x, y, z), 4);
            }
        }
    }

    // ========================
    // CHỮ CHẬM – RÕ
    // ========================
    private void drawSlowText(World w, String text) {
        double x = -22;

        for (int i = 0; i < textIndex; i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                x += 2.5;
                continue;
            }

            for (int y = 0; y < 7; y++) {
                w.spawnParticle(
                        Particle.END_ROD,
                        center.clone().add(x, y * 0.45, 0),
                        6
                );
            }
            x += 1.6;
        }

        if (time % 14 == 0 && textIndex < text.length()) {
            textIndex++;
        }
    }

    // ========================
    // PHÁO HOA 7 PHÚT
    // ========================
    private void finaleFireworks(World w) {

        if (time % 3 == 0)
            spawnFirework(w, randomLoc());

        if (time % 25 == 0) {
            for (int i = 0; i < 360; i += 24) {
                double rad = Math.toRadians(i);
                spawnFirework(w, center.clone().add(
                        Math.cos(rad) * 22,
                        28,
                        Math.sin(rad) * 22
                ));
            }
        }

        if (time % 12 == 0)
            w.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2f, 1f);
    }

    private Location randomLoc() {
        return center.clone().add(rand(-22, 22), rand(22, 38), rand(-22, 22));
    }

    private void spawnFirework(World w, Location l) {
        Firework fw = w.spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.RED, Color.YELLOW, Color.ORANGE, Color.FUCHSIA)
                .trail(true).flicker(true)
                .build());
        m.setPower(3);
        fw.setFireworkMeta(m);
    }

    private double rand(double min, double max) {
        return min + (r.nextDouble() * (max - min));
    }

    private void stopShow() {
        if (task != null) task.cancel();
        task = null;
        time = 0;
        getLogger().info("TetShow stopped");
    }
}
