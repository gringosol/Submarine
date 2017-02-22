package com.gameinstance.submarine.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.gameinstance.submarine.GameManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gringo on 12.01.2017 19:38.
 *
 */
public class SoundManager {
    private static final int DEFAULT_COUNT = 10;
    int soundCount;
    int [] soundIds;
    boolean  [] isPlaying;
    int streamPointer = 0;
    SoundPool sp;
    Map<Integer, Integer> loadedSounds = new HashMap<>();
    Map<Integer, Integer> loadedSoundsInv = new HashMap<>();
    Context context;
    Map<Integer, SoundSource> soundSources = new HashMap<>();
    float commonVolume = 1.0f;

    public SoundManager(int channelCount){
        soundCount = channelCount;
        soundIds = new int[soundCount];
        isPlaying = new boolean[soundCount];
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        context = GameManager.getRenderer().getActivityContext();
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                sp.play(sampleId, 1, 1, 1, isPlaying[loadedSoundsInv.get(sampleId)] ? -1 : 0, 1);
            }
        });
    }

    public SoundManager() {
        this(DEFAULT_COUNT);
    }

    private int loadSound(int resId, boolean repeat) {
      if (loadedSounds.containsKey(resId)){
          return loadedSounds.get(resId);
      } else {
          int i = 0;
          while (isPlaying[streamPointer]) {
              streamPointer++;
              if (streamPointer >= soundCount)
                  streamPointer = 0;
              if (i > soundCount)
                  break;
              i++;
          }
          soundIds[streamPointer] = sp.load(context, resId, 1);
          int sid = soundIds[streamPointer];
          loadedSounds.put(streamPointer, sid);
          loadedSoundsInv.put(sid, streamPointer);
          isPlaying[streamPointer] = repeat;
          streamPointer++;
          if (streamPointer >= soundCount)
              streamPointer = 0;
          sp.setVolume(sid, commonVolume, commonVolume);
          return sid;
      }
    }

    public int playSound(int resId, boolean repeat) {
        return loadSound(resId, repeat);
    }

    public void stopSound(int resId) {
        sp.stop(resId);
    }

    public void setVolume(int streamId, float volume) {
        sp.setVolume(streamId, volume * commonVolume, volume * commonVolume);
    }

    public void destroy() {
        sp.release();
    }

    public void setCommonVolume(float v) {
        commonVolume = v;
        for (int i = 0; i < soundCount; i++) {
            if (soundIds[i] != 0) {
                if (soundSources.containsKey(soundIds[i])) {
                    float vl = soundSources.get(soundIds[i]).getVolume();
                    sp.setVolume(soundIds[i], commonVolume * vl, commonVolume * vl);
                } else {
                    sp.setVolume(soundIds[i], commonVolume, commonVolume);
                }
            }
        }
    }
}
