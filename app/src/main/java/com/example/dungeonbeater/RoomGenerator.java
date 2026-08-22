package com.example.dungeonbeater;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoomGenerator {

    public static Map<String, Room> generateFloor(int targetRoomCount) {
        Map<String, Room> rooms = new HashMap<>();
        Random random = new Random();

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

        return rooms;
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
