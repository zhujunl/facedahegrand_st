package com.miaxis.face.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.constant.Constants;

import static com.miaxis.face.constant.Constants.LEFT_VOLUME;
import static com.miaxis.face.constant.Constants.LOOP;
import static com.miaxis.face.constant.Constants.PRIORITY;
import static com.miaxis.face.constant.Constants.RIGHT_VOLUME;
import static com.miaxis.face.constant.Constants.SOUND_RATE;

public class SoundManager {

    private SoundManager() {
    }

    public static SoundManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final SoundManager instance = new SoundManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static final int SOUND_SUCCESS = 1;
    public static final int SOUND_FAIL = 2;
    public static final int PLEASE_PRESS = 3;
    public static final int SOUND_OR = 4;
    public static final int SOUND_OTHER_FINGER  = 5;
    public static final int SOUND_VALIDATE_FAIL  = 6;
    public static final int FINGER_RIGHT_0 = 11;
    public static final int FINGER_RIGHT_1 = 12;
    public static final int FINGER_RIGHT_2 = 13;
    public static final int FINGER_RIGHT_3 = 14;
    public static final int FINGER_RIGHT_4 = 15;

    public static final int FINGER_LEFT_0 = 16;
    public static final int FINGER_LEFT_1 = 17;
    public static final int FINGER_LEFT_2 = 18;
    public static final int FINGER_LEFT_3 = 19;
    public static final int FINGER_LEFT_4 = 20;
    public static final int HAS_UPLOAD = 21;
    public static final int UPLOAD_FAILED = 22;
    public static final int PLEASE_BLINK = 23;

    public static final int GATHER_SUCCESS = 24;
    public static final int PRESS_FINGER = 25;
    public static final int PLEASE_CHANGE_FINGER = 26;

    private SoundPool soundPool;
    private SparseIntArray soundMap;

    private int mCurSoundId;
    private boolean continuePlaySoundFlag = true;

    public void init() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundMap = new SparseIntArray();
        Context context = App.getInstance();
        soundMap.put(SOUND_SUCCESS, soundPool.load(context, R.raw.success, 1));
        soundMap.put(SOUND_FAIL, soundPool.load(context, R.raw.fail, 1));
        soundMap.put(PLEASE_PRESS, soundPool.load(context, R.raw.please_press, 1));
        soundMap.put(SOUND_OR, soundPool.load(context, R.raw.sound_or, 1));
        soundMap.put(SOUND_OTHER_FINGER, soundPool.load(context, R.raw.please_press, 1));
        soundMap.put(SOUND_VALIDATE_FAIL, soundPool.load(context, R.raw.validate_fail, 1));
        soundMap.put(FINGER_RIGHT_0, soundPool.load(context, R.raw.finger_right_0, 1));
        soundMap.put(FINGER_RIGHT_1, soundPool.load(context, R.raw.finger_right_1, 1));
        soundMap.put(FINGER_RIGHT_2, soundPool.load(context, R.raw.finger_right_2, 1));
        soundMap.put(FINGER_RIGHT_3, soundPool.load(context, R.raw.finger_right_3, 1));
        soundMap.put(FINGER_RIGHT_4, soundPool.load(context, R.raw.finger_right_4, 1));
        soundMap.put(FINGER_LEFT_0, soundPool.load(context, R.raw.finger_left_0, 1));
        soundMap.put(FINGER_LEFT_1, soundPool.load(context, R.raw.finger_left_1, 1));
        soundMap.put(FINGER_LEFT_2, soundPool.load(context, R.raw.finger_left_2, 1));
        soundMap.put(FINGER_LEFT_3, soundPool.load(context, R.raw.finger_left_3, 1));
        soundMap.put(FINGER_LEFT_4, soundPool.load(context, R.raw.finger_left_4, 1));
        soundMap.put(PLEASE_BLINK, soundPool.load(context, R.raw.please_blink, 1));
        soundMap.put(HAS_UPLOAD, soundPool.load(context, R.raw.has_upload, 1));
        soundMap.put(UPLOAD_FAILED, soundPool.load(context, R.raw.upload_failed, 1));
        soundMap.put(GATHER_SUCCESS, soundPool.load(context, R.raw.get_face, 1));
        soundMap.put(PRESS_FINGER, soundPool.load(context, R.raw.please_press_finger, 1));
        soundMap.put(PLEASE_CHANGE_FINGER, soundPool.load(context, R.raw.please_change_finger, 1));
    }

    public void playSound(int soundID) {
        continuePlaySoundFlag = false;
        soundPool.stop(mCurSoundId);
        mCurSoundId = soundPool.play(soundMap.get(soundID), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    /* 连续播放4段音频 提示按指纹的 指位*/
    public void playSound(final int soundId0, final int soundId1, final int soundId2, final int soundId3) {
        continuePlaySoundFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId0), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId1), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId2), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(800);
                    }
                    if (continuePlaySoundFlag) {
                        mCurSoundId = soundPool.play(soundMap.get(soundId3), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopPlay() {
        soundPool.stop(mCurSoundId);
    }

}
