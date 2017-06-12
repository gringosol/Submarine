package com.gameinstance.submarine;

/**
 * Created by gringo on 30.12.2016 19:08.
 *
 */
public class Ship extends Movable {
   WaveTrack waveTrack;

    public Ship( Sprite sprite,String shipType) {
        super(sprite);
        isEnemy = true;
        this.type = shipType;
        setMaxRadius(0.75f);
        waveTrack = new WaveTrack(this, 36, 0.05f, 0.05f, 500, 0.003f, 0.0015f, 10.0f);


    }

    @Override
    public void move() {
        super.move();
        waveTrack.update();
    }
}
