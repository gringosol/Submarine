package com.gameinstance.submarine;

/**
 * Created by gringo on 16.12.2016 20:22.
 *
 */
public class Submarine extends Movable {
    int depth = 0;
    int [] resIds;
    int [] texHandles;

    public Submarine(Sprite sprite, int [] resIds, GameRenderer renderer) {
        super(sprite);
        this.resIds = resIds;
        texHandles = new int[resIds.length];
        for (int i = 0; i < texHandles.length; i++) {
            texHandles[i] = TextureManager.getTextureHandle(renderer.getActivityContext(), resIds[i]);
        }
        sprite.setTexHandle(texHandles[0]);
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
        if (depth < 2) {
            depth++;
            sprite.setTexHandle(texHandles[depth]);
        }
    }
}
