package com.miaxis.face.manager;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.miaxis.face.app.App;

import org.greenrobot.eventbus.EventBus;
import org.zz.api.MXFingerAPI;

public class FingerManager {

    private FingerManager() {
    }

    public static FingerManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final FingerManager instance = new FingerManager();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    private static final int LEVEL = 2;            // 指纹比对级别
    private static final int TIME_OUT = 14 * 1000;    // 等待按手指的超时时间，单位：ms
    private static final int IMAGE_X_BIG = 256;          // 指纹图像宽高 大小
    private static final int IMAGE_Y_BIG = 360;
    private static final int IMAGE_SIZE_BIG = IMAGE_X_BIG * IMAGE_Y_BIG;
    private static final int TZ_SIZE = 512;          // 指纹特征长度  BASE64
    private static int pid = 514;
    private static int vid = 33307;

    private Context context;
    private MXFingerAPI mxFingerAPI;

    private boolean verifyRun = true;
    private boolean gatherRun = true;

    private OnFingerVerifyListener verifyListener;
    private OnGatherFingerListener gatherListener;

    private boolean hasFingerDevice = false;

    public void init(Application application) {
        this.context = application;
        mxFingerAPI = new MXFingerAPI(application, pid, vid);
    }

    public void startVerifyFinger(byte[] fingerprint0, byte[] fingerprint1, OnFingerVerifyListener verifyListener) {
        stopVerifyFinger();
        verifyRun = true;
        this.verifyListener = verifyListener;
        new VerifyCardFingerThread(fingerprint0, fingerprint1).start();
    }

    public void stopVerifyFinger() {
        verifyRun = false;
        this.verifyListener = null;
        if (mxFingerAPI != null) {
            mxFingerAPI.mxCancelCaptue();
        }
    }

    public enum FingerVerifyResult {
        VERIFY_SUCCESS, VERIFY_FAILED, NO_FINGER_FEATURE
    }

    public interface OnFingerVerifyListener {
        void onFingerVerify(FingerVerifyResult result, String verifyFingerFeature, Bitmap fingerPhoto, int score);
    }

    private class VerifyCardFingerThread extends Thread {

        private byte[] fingerprint0;
        private byte[] fingerprint1;

        VerifyCardFingerThread(byte[] fingerprint0, byte[] fingerprint1) {
            this.fingerprint0 = fingerprint0;
            this.fingerprint1 = fingerprint1;
        }

        @Override
        public void run() {
            try {
                if (fingerprint0 == null || fingerprint1 == null) {
                    if (verifyListener != null) {
                        verifyListener.onFingerVerify(FingerVerifyResult.NO_FINGER_FEATURE, null, null, 0);
                    }
                    return;
                }
                if (mxFingerAPI == null) {
                    mxFingerAPI = new MXFingerAPI(App.getInstance(), pid, vid);
                }
                byte[] bImgBuf = new byte[IMAGE_SIZE_BIG];
                byte[] printFingerFeature = new byte[TZ_SIZE];
                int result = -1;
                while (result != 0 && verifyRun) {
                    result = mxFingerAPI.mxExtractFeatureID(bImgBuf, TIME_OUT, 0, printFingerFeature);
                }
                if (result == 0 && verifyRun) {
                    Bitmap bitmap = mxFingerAPI.Raw2Bimap(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG);
                    int score0 = verifyFingerScore(fingerprint0, printFingerFeature);
                    int score1 = verifyFingerScore(fingerprint1, printFingerFeature);
                    if (Math.max(score0, score1) > 40) {
                        if (verifyListener != null) {
                            verifyListener.onFingerVerify(FingerVerifyResult.VERIFY_SUCCESS,
                                    Base64.encodeToString(printFingerFeature, Base64.NO_WRAP),
                                    bitmap,
                                    Math.max(score0, score1));
                        }
                        return;
                    }
                    if (verifyListener != null) {
                        verifyListener.onFingerVerify(FingerVerifyResult.VERIFY_FAILED, null, null, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (verifyListener != null) {
                    verifyListener.onFingerVerify(FingerVerifyResult.VERIFY_FAILED, null, null, 0);
                }
            }
        }
    }

    public void gatherFingerprint(OnGatherFingerListener gatherListener) {
        stopGatherFinger();
        gatherRun = true;
        this.gatherListener = gatherListener;
        new GetFingerFeatureThread().start();
    }

    public void stopGatherFinger() {
        gatherRun = false;
        this.gatherListener = null;
        if (mxFingerAPI != null) {
            mxFingerAPI.mxCancelCaptue();
        }
    }

    public interface OnGatherFingerListener {
        void onGatherFinger(GatherFingerStatus status, Bitmap fingerImage, String feature);
    }

    public enum GatherFingerStatus {
        GATHER_FINGER, VERIFY_FINGER_SUCCESS, VERIFY_FINGER_FAILED
    }

    private class GetFingerFeatureThread extends Thread {

        @Override
        public void run() {
            try {
                byte[] bImgBuf = new byte[IMAGE_SIZE_BIG];
                byte[] printFingerFeature = new byte[TZ_SIZE];
                int result = -1;
                while (result != 0 && gatherRun) {
                    result = mxFingerAPI.mxExtractFeatureID(bImgBuf, TIME_OUT, 0, printFingerFeature);
                }
                if (result == 0 && gatherRun) {
                    Bitmap bitmap = mxFingerAPI.Raw2Bimap(bImgBuf, IMAGE_X_BIG, IMAGE_Y_BIG);
                    if (gatherListener != null) {
                        gatherListener.onGatherFinger(GatherFingerStatus.GATHER_FINGER, bitmap, "");
                    }
                    Thread.sleep(300);
                    byte[] bImgBuf1 = new byte[IMAGE_SIZE_BIG];
                    byte[] printFingerFeature1 = new byte[TZ_SIZE];
                    result = -1;
                    while (result != 0 && gatherRun) {
                        result = mxFingerAPI.mxExtractFeatureID(bImgBuf1, TIME_OUT, 0, printFingerFeature1);
                    }
                    if (result == 0 && gatherRun) {
                        Bitmap bitmap1 = mxFingerAPI.Raw2Bimap(bImgBuf1, IMAGE_X_BIG, IMAGE_Y_BIG);
                        if (verifyFinger(printFingerFeature, printFingerFeature1) == 0) {
                            if (gatherListener != null) {
                                gatherListener.onGatherFinger(GatherFingerStatus.VERIFY_FINGER_SUCCESS, bitmap1, Base64.encodeToString(printFingerFeature, Base64.NO_WRAP));
                            }
                        } else {
                            if (gatherListener != null) {
                                gatherListener.onGatherFinger(GatherFingerStatus.VERIFY_FINGER_FAILED, bitmap1, "");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (gatherListener != null) {
                    gatherListener.onGatherFinger(GatherFingerStatus.VERIFY_FINGER_FAILED, null, null);
                }
            }
        }
    }

    private int verifyFinger(byte[] alpha, byte[] beta) {
        //输出比对成功失败，想要相似度使用mxMatchFeatureScoreID方法
        return mxFingerAPI.mxMatchFeatureID(alpha, beta, LEVEL);
    }

    private int verifyFingerScore(byte[] alpha, byte[] beta) {
        int[] score = new int[] {0};
        int result = mxFingerAPI.mxMatchFeatureScoreID(alpha, beta, score);
        if (result == 0) {
            return score[0];
        } else {
            return 0;
        }
    }

    public boolean checkHasFingerDevice() {
        if (hasFingerDevice) return true;
        int re;
        if (mxFingerAPI != null) {
            for (int i = 0; i < 20; i++) {
                re = mxFingerAPI.mxGetDevVersion(new byte[120]);
                if (re == 0) {
                    hasFingerDevice = true;
                    return true;
                }
            }
        }
        return false;
    }

}
