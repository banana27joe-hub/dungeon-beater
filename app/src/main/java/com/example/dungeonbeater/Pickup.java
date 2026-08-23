package com.example.dungeonbeater;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Pickup extends Entity {

    public enum Type {
        HEALTH_POTION
    }

    private final Type type;
    private final Paint paint = new Paint();

    public Pickup(float x, float y, Type type) {
        super(x, y, 28, 28);
        this.type = type;
        paint.setColor(Color.rgb(220, 60, 90)); // розовый — заглушка под спрайт аптечки
    }

    public Type getType() {
        return type;
    }

    @Override
    public void update(float deltaTime) {
        // лежит на месте
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint);
    }
}
