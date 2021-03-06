package com.gameinstance.submarine;

import com.gameinstance.submarine.audio.SoundSource;
import com.gameinstance.submarine.gameplay.tasks.MobTask;

/**
 * Created by gringo on 03.12.2016 9:19.
 *
 */
public class Movable {
    Sprite sprite;
    float [] target = new float[] {0, 0};
    float speed = 0.015f;
    float angularSpeed = 0.1f;
    float angle = 0;
    float previousAngle = 0;
    float [] previousPosition = new float[] {0, 0};
    boolean motionEnabled = false;
    boolean motionDenied = false;
    boolean collide = true;
    MobTask currentTask = null;
    Sprite viewCircle = null;
    float viewRadius = 0;
    float maxRadius = 0.5f;
    boolean isEnemy = false;
    String type = null;
    SoundSource soundSource = null;
    Camera camera;

    public Movable(Sprite sprite) {
        this.sprite = sprite;
        camera = GameManager.getRenderer().getCamera();
    }

    public void update() {
        if (viewCircle != null) {
            viewCircle.setPosition(sprite.getPosition()[0], sprite.getPosition()[1]);
        }
        if (currentTask != null) {
            currentTask.run();
        }
        if (soundSource != null) {
            if (camera == null) {
                camera = GameManager.getRenderer().getCamera();
            } else {
                soundSource.update(camera.getPosition(), sprite.getPosition());
            }
        }
    }

    public void move() {
        update();
        if (!motionEnabled)
            return;
        previousAngle = angle;
        previousPosition[0] = sprite.getPosition()[0];
        previousPosition[1] = sprite.getPosition()[1];
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
        if (Math.abs(angle - angle2) > angularSpeed && l > speed * 2 && ((angle > angle2 && angle - angle2 < Math.PI) || (angle < angle2 && angle2 - angle > Math.PI))) {
            angle -= angularSpeed;
        } else if (Math.abs(angle - angle2) > angularSpeed && l > speed * 2) {
            angle += angularSpeed;
        }
        if (Math.abs(angle) > Math.PI)
            angle = (float)(angle - 2.0f * Math.PI * Math.signum(angle));
        float [] dir1 = new float[2];
        dir1[0] = (float)-Math.sin(angle);
        dir1[1] = (float)Math.cos(angle);
        float angleDeg = angle * (180 / (float)Math.PI);
        float deltaDeg = (angle2 - angle) * (180 / (float)Math.PI);
        sprite.setRotation(angleDeg);
        if (l > speed * 2 && Math.abs(deltaDeg) < 60) {
            sprite.setPosition(dir1[0] * speed + curPos[0], dir1[1] * speed + curPos[1]);
        } else if (l > speed * 2 && Math.abs(deltaDeg) > 120) {
            sprite.setPosition(dir[0] * speed + curPos[0], dir[1] * speed + curPos[1]);
        } else if (l <= speed * 2) {
            motionEnabled = false;
        }
    }

    public void setTarget(float [] newTarget) {
        target[0] = newTarget[0];
        target[1] = newTarget[1];
        if (!motionDenied)
            motionEnabled = true;
    }

    public float [] getTarget() {
        return target;
    }

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
                int g = backBuffer[(j * scrH + i) * 4 + 1];
                if (r < 0)
                    r = 256 + r;
                if (g < 0)
                    g = 256 + g;
                if (r > 0 && g > 0)
                    return true;
            }
        }
        return false;
    }

    public void resetMotion() {
        angle = previousAngle;
        sprite.setPosition(previousPosition[0], previousPosition[1]);
    }

    public void setMotionEnabled(boolean enabled) {
        motionEnabled = enabled;
    }

    public boolean getMotionEnabled() {
        return motionEnabled;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setAngle(float a) {
        angle = a;
        sprite.setRotation(a * (180 / (float)Math.PI));
    }

    public float getAngle() {
        return angle;
    }

    public void setCollide(boolean b) {
        collide = b;
    }

    public void setCurrentTask(MobTask task) {
        currentTask = task;
    }

    public MobTask getCurrentTask() {
        return currentTask;
    }

    public void setViewCircleVisible(boolean b, Layer layer) {
        if (viewCircle != null) {
            if (b) {
               layer.addSprite(viewCircle);
            } else {
                layer.removeSprite(viewCircle);
            }
        }
    }

    public void setViewCircle(Sprite sprite) {
        this.viewCircle = sprite;
    }

    public void setViewRadius(float r){
        viewRadius = r;
        if (viewCircle != null)
            viewCircle.setScale(r * 2.0f, r * 2.0f);
    }

    public float getViewRadius(){
        return viewRadius;
    }

    public void setMaxRadius(float r) {
        maxRadius = r;
    }

    public float getMaxRadius() {
        return maxRadius;
    }

    public void setEnemy(boolean b) {
        isEnemy = b;
    }

    public boolean getIsEnemy() {
        return isEnemy;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Sprite getViewCircle() {
        return viewCircle;
    }

    public void setSoundSource(SoundSource soundSource) {
        this.soundSource = soundSource;
    }

    public SoundSource getSoundSource() {
        return soundSource;
    }

    public void setCurrentSound(int resId, boolean repeat) {
        if (soundSource != null) {
            soundSource.play(resId, repeat);
        }
    }

    public void setSpeed(float v) {
        speed = v;
    }

    public float getSpeed() {
        return speed;
    }

    public void deactivate() {
        if (soundSource != null) {
            soundSource.stop();
        }
    }

    public void setMotionDenied(boolean b){
        motionDenied = b;
        motionEnabled = !b;
    }
}
