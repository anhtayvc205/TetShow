package me.tetshow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TetShow extends JavaPlugin {

    @Override
    public void onEnable() {
        TetShowProvider.set(this);
        getCommand("fw").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (args.length == 0) {
            p.sendMessage("§e/fw set | /fw start");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            ShowLocation.location = p.getLocation();
            p.sendMessage("§aĐã set vị trí pháo hoa!");
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (ShowLocation.location == null) {
                p.sendMessage("§cChưa set vị trí!");
                return true;
            }
            FireworkShow.start(ShowLocation.location);
            p.sendMessage("§eBắt đầu show pháo hoa!");
        }

        return true;
    }
}
