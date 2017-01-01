package com.gameinstance.submarine;

/**
 * Created by gringo on 30.12.2016 20:11.
 *
 */
public class Helicopter extends Movable {
    public Helicopter(Sprite sprite, String heliType) {
        super(sprite);
        isEnemy = true;
    }

    @Override
    public boolean collideWithLandscape(byte [] backBuffer, int scrH, float aspect, float [] vM) {
        return false;
    }

    @Override
    public void update() {
        sprite.playAnimation();
        super.update();
    }
}
