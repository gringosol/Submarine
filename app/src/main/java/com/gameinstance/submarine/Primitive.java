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
    final int mPositionDataSize = 4;
    int vertexCount = 0;
    int mTransformMatrixHandle;

    public Primitive(int mPositionHandle, int mTransformMatrixHandle) {
        final float[] positions =
                {
                        -0.5f, 0.5f, 0f, 1.0f,
                        -0.5f, -0.5f, 0f, 1.0f,
                        0.5f, 0.5f, 0f, 1.0f,

                        -0.5f, -0.5f, 0, 1.0f,
                        0.5f, -0.5f, 0, 1.0f,
                        0.5f, 0.5f, 0, 1.0f,
                };
        mPositions = ByteBuffer.allocateDirect(positions.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(positions).position(0);
        vertexCount = positions.length / mPositionDataSize;
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        this.mTransformMatrixHandle = mTransformMatrixHandle;
        mPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, mPositions);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
    }


    public void draw(float [] projectionMatrix, float [] viewMatrix, float [] modelMatrix) {



        float [] resultMatrix = new float[16];
        Matrix.setIdentityM(resultMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, projectionMatrix, 0, resultMatrix, 0);
        GLES20.glUniformMatrix4fv(mTransformMatrixHandle, 1, false, resultMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}
