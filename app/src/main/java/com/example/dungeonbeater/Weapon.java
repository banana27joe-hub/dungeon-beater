package com.example.dungeonbeater;

public abstract class Weapon {

    public enum Type {
        MELEE,
        RANGED
    }

    protected String name;
    protected Type type;
    protected int damage;
    protected float attackCooldown;

    public Weapon(String name, Type type, int damage, float attackCooldown) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.attackCooldown = attackCooldown;
    }

    public abstract void performAttack(Player owner, Room room);

    public Type getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public String getName() {
        return name;
    }
}
