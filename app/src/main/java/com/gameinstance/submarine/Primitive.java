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
    int mTexDataHandle;
    int mTextureUniformHandle;

    public Primitive(int mPositionHandle, int mTexCoordHandle, int mTexDataHandle, int mTextureUniformHandle, int mTransformMatrixHandle) {
        this.mTexDataHandle = mTexDataHandle;
        this.mTextureUniformHandle = mTextureUniformHandle;
        final float[] positions =
                {
                        -0.5f, 0.5f, 0f, 1.0f,
                        -0.5f, -0.5f, 0f, 1.0f,
                        0.5f, 0.5f, 0f, 1.0f,

                        -0.5f, -0.5f, 0, 1.0f,
                        0.5f, -0.5f, 0, 1.0f,
                        0.5f, 0.5f, 0, 1.0f,
                };
        final float[] texCoordinateData = {
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
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        this.mTransformMatrixHandle = mTransformMatrixHandle;
        mPositions.position(0);
        mTexCoordinates.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false, 0, mTexCoordinates);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
    }


    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix) {
        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
