package me.tetshow;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
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

    // dragon 3D
    private ArmorStand[] dragon = new ArmorStand[12];

    // text animation
    private int textIndex = 0;
    private boolean glow = false;

    // timeline
    private int tDragon, tLion, tHorse, tText, tFinale;
    private int total;

    // firework
    private int fwRadius, fwMinH, fwMaxH, fwInterval, fwRing;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        getLogger().info("=================================");
        getLogger().info(" TetShow 2026 ENABLED");
        getLogger().info(" World: " + getConfig().getString("show.world"));
        getLogger().info("=================================");
    }

    // ========================
    // CONFIG
    // ========================
    private void loadConfigValues() {
        FileConfiguration c = getConfig();

        tDragon = c.getInt("timeline.dragon");
        tLion   = c.getInt("timeline.lion");
        tHorse  = c.getInt("timeline.horse");
        tText   = c.getInt("timeline.text");
        tFinale = c.getInt("timeline.finale");

        fwRadius   = c.getInt("firework.radius");
        fwMinH     = c.getInt("firework.height_min");
        fwMaxH     = c.getInt("firework.height_max");
        fwInterval = c.getInt("firework.normal_interval");
        fwRing     = c.getInt("firework.ring_interval");

        total = tDragon + tLion + tHorse + tText + tFinale + 60;
    }

    // ========================
    // COMMANDS
    // ========================
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("tetreload")) {
            reloadConfig();
            loadConfigValues();
            sender.sendMessage("§aTetShow config reloaded!");
            getLogger().info("TetShow config reloaded.");
            return true;
        }

        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("tetset")) {
            Location l = p.getLocation();
            getConfig().set("show.world", l.getWorld().getName());
            getConfig().set("show.x", l.getX());
            getConfig().set("show.y", l.getY());
            getConfig().set("show.z", l.getZ());
            saveConfig();
            p.sendMessage("§aĐã set vị trí show!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tetshow")) {
            if (task != null) {
                p.sendMessage("§cShow đang chạy!");
                return true;
            }
            center = loadLocation().add(0, 25, 0);
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
    // START SHOW
    // ========================
    private void startShow() {
        World w = center.getWorld();
        time = 0;
        angle = 0;
        textIndex = 0;
        glow = false;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;
                angle += 4;
                if (time % 20 == 0) glow = !glow;

                // 🔤 0–60: TÀ GIÁO
                if (time < 60) {
                    spawnTextFirework("TA GIAO");
                }

                int t1 = 60 + tDragon;
                int t2 = t1 + tLion;
                int t3 = t2 + tHorse;
                int t4 = t3 + tText;

                // 🐲 RỒNG 3D
                if (time == 60) spawnDragon3D();
                if (time > 60 && time < t1) moveDragon3D((time - 60) / (double) tDragon);
                if (time == t1) removeDragon3D();

                // 🦁 LÂN
                if (time >= t1 && time < t2) {
                    double y = Math.sin(time * 0.4) * 2.5;
                    for (int i = 0; i < 8; i++)
                        w.spawnParticle(Particle.END_ROD, center.clone().add(-8 + i * 0.7, y, 8), 1);
                }

                // 🐎 NGỰA
                if (time >= t2 && time < t3) {
                    double r = 16;
                    double x = Math.cos(Math.toRadians(time * 6)) * r;
                    double z = Math.sin(Math.toRadians(time * 6)) * r;
                    for (int i = 0; i < 4; i++)
                        w.spawnParticle(Particle.CLOUD, center.clone().add(x + i * 0.4, 2, z), 1);
                }

                // 🔤 CHÚC MỪNG NĂM MỚI
                if (time >= t3 && time < t4) {
                    drawTextAnimated(w);
                }

                // 🎆 CAO TRÀO
                if (time >= t4) {
                    if (time % fwInterval == 0)
                        spawnRandomFirework(randomLoc());

                    if (time % fwRing == 0) {
                        spawnRing(8, 4, Color.RED);
                        spawnRing(12, 6, Color.YELLOW);
                        spawnRing(16, 8, Color.ORANGE);
                    }

                    if (time % 10 == 0)
                        w.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.5f, 1f);
                }

                if (time >= total) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    // ========================
    // DRAGON 3D
    // ========================
    private void spawnDragon3D() {
        World w = center.getWorld();
        for (int i = 0; i < dragon.length; i++) {
            ArmorStand a = w.spawn(center, ArmorStand.class);
            a.setInvisible(true);
            a.setMarker(true);
            a.setGravity(false);
            a.getEquipment().setHelmet(new ItemStack(i % 2 == 0 ? Material.RED_CONCRETE : Material.YELLOW_CONCRETE));
            dragon[i] = a;
        }
    }

    private void moveDragon3D(double scale) {
        for (int i = 0; i < dragon.length; i++) {
            double a = Math.toRadians(angle - i * 15);
            double r = 6 + scale * 4;
            double x = Math.cos(a) * r;
            double z = Math.sin(a) * r;
            double y = i * 0.3;
            dragon[i].teleport(center.clone().add(x, y, z));
        }
    }

    private void removeDragon3D() {
        for (ArmorStand a : dragon)
            if (a != null) a.remove();
    }

    // ========================
    // TEXT
    // ========================
    private void drawTextAnimated(World w) {
        String t = "CHUC MUNG NAM MOI 2026";
        double start = -18;
        Particle p = glow ? Particle.END_ROD : Particle.FIREWORK;

        for (int i = 0; i < Math.min(textIndex, t.length()); i++) {
            char c = t.charAt(i);
            if (c == ' ') { start += 1.5; continue; }
            for (int y = 0; y < 6; y++)
                w.spawnParticle(p, center.clone().add(start, y * 0.35, 0), 2);
            start += 0.9;
        }
        if (time % 10 == 0 && textIndex < t.length()) textIndex++;
    }

    // ========================
    // FIREWORK
    // ========================
    private Location randomLoc() {
        return center.clone().add(rand(-fwRadius, fwRadius), rand(fwMinH, fwMaxH), rand(-fwRadius, fwRadius));
    }

    private void spawnRandomFirework(Location l) {
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.values()[r.nextInt(FireworkEffect.Type.values().length)])
                .withColor(Color.RED, Color.YELLOW, Color.ORANGE, Color.FUCHSIA, Color.AQUA)
                .trail(true).flicker(true).build());
        m.setPower(0);
        fw.setFireworkMeta(m);
        Bukkit.getScheduler().runTaskLater(this, fw::detonate, 1L);
    }

    private void spawnRing(double r, double y, Color c) {
        for (int i = 0; i < 360; i += 30) {
            double rad = Math.toRadians(i);
            spawnColorFirework(center.clone().add(Math.cos(rad) * r, y, Math.sin(rad) * r), c);
        }
    }

    private void spawnColorFirework(Location l, Color c) {
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(c).trail(true).flicker(true).build());
        m.setPower(0);
        fw.setFireworkMeta(m);
        Bukkit.getScheduler().runTaskLater(this, fw::detonate, 1L);
    }

    private void spawnTextFirework(String t) {
        double start = -6;
        for (char c : t.toCharArray()) {
            if (c == ' ') { start += 2; continue; }
            spawnColorFirework(center.clone().add(start, 10, 0), Color.YELLOW);
            start += 1.2;
        }
    }

    private double rand(double min, double max) {
        return min + (r.nextDouble() * (max - min));
    }

    private void stopShow() {
        if (task != null) task.cancel();
        task = null;
        time = 0;
        getLogger().info("TetShow stopped.");
    }
}
