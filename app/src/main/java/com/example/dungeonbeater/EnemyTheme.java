package com.example.dungeonbeater;

import android.graphics.Color;

public enum EnemyTheme {

    ZOMBIE(30, 10, 80f, Color.rgb(80, 140, 60)),
    SKELETON(20, 8, 120f, Color.rgb(210, 210, 190));

    public final int maxHp;
    public final int contactDamage;
    public final float speed;
    public final int color;

    EnemyTheme(int maxHp, int contactDamage, float speed, int color) {
        this.maxHp = maxHp;
        this.contactDamage = contactDamage;
        this.speed = speed;
        this.color = color;
    }
}
