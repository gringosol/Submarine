package com.gameinstance.submarine;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;


public class GameActivity extends Activity {
    static Activity activity;
    GameSurfaceView view;

    public static Activity getActivity() {
        return activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.game_layout);
        view = (GameSurfaceView)findViewById(R.id.game_layout);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputController.onScreenTouch((int)event.getX(), (int)event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            InputController.onScreenDown((int)event.getX(), (int)event.getY());
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            InputController.onScreenUp((int)event.getX(), (int)event.getY());
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStop() {
        GameManager.clearMemory();
        super.onStop();
    }
}