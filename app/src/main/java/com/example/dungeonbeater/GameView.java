package com.example.dungeonbeater;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private volatile boolean running = false;

    private final SurfaceHolder holder;

    private VirtualJoystick joystick;
    private AttackButton attackButton;
    private Player player;
    private RunManager runManager;

    private long lastFrameTime;

    private static final int EDGE_MARGIN = 10;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
    }

    private void initEntitiesIfNeeded() {
        if (player != null) return;

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        joystick = new VirtualJoystick(180, screenHeight - 180, 120, 60);
        attackButton = new AttackButton(screenWidth - 180, screenHeight - 180, 90);
        player = new Player(screenWidth / 2f, screenHeight / 2f, joystick, attackButton);
        runManager = new RunManager();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (joystick != null) {
            joystick.handleTouch(event);
        }
        if (attackButton != null) {
            boolean attackTriggered = attackButton.handleTouch(event);
            if (attackTriggered && player != null) {
                player.tryAttack();
            }
        }
        return true;
    }

    @Override
    public void run() {
        lastFrameTime = System.nanoTime();

        while (running) {
            if (!holder.getSurface().isValid()) {
                continue;
            }

            initEntitiesIfNeeded();

            long now = System.nanoTime();
            float deltaTime = (now - lastFrameTime) / 1_000_000_000f;
            lastFrameTime = now;

            update(deltaTime);
            draw();
        }
    }

    private void update(float deltaTime) {
        if (player == null) return;

        player.update(deltaTime);
        checkRoomTransition();
    }

    private void checkRoomTransition() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        float px = player.getX();
        float py = player.getY();

        Door.Direction direction = null;

        if (px <= EDGE_MARGIN) {
            direction = Door.Direction.LEFT;
        } else if (px + 48 >= screenWidth - EDGE_MARGIN) {
            direction = Door.Direction.RIGHT;
        } else if (py <= EDGE_MARGIN) {
            direction = Door.Direction.UP;
        } else if (py + 48 >= screenHeight - EDGE_MARGIN) {
            direction = Door.Direction.DOWN;
        }

        if (direction == null) return;

        boolean moved = runManager.tryMoveThroughDoor(direction);
        if (moved) {
            repositionPlayerAfterTransition(direction, screenWidth, screenHeight);
        }
    }

    private void repositionPlayerAfterTransition(Door.Direction directionEntered, int screenWidth, int screenHeight) {
        switch (directionEntered) {
            case LEFT:
                player.setPosition(screenWidth - EDGE_MARGIN - 48, player.getY());
                break;
            case RIGHT:
                player.setPosition(EDGE_MARGIN, player.getY());
                break;
            case UP:
                player.setPosition(player.getX(), screenHeight - EDGE_MARGIN - 48);
                break;
            case DOWN:
                player.setPosition(player.getX(), EDGE_MARGIN);
                break;
        }
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        if (runManager != null) {
            runManager.getCurrentRoom().draw(canvas, getWidth(), getHeight());
        }

        if (player != null) {
            player.draw(canvas);
        }
        if (joystick != null) {
            joystick.draw(canvas);
        }
        if (attackButton != null) {
            attackButton.draw(canvas);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
