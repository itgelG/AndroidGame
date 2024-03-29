package com.android.cy.androidmazegame.GamePad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.cy.androidmazegame.GameTimer.GameTimer;
import com.android.cy.androidmazegame.GameTimer.GameTimerUpdateCallback;
import com.android.cy.androidmazegame.Utils.Vector2D;

import java.util.Date;

/**
 * Created by ItgelG on 2022/2/16.
 */
public class GamePadView extends View {

    private Paint centerPaint, keyUpPaint, keyDownPaint, bgPaint, textPaint;
    private Vector2D centerPos, leftPos, rightPos, upPos, downPos, textPos;
    private float centerRadius, gamePadRadius;
    private float hWidth, hHeight;
    private boolean[] keyStatus = {false, false, false, false};

    private int pointerID = -1;
    private Vector2D touchDown = new Vector2D();

    private GamePadMoveCallback moveCallback;

    public boolean isFinish = false;
    private GameTimer mGameTimer = new GameTimer();
    public Date startDate;

    public GamePadView(Context context) {
        super(context);
        startDate = new Date();
        // Буцах дуудлагыг шинэчлэх, цаг хэмжигч эхлэх үед тоглоомын харагдах байдлыг шинэчлэх болно
        mGameTimer.setTimerUpdateCallback(() -> {
            if (!isFinish) {
                invalidate();
            }
        });

        //
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(Color.RED);
        centerPaint.setAlpha(64);
        centerPaint.setStrokeWidth(1);
        centerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        keyUpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        keyUpPaint.setColor(Color.RED);
        keyUpPaint.setAlpha(160);
        keyUpPaint.setStrokeWidth(1);
        keyUpPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        keyDownPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        keyDownPaint.setColor(Color.RED);
        keyUpPaint.setAlpha(64);
        keyDownPaint.setStrokeWidth(1);
        keyDownPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.LTGRAY);
        bgPaint.setStrokeWidth(1);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.RED);
        textPaint.setStyle(Paint.Style.FILL);
    }

    public GamePadView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        // Game pad
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle(centerPos.x, centerPos.y, centerRadius, centerPaint);

        // Buttons
        canvas.drawRect(leftPos.x - hWidth, leftPos.y - hHeight, leftPos.x + hWidth, leftPos.y + hHeight, keyStatus[0] ? keyDownPaint : keyUpPaint);
        canvas.drawRect(rightPos.x - hWidth, rightPos.y - hHeight, rightPos.x + hWidth, rightPos.y + hHeight, keyStatus[1] ? keyDownPaint : keyUpPaint);
        canvas.drawRect(upPos.x - hWidth, upPos.y - hHeight, upPos.x + hWidth, upPos.y + hHeight, keyStatus[2] ? keyDownPaint : keyUpPaint);
        canvas.drawRect(downPos.x - hWidth, downPos.y - hHeight, downPos.x + hWidth, downPos.y + hHeight, keyStatus[3] ? keyDownPaint : keyUpPaint);

        // Text
        canvas.drawText(mGameTimer.getTimeString(), textPos.x, textPos.y, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // төгс тойрог байгаа эсэхийг шалгана
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);

        centerPos = new Vector2D(measuredWidth * 0.18f, measuredHeight - measuredHeight * 0.2f);

        centerRadius = measuredWidth * 0.01f;
        gamePadRadius = 6.5f * centerRadius + hWidth;
        hWidth = measuredWidth * 0.03f;
        hHeight = measuredWidth * 0.03f;
        leftPos = new Vector2D(centerPos.x - 6.5f * centerRadius, centerPos.y);
        rightPos = new Vector2D(centerPos.x + 6.5f * centerRadius, centerPos.y);
        upPos = new Vector2D(centerPos.x, centerPos.y - 6.5f * centerRadius);
        downPos = new Vector2D(centerPos.x, centerPos.y + 6.5f * centerRadius);

        textPos = new Vector2D(measuredWidth * 0.03f, measuredHeight * 0.06f);
        textPaint.setTextSize(measuredHeight * 0.06f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int pIndex, pID;
        pIndex = e.getActionIndex();
        pID = e.getPointerId(pIndex);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (processKeyDown(e.getX(pID), e.getY(pID))) {
                    Log.v("GamePadView", "In" + pID);
                    pointerID = pID;
                } else {
                    touchDown.setXY(e.getX(pID), e.getY(pID));
                    Log.v("GamePadView", "Out" + pID);
                }
                break;
            case MotionEvent.ACTION_MOVE: {
                for (int i = 0; i < e.getPointerCount(); i++) {
                    if (pointerID != i) {
                        processMotion(e.getX(i), e.getY(i));
                        touchDown.setXY(e.getX(i), e.getY(i));
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (pID == pointerID) {
                    Log.v("GamePadView", "up");
                    pointerID = -1;
                    processKeyUp();
                }
                break;
        }
        return true;
    }

    private void processMotion(float x, float y) {
        moveCallback.onMove(x - touchDown.x, y - touchDown.y);
    }

    private boolean processKeyDown(float x, float y) {
        Vector2D tPos = new Vector2D(x, y);
        int key = -1;

        if (Vector2D.isInRect(tPos, centerPos, gamePadRadius, gamePadRadius)) {
            if (Vector2D.isInRect(tPos, leftPos, hWidth, hHeight)) {
                key = 0;
            } else if (Vector2D.isInRect(tPos, rightPos, hWidth, hHeight)) {
                key = 1;
            } else if (Vector2D.isInRect(tPos, upPos, hWidth, hHeight)) {
                key = 2;
            } else if (Vector2D.isInRect(tPos, downPos, hWidth, hHeight)) {
                key = 3;
            } else {
                key = -1;
            }
            if (key != -1)
                keyStatus[key] = true;
            moveCharacter(key);
            invalidate();
            return true;
        } else {
            invalidate();
            return false;
        }
    }

    private void processKeyUp() {
        for (int i = 0; i < keyStatus.length; i++) {
            keyStatus[i] = false;
        }

        moveCallback.onKeyUp();
        invalidate();
    }

    private void moveCharacter(int direction) {
        moveCallback.onKeyDown(direction);
    }

    public void setMoveCallback(GamePadMoveCallback callback) {
        moveCallback = callback;
    }

    public void startTimer() {
        mGameTimer.startTimer();
    }

    public int getTimer() {
        return mGameTimer.getSeconds();
    }
}
