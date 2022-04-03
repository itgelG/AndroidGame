package com.android.cy.androidmazegame.Objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.android.cy.androidmazegame.R;
import com.android.cy.androidmazegame.Scene.RawResourceReader;

/**
 * Created by ItgelG on 2022/2/24.
 */
public class Plane extends BasicObject {

    private float[] mMVPMatrix = new float[16];

    public Plane(Context context, float hWidth, float hHeight, boolean isRoof, int mapIndex) {
        super(context);

        generatePlaneData(hWidth, hHeight);

        initializeBuffers();

        if (isRoof) {
            mTextureDataHandle = RawResourceReader.loadTexture(context,
                    mapIndex == 0 ?
                            R.drawable.floor1: mapIndex == 1 ?
                            R.drawable.floor2:
                            R.drawable.floor3
            );
        } else {
            mTextureDataHandle = RawResourceReader.loadTexture(context,
                    mapIndex == 0 ?
                            R.drawable.roof1: mapIndex == 1 ?
                            R.drawable.roof2:
                            R.drawable.roof3
                    );
        }

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
    }

    @Override
    public void setShaderHandles(int ph) {
        mProgramHandle = ph;

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    @Override
    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float[] mModelMatrix, float[] mLightPosInEyeSpace) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                0, colorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        normalBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, normalBuffer);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    @Override
    public void draw(float[] p) {

    }

    private void generatePlaneData(float hWidth, float hHeight) {


        // X Y Z
        positionData = new float[]
                {
                        -hWidth, 0.0f, hHeight,
                        hWidth, 0.0f, -hHeight,
                        -hWidth, 0.0f, -hHeight,
                        -hWidth, 0.0f, hHeight,
                        hWidth, 0.0f, hHeight,
                        hWidth, 0.0f, -hHeight
                };
        colorData = new float[]
                {
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f
                };
        normalData = new float[]
                {
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f
                };
        indexData = new short[]
                {
                        0, 1, 2,
                        0, 2, 3
                };
        textureData = new float[]
                {
                        0.0f, 0.0f,
                        4.0f, 4.0f,
                        0.0f, 4.0f,
                        0.0f, 0.0f,
                        4.0f, 0.0f,
                        4.0f, 4.0f,
                };
    }
}
