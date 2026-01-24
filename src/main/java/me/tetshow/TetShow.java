package me.tetshow;

import me.tetshow.gui.MainMenu;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class TetShow extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("fw").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player p)) return true;
            MainMenu.open(p);
            return true;
        });
        getLogger().info("TetShow enabled");
    }
}
