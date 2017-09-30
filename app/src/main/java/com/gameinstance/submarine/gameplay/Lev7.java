package com.gameinstance.submarine.gameplay;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.R;
import com.gameinstance.submarine.Sprite;
import com.gameinstance.submarine.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gringo on 23.09.2017 20:49.
 *
 */

public class Lev7 extends AbstractLevel {
    transient List<float []> targets = Arrays.asList(new float [] {-3.40f, -2.99f},
            new float [] { 4.38f,  1.63f},
            new float [] {-3.88f,  2.41f},
            new float [] { 0.70f,  4.14f});
    transient List<float []> items = Arrays.asList(new float [] {-1.97f, -2.83f},
            new float [] { 4.71f,  1.01f},
            new float [] {-4.03f,  1.84f},
            new float [] { 0.22f,  3.70f});
    transient List<Sprite> itemSprites = new ArrayList<>();
    transient List<Marker> markers = new ArrayList<>();
    List<Boolean> itemVisibility = new ArrayList<>();

    @Override
    public void init() {
        for (int i = 0; i < items.size(); i++) {
            itemSprites.add(GameManager.addSprite(R.drawable.receiver, items.get(i)[0], items.get(i)[1], 0.5f, 0.5f));
            itemSprites.get(i).setRotation((float)(Math.random() * 360.0));
            GameManager.getScene().getLayer("submarines").addSprite(itemSprites.get(itemSprites.size() - 1));
            itemVisibility.add(false);
            itemSprites.get(i).setVisible(false);
            markers.add(GameManager.getGameplay().addMarker(targets.get(i), true));
        }
    }

    @Override
    public void run() {
        boolean notCompl = false;
        for (int i = 0; i < items.size(); i++) {
            if (!itemVisibility.get(i)) {
                notCompl = true;
                float dist = MathUtils.distance(targets.get(i), GameManager.getSubmarineMovable()
                        .getSprite().getPosition());
                if (dist < 0.9f) {
                    itemVisibility.set(i, true);
                    itemSprites.get(i).setVisible(true);
                    GameManager.getGameplay().removeMarker(markers.get(i));
                    GameManager.getSoundManager().playSound(R.raw.two_rings_from_ship_bell, false);
                }
            }
        }
        completed = !notCompl;
    }

    @Override
    public void restore() {
        targets = Arrays.asList(new float [] {-3.40f, -2.99f},
                new float [] { 4.38f,  1.63f},
                new float [] {-3.88f,  2.41f},
                new float [] { 0.70f,  4.14f});
        items = Arrays.asList(new float [] {-5.55f, -0.71f},
                new float [] {-2.13f,  2.71f},
                new float [] { 0.37f,  6.99f},
                new float [] { 4.14f,  3.54f},
                new float [] { 0.72f, -0.44f});
        itemSprites = new ArrayList<>();
        markers = new ArrayList<>();
        for (int i = 0; i < itemVisibility.size(); i++) {
            itemSprites.add(GameManager.addSprite(R.drawable.receiver, items.get(i)[0], items.get(i)[1], 0.5f, 0.5f));
            itemSprites.get(i).setRotation((float)(Math.random() * 360.0));
            GameManager.getScene().getLayer("submarines").addSprite(itemSprites.get(i));
            if (itemVisibility.get(i)) {
                itemSprites.get(i).setVisible(true);
                markers.add(null);

            } else {
                markers.add(GameManager.getGameplay().addMarker(items.get(i), true));
                itemSprites.get(i).setVisible(false);
            }
        }
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
