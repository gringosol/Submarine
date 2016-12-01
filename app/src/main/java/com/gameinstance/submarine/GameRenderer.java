package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gringo on 26.11.2016 14:47.
 *
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    private final Context mActivityContext;
    private int mDefaultProgramHandle = 0;
    private int mSimpleProgramHandle = 0;
    private float [] mProjectionMatrix =  new float[16];
    private float [] mStaticViewMatrix =  new float[16];
    private float [] mDynamicViewMatrix =  new float[16];
    Sprite sprite;
    Sprite sprite2;
    float angle = 0;
    float [] color = new float[] {1.0f, 0.0f, 0.0f, 1.0f };

    Primitive primitive;
    Primitive primitive2;

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
        mDefaultProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position", "a_TexCoordinate"});
        int mPositionHandle = GLES20.glGetAttribLocation(mDefaultProgramHandle, "a_Position");
        int mTexCordHandle = GLES20.glGetAttribLocation(mDefaultProgramHandle, "a_TexCoordinate");
        int mTramsformMatrixHandle = GLES20.glGetUniformLocation(mDefaultProgramHandle, "u_MTransform");
        int mTextureUniformHandle = GLES20.glGetUniformLocation(mDefaultProgramHandle, "u_Texture");

        final String vertexShader2 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.simple_vertex_shader);
        final String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.simple_fragment_shader);
        final int vertexShaderHandle2 = ShaderUtils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader2);
        final int fragmentShaderHandle2 = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader2);
        mSimpleProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle2, fragmentShaderHandle2,
                new String[] {"a_Position", "a_TexCoordinate"});
        int mPositionHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_Position");
        int mTexCordHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_TexCoordinate");
        int mTramsformMatrixHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_MTransform");
        int mTextureUniformHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Texture");
        int mColorHandle = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Color");



        primitive = new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle, mTramsformMatrixHandle);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mStaticViewMatrix, 0);
        Matrix.translateM(mStaticViewMatrix, 0, 0, 0.5f, 0);
        Matrix.setIdentityM(mDynamicViewMatrix, 0);
        sprite = new Sprite(this, R.drawable.arrow, primitive, 1.0f, 0.5f);
        sprite.setPosition(0.5f, 0);
        sprite.setRotation(30.0f);
        GLES20.glEnable(GLES20.GL_BLEND);


        primitive2 = new Primitive(mPositionHandle2, mTexCordHandle2, mTextureUniformHandle2, mTramsformMatrixHandle2, mColorHandle);
        sprite2 = new Sprite(this, R.drawable.yellow, primitive2, 0.5f, 0.5f);
        sprite2.setPosition(-0.5f, 0);
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
        GLES20.glUseProgram(mDefaultProgramHandle);
        sprite.setRotation(angle);
        angle += 1.0f;
        sprite.draw(mStaticViewMatrix, mProjectionMatrix);
        GLES20.glUseProgram(mSimpleProgramHandle);
        sprite2.draw(mStaticViewMatrix, mProjectionMatrix, color);
    }
}
