package com.gameinstance.submarine;

import com.gameinstance.submarine.audio.SoundSource;

/**
 * Created by gringo on 30.12.2016 20:11.
 *
 */
public class Helicopter extends Movable {
    public Helicopter(Sprite sprite, String heliType) {
        super(sprite);
        isEnemy = true;
        setSoundSource(new SoundSource(GameManager.getSoundManager(), 0.5f));
        setCurrentSound(R.raw.helicopter02, true);
        this.type = heliType;
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
