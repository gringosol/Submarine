package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.util.Collections;
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
    int mPositionHandle;
    int mTexCordHandle;
    int mTramsformMatrixHandle;
    int mTextureUniformHandle;
    int mPositionHandle2;
    int mTexCordHandle2;
    int mTramsformMatrixHandle2;
    int mTextureUniformHandle2;
    int mColorHandle;
    Map<String, Integer> programHandles = new HashMap<>();

    private float [] mProjectionMatrix =  new float[16];
    private float [] mViewMatrix =  new float[16];
    private float [] mGuiViewMatrix =  new float[16];

    byte [] backBuffer;
    int [] backTexHandle = new int[1];
    int [] backBufferHandle = new int[1];
    ByteBuffer ib;
    ByteBuffer ib2;

    Primitive primitiveTex;
    Sprite backMap;

    int height;
    int width;
    int backWidth = 256;
    int bachHeight = 256;
    float aspect = 1.0f;





    Sprite cameraTarget = null;

    Scene mScene;

    private boolean drawBackMap = true;

    public Context getActivityContext() {
        return mActivityContext;
    }

    public GameRenderer(Context con, GameSurfaceView view) {
        super();
        mActivityContext = con;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initializeOglState();
        createShaders();
        createBackBuffer();
        initializeMatrices();
        createHelpers();
        GameManager.initGame(this);
        mScene = GameManager.getScene();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float aspect = height / (float)width;
        Matrix.scaleM(mProjectionMatrix, 0, aspect, 1.0f, 1.0f);
        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backBufferHandle[0]);
        GLES20.glViewport(0, 0, backWidth, bachHeight);
        GLES20.glClearColor(1.0f, 0, 0, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mScene.move();
        updateCamera();
        mScene.setLayerSet("BackBuffer");
        mScene.draw(mViewMatrix, mProjectionMatrix);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        GLES20.glReadPixels(0, 0, backWidth, bachHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib2);
        mScene.collide(ib2.array(), bachHeight, aspect, mViewMatrix);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mScene.setLayerSet("Front");
        mScene.draw(mViewMatrix, mProjectionMatrix);
       if (drawBackMap)
           drawBackBufferToScene();
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
            Matrix.setIdentityM(mViewMatrix,  0);
            Matrix.translateM(mViewMatrix, 0, -cameraTarget.getPosition()[0],
                    -cameraTarget.getPosition()[1], 0);
        }
    }

    private void createShaders() {
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
    }

    private void createBackBuffer() {
        backBuffer = new byte[backWidth * bachHeight * 4];
        for (int i = 0; i < backBuffer.length; i++){
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
    }

    private void createHelpers() {
        primitiveTex = new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle, mTramsformMatrixHandle);
        Map<Integer, Primitive> primitiveMap = Collections.singletonMap(mDefaultProgramHandle, primitiveTex);
        backMap = new Sprite(this, backTexHandle[0], primitiveMap, 1.0f, new float [] {-1.0f, 0.5f});
    }

    private void initializeOglState() {
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void initializeMatrices() {
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0, 0.5f, 0);
        Matrix.setIdentityM(mGuiViewMatrix, 0);
    }

    public float [] convertCoords(int x, int y) {
        aspect = height / (float)width;
        float xx1 = 2 * x / (width * aspect) - 1.0f / aspect - mViewMatrix[12];
        float y1 = - 2 * y / (float)height + 1.0f - mViewMatrix[13];
        return new float[] {xx1, y1};
    }

    private void drawBackBufferToScene() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        backMap.draw(mGuiViewMatrix, mProjectionMatrix, mDefaultProgramHandle);
    }
}
