package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Room {

    private final int col;
    private final int row;
    private final Map<Door.Direction, Door> doors = new EnumMap<>(Door.Direction.class);
    private final List<Enemy> enemies = new ArrayList<>();

    private final Paint floorPaint = new Paint();
    private final Paint doorPaint = new Paint();

    public Room(int col, int row) {
        this.col = col;
        this.row = row;
        floorPaint.setColor(Color.rgb(40, 40, 45));
        doorPaint.setColor(Color.rgb(90, 90, 100));
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

    public void draw(Canvas canvas, int screenWidth, int screenHeight) {
        canvas.drawRect(0, 0, screenWidth, screenHeight, floorPaint);

        int doorSize = 80;

        if (hasDoor(Door.Direction.UP)) {
            canvas.drawRect(screenWidth / 2f - doorSize / 2f, 0, screenWidth / 2f + doorSize / 2f, 20, doorPaint);
        }
        if (hasDoor(Door.Direction.DOWN)) {
            canvas.drawRect(screenWidth / 2f - doorSize / 2f, screenHeight - 20, screenWidth / 2f + doorSize / 2f, screenHeight, doorPaint);
        }
        if (hasDoor(Door.Direction.LEFT)) {
            canvas.drawRect(0, screenHeight / 2f - doorSize / 2f, 20, screenHeight / 2f + doorSize / 2f, doorPaint);
        }
        if (hasDoor(Door.Direction.RIGHT)) {
            canvas.drawRect(screenWidth - 20, screenHeight / 2f - doorSize / 2f, screenWidth, screenHeight / 2f + doorSize / 2f, doorPaint);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }
}
