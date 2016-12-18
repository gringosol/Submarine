package com.gameinstance.submarine.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.gameinstance.submarine.GameRenderer;
import com.gameinstance.submarine.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gringo on 18.12.2016 13:16.
 *
 */
public class LetterGenerator {
    static Map<Character, Integer> charMap = new HashMap<>();
    static int [] texHandle = new int[1];

    public static Integer getCharTexId(Character ch, GameRenderer renderer) {
        if (charMap.containsKey(ch))
            return charMap.get(ch);
        Bitmap bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        Drawable background = renderer.getActivityContext().getResources()
                .getDrawable(R.drawable.textbackground);
        background.setBounds(0, 0, 64, 64);
        background.draw(canvas);
        Paint textPaint = new Paint();
        textPaint.setTextSize(48);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
        canvas.drawText(ch.toString(), 16, 32, textPaint);
        GLES20.glGenTextures(1, texHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        int th = texHandle[0];
        charMap.put(ch, th);
        return th;
    }
}
