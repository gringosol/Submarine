package com.gameinstance.submarine.audio;

import com.gameinstance.submarine.utils.MathUtils;

/**
 * Created by gringo on 12.01.2017 20:03.
 *
 */
public class SoundSource {
    SoundManager sm;
    int soundId = -1;
    float maxVolume = 1.0f;

    public SoundSource(SoundManager sm, float maxVolume) {
        this.sm = sm;
        this.maxVolume = maxVolume;
    }

    public void play(int resId, boolean repeat) {
        soundId = sm.playSound(resId, repeat);
    }

    public void stop() {
        sm.stopSound(soundId);
    }

    public void update(float [] micro, float [] pos) {
        if (soundId >= 0) {
            float d = MathUtils.distance(micro, pos);
            float volume = d <= 1.0f ? maxVolume : maxVolume / d;
            sm.setVolume(soundId, volume);
        }
    }
}
