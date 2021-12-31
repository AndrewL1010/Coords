package io.github.AndrewL1010.coords;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;


public class CoordinateList implements CommandExecutor {
    public ArrayList<Coordinate> list;

    @Override
    public boolean onCommand(CommandSender sender, String[] args, Player player){
        if(args.length > 4){
            sender.sendMessage("Incorrect format\n");
            printHelp(sender);
            return true;
        }
        if(args[0].equals("help")){
            printHelp(sender);
            return true;
        }
        if(args.length < 2 && !args[0].equals("show")){
            Location location = player.getLocation();
            String name = args[1];
            Coordinate coordinate = new Coordinate(location, name);
            list.add(coordinate);
            return true;
        }
        if(arg[0].equals("show")){
            for(Coordinate coord: list){
                if(coord.name.equals(args[1])){
                    double x = coord.location.getX();
                    double y = coord.location.getY();
                    double z = coord.location.getZ();
                    sender.sendMessage(x + " " + y + " " + z + "\n");
                    return true;
                }
            }
            sender.sendMessage("Location does not exist");
            return true;
        }
        if(args[0].equals("list")){
            StringBuilder S = new StringBuilder();
            for(Coordinate C: list){
                S.append(C.name).append(" ");
            }
            sender.sendMessage(S.toString());
            return true;
        }

        return false;
    }
    private void printHelp(CommandSender sender){
        sender.sendMessage("/coords [location] -> saves the coordinates where you are standing\n");
        sender.sendMessage("/coords [location] [X] [Y] [Z] -> saves the coordinates of x y z input\n");
        sender.sendMessage("/coords [remove] [location]-> deletes the coordinates of corresponding location\n");
        sender.sendMessage("/coords [show] [location]-> returns the location of [name]");
        sender.sendMessage("/coords [list] returns the list of all locations saved");
    }

}
