package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Scene;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.Submarine;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 */
public class Gameplay {
    Scene scene;
    int submarineDepth = -1;
    boolean paused = false;
    Sprite missionFailedSprite;
    Sprite missionPassedSprite;
    LevelLogic currentLevel = null;
    Map<String, LevelLogic> levels = new HashMap<>();

    public void init() {
        scene = GameManager.getScene();
        missionFailedSprite = GameManager.addSprite(R.drawable.missionfailed, 0, 0, 4.0f, 2.67f);
        missionPassedSprite = GameManager.addSprite(R.drawable.missionpassed, 0, 0, 4.0f, 2.67f);
        levels.clear();
        levels.put("testlevel", new Level1());
    }

    public void update(){
        if (!scene.getLayerSets().get("Front").getEnabled() || paused)
            return;
        if (currentLevel != null) {
            currentLevel.run();
            if (currentLevel.isCompleted())
                missionPassed();
        }
    }

    public void updatePost(){
        if (!scene.getLayerSets().get("Front").getEnabled() || paused)
            return;
        refreshVisibility();
        checkVisibility();
    }

    public void missionPassed(){
        paused = true;
        currentLevel = null;
        scene.getLayer("hud").addSprite(missionPassedSprite);
        Timer endGameTimer = new Timer();
        endGameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scene.getLayer("hud").removeSprite(missionPassedSprite);
                GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GameManager.nextLevel();
                        paused = false;
                    }
                });
            }
        }, 3000);
    }

    public void missionFailed(){
        paused = true;
        scene.getLayer("hud").addSprite(missionFailedSprite);
        Timer endGameTimer = new Timer();
        endGameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scene.getLayer("hud").removeSprite(missionFailedSprite);
                GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        GameManager.clearLevel();
                        GameManager.showMainMenu(true);
                        paused = false;
                    }
                });
            }
        }, 2000);
    }

    public void checkVisibility() {
        Movable submarine = GameManager.getSubmarineMovable();
        for (Movable movable : scene.getMovables()) {
            if (!(movable instanceof Submarine) && movable.getIsEnemy()) {
                float dist = MathUtils.distance(movable.getSprite().getPosition(),
                        submarine.getSprite().getPosition());
                if (dist < movable.getViewRadius()) {
                    missionFailed();
                    break;
                }
            }
        }
    }

    public void refreshVisibility() {
        Submarine submarine = GameManager.getSubmarineMovable();
        if (submarineDepth != submarine.getDepth()) {
            submarineDepth = submarine.getDepth();
            for (Movable movable : scene.getMovables()) {
                if (movable.getIsEnemy()) {
                    switch (submarineDepth) {
                        case 0:
                            movable.setViewRadius(movable.getMaxRadius());
                            break;
                        case 1:
                            movable.setViewRadius(movable.getMaxRadius() * 0.6f);
                            break;
                        case 2:
                            movable.setViewRadius(submarine.getMotionEnabled() ?
                                    movable.getMaxRadius() * 0.2f : 0);
                            break;
                    }
                }
            }
        }
    }

    public void setCurrentLevel(LevelLogic level) {
        currentLevel = level;
    }

    public LevelLogic getCurrentLevel() {
        return currentLevel;
    }

    public Map<String, LevelLogic> getLevels() {
        return levels;
    }

    public void reinitGame() {
        resetSubmarineDepth();
    }

    public void resetSubmarineDepth() {
        submarineDepth = -1;
    }
}
