package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player extends Entity {

    private static final float SPEED = 300f;

    private final HealthComponent health;
    private Weapon equippedWeapon;
    private float attackCooldownTimer = 0f;

    private final VirtualJoystick joystick;
    private final AttackButton attackButton;

    private final Paint debugPaint = new Paint();

    public Player(float startX, float startY, VirtualJoystick joystick, AttackButton attackButton) {
        super(startX, startY, 48, 48);
        this.health = new HealthComponent(100);
        this.equippedWeapon = new MeleeWeapon();
        this.joystick = joystick;
        this.attackButton = attackButton;

        debugPaint.setColor(Color.CYAN);
    }

    @Override
    public void update(float deltaTime) {
        if (health.isDead()) {
            return;
        }

        float dx = joystick.getDirectionX();
        float dy = joystick.getDirectionY();

        x += dx * SPEED * deltaTime;
        y += dy * SPEED * deltaTime;

        if (attackCooldownTimer > 0f) {
            attackCooldownTimer -= deltaTime;
        }
    }

    public void tryAttack() {
        if (health.isDead()) return;
        if (attackCooldownTimer > 0f) return;

        equippedWeapon.performAttack(this);
        attackCooldownTimer = equippedWeapon.getAttackCooldown();
    }

    public void takeDamage(int amount) {
        health.takeDamage(amount);
    }

    public boolean isDead() {
        return health.isDead();
    }

    public HealthComponent getHealth() {
        return health;
    }

    public void setWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + width, y + height, debugPaint);
    }
}
