package com.gameinstance.submarine;

import com.gameinstance.submarine.ui.TextLine;

import java.util.Calendar;

/**
 * Created by gringo on 16.12.2016 20:22.
 *
 */
public class Submarine extends Movable {
    private static final Integer messageInterval = 500;
    int depth = 0;
    int [] resIds;
    int [] texHandles;
    int maxDepth = 2;
    static TextLine shallowWarning = null;
    GameRenderer renderer;
    boolean shallow = false;
    int lastMessageShallowShow = 0;

    public Submarine(Sprite sprite, int [] resIds, GameRenderer renderer) {
        super(sprite);
        this.resIds = resIds;
        texHandles = new int[resIds.length];
        for (int i = 0; i < texHandles.length; i++) {
            texHandles[i] = TextureManager.getTextureHandle(renderer.getActivityContext(), resIds[i]);
        }
        sprite.setTexHandle(texHandles[0]);
        this.renderer = renderer;
        if (shallowWarning == null) {
            shallowWarning = new TextLine(R.string.too_shallow, new float[] {-0.3f, 0.5f}, 0.2f, renderer);
        }
        speed = 0.025f;
    }

    public int getDepth() {
        return  depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void emerge() {
        if (depth > 0) {
            depth--;
            sprite.setTexHandle(texHandles[depth]);
        }
    }

    public void plunge() {
        if (depth < 2 && depth < maxDepth) {
            depth++;
            sprite.setTexHandle(texHandles[depth]);
        } else {
            onBottom();
        }
    }

    @Override
    public boolean collideWithLandscape(byte [] backBuffer, int scrH, float aspect, float [] vM) {
        maxDepth = 2;
        boolean stop = false;
        float radius = (float)Math.sqrt(sprite.getScaleX() * sprite.getScaleX()
                + sprite.getScaleY() * sprite.getScaleY()) / 2.0f;
        float x = sprite.getPosition()[0] + vM[12];
        float y = sprite.getPosition()[1] + vM[13];
        float [] p1 = new float[] {x - radius, y + radius};
        float [] p2 = new float[] {x + radius, y - radius};
        p1[0] = (p1[0] * aspect + 1.0f) * scrH / 2.0f;
        p1[1] = (p1[1] + 1.0f) * scrH / 2.0f;
        p2[0] = (p2[0] * aspect + 1.0f) * scrH / 2.0f;
        p2[1] = (p2[1] + 1.0f) * scrH / 2.0f;
        int x1 = (int)p1[0];
        int y1 = (int)p2[1];
        int x2 = (int)p2[0];
        int y2 = (int)p1[1];
        if (x1 < 0)
            x1 = 0;
        if (x2 >= scrH)
            x2  = scrH - 1;
        if (y1 < 0)
            y1 = 0;
        if (y2 >= scrH)
            y2  = scrH - 1;
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                int r = backBuffer[(j * scrH + i) * 4];
                int g = backBuffer[(j * scrH + i) * 4 + 1];
                int b = backBuffer[(j * scrH + i) * 4 + 2];
                int a = backBuffer[(j * scrH + i) * 4 + 3];
                if (r < 0)
                    r = 256 + r;
                if (g < 0)
                    g = 256 + g;
                if (b < 0)
                    b = 256 + b;
                if (a < 0)
                    a = 256 + a;
                if (r > 0 && b > 31) {
                    switch (b) {
                        case 63:
                            if (maxDepth > 1)
                                maxDepth = 1;
                            break;
                        case 127:
                            if (maxDepth > 0)
                                maxDepth = 0;
                            break;
                    }
                }
                if (r > 0 && g > 0)
                    stop = true;
            }
        }
        if (maxDepth < depth) {
            stop = true;
            onBottom();
        } else {
            if (shallow) {
                Calendar c = Calendar.getInstance();
                int curTime = c.get(Calendar.MILLISECOND);
                if (curTime - lastMessageShallowShow > messageInterval || (curTime < lastMessageShallowShow)) {
                    GameManager.getScene().hideText(shallowWarning, "hud");
                    shallow = false;
                }
            }
        }
        return stop;
    }

    public void onBottom() {
        shallow = true;
        Calendar c = Calendar.getInstance();
        lastMessageShallowShow = c.get(Calendar.MILLISECOND);
        GameManager.getScene().showText(shallowWarning, "hud");
    }
}
