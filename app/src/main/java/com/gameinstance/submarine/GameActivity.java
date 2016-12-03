package com.gameinstance.submarine;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;


public class GameActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        GameSurfaceView view = (GameSurfaceView)findViewById(R.id.game_layout);
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
        return super.onTouchEvent(event);
    }
}