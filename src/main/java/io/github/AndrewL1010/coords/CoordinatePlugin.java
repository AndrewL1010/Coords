package io.github.AndrewL1010.coords;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class CoordinatePlugin extends JavaPlugin {


    @Override
    public void onEnable(){
        createPlayerListConfig();
        ArrayList<UUID> restoredPlayerList = restorePlayerList();
        HashMap<UUID, HashMap<String, Coordinate>> restoredMap = restoreCoordsList(restoredPlayerList);

        PluginCommand command = getCommand("coords");

        if(command != null){
           command.setExecutor(new CoordinateCommand(this, restoredMap, restoredPlayerList));
        }




    }
    @Override
    public void onDisable(){

    }
    public void createPlayerListConfig(){
        if(!new File(this.getDataFolder(), "playerList.yml").exists()){
            File file = new File( this.getDataFolder(), "playerList.yml");
            FileConfiguration playerListData = YamlConfiguration.loadConfiguration(file);

            playerListData.createSection("players");

            try {
                playerListData.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<UUID> restorePlayerList(){
        ArrayList<UUID> restoredPlayerList = new ArrayList<>();
        File file = new File( this.getDataFolder(), "playerList.yml");
        if(file.exists()){
            FileConfiguration playerListData = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection players = playerListData.getConfigurationSection("players");
            if(players != null){
                Map<String, Object> map = players.getValues(false);
                for(String player : map.keySet()) {
                    String strID = map.get(player).toString();
                    UUID id = UUID.fromString(strID);
                    restoredPlayerList.add(id);

                }
            }
        }
        return restoredPlayerList;
    }
    public HashMap<UUID, HashMap<String, Coordinate>> restoreCoordsList(ArrayList<UUID> playerList){

        HashMap<UUID, HashMap<String, Coordinate>> restoredMap = new HashMap<>();
        for(UUID playerID : playerList){
            File file = new File(this.getDataFolder(), playerID.toString() + "-Coords.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection locations = playerData.getConfigurationSection("locations");
            restoredMap.put(playerID, new HashMap<String, Coordinate>());
            if(locations != null){
                Map<String, Object> map = locations.getValues(false);
                for(String location : map.keySet()) {
                    MemorySection coord = (MemorySection)map.get(location);
                    String strX = coord.getString("X");
                    String strY = coord.getString("Y");
                    String strZ = coord.getString("Z");
                    if(strX != null && strY != null && strZ != null){
                        int x = Integer.parseInt(strX);
                        int y = Integer.parseInt(strY);
                        int z = Integer.parseInt(strZ);

                        restoredMap.get(playerID).put(location, new Coordinate(x,y,z));
                    }

                }
            }

        }
        return restoredMap;
    }

}
