package com.gameinstance.submarine;

/**
 * Created by gringo on 30.12.2016 19:08.
 *
 */
public class Ship extends Movable {

    public Ship( Sprite sprite,String shipType) {
        super(sprite);
        isEnemy = true;
        this.type = shipType;
        setMaxRadius(0.75f);
    }
}
