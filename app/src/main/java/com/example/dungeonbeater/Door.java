package com.example.dungeonbeater;

public class Door {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public Direction opposite() {
            switch (this) {
                case UP: return DOWN;
                case DOWN: return UP;
                case LEFT: return RIGHT;
                case RIGHT: return LEFT;
            }
            return null;
        }
    }

    private final Direction direction;
    private final int targetCol;
    private final int targetRow;
    private boolean locked = false;

    public Door(Direction direction, int targetCol, int targetRow) {
        this.direction = direction;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
