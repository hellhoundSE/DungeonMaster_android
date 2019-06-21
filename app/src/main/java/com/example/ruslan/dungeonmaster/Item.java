package com.example.ruslan.dungeonmaster;

class Item {

    protected String name;
    protected int weight;

    public Item(String n,int w){
        weight = w;
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toString(){
        return name+" ("+weight+")";
    }
}
