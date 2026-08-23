package com.example.dungeonbeater;

import android.graphics.Color;

public enum EnemyTheme {

    ZOMBIE(30, 10, 80f, Color.rgb(80, 140, 60), 15, 10),
    SKELETON(20, 8, 120f, Color.rgb(210, 210, 190), 10, 5);

    public final int maxHp;
    public final int contactDamage;
    public final float speed;
    public final int color;
    public final int scoreValue;
    public final int coinValue;

    EnemyTheme(int maxHp, int contactDamage, float speed, int color, int scoreValue, int coinValue) {
        this.maxHp = maxHp;
        this.contactDamage = contactDamage;
        this.speed = speed;
        this.color = color;
        this.scoreValue = scoreValue;
        this.coinValue = coinValue;
    }
}
