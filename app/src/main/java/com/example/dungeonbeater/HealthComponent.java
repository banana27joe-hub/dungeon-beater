package com.example.dungeonbeater;

public class HealthComponent {

    private int maxHp;
    private int currentHp;

    public HealthComponent(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    public void takeDamage(int amount) {
        if (amount <= 0) return;
        currentHp -= amount;
        if (currentHp < 0) {
            currentHp = 0;
        }
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        currentHp += amount;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public float getHpFraction() {
        if (maxHp == 0) return 0f;
        return (float) currentHp / (float) maxHp;
    }

    public void setMaxHp(int newMaxHp) {
        this.maxHp = newMaxHp;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }
}
