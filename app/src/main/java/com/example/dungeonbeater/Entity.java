package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class Entity {

    protected float x;
    protected float y;
    protected int width;
    protected int height;

    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(float deltaTime);

    public abstract void draw(Canvas canvas);

    public Rect getHitbox() {
        return new Rect(
                (int) x,
                (int) y,
                (int) x + width,
                (int) y + height
        );
    }

    public boolean collidesWith(Entity other) {
        return getHitbox().intersect(other.getHitbox());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
