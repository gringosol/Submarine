package com.gameinstance.submarine;

import android.app.Activity;
import android.content.res.Resources;

import com.gameinstance.submarine.gameplay.LevelLogic;
import com.gameinstance.submarine.gameplay.tasks.MobTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final int PIXELS_PER_UNIT_VISIBLE = 64;
    private static final int PIXELS_PER_UNIT_BACK = 64;

    private static final Map<String, Integer []> shipSrites;
    static {
        shipSrites = new HashMap<>();
        shipSrites.put("default", new Integer[] {R.drawable.ship1});
        shipSrites.put("carrier", new Integer [] {R.drawable.carrier});
    }

    private static final Map<String, Integer []> tankSprites;
    static {
        tankSprites = new HashMap<>();
        tankSprites.put("default", new Integer [] {R.drawable.tank1});
        tankSprites.put("agent", new Integer[] {R.drawable.kuzmich1, R.drawable.kuzmich2});
    }

    private static final Map<String, Integer []> heliSprites;
    static {
        heliSprites = new HashMap<>();
        heliSprites.put("default", new Integer[] {R.drawable.heli1, R.drawable.heli11});
    }

    public static void loadLevel(Activity a, int levelId, boolean withMobs) {
        Sprite loadingSprite = GameManager.addSprite(R.drawable.loading, 0, 0, 4.0f, 2.0f);
        GameManager.setLevelId(levelId);
        activity = a;
        res = activity.getResources();
        scene = GameManager.getScene();
        renderer = GameManager.getRenderer();
        texPrimitive = GameManager.getTexPrimitive();
        scene.getLayer("hud").addSprite(loadingSprite);
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
            if (withMobs) {
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
                if (jsonObject.has("levelname")) {
                    String levelname = jsonObject.getString("levelname");
                    if (GameManager.getGameplay().getLevels().containsKey(levelname)) {
                        LevelLogic levelLogic = GameManager.getGameplay().getLevels().get(levelname);
                        GameManager.getGameplay().setCurrentLevel(levelLogic);
                        levelLogic.commonInit();
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка чтения json уровня");
        }
        GameManager.setCamera();
        InputController.setMaxOrder(100);
        InputController.setMinOrder(0);
        if (GameManager.getGameplay() != null)
            GameManager.getGameplay().reinitGame();
        scene.getLayer("hud").removeSprite(loadingSprite);
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
        int foregroundMapR = res.getIdentifier(foreground, "raw", activity.getPackageName());
        float unitsize = 0.25f;
        if (jsonObject.has("unitsize")) {
            unitsize = (float)jsonObject.getDouble("unitsize");
        }
        Sprite [] landscpB = GameManager.createLandScape(backgroundR, PIXELS_PER_UNIT_BACK, unitsize * 4, texPrimitive);
        List<Sprite> landslistB = new ArrayList<>(Arrays.asList(landscpB));
        GameManager.readLandscapeJson(foregroundMapR);
        Sprite [] landscp = GameManager.createLandScapeTiled(foregroundR,  PIXELS_PER_UNIT_VISIBLE, unitsize, texPrimitive);
        List<Sprite> landslist = new ArrayList<>(Arrays.asList(landscp));
        landscape_back.addSprites(landslistB);
        landscape.addSprites(landslist);
        Sprite radarmapBackground = GameManager.createRadarMap(foregroundR, radarMapPixelSize, texPrimitive, PIXELS_PER_UNIT_VISIBLE);
        radarmap.addSprite(radarmapBackground);
    }

    private static void loadSubmarine(JSONObject jsonObject) throws JSONException {
        Layer mobs_back = scene.getLayer("mobs_back");
        Layer submarines = scene.getLayer("submarines");
        Map<Integer, Primitive> primitiveMap = GameManager.getMovablePrimitiveMap();
        JSONObject jsSubmarine = jsonObject.getJSONObject("submarine");
        float x = (float)jsSubmarine.getDouble("x");
        float y = (float)jsSubmarine.getDouble("y");
        float angle = (float)jsSubmarine.getDouble("angle");
        float w = 0.2f;
        float h = 0.2f;
        if (jsSubmarine.has("w")) {
            w = (float)jsSubmarine.getDouble("w");
        }
        if (jsSubmarine.has("h")) {
            h = (float)jsSubmarine.getDouble("h");
        }
        Sprite submarineBack = new Sprite(renderer, R.drawable.submarine, primitiveMap, w, h);
        mobs_back.addSprite(submarineBack);
        submarines.addSprite(submarineBack);
        final Submarine submarineMovable = new Submarine(submarineBack,
                new int[] {R.drawable.subm1, R.drawable.subm2, R.drawable.subm3},
                renderer);
        scene.addMovable(submarineMovable);
        GameManager.setSubmarineMovable(submarineMovable);
        submarineMovable.getSprite().setPosition(x, y);
        submarineMovable.setAngle(angle);
    }

    public static void loadShip(JSONObject jsShip) throws JSONException {
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
        float w = 0.4f;
        float h = 0.4f;
        if (jsShip.has("w")) {
            w = (float)jsShip.getDouble("w");
        }
        if (jsShip.has("h")) {
            h = (float)jsShip.getDouble("h");
        }
        Integer [] spriteIds = getShipTexId(shipType);
        final Sprite shipBack = new Sprite(renderer, spriteIds[0], primitiveMap, w, h);
        mobs_back.addSprite(shipBack);
        ships_and_tanks.addSprite(shipBack);
        final Ship shipMovable = new Ship(shipBack, shipType);
        scene.addMovable(shipMovable);
        shipMovable.getSprite().setPosition(x, y);
        shipMovable.setAngle(angle);
        if (spriteIds.length > 1) {
            Animation shipAnimation = new Animation(200, true, getShipTexId(shipType));
            shipBack.setAnimation(shipAnimation);
        }
        if (jsShip.has("enemy")) {
            shipMovable.setEnemy(jsShip.getInt("enemy") != 0);
        }
        if (shipMovable.getIsEnemy())
          addViewCircle(shipMovable);

        MobTask task = loadMobTask(shipMovable, jsShip);
        if (task != null) {
            shipMovable.setCurrentTask(task);
        }
    }

    public static void loadTank(JSONObject jsTank) throws JSONException {
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
        float w = 0.3f;
        float h = 0.3f;
        if (jsTank.has("w")) {
            w = (float)jsTank.getDouble("w");
        }
        if (jsTank.has("h")) {
            h = (float)jsTank.getDouble("h");
        }
        Integer [] spriteIds = getTankTexId(tankType);
        Sprite tankBack = new Sprite(renderer, spriteIds[0], primitiveMap, w, h);
        mobs_back.addSprite(tankBack);
        ships_and_tanks.addSprite(tankBack);
        final Tank tankMovable = new Tank(tankBack, tankType);
        scene.addMovable(tankMovable);
        tankMovable.getSprite().setPosition(x, y);
        tankMovable.setAngle(angle);
        if (spriteIds.length > 1) {
            Animation tankAnimation = new Animation(300, true, getTankTexId(tankType));
            tankBack.setAnimation(tankAnimation);
        }
        if (jsTank.has("enemy")) {
            tankMovable.setEnemy(jsTank.getInt("enemy") != 0);
        }
        if (tankMovable.getIsEnemy())
            addViewCircle(tankMovable);

        MobTask task = loadMobTask(tankMovable, jsTank);
        if (task != null) {
            tankMovable.setCurrentTask(task);
        }
    }

    public static void loadHelicopter(JSONObject jsHeli) throws JSONException {
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
        float w = 0.3f;
        float h = 0.3f;
        if (jsHeli.has("w")) {
            w = (float)jsHeli.getDouble("w");
        }
        if (jsHeli.has("h")) {
            h = (float)jsHeli.getDouble("h");
        }
        Sprite heliBack = new Sprite(renderer, getHeliTexId(heliType)[0], primitiveMap, w, w);
        mobs_back.addSprite(heliBack);
        aircrafts.addSprite(heliBack);
        final Helicopter heliMovable = new Helicopter(heliBack, heliType);
        scene.addMovable(heliMovable);
        heliMovable.getSprite().setPosition(x, y);
        heliMovable.setAngle(angle);
        Animation heliAnimation = new Animation(200, true, getHeliTexId(heliType));
        heliBack.setAnimation(heliAnimation);
        if (jsHeli.has("enemy")) {
            heliMovable.setEnemy(jsHeli.getInt("enemy") != 0);
        }
        if (heliMovable.getIsEnemy())
            addViewCircle(heliMovable);

        MobTask task = loadMobTask(heliMovable, jsHeli);
        if (task != null) {
            heliMovable.setCurrentTask(task);
        }
    }

    private static Integer [] getShipTexId(String shipType) {
        if (shipType == null)
            shipType = "default";
        Integer [] i = shipSrites.get(shipType);
        return i != null ? i : shipSrites.get("default");
    }

    private static Integer [] getTankTexId(String tankType) {
        if (tankType == null)
            tankType = "default";
        Integer [] i = tankSprites.get(tankType);
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

    private static MobTask loadMobTask(Movable mob, JSONObject jsMovable) {
        MobTask task = null;
        try {
            if (jsMovable.has("taskclass") && jsMovable.has("taskstate")) {
                try {
                    Class cl = Class.forName(jsMovable.getString("taskclass"));
                    Class<? extends MobTask> cl1 = cl;
                    JSONArray byteArray = jsMovable.getJSONArray("taskstate");
                    byte [] bytes = new byte[byteArray.length()];
                    for (int i = 0; i < byteArray.length(); i++) {
                        bytes[i] = (byte)byteArray.getInt(i);
                    }
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    try {
                        ObjectInputStream objectInputStream =
                                new ObjectInputStream(inputStream);
                        task = cl1.cast(objectInputStream.readObject());
                        task.setMob(mob);
                        task.onRestore();
                        objectInputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return task;
    }

    public static LevelLogic loadLevelState(JSONObject jsLevel) {
        LevelLogic level = null;
        try {
            if (jsLevel.has("levelclass") && jsLevel.has("levelstate")) {
                try {
                    Class cl = Class.forName(jsLevel.getString("levelclass"));
                    Class<? extends LevelLogic> cl1 = cl;
                    JSONArray byteArray = jsLevel.getJSONArray("levelstate");
                    byte [] bytes = new byte[byteArray.length()];
                    for (int i = 0; i < byteArray.length(); i++) {
                        bytes[i] = (byte)byteArray.getInt(i);
                    }
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    try {
                        ObjectInputStream objectInputStream =
                                new ObjectInputStream(inputStream);
                        level = cl1.cast(objectInputStream.readObject());
                        level.commonRestore();
                        objectInputStream.close();
                        inputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return level;
    }
}
