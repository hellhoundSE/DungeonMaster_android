package com.example.ruslan.dungeonmaster;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int level;
    private int exp;

    private int space;
    private int filled;

    private int maxHp;
    private int hp;

    private int attack;

    private Sword sword;
    private Armor armor;

    private int currentRoom;
    private List<Item> items;

    public Player(int sp,int mhp,int atk){
        attack = atk;
        maxHp = mhp;
        hp = mhp;
        filled = 0;
        space = sp;
        items = new ArrayList<Item>();
        currentRoom = 0;
        level = 0;
        exp = 0;
    }

    public Player(){
        items = new ArrayList<Item>();
    }

    public Sword getSword() {
        return sword;
    }

    public void setSword(Sword sword) {
        this.sword = sword;
        attack += sword.getDamage();

    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
        maxHp += armor.getProtection();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getFilled() {
        return filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(int currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Item> getItems(){
        return items;
    }


    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void gettingExperience(int xp) {
        exp +=xp;
        if(exp >= 10+level*2){
            levelUp();
            exp = exp - 10+level*2;
        }
    }

    private void levelUp() {
        attack+=1;
        maxHp += 2;
        hp = maxHp;
        Log.w("LEVEL","LEVELUP");
    }

    public void use(Item item) {
        if(item.getClass() == Sword.class){
            if(sword == null){
                sword = (Sword)item;
                items.remove(item);
                attack += sword.getDamage();
                filled -= sword.getWeight();
            }else{
                items.add(sword);
                attack -= sword.getDamage();
                filled += sword.getWeight();
                sword = (Sword)item;
                attack += sword.getDamage();
                filled -= sword.getWeight();
                items.remove(item);
            }
        }
        if(item.getClass() == Armor.class){
            if(armor == null){
                armor = (Armor)item;
                items.remove(item);
                maxHp += armor.getProtection();
                filled -= armor.getWeight();
            }else{
                items.add(armor);
                maxHp -= armor.getProtection();
                filled += armor.getWeight();
                armor = (Armor)item;
                maxHp += armor.getProtection();
                filled -= armor.getWeight();
                items.remove(item);

            }
        }
        if(hp > maxHp){
            hp = maxHp;
        }
        if(item.getClass() == Cake.class){
            hp += ((Cake)item).getHeal();
            if(hp > maxHp)
                hp = maxHp;
            items.remove(item);
            filled -= item.getWeight();
        }

    }

    public void calculateWeight() {
        filled = 0;

        for(Item i : items){
            filled += i.getWeight();
        }
    }
}
