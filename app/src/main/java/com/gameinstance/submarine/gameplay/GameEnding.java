package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.ui.TextLine;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gringo on 02.04.2017 14:11.
 *
 */
public class GameEnding {
    public static void init() {
        GameManager.getGameplay().paused = true;
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                GameManager.getRenderer().setPaused(true);
                final Sprite backGroundSprite = GameManager.addSprite(R.drawable.blacksquare, 0, 0, 5.0f, 2.0f);
                GameManager.getScene().getLayer("hud").addSprite(backGroundSprite);
                final TextLine text = new TextLine(R.string.main_title, new float[] {-1.0f, 0}, 0.5f, GameManager.getRenderer());
                GameManager.getScene().showText(text, "hud");
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        turnToMenu(text, backGroundSprite);
                    }
                }, 5000);
                GameManager.getRenderer().setPaused(false);
            }
        });

    }

    private static void turnToMenu(final TextLine textLine, final Sprite backGroundSprite) {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                GameManager.getRenderer().setPaused(true);
                GameManager.getScene().getLayer("hud").removeSprite(backGroundSprite);
                GameManager.getScene().hideText(textLine, "hud");
                GameManager.clearLevel();
                GameManager.showMainMenu(true);
                GameManager.getRenderer().setPaused(false);
                GameManager.getGameplay().paused = false;
            }
        });

    }
}
