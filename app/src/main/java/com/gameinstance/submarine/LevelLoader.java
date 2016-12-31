package com.gameinstance.submarine;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;

import com.gameinstance.submarine.utils.MathUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gringo on 19.12.2016 19:49.
 *
 */

public class LevelLoader {
    static Activity activity;
    static Resources res;
    static Primitive texPrimitive;
    static Scene scene;
    static GameRenderer renderer;
    private static final int radarMapPixelSize = 512;

    private static final Map<String, Integer> shipSrites;
    static {
        shipSrites = new HashMap<>();
        shipSrites.put("default", R.drawable.ship1);
    }

    private static final Map<String, Integer> tankSprites;
    static {
        tankSprites = new HashMap<>();
        tankSprites.put("default", R.drawable.tank1);
    }

    private static final Map<String, Integer []> heliSprites;
    static {
        heliSprites = new HashMap<>();
        heliSprites.put("default", new Integer[] {R.drawable.heli1, R.drawable.heli11});
    }

    public static void loadLevel(Activity a, int levelId) {
        GameManager.setLevelId(levelId);
        activity = a;
        res = activity.getResources();
        scene = GameManager.getScene();
        renderer = GameManager.getRenderer();
        texPrimitive = GameManager.getTexPrimitive();
        InputStream inputStream = res.openRawResource(levelId);
        byte[] b;
        try {
            b = new byte[inputStream.available()];
            inputStream.read(b);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка ввода/вывода при чтении файла уровня");
        }
        String jsonString = new String(b);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            loadLandscape(jsonObject);
            loadSubmarine(jsonObject);
            if (jsonObject.has("ships")) {
                JSONArray shipArray = jsonObject.getJSONArray("ships");
                    for (int i = 0; i < shipArray.length(); i++) {
                        loadShip(shipArray.getJSONObject(i));
                    }
            }
            if (jsonObject.has("tanks")) {
                JSONArray tankArray = jsonObject.getJSONArray("tanks");
                for (int i = 0; i < tankArray.length(); i++) {
                    loadTank(tankArray.getJSONObject(i));
                }
            }
            if (jsonObject.has("helis")) {
                JSONArray heliArray = jsonObject.getJSONArray("helis");
                for (int i = 0; i < heliArray.length(); i++) {
                    loadHelicopter(heliArray.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка чтения json уровня");
        }
        GameManager.setCamera();
        InputController.setMaxOrder(100);
        InputController.setMinOrder(0);
        //final MediaPlayer mp = MediaPlayer.create(GameManager.getRenderer().getActivityContext(),
                //R.raw.music1);
        //mp.start();
    }


    private static void loadLandscape(JSONObject jsonObject)
            throws JSONException {
        Layer landscape_back = scene.getLayer("landscape_back");
        Layer landscape = scene.getLayer("landscape");
        Layer radarmap = scene.getLayer("radarmap");
        String background =  jsonObject.getString("background");
        int backgroundR = res.getIdentifier(background, "drawable", activity.getPackageName());
        String foreground = jsonObject.getString("foreground");
        int foregroundR = res.getIdentifier(foreground, "drawable", activity.getPackageName());
        Sprite [] landscpB = GameManager.createLandScape(backgroundR, 64, 1, texPrimitive);
        List<Sprite> landslistB = new ArrayList<>(Arrays.asList(landscpB));
        Sprite [] landscp = GameManager.createLandScape(foregroundR, 64, 1, texPrimitive);
        List<Sprite> landslist = new ArrayList<>(Arrays.asList(landscp));
        landscape_back.addSprites(landslistB);
        landscape.addSprites(landslist);
        Sprite radarmapBackground = GameManager.createRadarMap(foregroundR, radarMapPixelSize, texPrimitive);
        radarmap.addSprite(radarmapBackground);
    }

    private static void loadSubmarine(JSONObject jsonObject) throws JSONException {
        Layer mobs_back = scene.getLayer("mobs_back");
        Layer submarines = scene.getLayer("submarines");
        Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
        Sprite submarineBack = new Sprite(renderer, R.drawable.submarine, primitiveMap, 0.2f, 0.2f);
        mobs_back.addSprite(submarineBack);
        submarines.addSprite(submarineBack);
        final Submarine submarineMovable = new Submarine(submarineBack,
                new int[] {R.drawable.submarine, R.drawable.submarine1, R.drawable.submarine2},
                renderer);
        scene.addMovable(submarineMovable);
        GameManager.setSubmarineMovable(submarineMovable);
        JSONObject jsSubmarine = jsonObject.getJSONObject("submarine");
        float x = (float)jsSubmarine.getDouble("x");
        float y = (float)jsSubmarine.getDouble("y");
        float angle = (float)jsSubmarine.getDouble("angle");
        submarineMovable.getSprite().setPosition(x, y);
        submarineMovable.setAngle(angle);
    }

    private static void loadShip(JSONObject jsShip) throws JSONException {
        Layer mobs_back = scene.getLayer("mobs_back");
        Layer ships_and_tanks = scene.getLayer("ships_and_tanks");
        Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
        float x = (float)jsShip.getDouble("x");
        float y = (float)jsShip.getDouble("y");
        float angle = (float)jsShip.getDouble("angle");
        String shipType = null;
        if (jsShip.has("type")) {
            shipType = jsShip.getString("type");
        }
        final Sprite shipBack = new Sprite(renderer, getShipTexId(shipType), primitiveMap, 0.4f, 0.4f);
        mobs_back.addSprite(shipBack);
        ships_and_tanks.addSprite(shipBack);
        final Ship shipMovable = new Ship(shipBack, shipType);
        scene.addMovable(shipMovable);
        shipMovable.getSprite().setPosition(x, y);
        shipMovable.setAngle(angle);
        addViewCircle(shipMovable);

        final float [] p1 = new float[] {-1.37f, -0.54f};
        final float [] p2 = new float[] {-2.57f, -0.94f};
        shipMovable.setTarget(p1);
        shipMovable.setCurrentTask(new MobTask(shipMovable) {
            @Override
            public void run() {
                if (MathUtils.coordEquals(shipMovable.getTarget(), p1)) {
                    float dist = MathUtils.distance(p1, shipMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        shipMovable.setTarget(p2);
                    }
                }
                if (MathUtils.coordEquals(shipMovable.getTarget(), p2)) {
                    float dist = MathUtils.distance(p2, shipMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        shipMovable.setTarget(p1);
                    }
                }
            }
        });
    }

    private static void loadTank(JSONObject jsTank) throws JSONException {
        Layer mobs_back = scene.getLayer("mobs_back");
        Layer ships_and_tanks = scene.getLayer("ships_and_tanks");
        Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
        float x = (float)jsTank.getDouble("x");
        float y = (float)jsTank.getDouble("y");
        float angle = (float)jsTank.getDouble("angle");
        String tankType = null;
        if (jsTank.has("type")) {
            tankType = jsTank.getString("type");
        }
        Sprite tankBack = new Sprite(renderer, getTankTexId(tankType), primitiveMap, 0.3f, 0.3f);
        mobs_back.addSprite(tankBack);
        ships_and_tanks.addSprite(tankBack);
        final Tank tankMovable = new Tank(tankBack, tankType);
        scene.addMovable(tankMovable);
        tankMovable.getSprite().setPosition(x, y);
        tankMovable.setAngle(angle);
        addViewCircle(tankMovable);

        final float [] p1 = new float[] {0.47f, 1.67f};
        final float [] p2 = new float[] {0.57f, 2.9f};
        tankMovable.setTarget(p1);
        tankMovable.setCurrentTask(new MobTask(tankMovable) {
            @Override
            public void run() {
                if (MathUtils.coordEquals(tankMovable.getTarget(), p1)) {
                    float dist = MathUtils.distance(p1, tankMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        tankMovable.setTarget(p2);
                    }
                }
                if (MathUtils.coordEquals(tankMovable.getTarget(), p2)) {
                    float dist = MathUtils.distance(p2, tankMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        tankMovable.setTarget(p1);
                    }
                }
            }
        });
    }

    private static void loadHelicopter(JSONObject jsHeli) throws JSONException {
        Layer mobs_back = scene.getLayer("mobs_back");
        Layer aircrafts = scene.getLayer("aircrafts");
        Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
        float x = (float)jsHeli.getDouble("x");
        float y = (float)jsHeli.getDouble("y");
        float angle = (float)jsHeli.getDouble("angle");
        String heliType = null;
        if (jsHeli.has("type")) {
            heliType = jsHeli.getString("type");
        }
        Sprite heliBack = new Sprite(renderer, getHeliTexId(heliType)[0], primitiveMap, 0.3f, 0.3f);
        mobs_back.addSprite(heliBack);
        aircrafts.addSprite(heliBack);
        final Helicopter heliMovable = new Helicopter(heliBack, heliType);
        scene.addMovable(heliMovable);
        heliMovable.getSprite().setPosition(x, y);
        heliMovable.setAngle(angle);
        Animation heliAnimation = new Animation(200, true, getHeliTexId(heliType));
        heliBack.setAnimation(heliAnimation);
        addViewCircle(heliMovable);

        final float [] p1 = new float[] {0.67f, 0.0f};
        final float [] p2 = new float[] {-1.67f, -0.5f};
        heliMovable.setTarget(p1);
        heliMovable.setCurrentTask(new MobTask(heliMovable) {
            @Override
            public void run() {
                if (MathUtils.coordEquals(heliMovable.getTarget(), p1)) {
                    float dist = MathUtils.distance(p1, heliMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        heliMovable.setTarget(p2);
                    }
                }
                if (MathUtils.coordEquals(heliMovable.getTarget(), p2)) {
                    float dist = MathUtils.distance(p2, heliMovable.getSprite().getPosition());
                    if (dist < 0.1f) {
                        heliMovable.setTarget(p1);
                    }
                }
            }
        });
    }

    private static Integer getShipTexId(String shipType) {
        if (shipType == null)
            shipType = "default";
        Integer i = shipSrites.get(shipType);
        return i != null ? i : shipSrites.get("default");
    }

    private static Integer getTankTexId(String tankType) {
        if (tankType == null)
            tankType = "default";
        Integer i = tankSprites.get(tankType);
        return i != null ? i : tankSprites.get("default");
    }

    private static Integer [] getHeliTexId(String heliType) {
        if (heliType == null)
            heliType = "default";
        Integer [] i = heliSprites.get(heliType);
        return i != null ? i : heliSprites.get("default");
    }

    private static void addViewCircle(Movable movable) {
        Layer submarines = scene.getLayer("submarines");
        Sprite viewCircle = new Sprite(GameManager.getRenderer(), R.drawable.viewcircle,
                GameManager.getMovablePrimitiveMap(), 0.5f, 0.5f);
        movable.setViewCircle(viewCircle);
        movable.setViewCircleVisible(true, submarines);
    }
}
