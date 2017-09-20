package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 20.08.2017 15:36.
 *
 */

public class Lev1 extends AbstractLevel {
    float [] targetStrait = new float[] {2.70f, 2.83f};
    float [] empPos = new float[] {-7.32f,  3.22f};
    float [] targetGate = new float[] {-3.46f, 0.46f};
    float [] targetExit = new float[] {-2.5f, -6.85f};
    float [] currentMarkerPosition = new float[2];
    transient List<float[]> sh1points;
    transient List<float[]> sh2points;
    transient List<float[]> h1points;
    transient List<float[]> sh3points;

    int currentTarget = 0;
    transient Marker marker;

    @Override
    public void init() {
        marker = GameManager.getGameplay().addMarker(targetStrait, true);
        setMarkerPosition(targetStrait[0], targetStrait[1]);
    }

    @Override
    public void run() {
        switch (currentTarget) {
            case 0:
                if (isSubmarineInPoint(targetStrait, 0.5f)) {
                    alarm();
                    //briefMessageGoToEmp
                    GameManager.getGameplay().showBriefWindow(Arrays.asList(R.string.lev1_plunge,
                            R.string.lev1_path_blocked, R.string.lev1_go_to_emp));
                    setEmpTarget();
                    currentTarget++;
                }
                break;
            case 1:
                if (hasEmp()) {
                    setGateTarget();
                    //briefMessageGoToGate
                    currentTarget++;
                }
                break;
            case 2:
                if (isSubmarineInPoint(targetGate, 0.3f)) {
                    //briefMessageApplyEmp
                    currentTarget++;
                }
                break;
            case 3:
                if (!hasEmp()) {
                    //briefMessageGoToExit
                    setExitTarget();
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

    private void setEmpTarget() {
        setMarkerPosition(empPos[0], empPos[1]);
        marker.showOnLand = false;
    }

    private boolean hasEmp() {
        return GameManager.getGameplay().empCount > 0;
    }

    private void setGateTarget() {
        marker.showOnLand = true;
        setMarkerPosition(targetGate[0], targetGate[1]);
    }

    private void setExitTarget() {
        setMarkerPosition(targetExit[0], targetExit[1]);
    }

    @Override
    public void restore() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetStrait[0], targetStrait[1]}, true);
        setMarkerPosition(currentMarkerPosition[0], currentMarkerPosition[1]);
    }

    @Override
    public void briefing() {

    }

    @Override
    public void onShow() {
        super.onShow();

    }

    @Override
    protected void setupActors() {
        sh1points = Arrays.asList(new float[]{6.75f, -3.59f}, new float[]{4.27f, -1.66f},
                new float[]{3.27f, 0.46f}, new float[]{5.61f, 0.25f}, new float[]{6.88f, -1.29f});


        sh2points = Arrays.asList(new float[]{6.88f, -1.29f}, new float[]{-2.24f, -4.85f},
                new float[]{-3.92f, -2.39f}, new float[]{-1.14f, -1.22f},  new float[]{1.62f, -1.10f},
                new float[]{3.73f, -1.84f},  new float[]{4.45f, -3.10f}, new float[]{4.10f, -5.03f});


        h1points = Arrays.asList(new float[]{-7.01f, 3.38f}, new float[]{0.18f, 5.49f},
                new float[]{7.25f, 5.38f});


        sh3points = Arrays.asList(new float[]{-6.84f, 2.30f}, new float[]{-1.75f, 1.56f},
                new float[]{1.69f, 2.30f}, new float[]{5.11f, 4.02f});

    }

    private void setMarkerPosition(float x, float y) {
        currentMarkerPosition = new float[]{x, y};
        marker.setPosition(currentMarkerPosition);
    }

    private void alarm() {
        ships.get(0).setTarget(sh1points.get(0));
        ships.get(0).setCurrentTask(new PatrolPoints(ships.get(0), sh1points));
        ships.get(1).setTarget(sh2points.get(0));
        ships.get(1).setCurrentTask(new PatrolPoints(ships.get(1), sh2points));
        helicopters.get(0).setTarget(h1points.get(0));
        helicopters.get(0).setCurrentTask(new PatrolPoints(helicopters.get(0), h1points));
        ships.get(2).setTarget(sh3points.get(0));
        ships.get(2).setCurrentTask(new PatrolPoints(ships.get(2), sh3points));
    }
}
