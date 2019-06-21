package com.example.ruslan.dungeonmaster;

public class Sword extends Item{

    private int damage;


    public Sword(String n, int w,int d) {
        super(n, w);
        damage = d;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    public String toString(){
        return name+" (ATK "+ damage + ") ( W "+weight+")";
    }
}
