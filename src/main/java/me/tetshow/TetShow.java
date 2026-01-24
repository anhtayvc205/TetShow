import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Firework;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TetShow extends JavaPlugin implements Listener, CommandExecutor {

    Location center;
    BukkitRunnable task;
    boolean running = false;
    boolean waitText = false;

    String text;
    int duration, interval, radius, height, amount;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("fwmenu").setExecutor(this);
        getCommand("fwstart").setExecutor(this);
        getCommand("fwstop").setExecutor(this);
        getCommand("fwset").setExecutor(this);
        getCommand("fwreload").setExecutor(this);
    }

    void loadConfigValues() {
        World w = Bukkit.getWorld(getConfig().getString("show.world"));
        center = new Location(w,
                getConfig().getDouble("show.x"),
                getConfig().getDouble("show.y"),
                getConfig().getDouble("show.z"));

        text = getConfig().getString("text.value");
        duration = getConfig().getInt("firework.duration") * 20;
        interval = getConfig().getInt("firework.interval");
        radius = getConfig().getInt("firework.radius");
        height = getConfig().getInt("firework.height");
        amount = getConfig().getInt("firework.amount");
    }

    /* ================= COMMAND ================= */

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        switch (cmd.getName()) {
            case "fwmenu" -> openMainMenu(p);
            case "fwstart" -> startShow();
            case "fwstop" -> stopShow();
            case "fwset" -> {
                center = p.getLocation();
                saveLocation();
                p.sendMessage("§aĐã set vị trí");
            }
            case "fwreload" -> {
                reloadConfig();
                loadConfigValues();
                p.sendMessage("§aReload xong");
            }
        }
        return true;
    }

    void saveLocation() {
        getConfig().set("show.world", center.getWorld().getName());
        getConfig().set("show.x", center.getX());
        getConfig().set("show.y", center.getY());
        getConfig().set("show.z", center.getZ());
        saveConfig();
    }

    /* ================= MENU ================= */

    void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6TetShow PRO MAX");

        inv.setItem(10, item(Material.FIREWORK_ROCKET, "§aPháo hoa"));
        inv.setItem(12, item(Material.NAME_TAG, "§eChữ"));
        inv.setItem(14, item(Material.CLOCK, "§bThời gian"));
        inv.setItem(16, item(Material.REDSTONE, "§cStart / Stop"));

        p.openInventory(inv);
    }

    ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        i.setItemMeta(im);
        return i;
    }

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§6TetShow PRO MAX")) return;
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();

        switch (e.getSlot()) {
            case 10 -> openFireworkMenu(p);
            case 12 -> {
                waitText = true;
                p.closeInventory();
                p.sendMessage("§eNhập chữ mới:");
            }
            case 16 -> {
                if (running) stopShow();
                else startShow();
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (!waitText) return;
        waitText = false;
        text = e.getMessage();
        getConfig().set("text.value", text);
        saveConfig();
        e.getPlayer().sendMessage("§aĐã set chữ: " + text);
        e.setCancelled(true);
    }

    /* ================= SHOW ================= */

    void startShow() {
        if (running) return;
        running = true;

        task = new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick > duration) {
                    cancel();
                    running = false;
                    return;
                }

                spawnCircleFirework();
                if (tick == 100) spawnText();

                tick++;
            }
        };
        task.runTaskTimer(this, 0, interval);
    }

    void stopShow() {
        if (task != null) task.cancel();
        running = false;
    }

    /* ================= PHÁO HOA ================= */

    void spawnCircleFirework() {
        Color[] colors = {
                Color.RED, Color.ORANGE, Color.YELLOW,
                Color.GREEN, Color.AQUA, Color.BLUE, Color.PURPLE
        };

        FireworkEffect.Type[] types = FireworkEffect.Type.values();

        for (int i = 0; i < amount; i++) {
            double ang = Math.PI * 2 / amount * i;
            Location l = center.clone().add(
                    Math.cos(ang) * radius,
                    height,
                    Math.sin(ang) * radius
            );

            Firework f = center.getWorld().spawn(l, Firework.class);
            FireworkMeta m = f.getFireworkMeta();
            m.addEffect(FireworkEffect.builder()
                    .with(types[new Random().nextInt(types.length)])
                    .withColor(colors)
                    .trail(true)
                    .flicker(true)
                    .build());
            m.setPower(2);
            f.setFireworkMeta(m);
        }
    }

    void spawnText() {
        int x = -text.length() * 3;
        for (char c : text.toCharArray()) {
            if (c == ' ') {
                x += 4;
                continue;
            }
            for (int y = 0; y < 8; y++) {
                Location l = center.clone().add(x, height + y, 0);
                Firework f = center.getWorld().spawn(l, Firework.class);
                FireworkMeta m = f.getFireworkMeta();
                m.addEffect(FireworkEffect.builder()
                        .withColor(Color.YELLOW, Color.RED, Color.GREEN)
                        .trail(true).flicker(true)
                        .build());
                m.setPower(0);
                f.setFireworkMeta(m);
            }
            x += 4;
        }
    }
}
