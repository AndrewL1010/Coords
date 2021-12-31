package io.github.AndrewL1010.coords;

import org.bukkit.plugin.java.JavaPlugin;

public class CoordinatePlugin extends JavaPlugin {
    @Override
    public void onEnable(){
        getCommand("coords").setExecutor(new CoordinateList());
    }
    @Override
    public void onDisable(){

    }
}
