package com.gameinstance.submarine.utils;

/**
 * Created by gringo on 30.12.2016 20:38.
 *
 */
public class MathUtils {

    public static float distance(float [] p1, float [] p2) {
        float x = p2[0] - p1[0];
        float y = p2[1] - p1[1];
        return (float)Math.sqrt(x * x + y * y);
    }

    public static boolean coordEquals(float [] p1, float [] p2) {
        return p1[0] == p2[0] && p1[1] == p2[1];
    }

    public static boolean testQuads(float [] c1, float [] c2, float w1, float h1, float w2, float h2) {
        float minx1 = c1[0] - w1 / 2.0f;
        float maxx1 = c1[0] + w1 / 2.0f;
        float miny1 = c1[1] - h1 / 2.0f;
        float maxy1 = c1[1] + h1 / 2.0f;
        float minx2 = c2[0] - w2 / 2.0f;
        float maxx2 = c2[0] + w2 / 2.0f;
        float miny2 = c2[1] - h2 / 2.0f;
        float maxy2 = c2[1] + h2 / 2.0f;
        return !(maxx1 < minx2 || minx1 > maxx2 || maxy1 < miny2 || miny1 > maxy2);
    }
}
