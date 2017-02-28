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
    int ambientMusicId = 0;

    @Override
    public void init() {
        marker = GameManager.addSprite(R.drawable.marker, target[0], target[1], 0.1f, 0.1f);
        GameManager.getScene().getLayer("ships_and_tanks").addSprite(marker);
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

    @Override
    public void onClose() {
        GameManager.getSoundManager().stopSound(ambientMusicId);
    }

    @Override
    public void briefing() {
        GameManager.showMessage(R.string.briefing_level_1, -1.0f, 0.5f, 2000);
        GameManager.showMessage(R.string.briefing_level_1_1, -1.0f, 0.2f, 2000);
        GameManager.showMessage(R.string.briefing_level_1_2, -1.0f, -0.1f, 2000);
    }

    public void onShow() {
        GameManager.showMessage(R.string.go_to_marker, -1.0f, 0.5f, 3000);
        ambientMusicId = GameManager.getSoundManager().playSound(R.raw.molecular_dance_lite, true);
    }
}
