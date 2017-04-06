package com.gameinstance.submarine.gameplay;

import android.media.MediaPlayer;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Helicopter;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Ship;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.Tank;
import com.gameinstance.submarine.gameplay.tasks.PatrolPoints;
import com.gameinstance.submarine.gameplay.tasks.PatrolTwoPoints;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 01.04.2017 16:38.
 *
 */
public class Level2 implements LevelLogic {
    private boolean completed = false;
    transient List<float []> items = Arrays.asList(new float [] {-5.55f, -0.71f},
            new float [] {-2.13f,  2.71f},
            new float [] { 0.37f,  6.99f},
            new float [] { 4.14f,  3.54f},
            new float [] { 0.72f, -0.44f});
    transient List<Sprite> itemSprites = new ArrayList<>();
    transient List<Marker> markers = new ArrayList<>();
    List<Boolean> itemVisibility = new ArrayList<>();
    transient MediaPlayer ambientMusic = MediaPlayer.create(GameManager.getRenderer().getActivityContext(),
            R.raw.molecular_dance_lite);
    transient List<Ship> ships;
    transient List<Tank> tanks;
    transient List<Helicopter> helicopters;

    @Override
    public void init() {
        for (int i = 0; i < items.size(); i++) {
            itemSprites.add(GameManager.addSprite(R.drawable.satelite, items.get(i)[0], items.get(i)[1], 0.1f, 0.1f));
            GameManager.getScene().getLayer("submarines").addSprite(itemSprites.get(itemSprites.size() - 1));
            itemVisibility.add(true);
            markers.add(GameManager.getGameplay().addMarker(items.get(i), false));
        }
        completed = false;
        ships = GameManager.getScene().getShips();
        tanks = GameManager.getScene().getTanks();
        helicopters = GameManager.getScene().getHelis();
        setupActors();
        ambientMusic.setLooping(true);
    }

    @Override
    public void run() {
        boolean notCompl = false;
        for (int i = 0; i < items.size(); i++) {
            if (itemVisibility.get(i)) {
                notCompl = true;
                float dist = MathUtils.distance(items.get(i), GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.1f) {
                    itemVisibility.set(i, false);
                    GameManager.getScene().getLayer("submarines").removeSprite(itemSprites.get(i));
                    GameManager.getGameplay().removeMarker(markers.get(i));
                    GameManager.getSoundManager().playSound(R.raw.two_rings_from_ship_bell, false);
                }
            }
        }
        completed = !notCompl;
        if (completed)
            ambientMusic.stop();
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public void restore() {
        completed = false;
        items = Arrays.asList(new float [] {-5.55f, -0.71f},
                new float [] {-2.13f,  2.71f},
                new float [] { 0.37f,  6.99f},
                new float [] { 4.14f,  3.54f},
                new float [] { 0.72f, -0.44f});
        itemSprites = new ArrayList<>();
        markers = new ArrayList<>();
        for (int i = 0; i < itemVisibility.size(); i++) {
            itemSprites.add(GameManager.addSprite(R.drawable.satelite, items.get(i)[0], items.get(i)[1], 0.1f, 0.1f));
            if (itemVisibility.get(i)) {
                GameManager.getScene().getLayer("submarines").addSprite(itemSprites.get(i));
                markers.add(GameManager.getGameplay().addMarker(items.get(i), false));
            }
        }
        ambientMusic = MediaPlayer.create(GameManager.getRenderer().getActivityContext(),
                R.raw.molecular_dance_lite);
        ambientMusic.setLooping(true);
    }

    @Override
    public void onClose() {
        if (ambientMusic.isPlaying())
            ambientMusic.stop();
    }

    @Override
    public void briefing() {
        GameManager.showMessage(R.string.briefing_level_1, -1.0f, 0.5f, 2000);
        GameManager.showMessage(R.string.briefing_level_2_1, -1.0f, 0.2f, 2000);
    }

    public void onShow() {
        ambientMusic.start();
    }

    private void setupActors() {
        List<float []> sh1Points = Arrays.asList(new float [] {-4.84f,  3.24f},
                new float [] {-2.65f,  3.68f},
                new float [] { 0.64f,  6.46f},
                new float [] { 3.88f,  4.80f},
                new float [] { 2.41f,  2.11f},
                new float [] { 0.55f,  1.40f},
                new float [] {-1.64f,  1.86f},
                new float [] {-2.37f,  0.25f},
                new float [] {-3.42f, -0.76f},
                new float [] {-4.54f, -1.40f},
                new float [] {-6.79f, -0.30f});
        ships.get(0).setTarget(sh1Points.get(0));
        ships.get(0).setCurrentTask(new PatrolPoints(ships.get(0), sh1Points));

        List<float []> sh2Points = Arrays.asList(new float [] {-3.53f, -0.78f},
                new float [] {-1.88f,  1.06f},
                new float [] { 0.24f,  1.63f},
                new float [] { 2.54f,  2.02f},
                new float [] { 5.13f,  1.63f},
                new float [] { 5.48f, -0.09f},
                new float [] { 3.75f, -3.24f},
                new float [] { 1.67f, -3.63f},
                new float [] {-0.99f, -3.52f},
                new float [] {-3.70f, -2.46f});
        ships.get(1).setTarget(sh2Points.get(0));
        ships.get(1).setCurrentTask(new PatrolPoints(ships.get(1), sh2Points));

        final float [] p1 = new float [] { 2.24f,  4.30f};
        final float [] p2 = new float [] { 0.48f,  4.80f};
        tanks.get(0).setTarget(p1);
        tanks.get(0).setCurrentTask(new PatrolTwoPoints( tanks.get(0), p1, p2));

        final float [] p3 = new float [] { 5.04f, -2.94f};
        final float [] p4 = new float [] {-4.52f,  6.94f};
        helicopters.get(0).setTarget(p3);
        helicopters.get(0).setCurrentTask(new PatrolTwoPoints( helicopters.get(0), p3, p4));

    }
}
