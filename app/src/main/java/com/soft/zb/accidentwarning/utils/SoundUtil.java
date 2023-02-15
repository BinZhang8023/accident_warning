package com.soft.zb.accidentwarning.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import com.soft.zb.accidentwarning.R;

public class SoundUtil {
    private SoundPool soundPool;
    private int soundID;
    private Context context;

    public SoundUtil(Context context) {
        this.context = context;
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    }

     // 播放声音（手机铃声）
    public void playSound() {
        soundID = soundPool.load(context, R.raw.sound, Integer.MAX_VALUE);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                soundPool.play(soundID, 1f, 1f, 1, -1, 1);
            }
        });
    }

    // 关闭声音
    public void stopSound() {
        soundPool.stop(soundID);
        soundPool.release();
    }
}
