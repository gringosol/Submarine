package com.gameinstance.submarine;

import android.app.Activity;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void loadLevel(Activity a, int levelId) {
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
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка чтения json уровня");
        }
    }


    private static void loadLandscape(JSONObject jsonObject)
            throws JSONException {
        Layer landscape_back = scene.getLayer("landscape_back");
        Layer landscape = scene.getLayer("landscape");
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
}
