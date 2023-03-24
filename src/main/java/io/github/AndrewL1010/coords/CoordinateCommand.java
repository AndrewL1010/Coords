package io.github.AndrewL1010.coords;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


public class CoordinateCommand implements CommandExecutor {


    private final CoordinatePlugin plugin;

    public CoordinateCommand(CoordinatePlugin plugin) {
        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        if (args[0].equals("help")) {
            return false;
        }

        UUID playerID = player.getUniqueId();
        File file = new File(plugin.getDataFolder(), playerID + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection locations = playerData.getConfigurationSection("locations");
        if (!file.exists()) {
            playerData.createSection("locations");
            try {
                playerData.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (locations != null) {
            Map<String, Object> map = locations.getValues(false);
            if (args.length == 2 && args[0].equals("add")) {

                if (!map.containsKey(args[1])) {
                    String name = args[1];
                    try {
                        Location location = player.getLocation();
                        Coordinate coordinate = new Coordinate((int) location.getX(), (int) location.getY(), (int) location.getZ());
                        locations.set(name + ".X", coordinate.getX());
                        locations.set(name + ".Y", coordinate.getY());
                        locations.set(name + ".Z", coordinate.getZ());
                        playerData.save(file);
                        sender.sendMessage("" + ChatColor.GREEN + name + "Successfully added");

                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage("" + ChatColor.RED + name + "Add attempt failed");
                    }

                } else {
                    sender.sendMessage(ChatColor.RED + "Name of location taken already!");
                }
                return true;
            } else if (args[0].equals("set")) {
                if (args.length != 5) {
                    sender.sendMessage(ChatColor.RED + "Use format: /coords add [name] [x] [y] [z]");
                    return true;
                }
                if (!map.containsKey(args[1])) {
                    String name = args[1];

                    try {
                        int x = Integer.parseInt(args[2]);
                        int y = Integer.parseInt(args[3]);
                        int z = Integer.parseInt(args[4]);
                        Coordinate coordinate = new Coordinate(x, y, z);
                        locations.set(name + ".X", coordinate.getX());
                        locations.set(name + ".Y", coordinate.getY());
                        locations.set(name + ".Z", coordinate.getZ());
                        playerData.save(file);
                        sender.sendMessage("" + ChatColor.GREEN + name + "Successfully added");
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage("" + ChatColor.RED + name + "Add attempt failed");
                    } catch (NumberFormatException e) {
                        sender.sendMessage("" + ChatColor.RED + "Incorrect number format!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Name of location taken already!");
                }
                return true;
            } else if (args[0].equals("get") && args.length == 2) {
                MemorySection coord = (MemorySection) map.get(args[1]);
                if (map.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You don't have any coordinates saved");
                    return true;
                } else if (!map.containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "You don't have that location saved");
                    return true;
                }


                String strX = coord.getString("X");
                String strY = coord.getString("Y");
                String strZ = coord.getString("Z");
                if (strX != null && strY != null && strZ != null) {
                    int x = Integer.parseInt(strX);
                    int y = Integer.parseInt(strY);
                    int z = Integer.parseInt(strZ);
                    sender.sendMessage("" + ChatColor.GOLD + args[1] + ": " + ChatColor.WHITE + x + " " + y + " " + z + "\n");
                    return true;
                }


                sender.sendMessage(ChatColor.RED + "Location does not exist");
                return true;
            } else if (args[0].equals("list")) {

                if (map.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "You don't have any coordinates saved");
                    return true;
                }

                for (String locationName : map.keySet()) {
                    MemorySection coord = (MemorySection) map.get(locationName);
                    String x = coord.getString("X");
                    String y = coord.getString("Y");
                    String z = coord.getString("Z");
                    sender.sendMessage("" + ChatColor.GOLD + locationName + ": " + ChatColor.WHITE + x + "/" + y + "/" + z);
                }
                return true;
            } else if (args[0].equals("remove")) {
                String name;
                StringBuilder successfullMsg = new StringBuilder();
                StringBuilder unsuccessfullMsg = new StringBuilder();
                successfullMsg.append("");
                successfullMsg.append(ChatColor.GREEN);
                unsuccessfullMsg.append("");
                unsuccessfullMsg.append(ChatColor.RED);
                boolean unsuccessfull = false;
                boolean successfull = false;

                for (int i = 1; i < args.length; i++) {
                    name = args[i];
                    if (map.containsKey(name)) {
                        try {
                            locations.set(name, null);
                            playerData.save(file);
                            successfullMsg.append(name);
                            successfullMsg.append(", ");
                            successfull = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage("" + ChatColor.RED + args[1] + "Removal failed");
                        }
                    } else {
                        unsuccessfull = true;
                        unsuccessfullMsg.append(name);
                        unsuccessfullMsg.append(", ");
                    }
                }
                if (successfull) {
                    successfullMsg.deleteCharAt(successfullMsg.length() - 1);
                    successfullMsg.append(" has been removed");
                    sender.sendMessage(successfullMsg.toString());
                }
                if (unsuccessfull) {
                    unsuccessfullMsg.deleteCharAt((unsuccessfullMsg.length() - 1));
                    unsuccessfullMsg.append(" does not exist");
                    sender.sendMessage(unsuccessfullMsg.toString());

                }


                return true;
            } else if (args[0].equals("removeall")) {
                if (map.isEmpty()) {
                    sender.sendMessage("" + ChatColor.RED + args[1] + "You don't have any coords to remove");
                }
                for (String locationName : map.keySet()) {
                    locations.set(locationName, null);
                }
                try {
                    playerData.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage("" + ChatColor.GREEN + "Successfully removed all coords");
            }

        }

        return true;


    }


}
