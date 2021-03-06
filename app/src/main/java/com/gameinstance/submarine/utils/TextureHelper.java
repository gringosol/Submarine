package com.gameinstance.submarine.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by gringo on 01.01.2016 15:06.
 *
 */
public class TextureHelper {
    public static int loadTexture(final Context context, final int resourceId) {
        final int [] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture");
        }
        return textureHandle[0];
    }

    public static int [] loadTexture2(final Context context, final int resourceId, int cellSize) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        int n = bitmap.getWidth() / cellSize;
        int m = bitmap.getHeight() / cellSize;
        final int [] textureHandle = new int[2 + m * n];
        textureHandle[0] = n;
        textureHandle[1] = m;
        GLES20.glGenTextures(m * n, textureHandle, 2);
        if (textureHandle[2] != 0) {
            for (int j = 0; j < m; j++) {
                for (int i = 0; i < n; i++) {
                    Bitmap bitmap1 = Bitmap.createBitmap(bitmap,  i * cellSize, j * cellSize,
                            cellSize, cellSize);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[2 + j * n + i]);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap1, 0);
                    bitmap1.recycle();
                }
            }
        }
        if (textureHandle[2] == 0) {
            throw new RuntimeException("Error loading texture");
        }
        return textureHandle;
    }

    public static int [] loadTileset(final Context context, final int resourceId, int cellSize, int cellCount) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        int width = bitmap.getWidth() / cellSize;
        int height = bitmap.getHeight() / cellSize;
        final int [] textureHandle = new int[cellCount];
        GLES20.glGenTextures(cellCount , textureHandle, 0);
        for (int j = 0; j < height; j ++) {
            for (int i = 0; i < width; i++) {
                if (j * width + i < cellCount) {
                    Bitmap bitmap1 = Bitmap.createBitmap(bitmap, i * cellSize, j * cellSize, cellSize, cellSize);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[j * width + i]);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap1, 0);
                    bitmap1.recycle();
                }
            }
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture");
        }
        return textureHandle;
    }

    public static int loadRadarTexture(final Context context, final int resourceId, int pixelSize, int tilesetX, int tilesetY, int [][][] tiles, int bigTileSize) {
        final int [] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap tileSetBitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            int tileWidth = pixelSize / tilesetX;
            int tileHeight = pixelSize / tilesetY;
            Bitmap radarBitmap = Bitmap.createScaledBitmap(tileSetBitmap, pixelSize, pixelSize, false);
            Bitmap tmpBitmapBig = Bitmap.createScaledBitmap(tileSetBitmap, bigTileSize, bigTileSize, false);
            Bitmap tmpBitmapMin = null;
            int [] pixelsBig = new int[bigTileSize * bigTileSize];
            int [] pixelsMin = new int[tileWidth * tileHeight];
            for (int y = 0; y < tilesetY; y++) {
                for (int x = 0; x < tilesetX; x++) {
                    int bigX = (tiles[y][x][0] * bigTileSize) % tileSetBitmap.getWidth();
                    int bigY = (tiles[y][x][0] * bigTileSize) / tileSetBitmap.getWidth() * bigTileSize;
                    int minX = x * tileWidth;
                    int minY = y * tileHeight;
                    tileSetBitmap.getPixels(pixelsBig, 0, bigTileSize, bigX, bigY, bigTileSize, bigTileSize);
                    tmpBitmapBig.setPixels(pixelsBig, 0, bigTileSize, 0, 0, bigTileSize, bigTileSize);
                    tmpBitmapMin = Bitmap.createScaledBitmap(tmpBitmapBig, tileWidth, tileHeight, false);
                    tmpBitmapMin.getPixels(pixelsMin, 0, tileWidth, 0, 0, tileWidth, tileHeight);
                    radarBitmap.setPixels(pixelsMin, 0, tileWidth, minX, minY, tileWidth, tileHeight);
                }
            }
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, radarBitmap, 0);
            tileSetBitmap.recycle();
            radarBitmap.recycle();
            tmpBitmapBig.recycle();
            if (tmpBitmapMin != null)
                tmpBitmapMin.recycle();
        }
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture");
        }
        return textureHandle[0];
    }
}
