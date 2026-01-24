package me.tetshow;

import me.tetshow.data.ShowData;
import org.bukkit.plugin.java.JavaPlugin;

public class TetShow extends JavaPlugin {

    private static ShowData data;

    @Override
    public void onEnable() {
        data = new ShowData();
        getCommand("fw").setExecutor(new me.tetshow.command.FVCommand(this));
    }

    public ShowData getData() {
        return data;
    }
}
