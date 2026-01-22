import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TetShow extends JavaPlugin implements CommandExecutor {

    private boolean running = false;
    private Location center;
    private final List<Entity> spawned = new ArrayList<>();
    private BukkitRunnable currentTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadCenter();
        getCommand("tetshow").setExecutor(this);
        getLogger().info("TetShow enabled");
    }

    void loadCenter() {
        if (!getConfig().contains("show.world")) return;
        World w = Bukkit.getWorld(getConfig().getString("show.world"));
        center = new Location(
                w,
                getConfig().getDouble("show.x"),
                getConfig().getDouble("show.y"),
                getConfig().getDouble("show.z")
        );
    }

    void saveCenter(Location l) {
        getConfig().set("show.world", l.getWorld().getName());
        getConfig().set("show.x", l.getX());
        getConfig().set("show.y", l.getY());
        getConfig().set("show.z", l.getZ());
        saveConfig();
        center = l.clone();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        if (a.length == 0) {
            p.sendMessage("/tetshow set | start | stop | reload");
            return true;
        }

        switch (a[0].toLowerCase()) {
            case "set" -> {
                saveCenter(p.getLocation());
                p.sendMessage("§aĐã set vị trí show");
            }
            case "start" -> {
                if (running) {
                    p.sendMessage("§cShow đang chạy");
                    return true;
                }
                if (center == null) {
                    p.sendMessage("§cChưa set vị trí. Dùng /tetshow set");
                    return true;
                }
                startShow();
                p.sendMessage("§aBắt đầu show");
            }
            case "stop" -> {
                stopShow();
                p.sendMessage("§cĐã dừng show");
            }
            case "reload" -> {
                reloadConfig();
                loadCenter();
                p.sendMessage("§aĐã reload config");
            }
        }
        return true;
    }

    /* ================= SHOW ================= */

    void startShow() {
        running = true;
        runText(() ->
                runDragon(() ->
                        runFirework(() -> running = false)
                )
        );
    }

    void stopShow() {
        if (currentTask != null) currentTask.cancel();
        spawned.forEach(Entity::remove);
        spawned.clear();
        running = false;
    }

    /* ================= TEXT ================= */
    void runText(Runnable done) {
        String text = "TA GIAO";
        List<Location> blocks = new ArrayList<>();

        int x = -text.length() * 2;
        for (char c : text.toCharArray()) {
            if (c != ' ')
                blocks.add(center.clone().add(x, 0, 0));
            x += 4;
        }

        int duration = getConfig().getInt("timeline.text_duration") * 20;

        currentTask = new BukkitRunnable() {
            int i = 0;
            int t = 0;
            public void run() {
                if (i < blocks.size()) {
                    blocks.get(i).getBlock().setType(Material.GOLD_BLOCK);
                    i++;
                }
                if (t++ > duration) {
                    cancel();
                    done.run();
                }
            }
        };
        currentTask.runTaskTimer(this, 0, 10);
    }

    /* ================= DRAGON ================= */
    void runDragon(Runnable done) {
        List<ArmorStand> dragon = new ArrayList<>();

        int parts = getConfig().getInt("dragon.parts");
        double radius = getConfig().getDouble("dragon.radius");
        double speed = getConfig().getDouble("dragon.speed");
        int duration = getConfig().getInt("timeline.dragon_duration") * 20;

        for (int i = 0; i < parts; i++) {
            ArmorStand as = center.getWorld().spawn(center, ArmorStand.class);
            as.setInvisible(true);
            as.setGravity(false);
            as.getEquipment().setHelmet(new ItemStack(Material.RED_CONCRETE));
            dragon.add(as);
            spawned.add(as);
        }

        currentTask = new BukkitRunnable() {
            double t = 0;
            int tick = 0;

            public void run() {
                int i = 0;
                for (ArmorStand a : dragon) {
                    double angle = t + i * 0.25;
                    a.teleport(center.clone().add(
                            Math.cos(angle) * radius,
                            5 + i * 0.1,
                            Math.sin(angle) * radius
                    ));
                    i++;
                }
                t += speed;
                if (tick++ > duration) {
                    dragon.forEach(Entity::remove);
                    cancel();
                    done.run();
                }
            }
        };
        currentTask.runTaskTimer(this, 0, 1);
    }

    /* ================= FIREWORK ================= */
    void runFirework(Runnable done) {
        int interval = getConfig().getInt("firework.interval");
        int duration = getConfig().getInt("timeline.firework_duration") * 20;
        int power = getConfig().getInt("firework.power");

        currentTask = new BukkitRunnable() {
            int t = 0;
            public void run() {
                Firework fw = center.getWorld().spawn(center.clone().add(
                        Math.random()*20-10, 20, Math.random()*20-10), Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.RED, Color.YELLOW, Color.ORANGE)
                        .trail(true).flicker(true).build());
                meta.setPower(power);
                fw.setFireworkMeta(meta);
                spawned.add(fw);

                if (t++ > duration) {
                    cancel();
                    done.run();
                }
            }
        };
        currentTask.runTaskTimer(this, 0, interval);
    }
                }
