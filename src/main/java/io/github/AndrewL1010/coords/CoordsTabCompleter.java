package io.github.AndrewL1010.coords;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class CoordsTabCompleter implements TabCompleter {

    private final CoordinatePlugin plugin;

    public CoordsTabCompleter(CoordinatePlugin plugin) {
        this.plugin = plugin;

    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {


        if (args.length == 1) {
            return Arrays.asList("help", "save", "set", "list", "get", "remove");
        }
        if (args.length == 2) {

            if (args[0].equals("get") || args[0].equals("remove")) {
                ConfigurationSection locations = setupData((Player) commandSender);
                if (locations != null) {
                    Map<String, Object> map = locations.getValues(false);
                    return new ArrayList<String>(map.keySet());
                }
            }
        }


        return new ArrayList<String>();
    }

    public ConfigurationSection setupData(Player player) {
        UUID playerID = player.getUniqueId();
        File file = new File(plugin.getDataFolder(), playerID + ".yml");
        FileConfiguration playerData = YamlConfiguration.loadConfiguration(file);
        return playerData.getConfigurationSection("locations");
    }
}
