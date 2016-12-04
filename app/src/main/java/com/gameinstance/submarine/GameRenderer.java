package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.gameinstance.submarine.utils.RawResourceReader;
import com.gameinstance.submarine.utils.ShaderUtils;
import com.gameinstance.submarine.utils.TextureHelper;

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
    float [] color = new float[] {1.0f, 0.0f, 0.0f, 0.5f };

    Primitive primitive;
    Primitive primitive2;

    Movable movable;

    Sprite [] landscape;

    int height;
    int width;

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
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        primitive2 = new Primitive(mPositionHandle2, mTexCordHandle2, mTextureUniformHandle2, mTramsformMatrixHandle2, mColorHandle);
        sprite2 = new Sprite(this, R.drawable.submarine, primitive2, 0.2f, 0.2f);
        sprite2.setPosition(-0.5f, 0);
        movable = new Movable(sprite2);

        landscape = createLandScape(R.drawable.background, 64, 1, primitive);

        InputController.addTouchHandler(new InputController.TouchHandler() {
            @Override
            public void touch(int x, int y) {
                movable.setTarget(convertCoords(x, y));
            }
        });
    }

    private float [] convertCoords(int x, int y) {
        float aspect = height / (float)width;
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
    }



    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mDefaultProgramHandle);
        sprite.setRotation(angle);
        angle += 1.0f;
        for (Sprite sp : landscape) {
            sp.draw(mStaticViewMatrix, mProjectionMatrix);
        }
        sprite.draw(mStaticViewMatrix, mProjectionMatrix);



        GLES20.glUseProgram(mSimpleProgramHandle);

        movable.move();
        sprite2.draw(mStaticViewMatrix, mProjectionMatrix, color);

        Matrix.setIdentityM(mStaticViewMatrix,  0);
        Matrix.translateM(mStaticViewMatrix, 0, -sprite2.getPosition()[0], -sprite2.getPosition()[1], 0);
    }

    private Sprite [] createLandScape(int textureId, int pixelsPerUnit, float unitSize, Primitive primitive) {
        int [] texHandles = TextureHelper.loadTexture2(getActivityContext(), textureId, pixelsPerUnit);
        int n = texHandles[0];
        int m = texHandles[1];
        Sprite [] sprites = new Sprite[m * n];
        float left = -(n * unitSize) / 2.0f + 0.5f * unitSize;
        float top = (m * unitSize) / 2.0f - 0.5f * unitSize;
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                sprites[j * n + i] = new Sprite(this, texHandles[j * n + i + 2], primitive,
                        unitSize, new float[] {left + i * unitSize, top - j * unitSize});
            }
        }
        return sprites;
    }
}
