package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 23.09.2017 20:07.
 *
 */

public class Lev2 extends AbstractLevel {
    float [] targetCenter = new float [] { -5.06f, 3.84f};
    float [] currentMarkerPosition = new float[2];
    transient List<float[]> sh1points;
    transient List<float[]> sh2points;

    int currentTarget = 0;
    transient Marker marker;
    long startTime;

    @Override
    public void init() {
        marker = GameManager.getGameplay().addMarker(targetCenter, true);
    }

    @Override
    public void run() {
        switch (currentTarget) {
            case 0:
                if (isSubmarineInPoint(targetCenter, 0.8f)) {
                    currentTarget++;
                    setMarkerPosition(targetCenter[0], targetCenter[1]);
                    startTime = System.currentTimeMillis();
                    GameManager.getGameplay().showBriefWindow(Arrays.asList(R.string.lev2_cc_found,
                            R.string.lev2_mission_completed));
                }
                break;
            case 1:
                if (System.currentTimeMillis() - startTime > 4000) {

                    currentTarget++;
                    completed = true;
                }
                break;

        }
    }

    @Override
    public void restore() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetCenter[0], targetCenter[1]}, true);
        if (currentMarkerPosition != null) {
            setMarkerPosition(currentMarkerPosition[0], currentMarkerPosition[1]);
        }
    }

    @Override
    public void briefing() {

    }

    @Override
    public void onShow() {
        super.onShow();
        GameManager.getGameplay().showBriefWindow(Arrays.asList(R.string.lev2_find_command_center));
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
