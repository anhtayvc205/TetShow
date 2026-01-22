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

    private ArmorStand[] dragon = new ArmorStand[14];

    private int textIndex = 0;
    private boolean glow = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TetShow FINAL FIX ENABLED");
    }

    // ========================
    // COMMANDS
    // ========================
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("tetreload")) {
            reloadConfig();
            sender.sendMessage("§aReloaded config");
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
            p.sendMessage("§aSet show location!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("tetshow")) {
            if (task != null) return true;
            center = loadLocation().add(0, 25, 0);
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
    // START SHOW
    // ========================
    private void startShow() {
        World w = center.getWorld();
        time = 0;
        angle = 0;
        textIndex = 0;

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;
                angle += 4;
                if (time % 20 == 0) glow = !glow;

                // 🔤 0–60: TÀ GIÁO
                if (time < 60) spawnTextFirework("TA GIAO");

                // 🐲 60–200: RỒNG 3D
                if (time == 60) spawnDragon3D();
                if (time >= 60 && time < 200) moveDragon3D((time - 60) / 140.0);
                if (time == 200) removeDragon3D();

                // 🦁 200–300: LÂN
                if (time >= 200 && time < 300) {
                    double y = Math.sin(time * 0.45) * 3.0;
                    for (int i = 0; i < 12; i++)
                        w.spawnParticle(
                                Particle.FLAME,
                                center.clone().add(-6 + i * 0.6, y, 6),
                                8
                        );
                }

                // 🐎 300–380: NGỰA
                if (time >= 300 && time < 380) {
                    double r = 16;
                    double x = Math.cos(Math.toRadians(time * 7)) * r;
                    double z = Math.sin(Math.toRadians(time * 7)) * r;
                    for (int i = 0; i < 6; i++)
                        w.spawnParticle(
                                Particle.CLOUD,
                                center.clone().add(x + i * 0.3, 2.5, z),
                                8
                        );
                }

                // 🔤 380–520: CHÚC MỪNG NĂM MỚI
                if (time >= 380 && time < 520) {
                    drawTextAnimated(w);
                }

                // 🎆 520–900: CAO TRÀO
                if (time >= 520) {
                    if (time % 4 == 0)
                        spawnRandomFirework(randomLoc());

                    if (time % 20 == 0) {
                        spawnRing(8, 5, Color.RED);
                        spawnRing(12, 7, Color.YELLOW);
                        spawnRing(18, 10, Color.ORANGE);
                    }

                    if (time % 10 == 0)
                        w.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1.8f, 1f);
                }

                if (time >= 900) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    // ========================
    // RỒNG 3D CÓ ĐẦU
    // ========================
    private void spawnDragon3D() {
        World w = center.getWorld();
        for (int i = 0; i < dragon.length; i++) {
            ArmorStand a = w.spawn(center, ArmorStand.class);
            a.setInvisible(true);
            a.setMarker(true);
            a.setGravity(false);

            if (i == 0)
                a.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
            else if (i < 4)
                a.getEquipment().setHelmet(new ItemStack(Material.RED_CONCRETE));
            else
                a.getEquipment().setHelmet(new ItemStack(Material.YELLOW_CONCRETE));

            dragon[i] = a;
        }
    }

    private void moveDragon3D(double scale) {
        for (int i = 0; i < dragon.length; i++) {
            double a = Math.toRadians(angle - i * 18);
            double r = 7 + scale * 6;
            double x = Math.cos(a) * r;
            double z = Math.sin(a) * r;
            double y = i * 0.35;
            dragon[i].teleport(center.clone().add(x, y, z));
        }
    }

    private void removeDragon3D() {
        for (ArmorStand a : dragon)
            if (a != null) a.remove();
    }

    // ========================
    // TEXT RÕ – TO – SÁNG
    // ========================
    private void drawTextAnimated(World w) {
        String t = "CHUC MUNG NAM MOI 2026";
        double start = -22;
        Particle p = glow ? Particle.END_ROD : Particle.FLAME;

        for (int i = 0; i < Math.min(textIndex, t.length()); i++) {
            char c = t.charAt(i);
            if (c == ' ') { start += 2.2; continue; }

            for (int y = 0; y < 8; y++) {
                w.spawnParticle(
                        p,
                        center.clone().add(start, y * 0.45, 0),
                        8, 0.05, 0.05, 0.05, 0
                );
            }
            start += 1.4;
        }

        if (time % 6 == 0 && textIndex < t.length())
            textIndex++;
    }

    // ========================
    // FIREWORK TO – CAO – RỘNG
    // ========================
    private Location randomLoc() {
        return center.clone().add(rand(-20, 20), rand(6, 18), rand(-20, 20));
    }

    private void spawnRandomFirework(Location l) {
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.values()[r.nextInt(FireworkEffect.Type.values().length)])
                .withColor(Color.RED, Color.YELLOW, Color.ORANGE, Color.FUCHSIA, Color.AQUA)
                .trail(true).flicker(true).build());
        m.setPower(2); // TO – CAO
        fw.setFireworkMeta(m);
    }

    private void spawnRing(double r, double y, Color c) {
        for (int i = 0; i < 360; i += 24) {
            double rad = Math.toRadians(i);
            spawnColorFirework(center.clone().add(Math.cos(rad) * r, y, Math.sin(rad) * r), c);
        }
    }

    private void spawnColorFirework(Location l, Color c) {
        Firework fw = l.getWorld().spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(c)
                .trail(true).flicker(true).build());
        m.setPower(2);
        fw.setFireworkMeta(m);
    }

    private void spawnTextFirework(String t) {
        double start = -6;
        for (char c : t.toCharArray()) {
            if (c == ' ') { start += 2; continue; }
            spawnColorFirework(center.clone().add(start, 12, 0), Color.YELLOW);
            start += 1.4;
        }
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
