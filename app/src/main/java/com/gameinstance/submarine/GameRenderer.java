package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;
import com.gameinstance.submarine.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
    private int mDefaultProgramHandle = 0;
    private int mSimpleProgramHandle = 0;
    private float [] mProjectionMatrix =  new float[16];
    private float [] mStaticViewMatrix =  new float[16];
    private float [] mDynamicViewMatrix =  new float[16];

    float angle = 0;
    float [] color = new float[] {1.0f, 0.0f, 0.0f, 0.5f };
    byte [] backBuffer;
    int [] backTexHandle = new int[1];
    int [] backBufferHandle = new int[1];
    int [] texhandle2 = new int[1];

    Map<String, Integer> programHandles = new HashMap<>();


    Primitive primitive;
    Primitive primitive2;

    Movable movable;

    Sprite [] landscape;

    int height;
    int width;
    int backWidth = 256;
    int bachHeight = 256;

    float aspect = 1.0f;

    ByteBuffer ib;
    ByteBuffer ib2;

    int mPositionHandle;
    int mTexCordHandle;
    int mTramsformMatrixHandle;
    int mTextureUniformHandle;

    int mPositionHandle2;
    int mTexCordHandle2;
    int mTramsformMatrixHandle2;
    int mTextureUniformHandle2;
    int mColorHandle;

    Sprite cameraTarget = null;

    Scene mScene;

    public Context getActivityContext() {
        return mActivityContext;
    }

    public GameRenderer(Context con, GameSurfaceView view) {
        super();
        mActivityContext = con;
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
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
        programHandles.put("DefaultProgramHandle", mDefaultProgramHandle);
        mPositionHandle = GLES20.glGetAttribLocation(mDefaultProgramHandle, "a_Position");
        mTexCordHandle = GLES20.glGetAttribLocation(mDefaultProgramHandle, "a_TexCoordinate");
        mTramsformMatrixHandle = GLES20.glGetUniformLocation(mDefaultProgramHandle, "u_MTransform");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mDefaultProgramHandle, "u_Texture");

        final String vertexShader2 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.simple_vertex_shader);
        final String fragmentShader2 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.simple_fragment_shader);
        final int vertexShaderHandle2 = ShaderUtils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader2);
        final int fragmentShaderHandle2 = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader2);
        mSimpleProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle2, fragmentShaderHandle2,
                new String[] {"a_Position", "a_TexCoordinate"});
        programHandles.put("SimpleProgramHandle", mSimpleProgramHandle);
        mPositionHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_Position");
        mTexCordHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_TexCoordinate");
        mTramsformMatrixHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_MTransform");
        mTextureUniformHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Texture");
        mColorHandle = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Color");



        primitive = new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle, mTramsformMatrixHandle);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mStaticViewMatrix, 0);
        Matrix.translateM(mStaticViewMatrix, 0, 0, 0.5f, 0);
        Matrix.setIdentityM(mDynamicViewMatrix, 0);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        primitive2 = new Primitive(mPositionHandle2, mTexCordHandle2, mTextureUniformHandle2, mTramsformMatrixHandle2, mColorHandle);






        texhandle2[0] = TextureHelper.loadTexture(getActivityContext(), R.drawable.submarine);
        backBuffer = new byte[backWidth * bachHeight * 4];
        for (int i = 0; i < backBuffer.length; i++){
            //if (i % 2 == 0)
              backBuffer[i] = (byte)255;
        }
        ib = ByteBuffer.wrap(backBuffer);
        ib.position(0);
        ib2 = ByteBuffer.wrap(new byte[backWidth * bachHeight * 4]);
        ib2.position(0);
        GLES20.glGenTextures(1, backTexHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, backWidth, bachHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        GLES20.glGenFramebuffers(1, backBufferHandle, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backBufferHandle[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, backTexHandle[0], 0);


        GameManager.initGame(this);
        mScene = GameManager.getScene();
    }

    public float [] convertCoords(int x, int y) {
        aspect = height / (float)width;
        float xx1 = 2 * x / (width * aspect) - 1.0f / aspect - mStaticViewMatrix[12];
        float y1 = - 2 * y / (float)height + 1.0f - mStaticViewMatrix[13];
        return new float[] {xx1, y1};
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float aspect = height / (float)width;
        Matrix.scaleM(mProjectionMatrix, 0, aspect, 1.0f, 1.0f);
        this.width = width;
        this.height = height;
        //sprite3.setScale(1.0f / aspect, 1.0f);
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backBufferHandle[0]);
        GLES20.glViewport(0, 0, backWidth, bachHeight);
        GLES20.glClearColor(1.0f, 0, 0, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        /*GLES20.glUseProgram(mDefaultProgramHandle);
        sprite.setRotation(angle);
        angle += 1.0f;
        for (Sprite sp : landscape) {
            sp.draw(mStaticViewMatrix, mProjectionMatrix);
        }
        //sprite.draw(mStaticViewMatrix, mProjectionMatrix);
        GLES20.glUseProgram(mSimpleProgramHandle);
        movable.move();
        sprite2.draw(mStaticViewMatrix, mProjectionMatrix, color);
        Matrix.setIdentityM(mStaticViewMatrix,  0);
        Matrix.translateM(mStaticViewMatrix, 0, -sprite2.getPosition()[0], -sprite2.getPosition()[1], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        GLES20.glReadPixels(0, 0, backWidth, bachHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib2);


        boolean collide = movable.collideWithLandscape(ib2.array(), bachHeight, aspect, mStaticViewMatrix);
        if (collide) {
            movable.resetMotion();
        }*/

        mScene.move();
        updateCamera();
        mScene.setLayerSet("BackBuffer");
        mScene.draw(mStaticViewMatrix, mProjectionMatrix);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        GLES20.glReadPixels(0, 0, backWidth, bachHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib2);
        mScene.collide(ib2.array(), bachHeight, aspect, mStaticViewMatrix);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 0);





        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        /*GLES20.glUseProgram(mDefaultProgramHandle);
        sprite.setRotation(angle);
        angle += 1.0f;
        for (Sprite sp : landscape) {
            sp.draw(mStaticViewMatrix, mProjectionMatrix);
        }
        //sprite.draw(mStaticViewMatrix, mProjectionMatrix);



        GLES20.glUseProgram(mSimpleProgramHandle);

        //movable.move();
        sprite2.draw(mStaticViewMatrix, mProjectionMatrix, color);

        Matrix.setIdentityM(mStaticViewMatrix,  0);
        Matrix.translateM(mStaticViewMatrix, 0, -sprite2.getPosition()[0], -sprite2.getPosition()[1], 0);



        GLES20.glUseProgram(mDefaultProgramHandle);
        sprite3.setTexHandle(backTexHandle[0]);
        sprite3.draw(mDynamicViewMatrix, mProjectionMatrix);*/

        mScene.setLayerSet("Front");
        mScene.draw(mStaticViewMatrix, mProjectionMatrix);
    }



    public Integer getProgramHandle(String name) {
        return programHandles.get(name);
    }

    public Primitive createPrimitiveTextured() {
        return new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle, mTramsformMatrixHandle);
    }

    public Primitive createPrimitiveColored() {
        return new Primitive(mPositionHandle2, mTexCordHandle2, mTextureUniformHandle2, mTramsformMatrixHandle2, mColorHandle);
    }

    public void setCameraTarget(Sprite target) {
        cameraTarget = target;
    }

    public void updateCamera() {
        if (cameraTarget != null) {
            Matrix.setIdentityM(mStaticViewMatrix,  0);
            Matrix.translateM(mStaticViewMatrix, 0, -cameraTarget.getPosition()[0],
                    -cameraTarget.getPosition()[1], 0);
        }
    }
}
