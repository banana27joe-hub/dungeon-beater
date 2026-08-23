package com.example.dungeonbeater;

import java.util.Map;

public class RunManager {

    private static final int ROOMS_PER_FLOOR = 8;

    private final Map<String, Room> rooms;
    private Room currentRoom;

    private int score = 0;
    private int coins = 0;

    public RunManager(int screenWidth, int screenHeight) {
        rooms = RoomGenerator.generateFloor(ROOMS_PER_FLOOR, screenWidth, screenHeight);
        currentRoom = rooms.get(RoomGenerator.key(0, 0));
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public boolean tryMoveThroughDoor(Door.Direction direction) {
        if (!currentRoom.isCleared()) {
            return false;
        }

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

    public void addScore(int amount) {
        score += amount;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public int getScore() {
        return score;
    }

    public int getCoins() {
        return coins;
    }
}
