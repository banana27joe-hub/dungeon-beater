package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Room {

    public static final int DOOR_SIZE = 80;

    private final int col;
    private final int row;
    private final Map<Door.Direction, Door> doors = new EnumMap<>(Door.Direction.class);
    private final List<Enemy> enemies = new ArrayList<>();

    private final Paint floorPaint = new Paint();
    private final Paint doorOpenPaint = new Paint();
    private final Paint doorLockedPaint = new Paint();

    public Room(int col, int row) {
        this.col = col;
        this.row = row;
        floorPaint.setColor(Color.rgb(40, 40, 45));
        doorOpenPaint.setColor(Color.rgb(90, 90, 100));
        doorLockedPaint.setColor(Color.rgb(140, 50, 50));
    }

    public void addDoor(Door door) {
        doors.put(door.getDirection(), door);
    }

    public Door getDoor(Door.Direction direction) {
        return doors.get(direction);
    }

    public boolean hasDoor(Door.Direction direction) {
        return doors.containsKey(direction);
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isCleared() {
        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                return false;
            }
        }
        return true;
    }

    public void updateEnemies(float deltaTime, Player player) {
        for (Enemy enemy : enemies) {
            enemy.updateAI(deltaTime, player);
        }
    }

    public void resolveCollisions(Player player) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e1 = enemies.get(i);
            if (e1.isDead()) continue;

            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy e2 = enemies.get(j);
                if (e2.isDead()) continue;
                separate(e1, e2);
            }

            separate(e1, player);
        }
    }

    private void separate(Entity a, Entity b) {
        Rect ra = a.getHitbox();
        Rect rb = b.getHitbox();

        float ax = ra.centerX();
        float ay = ra.centerY();
        float bx = rb.centerX();
        float by = rb.centerY();

        float radiusA = ra.width() / 2f;
        float radiusB = rb.width() / 2f;
        float minDist = radiusA + radiusB;

        float dx = bx - ax;
        float dy = by - ay;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist >= minDist) return;

        float nx, ny;
        if (dist < 0.0001f) {
            nx = 1f;
            ny = 0f;
            dist = 0.0001f;
        } else {
            nx = dx / dist;
            ny = dy / dist;
        }

        float overlap = minDist - dist;
        float half = overlap / 2f;

        a.setPosition(a.getX() - nx * half, a.getY() - ny * half);
        b.setPosition(b.getX() + nx * half, b.getY() + ny * half);
    }

    public void draw(Canvas canvas, int screenWidth, int screenHeight) {
        canvas.drawRect(0, 0, screenWidth, screenHeight, floorPaint);

        Paint doorPaint = isCleared() ? doorOpenPaint : doorLockedPaint;

        if (hasDoor(Door.Direction.UP)) {
            canvas.drawRect(screenWidth / 2f - DOOR_SIZE / 2f, 0, screenWidth / 2f + DOOR_SIZE / 2f, 20, doorPaint);
        }
        if (hasDoor(Door.Direction.DOWN)) {
            canvas.drawRect(screenWidth / 2f - DOOR_SIZE / 2f, screenHeight - 20, screenWidth / 2f + DOOR_SIZE / 2f, screenHeight, doorPaint);
        }
        if (hasDoor(Door.Direction.LEFT)) {
            canvas.drawRect(0, screenHeight / 2f - DOOR_SIZE / 2f, 20, screenHeight / 2f + DOOR_SIZE / 2f, doorPaint);
        }
        if (hasDoor(Door.Direction.RIGHT)) {
            canvas.drawRect(screenWidth - 20, screenHeight / 2f - DOOR_SIZE / 2f, screenWidth, screenHeight / 2f + DOOR_SIZE / 2f, doorPaint);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }
}
