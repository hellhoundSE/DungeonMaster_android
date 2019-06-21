package com.example.ruslan.dungeonmaster;

class Monster {

    private int hp;
    private int attack;
    private int exp;

    public Monster(int hp,int attk,int xp){
        this.hp = hp;
        attack = attk;
        exp = xp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }
}
