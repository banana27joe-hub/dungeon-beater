package com.example.dungeonbeater;

public class ShopItem {

    private final String name;
    private final int cost;

    public ShopItem(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }
}
