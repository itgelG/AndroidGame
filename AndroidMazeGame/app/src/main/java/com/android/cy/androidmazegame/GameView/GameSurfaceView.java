package com.android.cy.androidmazegame.GameView;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by ItgelG on 2022/2/24.
 */
public class GameSurfaceView extends GLSurfaceView {

    public final static float WINDOW_WIDTH = 2560;
    public final static float WINDOW_HEIGHT = 1600;

    private final GameRenderer mGameRenderer;

    private GameViewCallback mGameViewCallback;

    public GameSurfaceView(Context context, int mapIndex) {
        super(context);

        setEGLContextClientVersion(2);

        mGameRenderer = new GameRenderer(context, this, mapIndex);

        setRenderer(mGameRenderer);
    }

    public GameSurfaceView(Context context, AttributeSet attributeSet, int mapIndex) {
        super(context, attributeSet);

        setEGLContextClientVersion(2);

        mGameRenderer = new GameRenderer(context, this, mapIndex);

        setRenderer(mGameRenderer);
    }


    public void onCameraTargetUpdate(float x, float y) {
        mGameRenderer.updateCamera(x, y);
    }

    public void onCharacterKeyDown(int direction) {
        mGameRenderer.onKeyDown(direction);
    }

    public void onCharacterKeyUp() {
        mGameRenderer.onKeyUp();
    }

    public void setGameViewCallback(GameViewCallback gameViewCallback) {
        mGameViewCallback = gameViewCallback;
    }

    public void gameStart() {
        mGameViewCallback.onGameStart();
    }
}
