package com.miaxis.face.manager;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.DaheResponseEntity;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.RecordDto;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.net.UpLoadRecord;
import com.miaxis.face.util.EncryptUtil;
import com.miaxis.face.util.FileUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private static final Gson GSON = new Gson();
    private static final String SECRET_KEY = "4af450e4dd333efd975f6901229d130ea74b2fad";

    private volatile boolean uploading = false;

    private Call<DaheResponseEntity> uploadCall1;
    private Call<DaheResponseEntity> uploadCall2;

    public void cancelRequest() {
        uploading = false;
        if (uploadCall1 != null) {
            uploadCall1.cancel();
        }
        if (uploadCall2 != null) {
            uploadCall2.cancel();
        }
    }

    public void uploadRecord(@NonNull IDCardRecord idCardRecord, @NonNull OnRecordUploadResultListener listener) {
        uploading = true;
        App.getInstance().getThreadExecutor().execute(() -> {
            uploadRecordSync(idCardRecord, listener);
        });
    }

    public void uploadRecordSync(@NonNull IDCardRecord idCardRecord, @NonNull OnRecordUploadResultListener listener) {
        boolean result = false;
        try {
            String json = makeUploadJson(idCardRecord);
            Config config = ConfigManager.getInstance().getConfig();
            try {
                if (!uploading) return;
                uploadCall1 = FaceNetApi.uploadRecord(config.getUploadRecordUrl1(), json);
                Response<DaheResponseEntity> execute = uploadCall1.execute();
                DaheResponseEntity body = execute.body();
                if (body != null && body.getErrCode() == 0) {
                    result = true;
                    if (!uploading) return;
                    listener.onUploadResult(true, body.getErrMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!uploading) return;
                uploadCall2 = FaceNetApi.uploadRecord(config.getUploadRecordUrl2(), json);
                Response<DaheResponseEntity> execute = uploadCall2.execute();
                DaheResponseEntity body = execute.body();
                if (!result && body != null && body.getErrCode() == 0) {
                    result = true;
                    if (!uploading) return;
                    listener.onUploadResult(true, body.getErrMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                if (!uploading) return;
                listener.onUploadResult(false, "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!uploading) return;
            listener.onUploadResult(false, e.getMessage() + "");
        }
    }

    public interface OnRecordUploadResultListener {
        void onUploadResult(boolean result, String message);
    }

    private String makeUploadJson(IDCardRecord idCardRecord) {
        Config config = ConfigManager.getInstance().getConfig();

        String account = config.getAccount();
        String client = config.getClientId();
        String cmd = getCmd(idCardRecord);
        String data = makeDataJson(idCardRecord, cmd);
        String nonce = String.valueOf(System.currentTimeMillis());
        String timeStamp = TIME_STAMP_FORMAT.format(new Date());
        String signature = EncryptUtil.md5Decode32(cmd + account + nonce + client + timeStamp + data + SECRET_KEY);

        Map<String, String> param = new HashMap<>();
        param.put("account", account);
        param.put("client", client);
        param.put("cmd", cmd);
        param.put("nonce", nonce);
        param.put("timestamp", timeStamp);
        param.put("signatrue", signature);

        if (config.getEncrypt()) {
            String encrypt = EncryptUtil.encryptAES(data, SECRET_KEY);
            if (encrypt == null) {
                param.put("aes", "false");
                param.put("data", data);
            } else {
                param.put("aes", "true");
                param.put("data", encrypt);
            }
        } else {
            param.put("aes", "false");
            param.put("data", data);
        }
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

    public void uploadUndocumented(@NonNull Undocumented undocumented, @NonNull OnRecordUploadResultListener listener) {
        uploading = true;
        App.getInstance().getThreadExecutor().execute(() -> {
            uploadUndocumentedSync(undocumented, listener);
        });
    }

    private void uploadUndocumentedSync(@NonNull Undocumented undocumented, @NonNull OnRecordUploadResultListener listener) {
        boolean result = false;
        try {
            String json = makeUndocumentedJson(undocumented);
            Config config = ConfigManager.getInstance().getConfig();
            try {
                if (!uploading) return;
                uploadCall1 = FaceNetApi.uploadRecord(config.getUploadRecordUrl1(), json);
                Response<DaheResponseEntity> execute = uploadCall1.execute();
                DaheResponseEntity body = execute.body();
                if (body != null && body.getErrCode() == 0) {
                    result = true;
                    if (!uploading) return;
                    listener.onUploadResult(true, body.getErrMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!uploading) return;
                uploadCall2 = FaceNetApi.uploadRecord(config.getUploadRecordUrl2(), json);
                Response<DaheResponseEntity> execute = uploadCall2.execute();
                DaheResponseEntity body = execute.body();
                if (!result && body != null && body.getErrCode() == 0) {
                    result = true;
                    if (!uploading) return;
                    listener.onUploadResult(true, body.getErrMsg());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!result) {
                if (!uploading) return;
                listener.onUploadResult(false, "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!uploading) return;
            listener.onUploadResult(false, e.getMessage() + "");
        }
    }

    private String makeUndocumentedJson(Undocumented undocumented) {
        Config config = ConfigManager.getInstance().getConfig();

        String account = config.getAccount();
        String client = config.getClientId();
        String cmd = "NoPaper";
        String data = makeUndocumentedDataJson(undocumented);
        String nonce = String.valueOf(System.currentTimeMillis());
        String timeStamp = TIME_STAMP_FORMAT.format(new Date());
        String signature = EncryptUtil.md5Decode32(cmd + account + nonce + client + timeStamp + data + SECRET_KEY);

        Map<String, String> param = new HashMap<>();
        param.put("account", account);
        param.put("client", client);
        param.put("cmd", cmd);
        param.put("nonce", nonce);
        param.put("timestamp", timeStamp);
        param.put("signatrue", signature);

        if (config.getEncrypt()) {
            String encrypt = EncryptUtil.encryptAES(data, SECRET_KEY);
            if (encrypt == null) {
                param.put("aes", "false");
                param.put("data", data);
            } else {
                param.put("aes", "true");
                param.put("data", encrypt);
            }
        } else {
            param.put("aes", "false");
            param.put("data", data);
        }
        return GSON.toJson(param);
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

}
