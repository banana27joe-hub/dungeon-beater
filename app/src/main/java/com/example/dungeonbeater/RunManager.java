package com.example.dungeonbeater;

import java.util.Map;

public class RunManager {

    private static final int ROOMS_PER_FLOOR = 8;

    private final Map<String, Room> rooms;
    private Room currentRoom;

    public RunManager(int screenWidth, int screenHeight) {
        rooms = RoomGenerator.generateFloor(ROOMS_PER_FLOOR, screenWidth, screenHeight);
        currentRoom = rooms.get(RoomGenerator.key(0, 0));
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public boolean tryMoveThroughDoor(Door.Direction direction) {
        Door door = currentRoom.getDoor(direction);
        if (door == null || door.isLocked()) {
            return false;
        }

        Room nextRoom = rooms.get(RoomGenerator.key(door.getTargetCol(), door.getTargetRow()));
        if (nextRoom == null) {
            return false;
        }

        currentRoom = nextRoom;
        return true;
    }
}
