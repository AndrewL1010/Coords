package io.github.AndrewL1010.coords;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class CoordinateCommand implements CommandExecutor {


    public HashMap<UUID, HashMap<String, Coordinate>> map;
    public ArrayList<UUID> playerList;
    private final CoordinatePlugin plugin;

    public CoordinateCommand(CoordinatePlugin plugin, HashMap<UUID, HashMap<String, Coordinate>> map, ArrayList<UUID> playerList){
        this.plugin = plugin;
        this.map = map;
        this.playerList = playerList;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label, String[] args){

        if(!(sender instanceof Player player)){
            sender.sendMessage("Only players can use this command");
            return true;
        }
        UUID playerID = player.getUniqueId();
        if(args.length > 4){
            sender.sendMessage(ChatColor.RED + "Incorrect format\n");
            printHelp(sender);
            return true;
        }
        else if(args[0].equals("help")){
            printHelp(sender);
            return true;
        }
        else if(args.length == 2 && args[0].equals("set")){
            if(!map.containsKey(playerID)){
                map.put(playerID, new HashMap<String, Coordinate>());
                makeFile(player);
            }

            if(!map.get(playerID).containsKey(args[1])){
                if(!playerList.contains(playerID)){
                    playerList.add(playerID);
                    File file = new File(plugin.getDataFolder(), "playerList.yml");
                    FileConfiguration playerListData = YamlConfiguration.loadConfiguration(file);
                    playerListData.createSection("players");
                    playerListData.set("players." + "player " + playerList.size(), playerID.toString());
                    try {
                        playerListData.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Location location = player.getLocation();
                String name = args[1];
                Coordinate coordinate = new Coordinate((int)location.getX(),(int)location.getY(),(int)location.getZ());
                map.get(playerID).put(name, coordinate);
                sender.sendMessage("" + ChatColor.GREEN + name + " successfully added");

                File file = new File(plugin.getDataFolder(), playerID.toString() + "-Coords.yml");
                FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection locations = playerData.getConfigurationSection(("locations"));
                if(locations != null){
                    locations.set(name + ".X", coordinate.getX());
                    locations.set(name + ".Y", coordinate.getY());
                    locations.set(name + ".Z", coordinate.getZ());
                }
                try {
                    playerData.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                sender.sendMessage(ChatColor.RED + "Name of location taken already!");
            }

            return true;
        }
        else if(args[0].equals("show")){
            if(!map.containsKey(playerID)){
                sender.sendMessage(ChatColor.RED + "you don't have any coordinates saved");
                return true;
            }
            for(String locationName: map.get(playerID).keySet()){
                if(locationName.equals(args[1])){
                    int x = map.get(playerID).get(locationName).getX();
                    int y = map.get(playerID).get(locationName).getY();
                    int z = map.get(playerID).get(locationName).getZ();
                    sender.sendMessage(args[1] + ": " + x + " " + y + " " + z + "\n");
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Location does not exist");
            return true;
        }
        else if(args[0].equals("list")){
            if(!map.containsKey(playerID)){
                sender.sendMessage(ChatColor.RED + "you don't have any coordinates saved");
                return true;
            }
            StringBuilder S = new StringBuilder();
            for(String locationName: map.get(playerID).keySet()){
                S.append(locationName).append(", ");
            }
            sender.sendMessage(S.toString());
            if(map.get(playerID).size() == 0){
                sender.sendMessage(ChatColor.RED + "you have no coordinates saved");
            }
            return true;
        }
        else if(args[0].equals("remove")){
            String name = args[1];
            if(map.get(playerID).containsKey(name)){
                map.get(playerID).remove(name);
                sender.sendMessage("" + ChatColor.GREEN + args[1] + " has been removed");

                File file = new File(plugin.getDataFolder(), playerID.toString() + "-Cords.yml");
                YamlConfiguration playerData = YamlConfiguration.loadConfiguration(file);

                ConfigurationSection locations = playerData.getConfigurationSection("keys");

                if(locations != null){
                    locations.set(name, null);
                }

                try {
                    playerData.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                sender.sendMessage( "" + ChatColor.RED + args[1] + " is not in the list");
            }
            return true;
        }

        return true;


    }
    private void printHelp(CommandSender sender){
        sender.sendMessage("/coords [location] -> saves the coordinates where you are standing\n");
        sender.sendMessage("/coords [location] [X] [Y] [Z] -> saves the coordinates of x y z input\n");
        sender.sendMessage("/coords [remove] [location]-> deletes the coordinates of corresponding location\n");
        sender.sendMessage("/coords [show] [location]-> returns the location of [name]");
        sender.sendMessage("/coords [list] returns the list of all locations saved");
    }
    private void makeFile(Player player){

        if(!new File(plugin.getDataFolder(), player.getUniqueId().toString() + "-Coords.yml").exists()){
            File f = new File( plugin.getDataFolder(), player.getUniqueId().toString() + "-Coords.yml");
            FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);

            playerData.createSection("locations");

            try {
                playerData.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
