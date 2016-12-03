package com.gameinstance.submarine;

/**
 * Created by gringo on 03.12.2016 9:19.
 *
 */
public class Movable {
    Sprite sprite;
    float [] target = new float[] {0, 0};
    float speed = 0.01f;
    float angularSpeed = 0.1f;
    float angle = 0;

    public Movable(Sprite sprite) {
        this.sprite = sprite;
    }

    public void move() {
        float [] curPos = sprite.getPosition();
        float [] dir = new float[2];
        dir[0] = target[0] - curPos[0];
        dir[1] = target[1] - curPos[1];
        float l = (float)Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
        if (l != 0) {
            dir[0] /= l;
            dir[1] /= l;
        }
        float angle2 = (float)(-Math.acos(dir[1]) * Math.signum(dir[0]));
        if (angle - angle2 > angularSpeed && l > speed * 2) {
            angle -= angularSpeed;
        } else if (angle - angle2 < -angularSpeed && l > speed * 2) {
            angle += angularSpeed;
        }
        float [] dir1 = new float[2];
        dir1[0] = (float)-Math.sin(angle);
        dir1[1] = (float)Math.cos(angle);
        sprite.setRotation(angle * (180 / (float)Math.PI));
        if (l > speed * 2)
          sprite.setPosition(dir1[0] * speed + curPos[0], dir1[1] * speed + curPos[1]);
    }

    public void setTarget(float [] newTarget) {
        target[0] = newTarget[0];
        target[1] = newTarget[1];
    }
}
