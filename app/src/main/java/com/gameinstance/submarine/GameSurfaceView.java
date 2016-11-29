package com.gameinstance.submarine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gringo on 26.11.2016 14:46.
 *
 */
public class GameSurfaceView extends GLSurfaceView {

    private GameRenderer renderer = null;
    //private InputController inputController;

    public GameSurfaceView (Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new GameRenderer(context, this);
        setRenderer(renderer);
        //inputController = new InputController(this);
    }

    /*@Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return inputController.onTouchEvent(event);
    }*/

    public GameRenderer getRenderer() {
        return renderer;
    }

    /*public InputController getInputController() {
        return inputController;
    }*/
}