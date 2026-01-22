package me.tetshow;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TetShow extends JavaPlugin {

    private BukkitRunnable task;
    private Location center;
    private int time = 0;

    private final List<ArmorStand> dragons = new ArrayList<>();
    private final List<ArmorStand> texts = new ArrayList<>();

    private double angle = 0;
    private double height = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("TetShow FINAL ARMORSTAND ENABLED");
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

    // ==========================
    // SHOW
    // ==========================
    private void startShow() {
        World w = center.getWorld();
        time = 0;
        angle = 0;
        height = 0;

        spawnDragons(w);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                time++;
                angle += 2;

                // 🐲 0–600 bay vòng
                if (time <= 600) {
                    moveDragons(0);
                }

                // 🐲 600–900 bay lên
                else if (time <= 900) {
                    height += 0.02;
                    moveDragons(height);
                }

                // 🔤 900–1200 TÀ GIÁO
                else if (time == 900) {
                    removeDragons();
                    spawnText("TA GIAO");
                }

                // 🔤 1200–1800 HAPPY
                else if (time == 1200) {
                    removeText();
                    spawnText("HAPPY NEW YEAR 2026");
                }

                // 🎆 1800+ pháo hoa
                else if (time >= 1800) {
                    fireworks(w);
                }

                if (time >= 5400) stopShow();
            }
        };
        task.runTaskTimer(this, 0L, 1L);
    }

    // ==========================
    // DRAGONS
    // ==========================
    private void spawnDragons(World w) {
        dragons.clear();
        Material[] colors = {
                Material.RED_CONCRETE,
                Material.YELLOW_CONCRETE,
                Material.LIME_CONCRETE
        };

        for (int d = 0; d < 3; d++) {
            for (int i = 0; i < 12; i++) {
                ArmorStand as = w.spawn(center, ArmorStand.class);
                as.setInvisible(true);
                as.setMarker(true);
                as.setGravity(false);
                as.setSmall(true);

                if (i == 0)
                    as.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
                else
                    as.getEquipment().setHelmet(new ItemStack(colors[d]));

                dragons.add(as);
            }
        }
    }

    private void moveDragons(double up) {
        int index = 0;

        for (int d = 0; d < 3; d++) {
            double offset = d * (Math.PI * 2 / 3);

            for (int i = 0; i < 12; i++) {
                ArmorStand as = dragons.get(index++);
                double rad = Math.toRadians(angle - i * 20) + offset;
                double r = 6;
                double x = Math.cos(rad) * r;
                double z = Math.sin(rad) * r;
                double y = 4 + i * 0.25 + up;

                as.teleport(center.clone().add(x, y, z));
            }
        }
    }

    private void removeDragons() {
        for (ArmorStand as : dragons) as.remove();
        dragons.clear();
    }

    // ==========================
    // TEXT (ARMORSTAND)
    // ==========================
    private void spawnText(String text) {
        World w = center.getWorld();
        texts.clear();

        double x = -text.length() * 0.7;

        for (char c : text.toCharArray()) {
            if (c == ' ') {
                x += 1.4;
                continue;
            }

            ArmorStand as = w.spawn(center.clone().add(x, 5, 0), ArmorStand.class);
            as.setInvisible(true);
            as.setMarker(true);
            as.setGravity(false);
            as.setCustomName("§6" + c);
            as.setCustomNameVisible(true);

            texts.add(as);
            x += 1.4;
        }
    }

    private void removeText() {
        for (ArmorStand as : texts) as.remove();
        texts.clear();
    }

    // ==========================
    // FIREWORK
    // ==========================
    private void fireworks(World w) {
        if (time % 6 != 0) return;

        Location l = center.clone().add(
                (Math.random() * 30) - 15,
                20 + Math.random() * 15,
                (Math.random() * 30) - 15
        );

        Firework fw = w.spawn(l, Firework.class);
        FireworkMeta m = fw.getFireworkMeta();
        m.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.RED, Color.YELLOW, Color.LIME)
                .trail(true).flicker(true)
                .build());
        m.setPower(2);
        fw.setFireworkMeta(m);
    }

    private void stopShow() {
        if (task != null) task.cancel();
        task = null;
        removeDragons();
        removeText();
        getLogger().info("TetShow stopped");
    }
}
