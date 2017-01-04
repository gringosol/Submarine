package com.gameinstance.submarine.gameplay.tasks;

import com.gameinstance.submarine.Movable;

import java.io.Serializable;

/**
 * Created by gringo on 30.12.2016 20:33.
 *
 */
public class MobTask implements Serializable {
    transient Movable mob;

    public MobTask(Movable mob) {
        this.mob = mob;
    }

    public void run() {

    }

    public void setMob(Movable mob) {
        this.mob = mob;
    }

    public void onRestore() {

    }
}
