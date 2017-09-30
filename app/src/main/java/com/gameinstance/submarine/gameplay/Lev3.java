package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.List;

/**
 * Created by gringo on 23.09.2017 20:48.
 *
 */

public class Lev3 extends AbstractLevel {
    float [] targetBase = new float [] {-6.4f,  4.50f};
    float [] targetBase2 = new float [] {-6.58f,  4.14f};
    float [] targetExit = new float [] {-6.22f, -5.52f};
    float [] currentMarkerPosition = new float[2];
    transient List<float[]> sh1points;
    transient List<float[]> sh2points;
    transient List<float[]> h1points;
    transient List<float[]> sh3points;

    int currentTarget = 0;
    transient Marker marker;
    long startTime;

    @Override
    public void init() {
        marker = GameManager.getGameplay().addMarker(targetBase, true);
        setMarkerPosition(targetBase[0], targetBase[1]);
    }

    @Override
    public void run() {
        float dist;
        switch (currentTarget) {
            case 0:
                if (isSubmarineInPoint(targetBase, 0.3f)) {
                    GameManager.getSubmarineMovable().setMotionDenied(true);
                    tanks.get(0).getSprite().setVisible(true);
                    tanks.get(0).getSprite().setPosition(targetBase[0], targetBase[1]);
                    tanks.get(0).setTarget(targetBase2);
                    setMarkerPosition(targetBase2[0], targetBase2[1]);
                    tanks.get(0).setSpeed(0.01f);
                    currentTarget++;
                }
                break;
            case 1:
                dist = MathUtils.distance(targetBase2, tanks.get(0).getSprite().getPosition());
                if (dist <= 0.1f) {
                    startTime = System.currentTimeMillis();
                    tanks.get(0).getSprite().setVisible(false);
                    currentTarget++;
                }
                break;
            case 2:
                if (System.currentTimeMillis() - startTime > 3000) {
                    tanks.get(0).getSprite().setVisible(true);
                    tanks.get(0).setTarget(targetBase);
                    currentTarget++;
                }
                break;
            case 3:
                dist = MathUtils.distance(targetBase, tanks.get(0).getSprite().getPosition());
                if (dist <= 0.1f) {
                    tanks.get(0).getSprite().setVisible(false);
                    setMarkerPosition(targetExit[0], targetExit[1]);
                    GameManager.getSubmarineMovable().setMotionDenied(false);
                    currentTarget++;
                }
                break;
            case 4:
                if (isSubmarineInPoint(targetExit, 0.3f)) {
                    currentTarget++;
                    completed = true;
                }
                break;
        }
    }


    @Override
    public void restore() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetBase[0], targetBase[1]}, true);
        setMarkerPosition(currentMarkerPosition[0], currentMarkerPosition[1]);
    }

    @Override
    public void briefing() {

    }

    @Override
    public void onShow() {
        super.onShow();
        tanks.get(0).getSprite().setVisible(false);
    }

    @Override
    protected void setupActors() {
        /*sh1points = Arrays.asList(new float[]{6.75f, -3.59f}, new float[]{4.27f, -1.66f},
                new float[]{3.27f, 0.46f}, new float[]{5.61f, 0.25f}, new float[]{6.88f, -1.29f});


        sh2points = Arrays.asList(new float[]{6.88f, -1.29f}, new float[]{-2.24f, -4.85f},
                new float[]{-3.92f, -2.39f}, new float[]{-1.14f, -1.22f},  new float[]{1.62f, -1.10f},
                new float[]{3.73f, -1.84f},  new float[]{4.45f, -3.10f}, new float[]{4.10f, -5.03f});


        h1points = Arrays.asList(new float[]{-7.01f, 3.38f}, new float[]{0.18f, 5.49f},
                new float[]{7.25f, 5.38f});


        sh3points = Arrays.asList(new float[]{-6.84f, 2.30f}, new float[]{-1.75f, 1.56f},
                new float[]{1.69f, 2.30f}, new float[]{5.11f, 4.02f});*/

    }

    private void setMarkerPosition(float x, float y) {
        currentMarkerPosition = new float[]{x, y};
        marker.setPosition(currentMarkerPosition);
    }

    private void alarm() {
        /*ships.get(0).setTarget(sh1points.get(0));
        ships.get(0).setCurrentTask(new PatrolPoints(ships.get(0), sh1points));
        ships.get(1).setTarget(sh2points.get(0));
        ships.get(1).setCurrentTask(new PatrolPoints(ships.get(1), sh2points));
        helicopters.get(0).setTarget(h1points.get(0));
        helicopters.get(0).setCurrentTask(new PatrolPoints(helicopters.get(0), h1points));
        ships.get(2).setTarget(sh3points.get(0));
        ships.get(2).setCurrentTask(new PatrolPoints(ships.get(2), sh3points));*/
    }
}
