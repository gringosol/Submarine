package com.gameinstance.submarine;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by gringo on 26.11.2016 15:55.
 *
 */


public class Primitive {
    final int mBytesPerFloat = 4;
    FloatBuffer mPositions;
    FloatBuffer mTexCoordinates;
    final int mTexCoordDataSize = 2;
    final int mPositionDataSize = 4;
    int vertexCount = 0;
    int mTransformMatrixHandle;
    int mTextureUniformHandle;
    int mColorHandle = -1;
    int mTransparencyHandle = -1;
    int mPositionHandle;
    int mTexCoordHandle;
    int mTexBgrHandle = -1;
    int mModelMatrixHandle;
    int mTimeHandle;

    public Primitive(int mPositionHandle, int mTexCoordHandle, int mTextureUniformHandle, int mTransformMatrixHandle, int mColorHandle, float [] texCoord) {
        this.mTextureUniformHandle = mTextureUniformHandle;
        this.mColorHandle = mColorHandle;
        this.mPositionHandle = mPositionHandle;
        this.mTexCoordHandle = mTexCoordHandle;
        final float[] positions =
                {
                        -0.5f, 0.5f, 0f, 1.0f,
                        -0.5f, -0.5f, 0f, 1.0f,
                        0.5f, 0.5f, 0f, 1.0f,

                        -0.5f, -0.5f, 0, 1.0f,
                        0.5f, -0.5f, 0, 1.0f,
                        0.5f, 0.5f, 0, 1.0f,
                };
        float[] texCoordinateData = texCoord !=  null ? texCoord : new float[] {
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 1.0f,
                        1.0f, 0.0f,};
        mPositions = ByteBuffer.allocateDirect(positions.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(positions).position(0);
        mTexCoordinates = ByteBuffer.allocateDirect(texCoordinateData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexCoordinates.put(texCoordinateData).position(0);
        vertexCount = positions.length / mPositionDataSize;
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        this.mTransformMatrixHandle = mTransformMatrixHandle;
        mPositions.position(0);
        mTexCoordinates.position(0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
    }

    public Primitive(int mPositionHandle, int mTexCoordHandle, int mTextureUniformHandle, int mTransformMatrixHandle) {
        this(mPositionHandle, mTexCoordHandle, mTextureUniformHandle, mTransformMatrixHandle, -1 , null);
    }

    public Primitive(int mPositionHandle, int mTexCoordHandle, int mTextureUniformHandle, int mTransformMatrixHandle, float [] texCoord) {
        this(mPositionHandle, mTexCoordHandle, mTextureUniformHandle, mTransformMatrixHandle, -1 , texCoord);
    }

    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix) {
        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        mTexCoordinates.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, 0, mTexCoordinates);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix, float [] color) {
        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mTexCoordinates.position(0);
        if (GameRenderer.getCurrentPosBuffer() != mPositions) {
            GameRenderer.setCurrentPosBuffer(mPositions);
            GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        }
        if (GameRenderer.getCurrentTexCoordBuffer() != mTexCoordinates) {
            GameRenderer.setCurrentTexCoordBuffer(mTexCoordinates);
            GLES20.glVertexAttribPointer(mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, 0, mTexCoordinates);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix, float fTransparensy) {
        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glUniform1f(mTransparencyHandle, fTransparensy);
        mTexCoordinates.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, 0, mTexCoordinates);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix, long time) {
        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, modelMatrix, 0);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glUniform1f(mTimeHandle, (float)time / 1000.0f);
        mTexCoordinates.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, 0, mTexCoordinates);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    public void onDestroy() {
        mPositions.limit(0);
        mPositions = null;
        mTexCoordinates.limit(0);
        mTexCoordinates = null;
    }

    public void setmTransparencyHandle(int handle) {
        mTransparencyHandle = handle;
    }

    public void setmTexBgrHandle(int handle) {
        mTexBgrHandle = handle;
    }

    public void setmModelMatrixHandle (int h) {
        mModelMatrixHandle = h;
    }

    public void setmTimeHandle(int h) {
        mTimeHandle = h;
    }
}
