package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Enemy extends Entity {

    private final HealthComponent health;
    private final EnemyTheme theme;
    private final Paint paint = new Paint();

    private float contactCooldownTimer = 0f;
    private static final float CONTACT_COOLDOWN = 0.6f;

    private boolean rewardClaimed = false;

    public Enemy(float x, float y, EnemyTheme theme) {
        super(x, y, 40, 40);
        this.theme = theme;
        this.health = new HealthComponent(theme.maxHp);
        paint.setColor(theme.color);
    }

    public void updateAI(float deltaTime, Player target) {
        if (health.isDead()) return;

        float dx = target.getX() - x;
        float dy = target.getY() - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 1f) {
            x += (dx / dist) * theme.speed * deltaTime;
            y += (dy / dist) * theme.speed * deltaTime;
        }

        if (contactCooldownTimer > 0f) {
            contactCooldownTimer -= deltaTime;
        }

        if (collidesWith(target) && contactCooldownTimer <= 0f) {
            target.takeDamage(theme.contactDamage);
            contactCooldownTimer = CONTACT_COOLDOWN;
        }
    }

    public void takeDamage(int amount) {
        health.takeDamage(amount);
    }

    public boolean isDead() {
        return health.isDead();
    }

    public boolean claimDeathReward() {
        if (isDead() && !rewardClaimed) {
            rewardClaimed = true;
            return true;
        }
        return false;
    }

    public int getScoreValue() {
        return theme.scoreValue;
    }

    public int getCoinValue() {
        return theme.coinValue;
    }

    @Override
    public void update(float deltaTime) {
        // движение обрабатывается в updateAI(), т.к. нужна ссылка на игрока
    }

    @Override
    public void draw(Canvas canvas) {
        if (health.isDead()) return;
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
}
