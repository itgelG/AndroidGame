package com.android.cy.androidmazegame.Objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by ItgelG on 2022/2/24.
 */
public class GamePad extends BasicObject{

    private final float[] mMVPMatrix = new float[16];

    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    public GamePad(Context context) {
        super(context);

        generatePadPosData();

        // Initialize buffers
        initializeBuffers();

        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    @Override
    public void setShaderHandles(int ph) {
        mProgramHandle = ph;

        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mTextureDataHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        mPointThickness = GLES20.glGetUniformLocation(mProgramHandle, "u_Thickness");
    }

    @Override
    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, float[] mModelMatrix, float[] mLightPosInEyeSpace) {
        GLES20.glUseProgram(mProgramHandle);

        // Ерөнхий оройн атрибут массивыг идэвхжүүлэх
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Гурвалжны координатын өгөгдлийг бэлтгэх
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // Текстурын координатын мэдээллийг оруулах
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // Энэ нь харах матрицыг загварын матрицаар үржүүлж, үр дүнг MVP матрицад хадгална.
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Энэ нь загвар харах матрицыг проекцийн матрицаар үржүүлж, үр дүнг MVP матрицад хадгална.
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Проекцийг хэрэглэж, хувиргалтыг харах
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Гурвалжин зурах
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.length,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Оройн массивыг идэвхгүй болгох
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void draw(float[] projectionMatrix) {
        GLES20.glUseProgram(mProgramHandle);

        // Ерөнхий оройн атрибут массивыг идэвхжүүлэх
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Гурвалжны координатын өгөгдлийг бэлтгэ
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        // цэгийн хэмжээг тохируулах
        GLES20.glUniform1f(mPointThickness, 50.f);

        // загвар харах матрицыг тохируулах
        Matrix.setIdentityM(mModelMatrix, 0);
        // Энэ нь харах матрицыг загварын матрицаар үржүүлж, үр дүнг MVP матрицад хадгална.
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Энэ нь загвар харах матрицыг проекцийн матрицаар үржүүлж, үр дүнг MVP матрицад хадгална.
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);

        // Проекцийг хэрэглэж, хувиргалтыг харах
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Гурвалжин зурах
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexData.length,
                GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        // Оройн массивыг идэвхгүй болгох
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    private void generatePadPosData() {
        // We have create the vertices of our view.
        positionData = new float[]
                {
                        10.0f, 200f, 0.0f,
                        10.0f, 100f, 0.0f,
                        100f, 100f, 0.0f,
                        100f, 200f, 0.0f
                };

        textureData = new float[]
                {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f
                };

        indexData = new short[] {0, 1, 2, 0, 2, 3};
    }
}
