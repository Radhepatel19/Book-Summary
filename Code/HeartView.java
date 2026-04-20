package com.example.booksummary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class HeartView extends View {
    private Paint fillPaint = new Paint();
    private Paint strokePaint = new Paint();
    private boolean isRed = false;
    private OnHeartStateChangeListener heartStateChangeListener;

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setAntiAlias(true);

        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);
        strokePaint.setAntiAlias(true);

        // Set up a click listener
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isRed = !isRed;
                fillPaint.setColor(isRed ? Color.RED : Color.WHITE);
                invalidate(); // Redraw the view
                if (heartStateChangeListener != null) {
                    heartStateChangeListener.onHeartStateChanged(isRed);
                }
            }
        });
    }
    public void setHeartColor(boolean isRed) {
        this.isRed = isRed;
        fillPaint.setColor(isRed ? Color.RED : Color.WHITE);
        invalidate(); // Redraw the view
    }
    public void setOnHeartStateChangeListener(OnHeartStateChangeListener listener) {
        this.heartStateChangeListener = listener;
    }

    public interface OnHeartStateChangeListener {
        void onHeartStateChanged(boolean isRed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path path = new Path();

        float width = getWidth();
        float height = getHeight();

        path.moveTo(width / 2f, height / 4f);
        path.cubicTo(5 * width / 14f, 0f, 0f, height / 15f, width / 28f, 2 * height / 5f);
        path.cubicTo(width / 14f, 2 * height / 3f, 3 * width / 7f, 5 * height / 6f, width / 2f, height);
        path.cubicTo(4 * width / 7f, 5 * height / 6f, 13 * width / 14f, 2 * height / 3f, 27 * width / 28f, 2 * height / 5f);
        path.cubicTo(width, height / 15f, 9 * width / 14f, 0f, width / 2f, height / 4f);
        path.close();

        // Draw the heart fill
        canvas.drawPath(path, fillPaint);

        // Draw the heart stroke
        canvas.drawPath(path, strokePaint);
    }
}