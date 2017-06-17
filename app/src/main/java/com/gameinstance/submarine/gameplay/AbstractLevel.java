package com.gameinstance.submarine.gameplay;

import android.media.MediaPlayer;

import com.gameinstance.submarine.GameManager;
import com.gameinstance.submarine.Helicopter;
import com.gameinstance.submarine.Ship;
import com.gameinstance.submarine.Tank;

import java.util.List;

/**
 * Created by gringo on 22.04.2017 17:06.
 *
 */
public class AbstractLevel implements LevelLogic {
    protected boolean completed = false;
    transient List<Ship> ships;
    transient List<Tank> tanks;
    transient List<Helicopter> helicopters;
    transient MediaPlayer ambientMusic;
    int totalScore;
    transient Cutscene cutscene;

    @Override
    public void commonInit() {
        totalScore = GameManager.getGameplay().getTotalScore();
        commonSetup();
        setupActors();
        init();
        commonPostSetup();
    }

    @Override
    public void commonRestore() {
        commonSetup();
        restore();
        commonPostSetup();
    }

    private void commonSetup() {
        GameManager.getGameplay().clearPackages();
        completed = false;
        ships = GameManager.getScene().getShips();
        tanks = GameManager.getScene().getTanks();
        helicopters = GameManager.getScene().getHelis();
    }

    private void commonPostSetup(){
        completed = false;
        if (ambientMusic != null)
            ambientMusic.setLooping(true);
    }

    public void init() {
    }

    public void runLevel() {
        if (cutscene != null && !cutscene.isFinished) {
            cutscene.run();
        }
        run();
    }

    @Override
    public void run() {
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    public void restore() {
    }

    @Override
    public void onClose() {
        if (ambientMusic != null && ambientMusic.isPlaying())
            ambientMusic.stop();
    }

    @Override
    public void briefing() {
    }

    public void onShow() {
        if (ambientMusic != null)
            ambientMusic.start();
    }

    protected void setupActors() {
    }

    @Override
    public void onFail() {
        if (ambientMusic != null && ambientMusic.isPlaying())
            ambientMusic.stop();
    }

    @Override
    public int getScore() {
        return 100;
    }

    @Override
    public int getTotalScore() {
        return totalScore;
    }
}
