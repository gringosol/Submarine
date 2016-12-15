package com.gameinstance.submarine;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by gringo on 15.12.2016 20:59.
 *
 */
public class Button extends Sprite {
    TouchHandler touchHandler;
    ClickListener clickListener;
    GameRenderer renderer;
    int [] texHandles;

    public Button(final GameRenderer renderer, @NonNull int [] texResourseIds,
                  Map<Integer, Primitive> primitives, final float width,
                  final float height, final ClickListener clickListener, float [] pos) {
        super(renderer, texResourseIds[0], primitives, width, height);
        texHandles = new int[texResourseIds.length];
        int i = 0;
        for (int texresId : texResourseIds) {
            texHandles[i] = TextureManager.getTextureHandle(renderer.getActivityContext(), texresId);
            i++;
        }
        setPosition(pos[0], pos[1]);
        this.clickListener = clickListener;
        this.renderer = renderer;
        touchHandler = new TouchHandler(0) {
            @Override
            public boolean touch(int x, int y) {
                if (inBounds(x, y)) {
                   return true;
                }
                return false;
            }

            @Override
            public boolean onDown(int x, int y) {
                if (inBounds(x, y)) {
                    if (texHandles.length > 1)
                        setTexHandle(texHandles[1]);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onUp(int x, int y) {
                if (inBounds(x, y)) {
                    setTexHandle(texHandles[0]);
                    clickListener.onClick();
                    return true;
                }
                return false;
            }
        };
        InputController.addTouchHandler(touchHandler);
    }

    private boolean inBounds(int x, int y) {
        float [] p = renderer.convertCoords(x, y, true);
        float width = getScaleX();
        float height = getScaleY();
        float minx = getPosition()[0] - width / 2.0f;
        float miny = getPosition()[1] - height /2.0f;
        float maxx = getPosition()[0] + width / 2.0f;
        float maxy = getPosition()[1] + height /2.0f;
        if (!(p[0] < minx || p[0] > maxx || p[1] < miny || p[1] > maxy)) {
            return true;
        }
        return false;
    }

    public interface ClickListener {
        void onClick();
    }
}
