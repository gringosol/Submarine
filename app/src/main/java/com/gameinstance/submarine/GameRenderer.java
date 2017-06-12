package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;

import java.nio.ByteBuffer;
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
    int mPositionHandle;
    int mTexCordHandle;
    int mTramsformMatrixHandle;
    int mTextureUniformHandle;
    int mPositionHandle2;
    int mTexCordHandle2;
    int mTramsformMatrixHandle2;
    int mTextureUniformHandle2;
    int mPositionHandle3;
    int mTexCordHandle3;
    int mTramsformMatrixHandle3;
    int mTextureUniformHandle3;
    int mColorHandle;
    int mTransparencyHandle;
    Map<String, Integer> programHandles = new HashMap<>();
    boolean paused = false;

    private float [] mProjectionMatrix =  new float[16];
    private float [] mViewMatrix =  new float[16];
    private float [] mGuiViewMatrix =  new float[16];

    Map<Integer, int[]> backBufferHandles = new HashMap<>();
    Map<Integer, ByteBuffer> backBufferData = new HashMap<>();

    int height;
    int width;
    int backWidth = 256;
    int bachHeight = 256;
    float aspect = 1.0f;

    Camera camera = null;

    Scene mScene;

    GameSurfaceView view;

    static FloatBuffer currentPosBuffer = null;
    static FloatBuffer currentTexCoordBuffer = null;

    long prevTime = 0;

    public Context getActivityContext() {
        return mActivityContext;
    }

    public GameRenderer(Context con, GameSurfaceView view) {
        super();
        mActivityContext = con;
        this.view = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initializeOglState();
        createShaders();
        initializeMatrices();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        aspect = height / (float)width;
        Matrix.scaleM(mProjectionMatrix, 0, aspect, 1.0f, 1.0f);
        this.width = width;
        this.height = height;
        GameManager.initGame(this);
        mScene = GameManager.getScene();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (paused)
            return;
        long curTime = SystemClock.uptimeMillis();
        if (curTime - prevTime > 50) {
            GameManager.getGameplay().update();
            prevTime = curTime;
        }
        for (Map.Entry<String, Layerset> entry : mScene.getLayerSets().entrySet())  {
            if (!entry.getValue().getEnabled())
               continue;
            int [] target = entry.getValue().target;
            if (target != null) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backBufferHandles.get(target[0])[0]);
            } else {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
            int [] viewport = entry.getValue().viewport;
            if (viewport != null)
                GLES20.glViewport(0, 0, viewport[0], viewport[1]);
            else
                GLES20.glViewport(0, 0, width, height);
            GLES20.glClearColor(0, 0, 1, 0);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            mScene.setLayerSet(entry.getKey());
            float [] projectionMatrix = entry.getValue().projectionMatrix == null ? mProjectionMatrix :
                    entry.getValue().projectionMatrix;
            if (entry.getValue().collide) {
                mScene.move();
            }
            float [] viewMatrix = entry.getValue().viewMatrix;
            updateCamera(1.0f / projectionMatrix[5], projectionMatrix[0] / projectionMatrix[5], viewMatrix);
            if (entry.getValue().collide) {
                mViewMatrix = viewMatrix;
            }
            mScene.draw(viewMatrix, projectionMatrix, mGuiViewMatrix);
            if (entry.getValue().collide && target != null) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, target[0]);
                ByteBuffer ib2 = backBufferData.get(target[0]);
                GLES20.glReadPixels(0, 0, backWidth, bachHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib2);
                mScene.collide(ib2.array(), bachHeight, aspect, viewMatrix);
            }
            if (entry.getValue().target != null) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
        GameManager.getGameplay().updatePost();
    }

    public Integer getProgramHandle(String name) {
        return programHandles.get(name);
    }

    public Primitive createPrimitiveTextured() {
        return new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle, mTramsformMatrixHandle);
    }

    public Primitive createPrimitiveTextured( float [] texcoord) {
        return new Primitive(mPositionHandle, mTexCordHandle, mTextureUniformHandle,
                mTramsformMatrixHandle, texcoord);
    }

    public Primitive createPrimitiveColored() {
        return new Primitive(mPositionHandle2, mTexCordHandle2, mTextureUniformHandle2,
                mTramsformMatrixHandle2, mColorHandle, null);
    }

    public Primitive createPrimitiveTransparent() {
        Primitive primitive =  new Primitive(mPositionHandle3, mTexCordHandle3, mTextureUniformHandle3,
                mTramsformMatrixHandle3);
        primitive.setmTransparencyHandle(mTransparencyHandle);
        return primitive;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    public void updateCamera(float scale, float aspect, float [] viewMatrix) {
        if (camera != null) {
            viewMatrix = camera.update(viewMatrix, aspect, scale);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    private void createShaders() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.default_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.default_fragment_shader);
        final int vertexShaderHandle = ShaderUtils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        int mDefaultProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_TexCoordinate"});
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
        int mSimpleProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle2, fragmentShaderHandle2,
                new String[]{"a_Position", "a_TexCoordinate"});
        programHandles.put("SimpleProgramHandle", mSimpleProgramHandle);
        mPositionHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_Position");
        mTexCordHandle2 = GLES20.glGetAttribLocation(mSimpleProgramHandle, "a_TexCoordinate");
        mTramsformMatrixHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_MTransform");
        mTextureUniformHandle2 = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Texture");
        mColorHandle = GLES20.glGetUniformLocation(mSimpleProgramHandle, "u_Color");
        final String vertexShader3 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.transparent_vertex_shader);
        final String fragmentShader3 = RawResourceReader.readTextFileFromRawResource(mActivityContext,
                R.raw.transparent_fragment_shader);
        final int vertexShaderHandle3 = ShaderUtils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader3);
        final int fragmentShaderHandle3 = ShaderUtils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader3);
        int TransparentProgramHandle = ShaderUtils.createAndLinkProgram(vertexShaderHandle3, fragmentShaderHandle3,
                new String[]{"a_Position", "a_TexCoordinate"});
        programHandles.put("TransparentProgramHandle", TransparentProgramHandle);
        mPositionHandle3 = GLES20.glGetAttribLocation(TransparentProgramHandle, "a_Position");
        mTexCordHandle3 = GLES20.glGetAttribLocation(TransparentProgramHandle, "a_TexCoordinate");
        mTramsformMatrixHandle3 = GLES20.glGetUniformLocation(TransparentProgramHandle, "u_MTransform");
        mTextureUniformHandle3 = GLES20.glGetUniformLocation(TransparentProgramHandle, "u_Texture");
        mTransparencyHandle = GLES20.glGetUniformLocation(TransparentProgramHandle, "u_Transp");
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

    public float [] convertCoords(int x, int y, boolean gui) {
        aspect = height / (float)width;
        float xx1 = 2 * x / (width * aspect) - 1.0f / aspect - (gui ? mGuiViewMatrix[12] : mViewMatrix[12]);
        float y1 = - 2 * y / (float)height + 1.0f - (gui ? mGuiViewMatrix[13] : mViewMatrix[13]);
        return new float[] {xx1, y1};
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean getPaused() {
        return paused;
    }

    public GameSurfaceView getSurfaceView() {
        return view;
    }

    public int [] createViewportTarget(int size) {
        byte [] backBuffer = new byte[size * size * 4];
        for (int i = 0; i < backBuffer.length; i++){
            backBuffer[i] = (byte)255;
        }
        ByteBuffer ib = ByteBuffer.wrap(backBuffer);
        ib.position(0);
        int [] backTexHandle = new int[1];
        GLES20.glGenTextures(1, backTexHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, backTexHandle[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, size, size, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
        int [] backBufferHandle = new int[1];
        GLES20.glGenFramebuffers(1, backBufferHandle, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, backBufferHandle[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, backTexHandle[0], 0);
        backBufferHandles.put(backTexHandle[0], backBufferHandle);
        backBufferData.put(backTexHandle[0], ib);
        return backTexHandle;
    }

    public float [] getDefaultProjectionMatrix() {
        return mProjectionMatrix;
    }

    public static FloatBuffer getCurrentPosBuffer() {
        return currentPosBuffer;
    }

    public static void setCurrentPosBuffer(FloatBuffer fb) {
        currentPosBuffer = fb;
    }

    public static FloatBuffer getCurrentTexCoordBuffer() {
        return currentTexCoordBuffer;
    }

    public static void setCurrentTexCoordBuffer(FloatBuffer fb) {
        currentTexCoordBuffer = fb;
    }

    public float getAspect() {
        return aspect;
    }
}
