package me.tetshow;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TetShow extends JavaPlugin {

    private BukkitRunnable task;
    private Location center;
    private double angle = 0;
    private int time = 0;
    private final Random r = new Random();

    @Override
    public void onEnable() {
        getLogger().info("TetShow 2026 loaded");
    }

    @Override
    public void onDisable() {
        if (task != null) task.cancel();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("tetshow")) {
            if (task != null) {
                p.sendMessage("§cShow đang chạy!");
                return true;
            }

            center = p.getWorld().getSpawnLocation().clone().add(0, 25, 0);
            startShow();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tetstop")) {
            stopShow();
            p.sendMessage("§cĐã dừng show!");
            return true;
        }

        return true;
    }

    // ========================
    // 🚀 START SHOW
    // ========================
    private void startShow() {
        World w = center.getWorld();
        time = 0;
        angle = 0;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;
                angle += 3;

                // 🔊 tiếng pháo hoa nền
                if (time % 20 == 0)
                    w.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);

                // =========================
                // 🐲 2 RỒNG BAY ĐỐI XỨNG
                // =========================
                for (int i = 0; i < 25; i++) {
                    double rad = Math.toRadians(angle + i * 10);
                    double r1 = 8;

                    spawnParticle(w, Math.cos(rad) * r1, i * 0.15, Math.sin(rad) * r1);
                    spawnParticle(w, Math.cos(rad + Math.PI) * r1, i * 0.15, Math.sin(rad + Math.PI) * r1);
                }

                // =========================
                // 🦁 LÂN MÚA (NHÚN MẠNH)
                // =========================
                double lionY = Math.sin(time * 0.4) * 2.5;
                for (int i = 0; i < 8; i++) {
                    w.spawnParticle(
                            Particle.FIREWORKS_SPARK,
                            center.clone().add(-8 + i * 0.7, lionY, 8),
                            2, 0, 0, 0, 0
                    );
                }

                // =========================
                // 🐎 NGỰA PHI (2 VÒNG, NHANH, XA)
                // =========================
                double horseR = 16;
                double hx = Math.cos(Math.toRadians(time * 6)) * horseR;
                double hz = Math.sin(Math.toRadians(time * 6)) * horseR;
                for (int i = 0; i < 6; i++) {
                    w.spawnParticle(
                            Particle.CLOUD,
                            center.clone().add(hx + i * 0.4, 2, hz),
                            1, 0, 0, 0, 0
                    );
                }

                // =========================
                // 🌸 HOA ĐÀO RƠI KHẮP SPAWN
                // =========================
                for (int i = 0; i < 12; i++) {
                    Location f = center.clone().add(rand(-15, 15), rand(0, 10), rand(-15, 15));
                    w.spawnParticle(Particle.HAPPY_VILLAGER, f, 3, 0.2, 0.2, 0.2, 0);
                }

                // =========================
                // 🔤 CHỮ PIXEL
                // =========================
                if (time > 40 && time < 220)
                    drawText(w);

                // =========================
                // 🎆 PHÁO HOA NỔ THẬT (CUỐI SHOW)
                // =========================
                if (time > 220 && time % 10 == 0) {
                    spawnFirework(w, center.clone().add(rand(-10, 10), 0, rand(-10, 10)));
                }

                if (time > 240) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    // ========================
    // 🧨 FIREWORK META
    // ========================
    private void spawnFirework(World w, Location l) {
        Firework fw = w.spawn(l, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.RED, Color.YELLOW)
                .withFade(Color.ORANGE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }

    // ========================
    // 🔤 TEXT PIXEL
    // ========================
    private void drawText(World w) {
        String text = "CHUC MUNG NAM MOI 2026";
        double start = -18;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                start += 1.5;
                continue;
            }
            for (int y = 0; y < 6; y++) {
                w.spawnParticle(
                        Particle.FIREWORKS_SPARK,
                        center.clone().add(start, y * 0.35, 0),
                        2
                );
            }
            start += 0.9;
        }
    }

    // ========================
    // 🔥 HELPERS
    // ========================
    private void spawnParticle(World w, double x, double y, double z) {
        w.spawnParticle(Particle.FLAME, center.clone().add(x, y, z), 1, 0, 0, 0, 0);
    }

    private double rand(double min, double max) {
        return min + (r.nextDouble() * (max - min));
    }

    // ========================
    // ⛔ STOP
    // ========================
    private void stopShow() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        time = 0;
    }
  }
