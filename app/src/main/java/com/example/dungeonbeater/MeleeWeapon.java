package com.example.dungeonbeater;

import android.util.Log;

public class MeleeWeapon extends Weapon {

    public MeleeWeapon() {
        super("Basic Sword", Type.MELEE, 10, 0.4f);
    }

    @Override
    public void performAttack(Player owner) {
        Log.d("MeleeWeapon", "Attack triggered, damage=" + damage);
    }
}
