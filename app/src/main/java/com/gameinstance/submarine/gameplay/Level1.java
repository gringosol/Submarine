package com.gameinstance.submarine.gameplay;

import android.media.MediaPlayer;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;
import com.gameinstance.submarine.gameplay.tasks.PatrolTwoPoints;
import com.gameinstance.submarine.utils.MathUtils;

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

    @Override
    public void init() {
        marker = GameManager.getGameplay().addMarker(new float[] {targetIsland[0], targetIsland[1]}, true);
        ambientMusic = MediaPlayer.create(GameManager.getRenderer().getActivityContext(),
                R.raw.the_environment_lite);
    }

    @Override
    public void run() {
        float dist;
        switch (currentTarget) {
            case 0: //проверяем, доплыли ли до острова; если да, тормозим лодку, пускаем кузьмича
                dist = MathUtils.distance(targetIsland, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.2f) {
                    GameManager.getSubmarineMovable().setMotionEnabled(false);
                    tanks.get(1).setTarget(targetIsland1);
                    marker.setPosition(new float[]{targetHarbor[0], targetHarbor[1]});
                    currentTarget++;
                    tanks.get(1).setSpeed(0.003f);
                }
                break;
            case 1: //ждем Кузьмича, растормаживаем лодку, скрываем кузьмича
                dist = MathUtils.distance(targetIsland1, tanks.get(1).getSprite().getPosition());
                if (dist < 0.1f) {
                    GameManager.getScene().getLayer("ships_and_tanks").removeSprite(tanks.get(1).getSprite());
                    GameManager.getSubmarineMovable().setMotionEnabled(true);
                    currentTarget++;
                }
                break;
            case 2: //проверяем расстояние до берега; тормозим лодку, высаживаем Кузьмича, направляем кузьмича в сарай
                dist = MathUtils.distance(targetHarbor, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.2f) {
                    marker.setPosition(new float[]{targetCarrier[0], targetCarrier[1]});
                    GameManager.getSubmarineMovable().setMotionEnabled(false);
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
                    GameManager.getSubmarineMovable().setMotionEnabled(true);
                    startTime = System.currentTimeMillis();
                    currentTarget++;
                }
                break;
            case 7://доплываем до авианосца, тормозим лодку, показываем кузьмича, засекаем 2 сек
                dist = MathUtils.distance(targetCarrier, GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.3f) {
                    GameManager.getGameplay().removeMarker(marker);
                    GameManager.getSubmarineMovable().setMotionEnabled(false);
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
        ambientMusic = MediaPlayer.create(GameManager.getRenderer().getActivityContext(),
                R.raw.the_environment_lite);
    }

    @Override
    public void briefing() {
        GameManager.showMessage(R.string.briefing_level_1, -1.0f, 0.5f, 2000);
        GameManager.showMessage(R.string.briefing_level_1_1, -1.0f, 0.2f, 2000);
        GameManager.showMessage(R.string.briefing_level_1_2, -1.0f, -0.1f, 2000);
    }

    public void onShow() {
        super.onShow();
        GameManager.showMessage(R.string.go_to_marker, -1.0f, 0.5f, 3000);
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
