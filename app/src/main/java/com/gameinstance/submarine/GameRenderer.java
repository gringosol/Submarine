package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;
import com.gameinstance.submarine.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gringo on 26.11.2016 14:47.
 *
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    private final Context mActivityContext;
    private int mPerVertexProgramHandle = 0;
    private float [] mProjectionMatrix =  new float[16];
    private float [] mStaticViewMatrix =  new float[16];
    private float [] mDynamicViewMatrix =  new float[16];
    Sprite sprite;
    float angle = 0;

    Primitive primitive;

    public Context getActivityContext() {
        return mActivityContext;
    }

    public GameRenderer(Context con, GameSurfaceView view) {
        super();
        mActivityContext = con;
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.0f, 0.7f, 1.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.default_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.default_fragment_shader);
        final int vertexShaderHandle = ShaderUtils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        mPerVertexProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position"});
        int mPositionHandle = GLES20.glGetAttribLocation(mPerVertexProgramHandle, "a_Position");
        int mTramsformMatrixHandle = GLES20.glGetUniformLocation(mPerVertexProgramHandle, "u_MTransform");
        primitive = new Primitive(mPositionHandle, mTramsformMatrixHandle);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mStaticViewMatrix, 0);
        Matrix.translateM(mStaticViewMatrix, 0, 0, 0.5f, 0);
        Matrix.setIdentityM(mDynamicViewMatrix, 0);
        sprite = new Sprite(primitive, 1.0f, 0.5f);
        sprite.setPosition(0.5f, 0);
        sprite.setRotation(30.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float aspect = height / (float)width;
        Matrix.scaleM(mProjectionMatrix, 0, aspect, 1.0f, 1.0f);
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mPerVertexProgramHandle);
        sprite.setRotation(angle);
        angle += 1.0f;
        sprite.draw(mStaticViewMatrix, mProjectionMatrix);
    }
}
