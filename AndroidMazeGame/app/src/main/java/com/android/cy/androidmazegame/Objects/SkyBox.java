package com.android.cy.androidmazegame.Objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.android.cy.androidmazegame.R;
import com.android.cy.androidmazegame.Scene.RawResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by ItgelG on 2022/2/24.
 */
public class SkyBox{

    private int mProgramHandle;

    public FloatBuffer[] vertexBuffer = new FloatBuffer[5];
    public FloatBuffer textureBuffer;

    private final static float SKYBOX_WIDTH = 500.f;

    private float[][] positionData = {
            // Left face
            {
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH
            },
            // Right face
            {
                    SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH
            },

            // Front face
            {
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH
            },

            // Back face
            {
                    SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, -SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH
            },



            // Top face
            {
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH,
                    -SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, SKYBOX_WIDTH,
                    SKYBOX_WIDTH, SKYBOX_WIDTH, -SKYBOX_WIDTH
            }
    };

    private float[] textureData = {
//            0.0f, 0.0f,
//            0.0f, 1.0f,
//            1.0f, 0.0f,
//            0.0f, 1.0f,
//            1.0f, 1.0f,
//            1.0f, 0.0f
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f
    };

    private int[] mTextureDataHandle = new int[5];

    private static final int LEFT = 0, RIGHT = 1, BACK = 2, FRONT = 3, TOP = 4;

    private float[] mMVPMatrix = new float[16];

    /** Үүнийг хувиргах матрицад нэвтрүүлэхэд ашиглана. */
    public int mMVPMatrixHandle;

    /** Энэ нь загварын байрлалын мэдээллийг дамжуулахад ашиглагдана. */
    public int mPositionHandle;

    /** Үүнийг загвар бүтэцтэй координатын мэдээллийг дамжуулахад ашиглана. */
    public int mTextureCoordinateHandle;

    /** Үүнийг бүтэцтэй болгоход ашиглах болно. */
    public int mTextureUniformHandle;

    public final int mPositionDataSize = 3;

    public final int mTextureCoordinateDataSize = 2;

    public void initializeBuffers() {
        //
        for (int i = 0; i< 5; i++) {
            vertexBuffer[i] = ByteBuffer.allocateDirect(positionData[i].length * BasicObject.BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer[i].put(positionData[i]).position(0);
        }
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * BasicObject.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureBuffer.put(textureData).position(0);
    }

    public void setShaderHandle(int ph) {
        mProgramHandle = ph;

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    public SkyBox(Context context) {
        //
        mTextureDataHandle[LEFT] = RawResourceReader.loadTexture(context, R.drawable.skybox_left);
        mTextureDataHandle[RIGHT] = RawResourceReader.loadTexture(context, R.drawable.skybox_right);
        mTextureDataHandle[BACK] = RawResourceReader.loadTexture(context, R.drawable.skybox_back);
        mTextureDataHandle[FRONT] = RawResourceReader.loadTexture(context, R.drawable.skybox_front);
        mTextureDataHandle[TOP] = RawResourceReader.loadTexture(context, R.drawable.skybox_top);

        // buffers
        initializeBuffers();
    }

    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float[] mModelMatrix ) {
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUseProgram(mProgramHandle);
        for (int i = 0; i< 5; i++) {
            drawFace(i);
        }
    }

    private void drawFace(int faceIndex) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle[faceIndex]);

        GLES20.glUniform1i(mTextureUniformHandle, 0);

        vertexBuffer[faceIndex].position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, vertexBuffer[faceIndex]);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

}
