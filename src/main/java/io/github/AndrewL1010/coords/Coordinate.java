package io.github.AndrewL1010.coords;

import org.bukkit.Location;

public class Coordinate{

    public int x;
    public int y;
    public int z;

    public Coordinate(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;

    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getZ(){
        return this.z;
    }
}
