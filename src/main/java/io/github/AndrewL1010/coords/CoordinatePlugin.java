package io.github.AndrewL1010.coords;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;


public class CoordinatePlugin extends JavaPlugin {


    @Override
    public void onEnable() {

        PluginCommand command = getCommand("coords");
        if (command != null) {
            command.setExecutor(new CoordinateCommand(this));
        }


    }

    @Override
    public void onDisable() {

    }


}
