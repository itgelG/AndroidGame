package com.android.cy.androidmazegame.GamePad;

/**
 * Created by ItgelG on 2022/2/16.
 */
public interface GamePadMoveCallback {
    public void onMove(float x, float y);
    public void onKeyDown(int direction);
    public void onKeyUp();
}
