package com.example.ruslan.dungeonmaster;

public class Cake extends Item{

    private int heal;

    public Cake(String n, int w,int heal) {
        super(n, w);
        this.heal = heal;

    }

    public int getHeal() {
        return heal;
    }

    public void setHeal(int heal) {
        this.heal = heal;
    }

    public String toString(){
        return name+" (HEAL "+ heal + ") ( W "+weight+")";
    }
}
