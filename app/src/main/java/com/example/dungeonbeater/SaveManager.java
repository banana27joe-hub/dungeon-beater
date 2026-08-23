package com.example.dungeonbeater;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveManager {

    private static final String PREFS_NAME = "dungeon_beater_save";
    private static final String KEY_COINS = "coins";

    private final SharedPreferences prefs;

    public SaveManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getCoins() {
        return prefs.getInt(KEY_COINS, 0);
    }

    public void addCoins(int amount) {
        setCoins(getCoins() + amount);
    }

    public boolean spendCoins(int amount) {
        int current = getCoins();
        if (current < amount) {
            return false;
        }
        setCoins(current - amount);
        return true;
    }

    private void setCoins(int amount) {
        prefs.edit().putInt(KEY_COINS, amount).apply();
    }
  }
