package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.utils.MathUtils;

/**
 * Created by gringo on 01.01.2017 13:07.
 *
 */
public class Level1 implements LevelLogic {
    private boolean completed = false;
    float [] target = new float[] {  -2.0f, 1.0f  };
    transient Sprite marker;

    @Override
    public void init() {
        marker = GameManager.addSprite(R.drawable.marker, target[0], target[1], 0.1f, 0.1f);
        GameManager.getScene().getLayer("ships_and_tanks").addSprite(marker);
        GameManager.showMessage("Плывите к маркеру", -1.0f, 0.5f, 2000);
        completed = false;
    }

    @Override
    public void run() {
        float dist = MathUtils.distance(target, GameManager.getSubmarineMovable().getSprite().getPosition());
        if (dist < 0.3f) {
            GameManager.getScene().getLayer("ships_and_tanks").removeSprite(marker);
            completed = true;
        }
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void restore() {
        marker = GameManager.addSprite(R.drawable.marker, target[0], target[1], 0.1f, 0.1f);
        GameManager.getScene().getLayer("ships_and_tanks").addSprite(marker);
        completed = false;
    }
}
