package com.android.cy.androidmazegame.Scene;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.android.cy.androidmazegame.GameView.GameRenderer;
import com.android.cy.androidmazegame.Objects.BasicObject;
import com.android.cy.androidmazegame.Objects.GamePad;
import com.android.cy.androidmazegame.R;
import com.android.cy.androidmazegame.Utils.Vector3D;

import java.util.Vector;

/**
 * Created by ItgelG on 2022/3/14.
 */
public class SceneManager {

    private Context mContextHandle;
    private int mProgramHandle;
    private int mGamePadProgramHandle;

    private MazeMap mazeMap;
    private Vector<BasicObject> mazeObjects;

    private float[] mViewMatrix;
    private float[] mProjectionMatrix;
    private float[] mLightPosInEyeSpace;
    private float[] mModelMatrix = new float[16];

    private GamePad gamePad;
    private float[] mGamePadProjectionMatrix = new float[16];

    public SceneManager(Context context, int mapIndex) {
        mContextHandle = context;

        // initialize scene object
        mazeObjects = new Vector<>();

        // shader
        mProgramHandle = generateShader();
        mGamePadProgramHandle = generateGamePadShader();

        // scene map
        mazeMap = new MazeMap(this, mapIndex);
        mazeMap.readMazeMap(mContextHandle, mapIndex == 0 ? R.raw.testmap1 : mapIndex == 1 ? R.raw.testmap2 : R.raw.testmap3);

        // game pad
        gamePad = new GamePad(mContextHandle);
        gamePad.setShaderHandles(mGamePadProgramHandle);
    }

    public void setViewMatrix(float[] viewMatrix) {
        this.mViewMatrix = viewMatrix;
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        this.mProjectionMatrix = projectionMatrix;
    }

    public void setGamePadProjectionMatrix(float[] projectionMatrix) {
        this.mGamePadProjectionMatrix = projectionMatrix;
    }

    public void setLightPosInEyeSpace(float[] lightPosInEyeSpace) {
        this.mLightPosInEyeSpace = lightPosInEyeSpace;
    }

    // render scene
    public void render() {
        // Tell OpenGL to use this program when rendering.
        GLES20.glUseProgram(mProgramHandle);

        // Draw objects in current scene
        for (BasicObject obj : mazeObjects) {
            // Draw plane
            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, obj.getX(), obj.getY(), obj.getZ());
            Matrix.rotateM(mModelMatrix, 0, obj.getAngle(), 0, 1.0f, 0);
            obj.draw(mViewMatrix, mProjectionMatrix, mModelMatrix, mLightPosInEyeSpace);
        }

        // Render game pad
        GLES20.glUseProgram(mGamePadProgramHandle);
        gamePad.draw(mGamePadProjectionMatrix);

        // Render skybox
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    public void addObject(BasicObject obj) {
        obj.setShaderHandles(mProgramHandle);

        mazeObjects.add(obj);
    }

    public boolean checkForCollision(float x, float y) {
        return mazeMap.checkForCollision(x, y);
    }

    public Vector3D getStartPos() {
        return mazeMap.getStartPos();
    }

    public int generateShader() {
        int vertexShader = GameRenderer.loadShader(GLES20.GL_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(mContextHandle, R.raw.vertex_shader));
        int fragmentShader = GameRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(mContextHandle, R.raw.fragment_shader));

        int programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(programHandle, fragmentShader); // add the fragment shader to program

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glBindAttribLocation(programHandle, 2, "a_Normal");
            GLES20.glBindAttribLocation(programHandle, 3, "a_TexCoordinate");
        }
        GLES20.glLinkProgram(programHandle);                  // create OpenGL program executables

        return programHandle;
    }

    public int generateGamePadShader() {
        //
        int vertexShader = GameRenderer.loadShader(GLES20.GL_VERTEX_SHADER, RawResourceReader.readTextFileFromRawResource(mContextHandle, R.raw.gamepad_vertex_shader));
        int fragmentShader = GameRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, RawResourceReader.readTextFileFromRawResource(mContextHandle, R.raw.gamepad_fragment_shader));

        int programHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(programHandle, fragmentShader); // add the fragment shader to program

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
        }
        GLES20.glLinkProgram(programHandle);                  // create OpenGL program executables

        return programHandle;
    }

    public Context getContext() {
        return mContextHandle;
    }
}
