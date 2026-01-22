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

    private double angle = 0;
    private double scale = 0.5;

    private int textIndex = 0;
    private int phase = 0;

    private final Random r = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TetShow ULTRA LIGHT ENABLED");
    }

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
            center = loadLocation().add(0, 10, 0);
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

    private void startShow() {
        World w = center.getWorld();
        time = 0;
        angle = 0;
        scale = 0.5;
        textIndex = 0;
        phase = 0;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;

                if (time <= 600) {
                    spawnDragon(w);
                }

                else if (time <= 900) {
                    if (phase != 1) { textIndex = 0; phase = 1; }
                    drawText(w, "TA GIAO");
                }

                else if (time <= 1200) {
                    if (phase != 2) { textIndex = 0; phase = 2; }
                    drawText(w, "HAPPY NEW YEAR 2026");
                }

                else if (time <= 1500) {
                    w.spawnParticle(
                            Particle.FLAME,
                            center.clone().add(0, 2 + Math.sin(time * 0.3), 6),
                            6
                    );
                }

                else if (time <= 1800) {
                    double r = 10;
                    double x = Math.cos(Math.toRadians(time * 4)) * r;
                    double z = Math.sin(Math.toRadians(time * 4)) * r;

                    w.spawnParticle(
                            Particle.CLOUD,
                            center.clone().add(x, 2.5, z),
                            6
                    );
                }

                else {
                    finaleFireworks(w);
                }

                if (time >= 7800) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    private void spawnDragon(World w) {
        angle += 3;
        scale = Math.min(2.0, scale + 0.002);

        for (int d = 0; d < 3; d++) {
            double offset = d * (Math.PI * 2 / 3);

            for (int i = 0; i < 12; i++) {
                double rad = Math.toRadians(angle) - i * 0.35 + offset;
                double r = 6 * scale;
                double x = Math.cos(rad) * r;
                double z = Math.sin(rad) * r;
                double y = 5 + i * 0.3;

                Particle p = (i % 2 == 0) ? Particle.FLAME : Particle.LAVA;
                w.spawnParticle(p, center.clone().add(x, y, z), 1);
            }
        }
    }

    private void drawText(World w, String text) {
        double x = -18;

        for (int i = 0; i < textIndex; i++) {
            char c = text.charAt(i);
            if (c == ' ') { x += 2.2; continue; }

            for (int y = 0; y < 6; y++) {
                w.spawnParticle(
                        Particle.END_ROD,
                        center.clone().add(x, y * 0.45, 0),
                        2
                );
            }
            x += 1.4;
        }

        if (time % 16 == 0 && textIndex < text.length()) {
            textIndex++;
        }
    }

    private void finaleFireworks(World w) {
        if (time % 6 == 0)
            spawnFirework(w, randomLoc());

        if (time % 30 == 0) {
            for (int i = 0; i < 360; i += 36) {
                double rad = Math.toRadians(i);
                spawnFirework(w, center.clone().add(
                        Math.cos(rad) * 18,
                        24,
                        Math.sin(rad) * 18
                ));
            }
        }
    }

    private Location randomLoc() {
        return center.clone().add(rand(-18, 18), rand(22, 30), rand(-18, 18));
    }

    private void spawnFirework(World w, Location l) {
        Firework fw = w.spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.RED, Color.YELLOW, Color.ORANGE)
                .trail(true).flicker(true)
                .build());
        m.setPower(2);
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
