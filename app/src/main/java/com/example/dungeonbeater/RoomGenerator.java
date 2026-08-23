package com.example.dungeonbeater;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoomGenerator {

    private static final Random random = new Random();

    public static Map<String, Room> generateFloor(int targetRoomCount, int screenWidth, int screenHeight) {
        Map<String, Room> rooms = new HashMap<>();

        int col = 0;
        int row = 0;
        rooms.put(key(col, row), new Room(col, row));

        Door.Direction[] directions = Door.Direction.values();

        while (rooms.size() < targetRoomCount) {
            Door.Direction dir = directions[random.nextInt(directions.length)];

            int newCol = col;
            int newRow = row;

            switch (dir) {
                case UP: newRow -= 1; break;
                case DOWN: newRow += 1; break;
                case LEFT: newCol -= 1; break;
                case RIGHT: newCol += 1; break;
            }

            String newKey = key(newCol, newRow);
            if (!rooms.containsKey(newKey)) {
                rooms.put(newKey, new Room(newCol, newRow));
            }

            col = newCol;
            row = newRow;
        }

        for (Room room : rooms.values()) {
            connectIfNeighborExists(rooms, room, Door.Direction.UP);
            connectIfNeighborExists(rooms, room, Door.Direction.DOWN);
            connectIfNeighborExists(rooms, room, Door.Direction.LEFT);
            connectIfNeighborExists(rooms, room, Door.Direction.RIGHT);
        }

        for (Room room : rooms.values()) {
            if (room.getCol() == 0 && room.getRow() == 0) continue;
            spawnEnemiesForRoom(room, screenWidth, screenHeight);
        }

        return rooms;
    }

    private static void spawnEnemiesForRoom(Room room, int screenWidth, int screenHeight) {
        EnemyTheme[] themes = EnemyTheme.values();
        EnemyTheme theme = themes[random.nextInt(themes.length)];

        int enemyCount = 2 + random.nextInt(3);
        int margin = 150;

        for (int i = 0; i < enemyCount; i++) {
            float x = margin + random.nextFloat() * (screenWidth - margin * 2f);
            float y = margin + random.nextFloat() * (screenHeight - margin * 2f);
            room.addEnemy(new Enemy(x, y, theme));
        }
    }

    private static void connectIfNeighborExists(Map<String, Room> rooms, Room room, Door.Direction direction) {
        int col = room.getCol();
        int row = room.getRow();

        int neighborCol = col;
        int neighborRow = row;

        switch (direction) {
            case UP: neighborRow -= 1; break;
            case DOWN: neighborRow += 1; break;
            case LEFT: neighborCol -= 1; break;
            case RIGHT: neighborCol += 1; break;
        }

        String neighborKey = key(neighborCol, neighborRow);
        if (rooms.containsKey(neighborKey) && !room.hasDoor(direction)) {
            room.addDoor(new Door(direction, neighborCol, neighborRow));
        }
    }

    public static String key(int col, int row) {
        return col + "," + row;
    }
}
