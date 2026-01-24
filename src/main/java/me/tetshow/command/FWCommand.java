package me.tetshow.command;

import me.tetshow.gui.MainMenu;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import me.tetshow.TetShow;

public class FWCommand implements CommandExecutor {

    private final TetShow plugin;

    public FWCommand(TetShow plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player p)) return true;

        if (a.length == 0 || a[0].equalsIgnoreCase("menu")) {
            MainMenu.open(p);
            return true;
        }

        if (a[0].equalsIgnoreCase("start")) {
            plugin.getData();
            p.sendMessage("§aĐã bắt đầu show!");
            return true;
        }

        if (a[0].equalsIgnoreCase("stop")) {
            p.sendMessage("§cĐã dừng show!");
            return true;
        }
        return true;
    }
}
