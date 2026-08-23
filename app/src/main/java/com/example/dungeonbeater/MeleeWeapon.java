package com.example.dungeonbeater;

import android.graphics.Rect;

public class MeleeWeapon extends Weapon {

    private static final int RANGE = 60;

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
        float fx = owner.getFacingX();
        float fy = owner.getFacingY();

        int centerX = playerBox.centerX();
        int centerY = playerBox.centerY();

        int offsetX = (int) (fx * RANGE);
        int offsetY = (int) (fy * RANGE);

        int left = centerX + offsetX - RANGE / 2;
        int top = centerY + offsetY - RANGE / 2;
        int right = left + RANGE;
        int bottom = top + RANGE;

        return new Rect(left, top, right, bottom);
    }
}
