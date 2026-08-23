package com.example.dungeonbeater;

import android.graphics.Rect;

public class MeleeWeapon extends Weapon {

    private static final int RANGE = 50;

    public MeleeWeapon() {
        super("Basic Sword", Type.MELEE, 10, 0.4f);
    }

    @Override
    public void performAttack(Player owner, Room room) {
        Rect hitbox = buildHitbox(owner);

        for (Enemy enemy : room.getEnemies()) {
            if (enemy.isDead()) continue;
            if (Rect.intersects(hitbox, enemy.getHitbox())) {
                enemy.takeDamage(damage);
            }
        }
    }

    private Rect buildHitbox(Player owner) {
        Rect playerBox = owner.getHitbox();
        Rect expanded = new Rect(playerBox);
        expanded.inset(-RANGE, -RANGE);
        return expanded;
    }
}
