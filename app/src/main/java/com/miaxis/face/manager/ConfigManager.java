package com.miaxis.face.manager;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.model.ConfigModel;
import com.miaxis.face.util.DeviceUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ConfigManager {

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ConfigManager instance = new ConfigManager();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void checkConfig() {
        config = ConfigModel.loadConfig();
        if (config == null) {
            config = new Config.Builder()
                    .id(1L)
                    .updateUrl(Constants.DEFAULT_UPDATE_URL)
                    .uploadRecordUrl(Constants.DEFAULT_UPLOAD_URL)
                    .advertisementUrl(Constants.DEFAULT_ADVERTISEMENT_URL)
                    .deviceSerialNumber(DeviceUtil.getDeviceId(App.getInstance()))
                    .account(Constants.DEFAULT_ACCOUNT)
                    .clientId(Constants.DEFAULT_CLIENT_ID)
                    .encrypt(Constants.DEFAULT_ENCRYPT)
                    .verifyMode(Constants.DEFAULT_VERIFY_MODE)
                    .netFlag(Constants.DEFAULT_NET_FLAG)
                    .resultFlag(Constants.DEFAULT_RESULT_FLAG)
                    .sequelFlag(Constants.DEFAULT_SEQUEL_FLAG)
                    .gatherFingerFlag(Constants.DEFAULT_GATHER_FINGER_FLAG)
                    .saveLocalFlag(Constants.DEFAULT_SAVE_LOCAL_FLAG)
                    .documentFlag(Constants.DEFAULT_DOCUMENT_FLAG)
                    .livenessFlag(Constants.DEFAULT_LIVENESS_FLAG)
                    .queryFlag(Constants.DEFAULT_QUERY_FLAG)
                    .whiteFlag(Constants.DEFAULT_WHITE_FLAG)
                    .blackFlag(Constants.DEFAULT_BLACK_FLAG)
                    .advertiseFlag(Constants.DEFAULT_ADVERTISE_FLAG)
                    .verifyScore(Constants.DEFAULT_VERIFY_SCORE)
                    .qualityScore(Constants.DEFAULT_QUALITY_SCORE)
                    .livenessQualityScore(Constants.DEFAULT_LIVENESS_QUALITY_SCORE)
                    .titleStr(Constants.DEFAULT_TITLE_STR)
                    .password(Constants.DEFAULT_PASSWORD)
                    .upTime(Constants.DEFAULT_UPTIME)
                    .intervalTime(Constants.DEFAULT_INTERVAL)
                    .orgName(Constants.DEFAULT_ORG_NAME)
                    .advertiseDelayTime(Constants.DEFAULT_ADVERTISE_DELAY_TIME) //广告显示延迟
                    .advertisementMode(Constants.DEFAULT_ADVERTISEMENT_MODE)
                    .build();
            ConfigModel.saveConfig(config);
        } else {
            if (TextUtils.isEmpty(config.getDeviceSerialNumber())) {
                config.setDeviceSerialNumber(DeviceUtil.getDeviceId(App.getInstance()));
                ConfigModel.saveConfig(config);
            }
            if (!FingerManager.getInstance().checkHasFingerDevice() && config.getVerifyMode() != Config.MODE_LOCAL_FEATURE) {
                config.setVerifyMode(Config.MODE_FACE_ONLY);
                ConfigModel.saveConfig(config);
            }
        }
    }

    public void saveConfigSync(@NonNull Config config) {
        ConfigModel.saveConfig(config);
        this.config = config;
    }

    public void saveConfig(@NonNull Config config, @NonNull OnConfigSaveListener listener) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ConfigModel.saveConfig(config);
            this.config = config;
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> listener.onConfigSave(true, "保存成功")
                        , throwable -> listener.onConfigSave(false, "保存失败，" + throwable.getMessage()));
    }

    public static boolean isFaceFirst(int mode) {
        switch (mode) {
            case Constants.VERIFY_MODE_FACE_ONLY:
            case Constants.VERIFY_MODE_FACE_FIRST_ONCE:
            case Constants.VERIFY_MODE_FACE_FIRST_DOUBLE:
                return true;
            case Constants.VERIFY_MODE_FINGER_ONLY:
            case Constants.VERIFY_MODE_FINGER_FIRST_ONCE:
            case Constants.VERIFY_MODE_FINGER_FIRST_DOUBLE:
                return false;
        }
        return true;
    }

    public static boolean needExecuteNext(int mode, boolean lastResult, boolean lastFaceMode) {
        switch (mode) {
            case Constants.VERIFY_MODE_FACE_ONLY:
            case Constants.VERIFY_MODE_FINGER_ONLY:
                return false;
            case Constants.VERIFY_MODE_FACE_FIRST_ONCE:
                return !lastResult && lastFaceMode;
            case Constants.VERIFY_MODE_FINGER_FIRST_ONCE:
                return !lastResult && !lastFaceMode;
            case Constants.VERIFY_MODE_FACE_FIRST_DOUBLE:
                return lastResult && lastFaceMode;
            case Constants.VERIFY_MODE_FINGER_FIRST_DOUBLE:
                return lastResult && !lastFaceMode;
        }
        return true;
    }

    public static boolean whatNextExecute(int mode) {
        switch (mode) {
            case Constants.VERIFY_MODE_FACE_FIRST_ONCE:
            case Constants.VERIFY_MODE_FACE_FIRST_DOUBLE:
                return false;
            case Constants.VERIFY_MODE_FINGER_FIRST_ONCE:
            case Constants.VERIFY_MODE_FINGER_FIRST_DOUBLE:
                return true;
        }
        return true;
    }

    public static boolean isPass(int mode, boolean faceResult, boolean fingerResult) {
        switch (mode) {
            case Constants.VERIFY_MODE_FACE_ONLY:
                return faceResult;
            case Constants.VERIFY_MODE_FINGER_ONLY:
                return fingerResult;
            case Constants.VERIFY_MODE_FACE_FIRST_ONCE:
            case Constants.VERIFY_MODE_FINGER_FIRST_ONCE:
                return faceResult || fingerResult;
            case Constants.VERIFY_MODE_FACE_FIRST_DOUBLE:
            case Constants.VERIFY_MODE_FINGER_FIRST_DOUBLE:
                return faceResult && fingerResult;
        }
        return false;
    }

    public static String getDescribe(int mode, boolean faceResult, boolean fingerResult) {
        switch (mode) {
            case Constants.VERIFY_MODE_FACE_ONLY:
                return faceResult ? "人脸通过" : "人脸不通过";
            case Constants.VERIFY_MODE_FINGER_ONLY:
                return fingerResult ? "指纹通过" : "指纹不通过";
            case Constants.VERIFY_MODE_FACE_FIRST_ONCE:
            case Constants.VERIFY_MODE_FINGER_FIRST_ONCE:
                return faceResult || fingerResult ? (faceResult ? "人脸通过" : "指纹通过") : "人脸、指纹不通过";
            case Constants.VERIFY_MODE_FACE_FIRST_DOUBLE:
            case Constants.VERIFY_MODE_FINGER_FIRST_DOUBLE:
                return faceResult && !fingerResult ? "人脸、指纹通过" : "人脸或指纹不通过";
        }
        return "";
    }

    public interface OnConfigSaveListener {
        void onConfigSave(boolean result, String message);
    }

}
