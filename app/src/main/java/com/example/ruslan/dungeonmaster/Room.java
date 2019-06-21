package com.example.ruslan.dungeonmaster;

import java.util.ArrayList;
import java.util.List;

public class Room
{
    static final int NO_EXIT = -1;

    static final String NOTHING = "NOTHING";

    private int north;
    private int east;
    private int south;
    private int west;

    Monster monster;

    private String description;

    private List<Item> inventory;

    Room()
    {
        monster = null;

        north = NO_EXIT;
        east = NO_EXIT;
        south = NO_EXIT;
        west = NO_EXIT;

        description = NOTHING;

        inventory = new ArrayList<Item>();
    }

    public void setMonster(Monster mons){
        monster = mons;
    }

    public Monster getMonster(){
        return monster;
    }


    public int getNorth() {
        return north;
    }

    public void setNorth(int north) {
        this.north = north;
    }

    public int getEast() {
        return east;
    }

    public void setEast(int east) {
        this.east = east;
    }

    public int getSouth() {
        return south;
    }

    public void setSouth(int south) {
        this.south = south;
    }

    public int getWest() {
        return west;
    }

    public void setWest(int west) {
        this.west = west;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<Item> inventory) {
        this.inventory = inventory;
    }

    public String toString(){
        String s="";
        for(Item i : inventory){
            s+=i.toString()+"   ";
        }
        return s;
    }

}   //  public class Room
