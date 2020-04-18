package com.miaxis.face.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.DaheResponse;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.event.UploadResultEvent;
import com.miaxis.face.net.UpLoadRecord;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.util.MyUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class UpLoadRecordService extends IntentService {

    private static final String ACTION_UPLOAD = "com.miaxis.face.service.action.UPLOAD";

    private static final String RECORD = "com.miaxis.face.service.extra.RECORD";
    private static final String UNDOCUTENTED = "com.miaxis.face.service.extra.UNDOCUTENTED";
    private static final String CONFIG = "com.miaxis.face.service.extra.CONFIG";

    public UpLoadRecordService() {
        super("UpLoadRecordService");
    }

    public static void startActionUpLoad(Context context, Record record, Config config) {
        Intent intent = new Intent(context, UpLoadRecordService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(RECORD, record);
        intent.putExtra(CONFIG, config);
        context.startService(intent);
    }

    public static void startActionUploadUndocumented(Context context, Undocumented undocumented, Config config) {
        Intent intent = new Intent(context, UpLoadRecordService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(UNDOCUTENTED, undocumented);
        intent.putExtra(CONFIG, config);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if (intent != null && TextUtils.equals(ACTION_UPLOAD, intent.getAction())) {
//            try {
//                Config config = (Config) intent.getSerializableExtra(CONFIG);
//                Record record = (Record) intent.getSerializableExtra(RECORD);
//                if (record != null) {
//                    handleActionUploadRecord(record, config);
//                    return;
//                }
//                Undocumented undocumented = (Undocumented) intent.getSerializableExtra(UNDOCUTENTED);
//                if (undocumented != null) {
//                    handleActionUploadUndocumented(undocumented, config);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                LogUtil.writeLog("UpLoadRecordService Exception" + e.getMessage());
//            }
//        }
    }

//    private void handleActionUploadUndocumented(Undocumented undocumented, Config config) {
//        Observable.just(0)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .flatMap((Function<Integer, ObservableSource<DaheResponse>>) integer -> {
//                    URL url = new URL(config.getHost());
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/")
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                            .build();
//                    UpLoadRecord up = retrofit.create(UpLoadRecord.class);
//                    Map<String, String> param = new HashMap<>();
//                    param.put("name", undocumented.getName());
//                    param.put("folk", undocumented.getNation());
//                    param.put("number", undocumented.getCardNumber());
//                    param.put("faceImage", undocumented.getFaceImage());
//                    param.put("source", "zjzz");
//                    param.put("deviceId", MyUtil.md5(undocumented.getCardNumber() + "zjzz" + "dhmyyjtforhotel2017"));
//                    param.put("mode", "3");
//                    String strEntity = new Gson().toJson(param);
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
//                    return up.post(url.getPath(), body);
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(daheResponse -> {
//                            if (TextUtils.equals("0", daheResponse.getCode())) {
//                                EventBus.getDefault().post(new UploadResultEvent(true));
//                            } else {
//                                EventBus.getDefault().post(new UploadResultEvent(false));
//                            }
//                            Log.e("asd", "handleActionUploadUndocumented" + "true");
//                        },
//                        throwable -> {
//                            EventBus.getDefault().post(new UploadResultEvent(false));
//                            Log.e("asd", "handleActionUploadUndocumented" + "false");
//                        });
//    }
//
//    private void handleActionUploadRecord(Record record, Config config) {
//        Observable.just(0)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .flatMap((Function<Integer, ObservableSource<DaheResponse>>) integer -> {
//                    URL url = new URL(config.getHost());
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/")
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                            .build();
//                    UpLoadRecord up = retrofit.create(UpLoadRecord.class);
//                    Map<String, String> param = new HashMap<>();
//                    param.put("headImage", record.getCardImgData() != null ? Base64.encodeToString(record.getCardImgData(), Base64.DEFAULT) : "null");
//                    param.put("faceImage", record.getFaceImgData() != null ? Base64.encodeToString(record.getFaceImgData(), Base64.DEFAULT) : "null");
//                    param.put("faceValue", TextUtils.isEmpty(record.getScore()) ? "0" : record.getScore());
//                    param.put("isPass", MyUtil.getStatusType(record.getStatus()) ? "true" : "false");
//                    param.put("source", "zjzz");
//                    param.put("corpCode", "");
//                    param.put("deviceId", MyUtil.md5(record.getCardNo() + "zjzz" + "dhmyyjtforhotel2017"));
//                    param.put("mode", "0");
//                    if (TextUtils.equals(record.getType(), "I")) { //外国人永久居留证
//                        param.put("name", record.getChineseName());
//                        param.put("sex", record.getSex());
//                        param.put("folk", record.getRace());
//                        param.put("birthday", record.getBirthday());
//                        param.put("address", "");
//                        param.put("validterm", record.getValidate());
//                        param.put("number", record.getCardNo());
//                        param.put("enname", record.getName());
//                        param.put("changenum", "");
//                        param.put("paperType", "12");
//                    } else if (TextUtils.equals(record.getType(), "J")) { //港澳台居民居住证
//                        param.put("name", record.getName());
//                        param.put("sex", record.getSex());
//                        param.put("folk", record.getRace());
//                        param.put("birthday", record.getBirthday());
//                        param.put("address", record.getAddress());
//                        param.put("validterm", record.getValidate());
//                        param.put("number", record.getCardNo());
//                        param.put("enname", "");
//                        param.put("changenum", record.getIssueNum());
//                        if (record.getCardNo().startsWith("83")) {
//                            param.put("paperType", "14");
//                        } else {
//                            param.put("paperType", "13");
//                        }
//                    } else {
//                        param.put("name", record.getName());
//                        param.put("sex", record.getSex());
//                        param.put("folk", record.getRace());
//                        param.put("birthday", record.getBirthday());
//                        param.put("address", record.getAddress());
//                        param.put("validterm", record.getValidate());
//                        param.put("number", record.getCardNo());
//                        param.put("enname", "");
//                        param.put("changenum", "");
//                        param.put("paperType", "1");
//                    }
//                    String strEntity = new Gson().toJson(param);
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), strEntity);
//                    return up.post(url.getPath(), body);
//                })
//                .subscribe(responseEntity -> {
//                    Log.e("asd", "handleActionUploadRecord" + "true");
//                    if (config.getSaveLocal()) {
//                        record.setHasUp(true);
//                        Face_App.getInstance().getDaoSession().getRecordDao().update(record);
//                    }
//                }, throwable -> Log.e("asd", "handleActionUploadRecord" + "false"));
//    }

}
