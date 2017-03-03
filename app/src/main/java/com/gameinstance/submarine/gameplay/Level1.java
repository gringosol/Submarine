package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Helicopter;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Ship;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.Tank;
import com.gameinstance.submarine.gameplay.tasks.MobTask;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;
import com.gameinstance.submarine.gameplay.tasks.PatrolTwoPoints;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 01.01.2017 13:07.
 *
 */
public class Level1 implements LevelLogic {
    private boolean completed = false;
    float [] target = new float[] {  -4.28f, -0.04f  };
    transient Sprite marker;
    int ambientMusicId = 0;
    List<Ship> ships;
    List<Tank> tanks;
    List<Helicopter> helicopters;

    @Override
    public void init() {
        marker = GameManager.addSprite(R.drawable.marker, target[0], target[1], 0.1f, 0.1f);
        GameManager.getScene().getLayer("ships_and_tanks").addSprite(marker);
        completed = false;
        ships = GameManager.getScene().getShips();
        tanks = GameManager.getScene().getTanks();
        helicopters = GameManager.getScene().getHelis();
        setupActors();
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

    private void setupActors() {
        List<float []> sh1Points = Arrays.asList(new float [] {-4.17f, -2.29f},
                new float [] {-5.07f, -1.59f}, new float[] {-5.15f, -0.93f},
                new float[] {-4.99f, -0.26f}, new float[] {-4.71f, 0.39f},
                new float[] {-4.12f, 1.05f}, new float[] {-3.56f, 1.69f},
                new float[] {-2.43f, 1.91f}, new float[] {-1.23f, 1.39f},
                new float[] {-0.79f, 0.52f}, new float[] {-1.39f, -1.85f});
        ships.get(0).setTarget(sh1Points.get(0));
        ships.get(0).setCurrentTask(new PatrolPoints(ships.get(0), sh1Points));

        List<float []> sh2Points = Arrays.asList(new float[] {-2.77f, 4.45f},
                new float[] {1.74f, 2.78f}, new float[] {5.26f, 4.18f},
                new float[] {1.74f, 2.78f});
        ships.get(1).setTarget(sh2Points.get(0));
        ships.get(1).setCurrentTask(new PatrolPoints(ships.get(1), sh2Points));

        final float [] p1 = new float[] {0.06f, 5.96f};
        final float [] p2 = new float[] {0.73f, 4.92f};
        tanks.get(0).setTarget(p1);
        tanks.get(0).setCurrentTask(new PatrolTwoPoints( tanks.get(0), p1, p2));

    }
}
