package com.example.dungeonbeater;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private long lastFrameTime;

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
        if (player != null) {
            player.update(deltaTime);
        }
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.rgb(30, 30, 30));

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
