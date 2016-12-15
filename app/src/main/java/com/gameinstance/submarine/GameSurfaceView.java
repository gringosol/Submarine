package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by gringo on 26.11.2016 14:46.
 *
 */
public class GameSurfaceView extends GLSurfaceView {

    private GameRenderer renderer = null;

    public GameSurfaceView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new GameRenderer(context, this);
        setRenderer(renderer);
    }

    public GameRenderer getRenderer() {
        return renderer;
    }
}