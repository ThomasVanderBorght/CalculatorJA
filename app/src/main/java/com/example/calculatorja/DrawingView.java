package com.example.calculatorja;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private Path path;
    private Paint paint;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            path.moveTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            path.lineTo(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // No action needed here
        }

        invalidate();
        return true;
    }

    public Path getPath() {
        return path;
    }

    public void clear() {
        path.reset();
        invalidate();
    }
}
