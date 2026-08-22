package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class VirtualJoystick {

    private final float baseX;
    private final float baseY;
    private final float baseRadius;
    private final float stickRadius;

    private float stickX;
    private float stickY;

    private boolean active = false;
    private int pointerId = -1;

    private final Paint basePaint = new Paint();
    private final Paint stickPaint = new Paint();

    public VirtualJoystick(float baseX, float baseY, float baseRadius, float stickRadius) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseRadius = baseRadius;
        this.stickRadius = stickRadius;
        this.stickX = baseX;
        this.stickY = baseY;

        basePaint.setColor(Color.argb(120, 255, 255, 255));
        stickPaint.setColor(Color.argb(200, 255, 255, 255));
    }

    public boolean handleTouch(MotionEvent event) {
        int action = event.getActionMasked();
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        float touchX = event.getX(index);
        float touchY = event.getY(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!active && distance(touchX, touchY, baseX, baseY) <= baseRadius * 1.5f) {
                    active = true;
                    pointerId = id;
                    updateStick(touchX, touchY);
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (active) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        if (event.getPointerId(i) == pointerId) {
                            updateStick(event.getX(i), event.getY(i));
                            return true;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (active && id == pointerId) {
                    reset();
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
        }
        return false;
    }

    private void updateStick(float touchX, float touchY) {
        float dx = touchX - baseX;
        float dy = touchY - baseY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist <= baseRadius) {
            stickX = touchX;
            stickY = touchY;
        } else {
            float ratio = baseRadius / dist;
            stickX = baseX + dx * ratio;
            stickY = baseY + dy * ratio;
        }
    }

    private void reset() {
        active = false;
        pointerId = -1;
        stickX = baseX;
        stickY = baseY;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public float getDirectionX() {
        if (!active) return 0f;
        return (stickX - baseX) / baseRadius;
    }

    public float getDirectionY() {
        if (!active) return 0f;
        return (stickY - baseY) / baseRadius;
    }

    public boolean isActive() {
        return active;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(baseX, baseY, baseRadius, basePaint);
        canvas.drawCircle(stickX, stickY, stickRadius, stickPaint);
    }
}
