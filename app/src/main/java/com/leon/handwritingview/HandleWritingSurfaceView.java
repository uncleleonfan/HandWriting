package com.leon.handwritingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Leon on 2017/6/9.
 */

public class HandleWritingSurfaceView extends SurfaceView {

    private SurfaceHolder mHolder;

    private boolean mDrawing = false;

    private Path mPath;
    private Paint mPaint;

    private static final int FRAME_RATE = 62;// 1/16 millis

    public HandleWritingSurfaceView(Context context) {
        this(context, null);
    }

    public HandleWritingSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(mCallback);
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mDrawing = true;
            new Thread(new DrawTask()).start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mDrawing = false;
        }
    };

    private class DrawTask implements Runnable {

        @Override
        public void run() {
            while (mDrawing) {
                long start = System.currentTimeMillis();
                onDraw();
                long end = System.currentTimeMillis();
                long timeCost = end - start;

                if (timeCost < FRAME_RATE) {
                    long sleepDuration = FRAME_RATE - timeCost;
                    try {
                        Thread.sleep(sleepDuration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        private void onDraw() {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);
                canvas.drawPath(mPath, mPaint);
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                break;
        }
        return true;
    }
}
