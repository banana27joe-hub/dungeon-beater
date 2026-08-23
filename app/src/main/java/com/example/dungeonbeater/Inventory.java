package com.example.dungeonbeater;

import java.util.HashMap;
import java.util.Map;

// Простой инвентарь по ключам ("health_potion" и т.д.) — пока используется только для аптечек,
// но легко расширяется под новые типы предметов позже.
public class Inventory {

    private final Map<String, Integer> items = new HashMap<>();

    public void add(String key, int amount) {
        items.put(key, getCount(key) + amount);
    }

    public int getCount(String key) {
        Integer value = items.get(key);
        return value == null ? 0 : value;
    }

    public boolean consume(String key, int amount) {
        int current = getCount(key);
        if (current < amount) {
            return false;
        }
        items.put(key, current - amount);
        return true;
    }
            }
