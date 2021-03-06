package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.gameplay.cutscene.CameraMotion;
import com.gameinstance.submarine.gameplay.cutscene.Cutscene;
import com.gameinstance.submarine.gameplay.cutscene.CutsceneAction;
import com.gameinstance.submarine.gameplay.cutscene.Idle;
import com.gameinstance.submarine.gameplay.cutscene.SetCameraToSprite;
import com.gameinstance.submarine.gameplay.cutscene.ShowMessage;
import com.gameinstance.submarine.gameplay.cutscene.ShowSprite;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;
import com.gameinstance.submarine.gameplay.tasks.PatrolTwoPoints;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 01.01.2017 13:07.
 *
 */
public class Level1 extends AbstractLevel {
    float [] targetIsland = new float[] {  -4.1f, 0.2f  };
    float [] targetIsland1 = new float[] {  -4.0f, 0.2f  };
    float [] targetHarbor = new float[] {  1.94f, 4.41f  };
    float [] targetHarbor1 = new float[] {  1.7f, 4.7f  };
    float [] targetHarbor2 = new float[] {  1.9f, 4.57f  };
    float [] targetCarrier = new float[] {  5.71f, -1.07f  };
    int currentTarget = 0;
    long startTime;

    transient Marker marker;
    float [] currentMarkerPosition = new float[2];

    @Override
    public void init() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetIsland[0], targetIsland[1]}, true);
        setMarkerPosition(targetIsland[0], targetIsland[1]);
        ambientMusic = GameManager.getSoundManager().addMediaPlayer(R.raw.the_environment_lite);
    }

    private void setMarkerPosition(float x, float y) {
        currentMarkerPosition = new float[]{x, y};
        marker.setPosition(currentMarkerPosition);
    }

    @Override
    public void run() {
        float dist;
        switch (currentTarget) {
            case 0: //проверяем, доплыли ли до острова; если да, тормозим лодку, пускаем кузьмича
                dist = MathUtils.distance(targetIsland, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.2f) {
                    GameManager.getSubmarineMovable().setMotionDenied(true);
                    tanks.get(1).setTarget(targetIsland1);
                    setMarkerPosition(targetHarbor[0], targetHarbor[1]);
                    currentTarget++;
                    tanks.get(1).setSpeed(0.01f);
                }
                break;
            case 1: //ждем Кузьмича, растормаживаем лодку, скрываем кузьмича
                dist = MathUtils.distance(targetIsland1, tanks.get(1).getSprite().getPosition());
                if (dist < 0.1f) {
                    GameManager.getScene().getLayer("ships_and_tanks").removeSprite(tanks.get(1).getSprite());
                    GameManager.getSubmarineMovable().setMotionDenied(false);
                    currentTarget++;
                }
                break;
            case 2: //проверяем расстояние до берега; тормозим лодку, высаживаем Кузьмича, направляем кузьмича в сарай
                dist = MathUtils.distance(targetHarbor, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.2f) {
                    setMarkerPosition(targetCarrier[0], targetCarrier[1]);
                    GameManager.getSubmarineMovable().setMotionDenied(true);
                    tanks.get(1).getSprite().setPosition(targetHarbor2[0], targetHarbor2[1]);
                    GameManager.getScene().getLayer("ships_and_tanks").addSprite(tanks.get(1).getSprite());

                    startTime = System.currentTimeMillis();
                    currentTarget++;
                }
                break;
            case 3://тормозим Кузьмича на 2 сек
                if (System.currentTimeMillis() - startTime > 1000) {
                    tanks.get(1).setTarget(targetHarbor1);
                    currentTarget++;
                }
                break;
            case 4: // если кузьмич в сарае, скрываем кузьмича, засекаем 2 сек
                dist = MathUtils.distance(targetHarbor1, tanks.get(1).getSprite().getPosition());
                if (dist < 0.1f) {
                    GameManager.getScene().getLayer("ships_and_tanks").removeSprite(tanks.get(1).getSprite());
                    startTime = System.currentTimeMillis();
                    currentTarget++;
                }
                break;
            case 5://если 2 сек прошло, направляем кузьмича к лодке
                if (System.currentTimeMillis() - startTime > 2000) {
                    GameManager.getScene().getLayer("ships_and_tanks").addSprite(tanks.get(1).getSprite());
                    tanks.get(1).setTarget(targetHarbor2);
                    currentTarget++;
                }
                break;
            case 6://ждем Кузьмича, растормаживаем лодку, скрываем кузьмича
                dist = MathUtils.distance(targetHarbor2, tanks.get(1).getSprite().getPosition());
                if (dist < 0.1f) {
                    GameManager.getScene().getLayer("ships_and_tanks").removeSprite(tanks.get(1).getSprite());
                    GameManager.getSubmarineMovable().setMotionDenied(false);
                    startTime = System.currentTimeMillis();
                    currentTarget++;
                }
                break;
            case 7://доплываем до авианосца, тормозим лодку, показываем кузьмича, засекаем 2 сек
                dist = MathUtils.distance(targetCarrier, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.3f) {
                    GameManager.getGameplay().removeMarker(marker);
                    GameManager.getSubmarineMovable().setMotionDenied(true);
                    tanks.get(1).setCollide(false);
                    tanks.get(1).getSprite().setPosition(ships.get(2).getSprite().getPosition()[0],
                            ships.get(2).getSprite().getPosition()[1]);
                    GameManager.getScene().getLayer("aircrafts").addSprite(tanks.get(1).getSprite());
                    startTime = System.currentTimeMillis();
                    currentTarget++;
                }
                break;
            case 8://миссия пройдена
                if (System.currentTimeMillis() - startTime > 2000) {
                    GameManager.getSubmarineMovable().setMotionDenied(false);
                    ambientMusic.stop();
                    completed = true;
                    currentTarget++;
                }
                break;
        }
    }

    @Override
    public void restore() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetIsland[0], targetIsland[1]}, true);
        setMarkerPosition(currentMarkerPosition[0], currentMarkerPosition[1]);
        ambientMusic = GameManager.getSoundManager().addMediaPlayer(R.raw.the_environment_lite);
        switch (currentTarget) {
            case 1:
                tanks.get(1).setTarget(targetIsland1);
                tanks.get(1).setSpeed(0.01f);
                break;
            case 6:
                tanks.get(1).setTarget(targetHarbor2);
                break;
        }
    }

    @Override
    public void briefing() {
        GameManager.showMessage(R.string.briefing_level_1, -1.0f, 0.5f, 4500);
        GameManager.showMessage(R.string.briefing_level_1_1, -1.0f, 0.2f, 4500);
        GameManager.showMessage(R.string.briefing_level_1_2, -1.0f, -0.1f, 4500);
    }

    public void onShow() {
        super.onShow();
        List<CutsceneAction> actions = new ArrayList<>();

        ShowMessage showMessage = new ShowMessage(R.string.go_to_marker, -1.0f, 0.5f, 3000);
        actions.add(showMessage);
        CameraMotion cameraMotion = new CameraMotion(targetIsland, 1.0f);
        actions.add(cameraMotion);
        ShowSprite showSprite = new ShowSprite(GameManager.getGameplay().getBlinkingArrow(),
                3000, targetIsland[0] - 0.25f, targetIsland[1] + 0.25f);
        actions.add(showSprite);
        Idle idle = new Idle(1000);
        actions.add(idle);

        ShowMessage showMessage1 = new ShowMessage(R.string.set_kuzia_to_harbor, -1.0f, 0.5f, 3000);
        actions.add(showMessage1);
        CameraMotion cameraMotion1 = new CameraMotion(targetHarbor, 1.0f);
        actions.add(cameraMotion1);
        ShowSprite showSprite1 = new ShowSprite(GameManager.getGameplay().getBlinkingArrow(),
                3000, targetHarbor[0] - 0.25f, targetHarbor[1] + 0.25f);
        actions.add(showSprite1);
        Idle idle1 = new Idle(1000);
        actions.add(idle1);

        ShowMessage showMessage2 = new ShowMessage(R.string.go_to_carrier, -1.0f, 0.5f, 3000);
        actions.add(showMessage2);
        CameraMotion cameraMotion2 = new CameraMotion(targetCarrier, 1.0f);
        actions.add(cameraMotion2);
        ShowSprite showSprite2 = new ShowSprite(GameManager.getGameplay().getBlinkingArrow(),
                3000, targetCarrier[0] - 0.25f, targetCarrier[1] + 0.25f);
        actions.add(showSprite2);
        Idle idle2 = new Idle(1000);
        actions.add(idle2);

        SetCameraToSprite setCameraToSprite = new SetCameraToSprite(GameManager.getSubmarineMovable().getSprite());
        actions.add(setCameraToSprite);
        cutscene = new Cutscene(actions);
    }

    @Override
    protected void setupActors() {
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
