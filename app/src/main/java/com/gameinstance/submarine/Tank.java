package com.gameinstance.submarine;

/**
 * Created by gringo on 30.12.2016 19:53.
 *
 */
public class Tank extends Movable {
    public Tank(Sprite sprite, String type) {
        super(sprite);
        isEnemy = true;
    }

    @Override
    public boolean collideWithLandscape(byte [] backBuffer, int scrH, float aspect, float [] vM) {
        if (!collide)
            return false;
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
                int b = backBuffer[(j * scrH + i) * 4 + 2];
                if (r < 0)
                    r = 256 + r;
                if (b < 0)
                    b = 256 + b;
                if (r > 0 && b > 0)
                    return true;
            }
        }
        return false;
    }
}
