package com.gameinstance.submarine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gringo on 02.12.2016 21:01.
 *
 */
public class InputController {
    static List<TouchHandler> touchHandlers = new ArrayList<>();
    public static void onScreenTouch(int x, int y) {
        boolean stop = false;
        for (TouchHandler handler : touchHandlers) {
            if (!stop) {
               stop = handler.touch(x, y);
            }
        }
    }

    public static void onScreenDown(int x, int y) {
        boolean stop = false;
        for (TouchHandler handler : touchHandlers) {
            if (!stop) {
                stop = handler.onDown(x, y);
            }
        }
    }

    public static void onScreenUp(int x, int y) {
        boolean stop = false;
        for (TouchHandler handler : touchHandlers) {
            if (!stop) {
                stop = handler.onUp(x, y);
            }
        }
    }

    public static void addTouchHandler(TouchHandler handler) {
        touchHandlers.add(handler);
        Collections.sort(touchHandlers);
    }


}
