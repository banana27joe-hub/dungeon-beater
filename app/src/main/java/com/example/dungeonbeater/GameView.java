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

    private GameState state = GameState.MENU;

    private long lastFrameTime;

    private static final int PLAYER_SIZE = 48;
    private static final int REPOSITION_MARGIN = 10;

    private final Paint hpBarBg = new Paint();
    private final Paint hpBarFg = new Paint();
    private final Paint titlePaint = new Paint();
    private final Paint subtitlePaint = new Paint();
    private final Paint menuBgPaint = new Paint();

    public GameView(Context context) {
        super(context);
        holder = getHolder();

        hpBarBg.setColor(Color.rgb(60, 20, 20));
        hpBarFg.setColor(Color.rgb(200, 40, 40));

        menuBgPaint.setColor(Color.rgb(20, 20, 24));

        titlePaint.setColor(Color.rgb(230, 230, 235));
        titlePaint.setTextSize(90f);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);

        subtitlePaint.setColor(Color.rgb(160, 160, 170));
        subtitlePaint.setTextSize(45f);
        subtitlePaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initControlsIfNeeded() {
        if (joystick != null) return;

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        joystick = new VirtualJoystick(180, screenHeight - 180, 120, 60);
        attackButton = new AttackButton(screenWidth - 180, screenHeight - 180, 90);
    }

    private void startRun() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        player = new Player(screenWidth / 2f, screenHeight / 2f, joystick, attackButton);
        runManager = new RunManager(screenWidth, screenHeight);
        state = GameState.RUNNING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (state == GameState.MENU) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                startRun();
            }
            return true;
        }

        if (state == GameState.GAME_OVER) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                state = GameState.MENU;
            }
            return true;
        }

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

            initControlsIfNeeded();

            long now = System.nanoTime();
            float deltaTime = (now - lastFrameTime) / 1_000_000_000f;
            lastFrameTime = now;

            update(deltaTime);
            draw();
        }
    }

    private void update(float deltaTime) {
        if (state != GameState.RUNNING) return;
        if (player == null || runManager == null) return;

        player.update(deltaTime);
        runManager.getCurrentRoom().updateEnemies(deltaTime, player);
        handleWallsAndDoors();

        if (player.isDead()) {
            state = GameState.GAME_OVER;
        }
    }

    private void handleWallsAndDoors() {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        Room room = runManager.getCurrentRoom();

        float px = player.getX();
        float py = player.getY();

        if (px <= 0) {
            if (room.hasDoor(Door.Direction.LEFT) && isWithinDoorway(py, screenHeight)
                    && runManager.tryMoveThroughDoor(Door.Direction.LEFT)) {
                repositionPlayerAfterTransition(Door.Direction.LEFT, screenWidth, screenHeight);
            } else {
                player.setPosition(0, py);
            }
        } else if (px + PLAYER_SIZE >= screenWidth) {
            if (room.hasDoor(Door.Direction.RIGHT) && isWithinDoorway(py, screenHeight)
                    && runManager.tryMoveThroughDoor(Door.Direction.RIGHT)) {
                repositionPlayerAfterTransition(Door.Direction.RIGHT, screenWidth, screenHeight);
            } else {
                player.setPosition(screenWidth - PLAYER_SIZE, py);
            }
        }

        px = player.getX();
        py = player.getY();
        room = runManager.getCurrentRoom();

        if (py <= 0) {
            if (room.hasDoor(Door.Direction.UP) && isWithinDoorway(px, screenWidth)
                    && runManager.tryMoveThroughDoor(Door.Direction.UP)) {
                repositionPlayerAfterTransition(Door.Direction.UP, screenWidth, screenHeight);
            } else {
                player.setPosition(px, 0);
            }
        } else if (py + PLAYER_SIZE >= screenHeight) {
            if (room.hasDoor(Door.Direction.DOWN) && isWithinDoorway(px, screenWidth)
                    && runManager.tryMoveThroughDoor(Door.Direction.DOWN)) {
                repositionPlayerAfterTransition(Door.Direction.DOWN, screenWidth, screenHeight);
            } else {
                player.setPosition(px, screenHeight - PLAYER_SIZE);
            }
        }
    }

    private boolean isWithinDoorway(float coord, int totalSize) {
        float center = coord + PLAYER_SIZE / 2f;
        float doorCenter = totalSize / 2f;
        return Math.abs(center - doorCenter) <= Room.DOOR_SIZE / 2f;
    }

    private void repositionPlayerAfterTransition(Door.Direction directionEntered, int screenWidth, int screenHeight) {
        switch (directionEntered) {
            case LEFT:
                player.setPosition(screenWidth - REPOSITION_MARGIN - PLAYER_SIZE, player.getY());
                break;
            case RIGHT:
                player.setPosition(REPOSITION_MARGIN, player.getY());
                break;
            case UP:
                player.setPosition(player.getX(), screenHeight - REPOSITION_MARGIN - PLAYER_SIZE);
                break;
            case DOWN:
                player.setPosition(player.getX(), REPOSITION_MARGIN);
                break;
        }
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        switch (state) {
            case MENU:
                drawMenu(canvas);
                break;
            case RUNNING:
                drawRunning(canvas);
                break;
            case GAME_OVER:
                drawRunning(canvas);
                drawGameOverOverlay(canvas);
                break;
        }

        holder.unlockCanvasAndPost(canvas);
    }

    private void drawMenu(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), menuBgPaint);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText("DUNGEON BEATER", centerX, centerY - 20, titlePaint);
        canvas.drawText("Нажми, чтобы начать", centerX, centerY + 60, subtitlePaint);
    }

    private void drawRunning(Canvas canvas) {
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
    }

    private void drawGameOverOverlay(Canvas canvas) {
        Paint overlay = new Paint();
        overlay.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlay);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText("ТЫ ПОГИБ", centerX, centerY - 20, titlePaint);
        canvas.drawText("Нажми, чтобы вернуться в меню", centerX, centerY + 60, subtitlePaint);
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
