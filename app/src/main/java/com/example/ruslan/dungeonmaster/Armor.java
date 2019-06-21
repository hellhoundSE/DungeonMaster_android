package com.example.ruslan.dungeonmaster;

public class Armor extends Item{

    private int protection;

    public Armor(String n, int w,int p) {
        super(n, w);
        protection = p;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public String toString(){
        return name+" (ARMOR "+ protection + ") ( W "+weight+")";
    }
}
