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
}
