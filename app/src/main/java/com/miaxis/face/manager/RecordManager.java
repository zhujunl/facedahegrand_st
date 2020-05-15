package com.miaxis.face.manager;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.DaheResponseEntity;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.RecordDto;
import com.miaxis.face.bean.Task;
import com.miaxis.face.bean.TaskOver;
import com.miaxis.face.bean.TaskResult;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.util.EncryptUtil;
import com.miaxis.face.util.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class RecordManager {

    private RecordManager() {
    }

    public static RecordManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final RecordManager instance = new RecordManager();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    private static final SimpleDateFormat TIME_STAMP_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    private static final Gson GSON = new GsonBuilder().create();
    private static final String SECRET_KEY = "4af450e4dd333efd975f6901229d130ea74b2fad";
    private static final String SIGN_KEY = "dhmyyjtforhotel2017";

    private volatile boolean uploading = false;

    private Call<DaheResponseEntity> uploadCall;

    public interface OnRecordUploadResultListener {
        void onUploadResult(boolean result, String message, boolean playVoice, String voiceText);
    }

    public void cancelRequest() {
        uploading = false;
        if (uploadCall != null) {
            uploadCall.cancel();
        }
    }

    public void uploadRecord(@NonNull IDCardRecord idCardRecord, @NonNull OnRecordUploadResultListener listener) {
        uploading = true;
        App.getInstance().getThreadExecutor().execute(() -> {
            String cmd = getCmd(idCardRecord);
            String data = makeDataJson(idCardRecord, cmd);
            String json = makeUploadResultJson(cmd, data);
            uploadResultSync(json, listener);
        });
    }

    public void uploadUndocumented(@NonNull Undocumented undocumented, @NonNull OnRecordUploadResultListener listener) {
        uploading = true;
        App.getInstance().getThreadExecutor().execute(() -> {
            String cmd = "NoPaper";
            String data = makeUndocumentedDataJson(undocumented);
            String json = makeUploadResultJson(cmd, data);
            uploadResultSync(json, listener);
        });
    }

//    public void uploadTaskOver(@NonNull Task task, @NonNull TaskResult taskResult, @NonNull OnRecordUploadResultListener listener) {
//        uploading = true;
//        App.getInstance().getThreadExecutor().execute(() -> {
//            String cmd = "TaskOver";
//            String data = makeTaskDataJson(task, taskResult);
//            String json = makeUploadResultJson(cmd, data);
//            uploadResultSync(json, listener);
//        });
//    }

    private void uploadResultSync(@NonNull String json, @NonNull OnRecordUploadResultListener listener) {
        try {
            Config config = ConfigManager.getInstance().getConfig();
            if (!uploading) return;
            uploadCall = FaceNetApi.uploadRecord(config.getUploadRecordUrl(), json);
            Response<DaheResponseEntity> execute = uploadCall.execute();
            DaheResponseEntity body = execute.body();
            if (!uploading) return;
            if (body != null) {
                String message = "服务器回执：" + body.getErrCode()
                        + "\nMsg:" + body.getErrMsg()
                        + "\nplayVoice:" + body.isPlayVoice()
                        + "\nvoicetext:" + body.getVoiceText();
                Log.e("asd", message);
                ToastManager.toast(message);
            }
            if (body != null && body.getErrCode() == 0) {
                listener.onUploadResult(true, body.getErrMsg(), body.isPlayVoice(), body.getVoiceText());
            } else {
                listener.onUploadResult(false, "上传失败", true, "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!uploading) return;
            ToastManager.toast("Http：" + e.getMessage());
            listener.onUploadResult(false, e.getMessage() + "", true, "上传失败");
        }
    }

    private String makeUploadResultJson(String cmd, String data) {
        Config config = ConfigManager.getInstance().getConfig();

        String account = config.getAccount();
        String client = config.getClientId();
        String nonce = String.valueOf(System.currentTimeMillis());
        String timeStamp = TIME_STAMP_FORMAT.format(new Date());
        boolean ase = false;
        if (config.getEncrypt()) {
            String encrypt = EncryptUtil.encryptAES(data, SECRET_KEY);
            if (encrypt != null) {
                ase = true;
                data = encrypt;
            }
        }
        String sign = cmd + account + nonce + client + timeStamp + data + SIGN_KEY;
        String signature = EncryptUtil.md5Decode32(sign);

        JsonObject param = new JsonObject();
        param.addProperty("account", account);
        param.addProperty("client", client);
        param.addProperty("cmd", cmd);
        param.addProperty("nonce", nonce);
        param.addProperty("timestamp", timeStamp);
        param.addProperty("signatrue", signature);
        param.addProperty("data", data);
        param.addProperty("aes", ase);

        return GSON.toJson(param);
    }

    private String getCmd(IDCardRecord idCardRecord) {
        if (idCardRecord.getFaceResult()) {
            return "FaceVerify";
        } else if (idCardRecord.getFingerResult()) {
            return "FPVerify";
        } else {
            if (ConfigManager.isFaceFirst(idCardRecord.getVerifyMode())) {
                return "FaceVerify";
            } else {
                return "FPVerify";
            }
        }
    }

    private String makeDataJson(IDCardRecord idCardRecord, String cmd) {
        RecordDto recordDto;
        if (TextUtils.equals("I", idCardRecord.getCardType())) {
            recordDto = new RecordDto.Builder()
                    .name(idCardRecord.getChineseName())
                    .enname(idCardRecord.getName())
                    .paperType("12")
                    .number(idCardRecord.getCardNumber())
                    .sex(idCardRecord.getSex())
                    .country(idCardRecord.getNation())
                    .address(idCardRecord.getAddress())
                    .birthday(idCardRecord.getBirthday())
                    .signOrgan(idCardRecord.getIssuingAuthority())
                    .valid(idCardRecord.getValidateStart() + "-" + idCardRecord.getValidateEnd())
                    .idCard(idCardRecord.getCardId())
                    .headImage(idCardRecord.getCardBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getCardBitmap()) : "")
                    .snapshot(idCardRecord.getFaceBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getFaceBitmap()) : "")
                    .similarity(String.valueOf(idCardRecord.getFaceScore()))
                    .isPass(String.valueOf(idCardRecord.getFaceResult() || idCardRecord.getFingerResult()))
                    .fpFeatrue1(idCardRecord.getFingerprint0())
                    .fpFeatrue2(idCardRecord.getFingerprint1())
                    .sceneFP1(idCardRecord.getGatherFingerprint1())
                    .sceneFP2(idCardRecord.getGatherFingerprint2())
                    .fpImage1(idCardRecord.getGatherFingerprintBitmap1() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap1()) : "")
                    .fpImage2(idCardRecord.getGatherFingerprintBitmap2() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap2()) : "")
                    .deviceSN(ConfigManager.getInstance().getConfig().getDeviceSerialNumber())
                    .build();
        } else if (TextUtils.equals("J", idCardRecord.getCardType())) {
            String gatCardType;
            if (idCardRecord.getCardNumber().startsWith("83")) {
                gatCardType = "14";
            } else {
                gatCardType = "13";
            }
            recordDto = new RecordDto.Builder()
                    .name(idCardRecord.getName())
                    .paperType(gatCardType)
                    .number(idCardRecord.getCardNumber())
                    .sex(idCardRecord.getSex())
                    .folk(idCardRecord.getNation())
                    .address(idCardRecord.getAddress())
                    .birthday(idCardRecord.getBirthday())
                    .signOrgan(idCardRecord.getIssuingAuthority())
                    .valid(idCardRecord.getValidateStart() + "-" + idCardRecord.getValidateEnd())
                    .idCard(idCardRecord.getCardId())
                    .passNumber(idCardRecord.getPassNumber())
                    .changeNum(idCardRecord.getIssueCount())
                    .headImage(idCardRecord.getCardBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getCardBitmap()) : "")
                    .snapshot(idCardRecord.getFaceBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getFaceBitmap()) : "")
                    .similarity(String.valueOf(idCardRecord.getFaceScore()))
                    .isPass(String.valueOf(idCardRecord.getFaceResult() || idCardRecord.getFingerResult()))
                    .fpFeatrue1(idCardRecord.getFingerprint0())
                    .fpFeatrue2(idCardRecord.getFingerprint1())
                    .sceneFP1(idCardRecord.getGatherFingerprint1())
                    .sceneFP2(idCardRecord.getGatherFingerprint2())
                    .fpImage1(idCardRecord.getGatherFingerprintBitmap1() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap1()) : "")
                    .fpImage2(idCardRecord.getGatherFingerprintBitmap2() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap2()) : "")
                    .deviceSN(ConfigManager.getInstance().getConfig().getDeviceSerialNumber())
                    .build();
        } else {
            recordDto = new RecordDto.Builder()
                    .name(idCardRecord.getName())
                    .paperType("1")
                    .number(idCardRecord.getCardNumber())
                    .sex(idCardRecord.getSex())
                    .folk(idCardRecord.getNation())
                    .address(idCardRecord.getAddress())
                    .birthday(idCardRecord.getBirthday())
                    .signOrgan(idCardRecord.getIssuingAuthority())
                    .valid(idCardRecord.getValidateStart() + "-" + idCardRecord.getValidateEnd())
                    .idCard(idCardRecord.getCardId())
                    .headImage(idCardRecord.getCardBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getCardBitmap()) : "")
                    .snapshot(idCardRecord.getFaceBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getFaceBitmap()) : "")
                    .similarity(String.valueOf(idCardRecord.getFaceScore()))
                    .isPass(String.valueOf(idCardRecord.getFaceResult() || idCardRecord.getFingerResult()))
                    .fpFeatrue1(idCardRecord.getFingerprint0())
                    .fpFeatrue2(idCardRecord.getFingerprint1())
                    .sceneFP1(idCardRecord.getGatherFingerprint1())
                    .sceneFP2(idCardRecord.getGatherFingerprint2())
                    .fpImage1(idCardRecord.getGatherFingerprintBitmap1() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap1()) : "")
                    .fpImage2(idCardRecord.getGatherFingerprintBitmap2() != null ? FileUtil.bitmapToBase64(idCardRecord.getGatherFingerprintBitmap2()) : "")
                    .deviceSN(ConfigManager.getInstance().getConfig().getDeviceSerialNumber())
                    .build();
        }
        if (!TextUtils.equals(cmd, "FaceVerify")) {
            recordDto.setSceneFP1(idCardRecord.getLocaleFingerprint());
            recordDto.setFpImage1(idCardRecord.getLocaleFingerprintBitmap() != null ? FileUtil.bitmapToBase64(idCardRecord.getLocaleFingerprintBitmap()) : "");
            recordDto.setSimilarity(String.valueOf(idCardRecord.getFingerScore()));
        }
        return GSON.toJson(recordDto);
    }

    private String makeUndocumentedDataJson(Undocumented undocumented) {
        RecordDto recordDto = new RecordDto.Builder()
                .name(undocumented.getName())
                .number(undocumented.getCardNumber())
                .folk(undocumented.getNation())
                .snapshot(undocumented.getFaceImage() != null ? FileUtil.bitmapToBase64(undocumented.getFaceImage()) : "")
                .sceneFP1(undocumented.getGatherFingerprint1())
                .sceneFP2(undocumented.getGatherFingerprint2())
                .fpImage1(undocumented.getGatherFingerprintBitmap1() != null ? FileUtil.bitmapToBase64(undocumented.getGatherFingerprintBitmap1()) : "")
                .fpImage2(undocumented.getGatherFingerprintBitmap2() != null ? FileUtil.bitmapToBase64(undocumented.getGatherFingerprintBitmap2()) : "")
                .deviceSN(ConfigManager.getInstance().getConfig().getDeviceSerialNumber())
                .build();
        return GSON.toJson(recordDto);
    }

    public void uploadHeartBeat(String data) {
        try {
            String cmd = "Heartbeat";
            String json = makeUploadResultJson(cmd, data);
            Config config = ConfigManager.getInstance().getConfig();
            uploadCall = FaceNetApi.uploadRecord(config.getUploadRecordUrl(), json);
            Response<DaheResponseEntity> execute = uploadCall.execute();
            DaheResponseEntity body = execute.body();
            if (body != null) {
                Log.e("asd", "心跳回执：" + body.getErrCode() + "，Msg:" + body.getErrMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
