package com.gameinstance.submarine.gameplay;

import java.io.Serializable;

/**
 * Created by gringo on 01.01.2017 13:01.
 *
 */
public interface LevelLogic extends Serializable {
    void commonInit();
    void run();
    boolean isCompleted();
    void commonRestore();
    void onClose();
    void briefing();
    void onShow();
    void onFail();
}
