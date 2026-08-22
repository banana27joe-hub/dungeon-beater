package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

public class AttackButton {

    private final float centerX;
    private final float centerY;
    private final float radius;

    private boolean pressed = false;
    private int pointerId = -1;

    private final Paint paint = new Paint();

    public AttackButton(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        paint.setColor(Color.argb(160, 220, 60, 60));
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
                if (!pressed && isInside(touchX, touchY)) {
                    pressed = true;
                    pointerId = id;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (pressed && id == pointerId) {
                    pressed = false;
                    pointerId = -1;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                pointerId = -1;
                break;
        }
        return false;
    }

    private boolean isInside(float touchX, float touchY) {
        float dx = touchX - centerX;
        float dy = touchY - centerY;
        return Math.sqrt(dx * dx + dy * dy) <= radius;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
}
