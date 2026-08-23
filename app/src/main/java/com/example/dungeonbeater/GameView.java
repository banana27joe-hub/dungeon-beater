package com.example.dungeonbeater;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private final Paint hpBarBg = new Paint();
    private final Paint hpBarFg = new Paint();

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        hpBarBg.setColor(Color.rgb(60, 20, 20));
        hpBarFg.setColor(Color.rgb(200, 40, 40));
    }

    private void initEntitiesIfNeeded() {
        if (player != null) return;

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        joystick = new VirtualJoystick(180, screenHeight - 180, 120, 60);
        attackButton = new AttackButton(screenWidth - 180, screenHeight - 180, 90);
        player = new Player(screenWidth / 2f, screenHeight / 2f, joystick, attackButton);
        runManager = new RunManager(screenWidth, screenHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (joystick != null) {
            joystick.handleTouch(event);
        }
        if (attackButton != null) {
            boolean attackTriggered = attackButton.handleTouch(event);
            if (attackTriggered && player != null && runManager != null) {
                player.tryAttack(runManager.getCurrentRoom());
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
        if (player == null || runManager == null) return;

        player.update(deltaTime);
        runManager.getCurrentRoom().updateEnemies(deltaTime, player);
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
            drawHealthBar(canvas);
        }
        if (joystick != null) {
            joystick.draw(canvas);
        }
        if (attackButton != null) {
            attackButton.draw(canvas);
        }

        holder.unlockCanvasAndPost(canvas);
    }

    private void drawHealthBar(Canvas canvas) {
        int barWidth = 300;
        int barHeight = 30;
        int left = 40;
        int top = 40;

        canvas.drawRect(left, top, left + barWidth, top + barHeight, hpBarBg);

        float fraction = player.getHealth().getHpFraction();
        canvas.drawRect(left, top, left + barWidth * fraction, top + barHeight, hpBarFg);
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
