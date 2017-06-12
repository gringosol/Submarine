package com.gameinstance.submarine;

import android.os.SystemClock;

import com.gameinstance.submarine.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gringo on 12.06.2017 9:09.
 *
 */
public class WaveTrack {
    int trackNodeCount;
    float trackNodeLength;
    float trackNodeWidth;
    int maxTrackPeriod;
    List<Sprite> trackNodes = new ArrayList<>();
    List<Long> trackTimes = new ArrayList<>();
    float [] lastTrackPoint;
    long lastTrackTime;
    int curNode = 0;
    float trackExpansionSpeed;
    float trackTransparencySpeed;
    float maxTrackScale;
    Movable movable;

    public WaveTrack(Movable m, final int trackNodeCount, final float trackNodeLength,
                     final float trackNodeWidth, final int maxTrackPeriod,
                     final float trackExpansionSpeed, final float trackTransparencySpeed,
                     final  float maxTrackScale) {
        this.trackNodeCount = trackNodeCount;
        this.trackNodeLength = trackNodeLength;
        this.trackNodeWidth = trackNodeWidth;
        this.maxTrackPeriod = maxTrackPeriod;
        this.trackExpansionSpeed = trackExpansionSpeed;
        this.trackTransparencySpeed = trackTransparencySpeed;
        this.maxTrackScale = maxTrackScale;
        movable = m;
        GameManager.getRenderer().getSurfaceView().queueEvent(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < trackNodeCount; i++) {
                    float [] p = movable.getSprite().getPosition();
                    Sprite node = GameManager.addSprite(R.drawable.wawetrack, p[0], p[1], trackNodeLength, trackNodeWidth);
                    GameManager.getScene().getLayer("waves").addSprite(node);
                    node.setVisible(false);
                    trackNodes.add(node);
                    trackTimes.add(0L);
                }
            }
        });
    }

    public WaveTrack(Movable m) {
        this(m, 18, 0.05f, 0.05f, 500, 0.006f, 0.006f, 10.0f);
    }

    public void update(){
        update(true);
    }

    public void update(boolean addNewNodes) {
        if (trackNodes.size() == 0)
            return;
        if (addNewNodes) {
            addTrackNode();
        }
        manageTrackSizeAndTransparency();
    }

    private void addTrackNode() {
        float [] curPos = movable.getSprite().getPosition();
        long curTime = SystemClock.uptimeMillis();
        if (lastTrackPoint == null) {
            lastTrackPoint = new float[2];
            lastTrackPoint[0] = curPos[0];
            lastTrackPoint[1] = curPos[1];
            lastTrackTime = curTime;
        } else {
            float dist = MathUtils.distance(movable.getSprite().getPosition(), lastTrackPoint);
            if (dist >= trackNodeLength * 0.8f) {
                long interval = curTime - lastTrackTime;
                if (interval < maxTrackPeriod) {
                    float dirX = -(float)Math.sin(movable.getAngle());
                    float dirY = (float)Math.cos(movable.getAngle());
                    float x = curPos[0] - 0.5f * dirX * (movable.getSprite().getScaleY() + trackNodeLength);
                    float y = curPos[1] - 0.5f * dirY * (movable.getSprite().getScaleY() + trackNodeLength);
                    trackNodes.get(curNode).setRotation(movable.getAngle() * 180 / (float)Math.PI);
                    trackNodes.get(curNode).setPosition(x, y);
                    trackNodes.get(curNode).setVisible(true);
                    trackNodes.get(curNode).setTransparency(1.0f);
                    trackTimes.set(curNode, curTime);
                    curNode = (curNode + 1) % trackNodeCount;
                }
                lastTrackPoint[0] = curPos[0];
                lastTrackPoint[1] = curPos[1];
                lastTrackTime = curTime;
            }
        }
    }

    private void manageTrackSizeAndTransparency() {
        long curTime = SystemClock.uptimeMillis();
        long interval;
        for (int i = 0; i < trackNodes.size(); i++) {
            interval = curTime - trackTimes.get(i);
            trackNodes.get(i).setScale((1.0f + interval * trackExpansionSpeed) * trackNodeWidth, trackNodeLength);
            trackNodes.get(i).setTransparency(1.0f / (1.0f + interval * trackTransparencySpeed));
            if (trackNodes.get(i).getScaleX() > maxTrackScale * trackNodeWidth) {
                trackNodes.get(i).setVisible(false);
            }
        }
    }
}
