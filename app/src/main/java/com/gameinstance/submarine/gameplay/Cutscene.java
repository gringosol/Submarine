package com.gameinstance.submarine.gameplay;

import java.util.List;

/**
 * Created by gringo on 15.06.2017 7:39.
 *
 */
public class Cutscene {
    List<CutsceneAction> actions;
    int currentAction = 0;
    boolean isFinished = false;

    public Cutscene(List<CutsceneAction> actions) {
        this.actions = actions;
    }

    public void run() {
        if (actions != null && currentAction < actions.size()) {
            actions.get(currentAction).run();
            if (actions.get(currentAction).isFinished()){
                currentAction++;
            }
            if (currentAction >= actions.size()) {
                isFinished = true;
            }
        }
    }

    public boolean getFinished() {
        return isFinished;
    }
}
