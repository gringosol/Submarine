package com.gameinstance.submarine;

import android.content.Context;

import com.gameinstance.submarine.utils.TextureHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gringo on 01.12.2016 20:05.
 *
 */

public class TextureManager {
    private static Map<Integer, Integer> textureHandles = new HashMap<>();

    public static Integer getTextureHandle(Context activityContext, Integer resourseid) {
        if (textureHandles.containsKey(resourseid)) {
            return textureHandles.get(resourseid);
        } else {
            int texHandle = TextureHelper.loadTexture(activityContext, resourseid);
            textureHandles.put(resourseid, texHandle);
            return texHandle;
        }
    }
}
