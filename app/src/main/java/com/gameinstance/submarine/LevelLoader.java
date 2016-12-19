package com.gameinstance.submarine;

import android.app.Activity;
import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gringo on 19.12.2016 19:49.
 *
 */

public class LevelLoader {
    public static void loadLevel(Activity activity, int levelId) {
        Resources res = activity.getResources();
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
            String background =  jsonObject.getString("background");
            int backgroundR = res.getIdentifier(background, "drawable", activity.getPackageName());
            if (backgroundR != 0) {

            }
        } catch (JSONException e) {
            throw new RuntimeException("Ошибка чтения json уровня");
        }
    }
}
