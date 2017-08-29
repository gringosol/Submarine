package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.Animation;
import com.gameinstance.submarine.Button;
import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.GameRenderer;
import com.gameinstance.submarine.Layer;
import com.gameinstance.submarine.Movable;
import com.gameinstance.submarine.Primitive;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Scene;
import com.gameinstance.submarine.Ship;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.Submarine;
import com.gameinstance.submarine.gameplay.tasks.GoToPointByRouteTask;
import com.gameinstance.submarine.gameplay.tasks.MobTask;
import com.gameinstance.submarine.ui.TextLine;
import com.gameinstance.submarine.utils.MathUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    List<Sprite> strapSprites = new ArrayList<>();
    Sprite briefingSprite;
    AbstractLevel currentLevel = null;
    Map<String, AbstractLevel> levels = new HashMap<>();
    String languageOption = "";
    List<Marker> markers = new ArrayList<>();
    Sprite radarHudSprite;
    Sprite radarArrowSprite;
    Movable radarArrowMovable;
    int totalScore = 0;
    static int SCORE_STEP = 100;
    int [] straps = new int[] {R.drawable.strap_1, R.drawable.strap_2, R.drawable.strap_3};
    GameRenderer renderer;
    int empCount = 0;
    int baitCount = 0;
    Button empButton;
    Button baitButton;
    private static final float bSize = 0.4f;
    private static final float empRadius = 4.0f;
    private static final float baitRadius = 10.0f;
    private static final int empDelay = 10000;
    private static final int baitDelay = 15000;
    List<Timer> events = new ArrayList<>();
    Map<Timer, Integer> eventCountMap = new HashMap<>();
    Sprite baitSprite;
    List<SeaPackage> packages = new ArrayList<>();
    TextLine textEmp;
    TextLine textBait;
    Sprite blinkingArrow;

    public void init() {
        renderer = GameManager.getRenderer();
        scene = GameManager.getScene();
        missionFailedSprite = GameManager.addSprite(R.drawable.failed, 0, 0, 4.0f, 2.67f);
        missionPassedSprite = GameManager.addSprite(R.drawable.levelup, 0, 0, 4.0f, 2.67f);
        for (int strap : straps) {
            strapSprites.add(GameManager.addSprite(strap, 0, 0, 1.0f, 1.0f));
        }

        briefingSprite = GameManager.addSprite(R.drawable.briefing, 0, 0, 4.0f, 2.0f);
        levels.clear();
        levels.put("testlevel", new Level1());
        levels.put("testlevel2", new Level2());
        levels.put("lev1", new Lev1());
        Layer hud = scene.getLayer("hud");
        float leftScreenSide = -1 / renderer.getAspect();
        if (empButton == null) {
            empButton = new Button(renderer, new int [] {R.drawable.flash, R.drawable.flash1},
                    GameManager.getMovablePrimitiveMap(), bSize, bSize, new Button.ClickListener() {
                @Override
                public void onClick() {
                    applyEmp();
                }
            }, new float[] {leftScreenSide + bSize, 0.5f});
            hud.addSprite(empButton);
            empButton.setVisible(false);
            empButton.setTimerInterval(5000);
        }
        if (baitButton == null) {
            baitButton = new Button(renderer, new int [] {R.drawable.bait, R.drawable.bait1},
                    GameManager.getMovablePrimitiveMap(), bSize, bSize, new Button.ClickListener() {
                @Override
                public void onClick() {
                    applyBait();
                }
            }, new float[] {leftScreenSide + bSize, -0.5f});
            hud.addSprite(baitButton);
            baitButton.setVisible(false);
            baitButton.setTimerInterval(5000);
        }
        if (baitSprite == null ){
            GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
                @Override
                public void run() {
                    baitSprite = GameManager.addSprite(R.drawable.soundcircle, 0, 0, 0.25f, 0.25f);
                }
            });
        }
        blinkingArrow = GameManager.addSprite(R.drawable.yellowarrow, 0, 0, 0.5f, 0.5f);
        blinkingArrow.setAnimation(new Animation(300, true, R.drawable.yellowarrow, R.drawable.yellowarrow1));
        blinkingArrow.setVisible(false);
        GameManager.getScene().getLayer("aircrafts").addSprite(blinkingArrow);
    }

    public void update(){
        if (!scene.getLayerSets().get("Front").getEnabled() || paused)
            return;
        if (currentLevel != null) {
            currentLevel.runLevel();
            for (Marker marker : markers) {
                marker.update();
            }
            if (currentLevel.isCompleted())
                missionPassed();
        }
        for (SeaPackage seaPackage : packages) {
            seaPackage.update();
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
        GameManager.getScene().deactivateMovables();
        //выводим звуковое опопвещение и надпись
        GameManager.showMessage(R.string.level_complete, -1.0f, 0.5f, 2000);
        GameManager.getSoundManager().playSound(R.raw.two_rings_from_ship_bell, false);
        if (currentLevel != null) {
            totalScore = totalScore + currentLevel.getScore();
        }
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
                        strapSprite = null;
                        if (currentLevel != null) {
                            if (totalScore - currentLevel.getTotalScore() >= SCORE_STEP) {
                                int n = totalScore / SCORE_STEP - 1;
                                if (n < 0)
                                    n= 0;
                                if (n >= straps.length)
                                    n = straps.length - 1;
                                strapSprite = strapSprites.get(n);
                                scene.getLayer("hud").addSprite(strapSprite);
                            }
                        }
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
                                        if (strapSprite != null)
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
                addRadarHud();
                if (currentLevel != null) {
                    currentLevel.onShow();
                    GameManager.saveGame(GameManager.AUTO_SAVE);
                }
            }
        }, 5000);

    }

    public void addBriefSprite() {
        scene.getLayer("hud").addSprite(briefingSprite);
    }

    public void missionFailed(){
        if (currentLevel != null)
            currentLevel.onFail();
        GameManager.getScene().deactivateMovables();
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
            refreshVisibility(submarine);
        }
    }

    public void refreshVisibility(Submarine submarine) {
        for (Movable movable : scene.getMovables()) {
            if (movable.getIsEnemy() && movable.getViewRadius() != 0.01f) {
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

    public void setCurrentLevel(AbstractLevel level) {
        currentLevel = level;
    }

    public LevelLogic getCurrentLevel() {
        return currentLevel;
    }

    public Map<String, AbstractLevel> getLevels() {
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

    public void addRadarHud() {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                GameRenderer renderer = GameManager.getRenderer();
                float radarScale = GameManager.radarScale;
                Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
                if (radarHudSprite == null)
                    radarHudSprite = new Sprite(renderer, R.drawable.radarhud, primitiveMap,
                        2.0f / radarScale, 2.0f / radarScale);
                scene.getLayer("radarhud").addSprite(radarHudSprite);
                if (radarArrowSprite == null)
                    radarArrowSprite = new Sprite(renderer, R.drawable.radararrow, primitiveMap,
                        2.0f / radarScale, 2.0f / radarScale);
                scene.getLayer("radarhud").addSprite(radarArrowSprite);
                if (radarArrowMovable == null) {
                    radarArrowMovable = new Movable(radarArrowSprite) {
                        @Override
                        public void update() {
                            setAngle(getAngle() - 0.5f);
                        }
                    };
                    scene.addMovable(radarArrowMovable);
                    radarArrowMovable.setCollide(false);
                }
            }
        });
    }

    public int getTotalScore() {
        return totalScore;
    }

    public String getDataToSave() {
        JSONObject gameplayData = new JSONObject();
        try {
            gameplayData.put("empCount", empCount);
            return gameplayData.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void restoreSavedData(String data) {
        try {
            JSONObject gameplayData = new JSONObject(data);
            if (gameplayData.has("empCount")) {
                empCount = gameplayData.getInt("empCount");
                if (empCount > 0) {
                    empButton.setVisible(true);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void addEmp() {
        if (!empButton.getVisible()) {
            empButton.setVisible(true);
        }
        empCount++;
        textEmp = updateTextCount(textEmp, empCount, empButton.getPosition());
    }

    public void applyEmp() {
        if (empCount > 0) {
            empCount--;
            textEmp = updateTextCount(textEmp, empCount, empButton.getPosition());
            final List<Movable> enemyMovables = getEnemyMovablesInRadius(empRadius);
            Timer restoreTimer = new Timer();
            final Submarine submarine = GameManager.getSubmarineMovable();
            final Sprite empSprite = GameManager.addSprite(R.drawable.viewcircle,
                    submarine.getSprite().getPosition()[0], submarine.getSprite().getPosition()[1],
                    0.25f, 0.25f);
            GameManager.getSoundManager().playSound(R.raw.up_and_high_beep, false);
            scene.getLayer("submarines").addSprite(empSprite);
            scheduleEvent(new Runnable() {
                @Override
                public void run() {
                    scene.getLayer("submarines").removeSprite(empSprite);
                    for (Movable movable : enemyMovables) {
                        movable.setMotionEnabled(false);
                        movable.setViewRadius(0.01f);
                    }
                }
            }, 500);
            scheduleEvent(new Runnable() {
                @Override
                public void run() {
                    float sx = empSprite.getScaleX();
                    empSprite.setScale(sx * 1.5f, sx * 1.5f);
                }
            }, 50, 10);
            restoreTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for(Movable movable : enemyMovables) {
                        movable.setMotionEnabled(true);
                        movable.setViewRadius(0.02f);
                        refreshVisibility(submarine);
                    }
                }
            }, empDelay);
            if (empCount <= 0 && empButton != null) {
                empButton.setVisible(false);
            }
        }
    }

    public List<Movable> getEnemyMovablesInRadius(float empRadius, Class<? extends Movable> movClass) {
        List<Movable> movables = new ArrayList<>();
        for (Movable movable : GameManager.getScene().getMovables()) {
            float dist = MathUtils.distance(movable.getSprite().getPosition(),
                    GameManager.getSubmarineMovable().getSprite().getPosition());
            if (movable.getIsEnemy() && dist <= empRadius
                    && (movClass == null || movable.getClass().equals(movClass))) {
                movables.add(movable);
            }
        }
        return movables;
    }

    public List<Movable> getEnemyMovablesInRadius(float empRadius) {
        return getEnemyMovablesInRadius(empRadius, null);
    }

    public void scheduleEvent(final Runnable runnable, int delay) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(task, delay);
        events.add(timer);
    }

    public void scheduleEvent(final Runnable runnable, int period, final int count) {
        final Timer timer = new Timer();
        if (count != 0)
            eventCountMap.put(timer, 0);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Integer curCnt = eventCountMap.get(timer);
                if (curCnt == null || count == 0) {
                    runnable.run();
                } else {
                    if (curCnt < count) {
                        runnable.run();
                        curCnt++;
                        eventCountMap.put(timer, curCnt);
                    }
                }
            }
        };
        timer.schedule(task, period, period);
        events.add(timer);
    }

    public void clearEvents() {
        for (Timer timer : events) {
            timer.cancel();
        }
        events.clear();
        eventCountMap.clear();
    }

    public void addBait() {
        if (!baitButton.getVisible()) {
            baitButton.setVisible(true);
        }
        baitCount++;
        textBait = updateTextCount(textBait, baitCount, baitButton.getPosition());
    }

    public void applyBait() {
        if (baitCount > 0) {
            baitCount--;
            textBait = updateTextCount(textBait, baitCount, baitButton.getPosition());
            final List<Movable> enemyMovables = getEnemyMovablesInRadius(baitRadius, Ship.class);
            for (final Movable movable : enemyMovables) {
                final MobTask currentTask  = movable.getCurrentTask();
                final MobTask backTask = new GoToPointByRouteTask(movable,
                        GameManager.getSubmarineMovable().getSprite().getPosition(),
                        movable.getSprite().getPosition()) {
                    @Override
                    public void onComplete() {
                        movable.setCurrentTask(currentTask);
                        currentTask.onRestore();
                    }
                };
                movable.setCurrentTask(new GoToPointByRouteTask(movable, null,
                GameManager.getSubmarineMovable().getSprite().getPosition()) {
                    @Override
                    public void onComplete() {
                        movable.setCurrentTask(backTask);
                    }
                });
            }
            final Submarine submarine = GameManager.getSubmarineMovable();
            float [] subPos = submarine.getSprite().getPosition();
            baitSprite.setPosition(subPos[0], subPos[1]);
            scene.getLayer("submarines").addSprite(baitSprite);
            final int sId = GameManager.getSoundManager().playSound(R.raw.sonar, true);
            scheduleEvent(new Runnable() {
                    @Override
                    public void run() {
                        scene.getLayer("submarines").removeSprite(baitSprite);
                        GameManager.getSoundManager().stopSound(sId);
                    }
                }, baitDelay);
                scheduleEvent(new Runnable() {
                    @Override
                    public void run() {
                        float sx = baitSprite.getScaleX();
                        if (sx > 1.5f) {
                            sx = 1.0f / 1.05f;
                        }
                        baitSprite.setScale(sx * 1.05f, sx * 1.05f);
                        }
                }, 50, baitDelay / 50);
            if (baitCount == 0) {
                baitButton.setVisible(false);
            }
        }
    }

    public void addSeaPackage(final float [] pos, final int texId, final Runnable action) {
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                SeaPackage seaPackage = new SeaPackage(pos, texId, action);
                GameManager.getScene().getLayer("landscape").addSprite(seaPackage.sprite);
                packages.add(seaPackage);
            }
        });
    }

    public void clearPackages() {
        packages.clear();
    }

    public void loadCustomLevelData(JSONObject jsObject) {
        try {
            if (jsObject.has("emps")) {
                JSONArray emps = jsObject.getJSONArray("emps");
                for (int i = 0; i < emps.length(); i++) {
                    JSONObject jsEmp = emps.getJSONObject(i);
                    Runnable action = new Runnable() {
                        @Override
                        public void run() {
                            GameManager.getSoundManager().playSound(R.raw.up_and_high_beep, false);
                            addEmp();
                        }
                    };
                    loadSeaPachage(jsEmp, action, R.drawable.flash_trnsp);
                }
            }
            if (jsObject.has("baits")) {
                JSONArray baits = jsObject.getJSONArray("baits");
                for (int i = 0; i < baits.length(); i++) {
                    JSONObject jsBait = baits.getJSONObject(i);
                    Runnable action =  new Runnable() {
                        @Override
                        public void run() {
                            GameManager.getSoundManager().playSound(R.raw.up_and_high_beep, false);
                            addBait();
                        }
                    };
                    loadSeaPachage(jsBait, action, R.drawable.bait_trnsp);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSeaPachage(JSONObject jsPackage, Runnable action, int texId) throws JSONException{
        float[] pos = new float[2];
        pos[0] = (float) jsPackage.getDouble("x");
        pos[1] = (float) jsPackage.getDouble("y");
        addSeaPackage(pos, texId, action);
    }

    private TextLine updateTextCount(TextLine textLine, Integer number, float [] pos) {
        if (textLine != null) {
            GameManager.getScene().getLayer("hud").removeTextLine(textLine);
            textLine = null;
        }
        if (number > 1) {
            textLine = new TextLine(number.toString(), pos, 0.2f, renderer);
            GameManager.getScene().getLayer("hud").addTextline(textLine);
        }
        return textLine;
    }

    public Sprite getBlinkingArrow() {
        return blinkingArrow;
    }
}
