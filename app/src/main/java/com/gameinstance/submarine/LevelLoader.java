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

/**
 * Created by gringo on 19.12.2016 19:49.
 *
 */

public class LevelLoader {
    static Activity activity;
    static Resources res;
    static Primitive texPrimitive;

    public static void loadLevel(Activity a, int levelId, Layer landscape_back, Layer landscape) {
        activity = a;
        res = activity.getResources();
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
            loadLandscape(jsonObject, landscape_back, landscape);
            loadSubmarine(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка чтения json уровня");
        }
    }


    private static void loadLandscape(JSONObject jsonObject, Layer landscape_back, Layer landscape)
            throws JSONException {
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

    }
}
