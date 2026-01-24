import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class TetShow extends JavaPlugin implements CommandExecutor {

    Location center;
    boolean running = false;
    List<Entity> spawned = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadCenter();
        getCommand("tetshow").setExecutor(this);
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
        center.getChunk().setForceLoaded(true);
    }

    void saveCenter(Location l) {
        getConfig().set("show.world", l.getWorld().getName());
        getConfig().set("show.x", l.getX());
        getConfig().set("show.y", l.getY());
        getConfig().set("show.z", l.getZ());
        saveConfig();
        center = l.clone();
        center.getChunk().setForceLoaded(true);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length == 0) {
            p.sendMessage("/tetshow set | start | stop | reload");
            return true;
        }

        switch (a[0]) {
            case "set" -> {
                saveCenter(p.getLocation());
                p.sendMessage("§aĐã set vị trí show");
            }
            case "start" -> {
                if (running) return true;
                if (center == null) {
                    p.sendMessage("§cChưa set vị trí!");
                    return true;
                }
                startShow();
            }
            case "stop" -> {
                spawned.forEach(Entity::remove);
                spawned.clear();
                running = false;
            }
            case "reload" -> {
                reloadConfig();
                loadCenter();
                p.sendMessage("§aReload config xong");
            }
        }
        return true;
    }

    /* ================= SHOW ================= */

    void startShow() {
        running = true;
        runDragonAndFirework();
    }

    /* ================= RỒNG + PHÁO HOA ================= */

    void runDragonAndFirework() {
        World w = center.getWorld();

        int parts = getConfig().getInt("dragon.parts");
        double radius = getConfig().getDouble("dragon.radius");
        double speed = getConfig().getDouble("dragon.speed");

        int fireworkInterval = getConfig().getInt("firework.interval");
        int fireworkDuration = getConfig().getInt("firework.duration") * 20;

        List<ArmorStand> dragon = new ArrayList<>();

        // tạo rồng
        for (int i = 0; i < parts; i++) {
            ArmorStand as = w.spawn(center, ArmorStand.class);
            as.setSmall(true);
            as.setBasePlate(false);
            as.setGravity(false);

            if (i == 0) {
                as.getEquipment().setHelmet(new ItemStack(Material.DRAGON_HEAD));
            } else {
                as.getEquipment().setHelmet(
                        new ItemStack(i % 2 == 0 ? Material.RED_CONCRETE : Material.YELLOW_CONCRETE)
                );
            }

            dragon.add(as);
            spawned.add(as);
        }

        new BukkitRunnable() {
            double t = 0;
            int tick = 0;

            Color[] colors = new Color[]{
                    Color.RED, Color.YELLOW, Color.GREEN,
                    Color.WHITE, Color.AQUA, Color.PURPLE
            };

            @Override
            public void run() {
                int i = 0;
                for (ArmorStand a : dragon) {
                    double angle = t + i * 0.25;
                    a.teleport(center.clone().add(
                            Math.cos(angle) * radius,
                            6 + i * 0.12,
                            Math.sin(angle) * radius
                    ));
                    i++;
                }
                t += speed;

                // vòng pháo hoa đổi màu
                if (tick % fireworkInterval == 0) {
                    Color c = colors[(tick / fireworkInterval) % colors.length];
                    double base = tick * 0.05;

                    for (int k = 0; k < 12; k++) {
                        double ang = base + (Math.PI * 2 / 12) * k;
                        Location l = center.clone().add(
                                Math.cos(ang) * 25,
                                22,
                                Math.sin(ang) * 25
                        );

                        Firework f = w.spawn(l, Firework.class);
                        FireworkMeta m = f.getFireworkMeta();
                        m.addEffect(FireworkEffect.builder()
                                .withColor(c)
                                .trail(true)
                                .flicker(true)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .build());
                        m.setPower(2);
                        f.setFireworkMeta(m);
                        spawned.add(f);
                    }
                }

                // bắn chữ
                if (tick == 200) fireworkText("TA GIAO");
                if (tick == 400) fireworkText("HAPPY NEW YEAR");

                if (tick++ > fireworkDuration) {
                    dragon.forEach(Entity::remove);
                    cancel();
                    running = false;
                }
            }
        }.runTaskTimer(this, 0, 2);
    }

    /* ================= CHỮ PHÁO HOA ================= */

    void fireworkText(String text) {
        World w = center.getWorld();
        int baseX = -text.length() * 2;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                baseX += 4;
                continue;
            }
            for (int y = 0; y < 8; y++) {
                Location l = center.clone().add(baseX, 15 + y, 0);
                Firework f = w.spawn(l, Firework.class);
                FireworkMeta m = f.getFireworkMeta();
                m.addEffect(FireworkEffect.builder()
                        .withColor(Color.YELLOW, Color.RED)
                        .trail(true).flicker(true).build());
                m.setPower(0);
                f.setFireworkMeta(m);
                spawned.add(f);
            }
            baseX += 4;
        }
    }
}
