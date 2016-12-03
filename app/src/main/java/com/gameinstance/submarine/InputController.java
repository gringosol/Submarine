package com.gameinstance.submarine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gringo on 02.12.2016 21:01.
 *
 */
public class InputController {
    static List<TouchHandler> touchHandlers = new ArrayList<>();
    public static void onScreenTouch(int x, int y) {
        for (TouchHandler handler : touchHandlers) {
            handler.touch(x, y);
        }
    }

    public static void addTouchHandler(TouchHandler handler) {
        touchHandlers.add(handler);
    }

    public interface TouchHandler {
        public void touch(int x, int y);
    }
}
