package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Scene;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.Submarine;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    Sprite strapSprite;
    Sprite briefingSprite;
    LevelLogic currentLevel = null;
    Map<String, LevelLogic> levels = new HashMap<>();
    String languageOption = "";
    List<Marker> markers = new ArrayList<>();

    public void init() {
        scene = GameManager.getScene();
        missionFailedSprite = GameManager.addSprite(R.drawable.failed, 0, 0, 4.0f, 2.67f);
        missionPassedSprite = GameManager.addSprite(R.drawable.levelup, 0, 0, 4.0f, 2.67f);
        strapSprite = GameManager.addSprite(R.drawable.strap_1, 0, 0, 1.0f, 1.0f);
        briefingSprite = GameManager.addSprite(R.drawable.briefing, 0, 0, 4.0f, 2.0f);
        levels.clear();
        levels.put("testlevel", new Level1());
        levels.put("testlevel2", new Level2());
    }

    public void update(){
        if (!scene.getLayerSets().get("Front").getEnabled() || paused)
            return;
        if (currentLevel != null) {
            currentLevel.run();
            for (Marker marker : markers) {
                marker.update();
            }
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

        //выводим звуковое опопвещение и надпись
        GameManager.showMessage(R.string.level_complete, -1.0f, 0.5f, 2000);
        GameManager.getSoundManager().playSound(R.raw.two_rings_from_ship_bell, false);

        Timer notifyTimer = new Timer();
        notifyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                scene.getLayer("hud").addSprite(missionPassedSprite);
                Timer levelupTimer = new Timer();
                levelupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //выводим погон и звук
                        scene.getLayer("hud").addSprite(strapSprite);
                        GameManager.getSoundManager().playSound(R.raw.up_and_high_beep, false);
                        Timer endGameTimer = new Timer();
                        endGameTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                                    @Override
                                    public void run() {
                                        GameManager.getRenderer().setPaused(true);
                                        scene.getLayer("hud").removeSprite(missionPassedSprite);
                                        scene.getLayer("hud").removeSprite(strapSprite);
                                        GameManager.getRenderer().setPaused(false);
                                    }
                                });

                                if (GameManager.getCurrentLevel() < GameManager.getLevelCount() - 1) {
                                    GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                                        @Override
                                        public void run() {
                                            scene.getLayer("hud").addSprite(briefingSprite);
                                            GameManager.nextLevel();
                                        }
                                    });
                                } else {
                                    GameEnding.init();
                                }
                            }
                        }, 3000);
                    }
                }, 1000);
            }
        }, 2000);

    }

    public void beforeNewLevel() {
        Timer briefTimer = new Timer();
        if (currentLevel != null) {
            currentLevel.briefing();
        }
        GameManager.getRenderer().setPaused(false);
        briefTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (briefingSprite != null)
                    scene.getLayer("hud").removeSprite(briefingSprite);
                paused = false;
                if (GameManager.getRenderer().getPaused())
                    GameManager.getRenderer().setPaused(false);
                if (currentLevel != null)
                    currentLevel.onShow();
            }
        }, 3000);

    }

    public void addBriefSprite() {
        scene.getLayer("hud").addSprite(briefingSprite);
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

    public String getLanguageOption() {
        return languageOption;
    }

    public void setLanguageOption(String v) {
        languageOption = v;
    }

    public Marker addMarker(float [] pos, boolean onLand) {
        Marker marker = new Marker(pos, onLand);
        marker.add();
        markers.add(marker);
        return marker;
    }

    public void removeMarker(Marker marker) {
        marker.remove();
        markers.remove(marker);
    }
}
