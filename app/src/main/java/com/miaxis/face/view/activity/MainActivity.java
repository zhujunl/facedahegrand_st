package com.miaxis.face.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.jakewharton.rxbinding2.view.RxView;
import com.miaxis.face.R;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.LocalFeature;
import com.miaxis.face.bean.Record;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.bean.WhiteItem;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.DrawRectEvent;
import com.miaxis.face.event.HasCardEvent;
import com.miaxis.face.event.LoadProgressEvent;
import com.miaxis.face.event.NoCardEvent;
import com.miaxis.face.event.ResultEvent;
import com.miaxis.face.event.TimeChangeEvent;
import com.miaxis.face.event.UndocumentedEvent;
import com.miaxis.face.event.UploadResultEvent;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.greendao.gen.WhiteItemDao;
import com.miaxis.face.presenter.AdvertisePresenter;
import com.miaxis.face.receiver.TimeReceiver;
import com.miaxis.face.service.UpLoadRecordService;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.view.custom.AdvertiseDialogFragment;
import com.miaxis.face.view.custom.ContentLoadingDialog;
import com.miaxis.face.view.custom.ResultLayout;
import com.miaxis.face.view.fragment.AlertDialog;
import com.miaxis.face.view.fragment.UndocumentedDialogFragment;
import com.miaxis.image.MXImage;
import com.miaxis.image.MXImages;
import com.miaxis.livedetect.jni.MXLiveDetectApi;
import com.miaxis.livedetect.jni.vo.FaceInfo;
import com.miaxis.livedetect.jni.vo.FaceQuality;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.zz.api.MXFaceAPI;
import org.zz.api.MXFaceInfoEx;
import org.zz.idcard_hid_driver.IdCardDriver;
import org.zz.jni.mxImageTool;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.miaxis.face.constant.Constants.CP_WIDTH;
import static com.miaxis.face.constant.Constants.GET_CARD_ID;
import static com.miaxis.face.constant.Constants.GPIO_INTERVAL;
import static com.miaxis.face.constant.Constants.LEFT_VOLUME;
import static com.miaxis.face.constant.Constants.LOOP;
import static com.miaxis.face.constant.Constants.MAX_FACE_NUM;
import static com.miaxis.face.constant.Constants.NO_CARD;
import static com.miaxis.face.constant.Constants.PIC_HEIGHT;
import static com.miaxis.face.constant.Constants.PIC_WIDTH;
import static com.miaxis.face.constant.Constants.PRE_HEIGHT;
import static com.miaxis.face.constant.Constants.PRE_WIDTH;
import static com.miaxis.face.constant.Constants.PRIORITY;
import static com.miaxis.face.constant.Constants.RIGHT_VOLUME;
import static com.miaxis.face.constant.Constants.SOUND_RATE;
import static com.miaxis.face.constant.Constants.ZOOM_HEIGHT;
import static com.miaxis.face.constant.Constants.ZOOM_WIDTH;
import static com.miaxis.face.constant.Constants.mFingerDataSize;
import static com.miaxis.face.constant.Constants.zoomRate;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, AMapLocationListener, WeatherSearch.OnWeatherSearchListener {

    long at1;

    private static final boolean WRITE_TIME = false;
    private static final int MSG_LIVENESS_HINT = 202;
    private static final int MSG_LIVENESS_DISMISS_FACE_BOX = 203;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_wel_msg)
    TextView tvWelMsg;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.sv_main)
    SurfaceView svMain;
    @BindView(R.id.sv_rect)
    SurfaceView svRect;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.rv_result)
    ResultLayout rvResult;
    @BindColor(R.color.white)
    int white;
    @BindView(R.id.tv_pass)
    TextView tvPass;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.iv_import_from_u)
    ImageView ivImportFromU;
    @BindView(R.id.iv_cloud_down)
    ImageView ivCloudDown;
    @BindView(R.id.iv_face_box)
    ImageView ivFaceBox;
    @BindView(R.id.tv_liveness_hint)
    TextView tvLivenessHint;

    private Record mRecord;
    private int mCurSoundId;

    private Camera mCamera;
    private SurfaceHolder shMain;
    private SurfaceHolder shRect;

    private SoundPool soundPool;
    private SoundPool arSoundPool;
    private Map<Integer, Integer> soundMap;
    private SparseIntArray arSoundMap;

    private MXFaceAPI mxFaceAPI;
    private MXLiveDetectApi mxLiveDetectApi;
    private IdCardDriver idCardDriver;          // 身份证
//    private mxImageLoad dtload;                 // 加载图像
    private org.zz.jni.mxImageTool mxImageTool;
    public AMapLocationClient mLocationClient;
    private SmdtManager smdtManager;
    private EventBus eventBus;
    private TimeReceiver timeReceiver;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();  //用来进行特征提取的线程池

    private boolean isExtractWorking;
    private volatile boolean detectFlag;
    private boolean extractFlag;
    private boolean matchFlag;
    private boolean readIdFlag = true;
    private boolean noCardFlag = false;
    private boolean monitorFlag = true;
    private boolean localFlag = false;
    private boolean continuePlaySoundFlag = true;
    private boolean humanInductionFlag = false;
    private volatile boolean advertiseLock = true;

    private byte[] idFaceFeature;               // 身份证照片 人脸特征
    private byte[] curFaceFeature;
    private byte[] curCameraImg;
    private MXFaceInfoEx curFaceInfo;

    private double latitude;
    private double longitude;
    private String location;

    private Config config;
    private RecordDao recordDao;

    private static final String TAG = "MainActivity";
    private long lastCameraCallBackTime = 9999999999999L;
    private volatile long noActionSecond = 0;

    private List<WhiteItem> whiteItemList;
    private List<LocalFeature> localFeatureList;
    private ReadIdThread readIdThread;

    private ContentLoadingDialog loadingDialog;
    private Subscription mSubscription;
    private int max;
    private WhiteItemDao whiteItemDao;

    private int mState = 0;         // 记录点击次数
    private long firstTime = 0;
    private int toType;             // 0 SettingActivity   1 RecordActivity
    private final Byte lock1 = 1;
    private final Byte lock2 = 2;
    //    private final Byte cameraLock = 3;
    private ReentrantLock cameraLock = new ReentrantLock();
    private AdvertiseDialogFragment advertiseDialog;
    private Undocumented undocumented;
    private volatile boolean undocumentedFlag;
    private Disposable undocumentedCount;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initWindow();
        initData();
        initView();
        initSurface();
        initAMapSDK();
        initTimeReceiver();
        startMonitor();
    }

    private void initData() {
        whiteItemDao = Face_App.getInstance().getDaoSession().getWhiteItemDao();
//        mxFaceAPI = Face_App.getMxAPI();
//        mxLiveDetectApi = Face_App.getMxLiveDetectApi();
        idCardDriver = new IdCardDriver(this);
        smdtManager = SmdtManager.create(this);
//        dtload = new mxImageLoad();
        mxImageTool = new mxImageTool();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
//        recordDao = Face_App.getInstance().getDaoSession().getRecordDao();

        soundPool = new SoundPool(21, AudioManager.STREAM_MUSIC, 0);
        soundMap = new HashMap<>();
        soundMap.put(Constants.SOUND_SUCCESS, soundPool.load(this, R.raw.success, 1));
        soundMap.put(Constants.SOUND_FAIL, soundPool.load(this, R.raw.fail, 1));
        soundMap.put(Constants.PLEASE_PRESS, soundPool.load(this, R.raw.please_press, 1));
        soundMap.put(Constants.SOUND_OR, soundPool.load(this, R.raw.sound_or, 1));
        soundMap.put(Constants.SOUND_OTHER_FINGER, soundPool.load(this, R.raw.please_press, 1));
        soundMap.put(Constants.SOUND_VALIDATE_FAIL, soundPool.load(this, R.raw.validate_fail, 1));
        soundMap.put(Constants.FINGER_RIGHT_0, soundPool.load(this, R.raw.finger_right_0, 1));
        soundMap.put(Constants.FINGER_RIGHT_1, soundPool.load(this, R.raw.finger_right_1, 1));
        soundMap.put(Constants.FINGER_RIGHT_2, soundPool.load(this, R.raw.finger_right_2, 1));
        soundMap.put(Constants.FINGER_RIGHT_3, soundPool.load(this, R.raw.finger_right_3, 1));
        soundMap.put(Constants.FINGER_RIGHT_4, soundPool.load(this, R.raw.finger_right_4, 1));
        soundMap.put(Constants.FINGER_LEFT_0, soundPool.load(this, R.raw.finger_left_0, 1));
        soundMap.put(Constants.FINGER_LEFT_1, soundPool.load(this, R.raw.finger_left_1, 1));
        soundMap.put(Constants.FINGER_LEFT_2, soundPool.load(this, R.raw.finger_left_2, 1));
        soundMap.put(Constants.FINGER_LEFT_3, soundPool.load(this, R.raw.finger_left_3, 1));
        soundMap.put(Constants.FINGER_LEFT_4, soundPool.load(this, R.raw.finger_left_4, 1));
        soundMap.put(Constants.PLEASE_BLINK, soundPool.load(this, R.raw.please_blink, 1));
        arSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        arSoundMap = new SparseIntArray();
        arSoundMap.put(Constants.HAS_UPLOAD, arSoundPool.load(this, R.raw.has_upload, 1));
        arSoundMap.put(Constants.UPLOAD_FAILED, arSoundPool.load(this, R.raw.upload_failed, 1));

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_LIVENESS_HINT) {
                    tvLivenessHint.setVisibility(View.VISIBLE);
                    ivFaceBox.setVisibility(View.VISIBLE);
                    String message = (String) msg.obj;
                    tvLivenessHint.setText(message);
                } else if (msg.what == MSG_LIVENESS_DISMISS_FACE_BOX) {
                    ivFaceBox.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private void initView() {
        loadingDialog = new ContentLoadingDialog();
        loadingDialog.setListener(view -> {
            loadingDialog.dismiss();
            mSubscription.cancel();
            mSubscription = null;
        });
        llTop.bringToFront();
        svRect.setZOrderOnTop(true);
        rvResult.bringToFront();
        smdtManager.smdtSetStatusBar(this, false);
        RxView.clicks(ivCloudDown)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    advertiseLock = false;
                    UndocumentedDialogFragment.newInstance(new UndocumentedDialogFragment.OnDialogButtonClickListener() {
                        @Override
                        public void onCancel() {
                            noActionSecond = 0;
                            advertiseLock = true;
                        }

                        @Override
                        public void onConfirm(String name, String cardNumber, String nation) {
                            undocumentedFlag = true;
                            runOnUiThread(() -> {
                                if (config.getLivenessFlag()) {
                                    tvLivenessHint.setText("请缓慢眨眼");
                                    tvLivenessHint.setVisibility(View.VISIBLE);
                                    ivFaceBox.setVisibility(View.VISIBLE);
                                    playSound(Constants.PLEASE_BLINK);
                                }
                                detectFlag = true;
                            });
                            undocumented = new Undocumented.Builder()
                                    .name(name)
                                    .cardNumber(cardNumber)
                                    .nation(nation)
                                    .build();
                            openLed();
                            tvPass.setVisibility(View.GONE);
                            undocumentedCount = Observable.just(0)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .delay(10, TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(integer -> {
                                        tvPass.setVisibility(View.VISIBLE);
                                        ivFaceBox.setVisibility(View.INVISIBLE);
                                        tvLivenessHint.setVisibility(View.INVISIBLE);
                                    })
                                    .subscribe(integer -> {
                                        closeLed();
                                        undocumentedFlag = false;
                                        undocumented = null;
                                        noActionSecond = 0;
                                        advertiseLock = true;
                                        detectFlag = false;
                                    });
                        }
                    }).show(getSupportFragmentManager(), "undocumented");
                });
    }

    private void initSurface() {
        shMain = svMain.getHolder();
        shMain.addCallback(this);
        shMain.setFormat(SurfaceHolder.SURFACE_TYPE_NORMAL);
        shRect = svRect.getHolder();
        shRect.setFormat(PixelFormat.TRANSLUCENT);
    }

    private void initAMapSDK() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(1000 * 60 * 5);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void initTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver();
        registerReceiver(timeReceiver, filter);
        onTimeEvent(null);
    }

    private void restartCamera() {
        try {
            while (cameraLock.isLocked()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cameraLock.lock();
            smdtManager.smdtSetGpioValue(2, true);
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            openCamera();
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            noActionSecond = 0;
            lastCameraCallBackTime = System.currentTimeMillis();
            monitorFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cameraLock.unlock();
        }
    }

    private void openCamera() {
        try {
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(PRE_WIDTH, PRE_HEIGHT);
            parameters.setPictureSize(PIC_WIDTH, PIC_HEIGHT);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(180);
            mCamera.setPreviewDisplay(shMain);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            Log.e("asd", "开启摄像头");
        } catch (Exception e) {
            LogUtil.writeLog("打开摄像头异常" + e.getMessage());
        }
    }

    private void closeCamera() {
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                Log.e("asd", "关闭摄像头");
            } else {
                Log.e(TAG, "mCamera == null");
            }
        } catch (Exception e) {
            LogUtil.writeLog("关闭摄像头异常" + e.getMessage());
        }
    }

    private void startReadId() {
        if (readIdThread == null) {
            readIdThread = new ReadIdThread();
            readIdThread.start();
        }
    }

    private void startMonitor() {
        Thread monitorThread = new MonitorThread();
        monitorThread.start();
    }

    private void playSound(int soundID) {
        continuePlaySoundFlag = false;
        soundPool.stop(mCurSoundId);
        mCurSoundId = soundPool.play(soundMap.get(soundID), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    private void playSoundAr(int soundId) {
        arSoundPool.play(arSoundMap.get(soundId), LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, SOUND_RATE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        lastCameraCallBackTime = System.currentTimeMillis();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        closeCamera();
    }


    private byte[] bestImage;
    //    private Queue<NearbyFace> nearbyImageQueue = new LinkedList<>();
    private int bestQuality = 0;
    private boolean livenessDone = false;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        lastCameraCallBackTime = System.currentTimeMillis();
        if (!detectFlag) {
            return;
        }
        if (config.getLivenessFlag() && !livenessDone) {
            try {
                MXImage srcImage = new MXImage(data, 640, 480, MXImage.FORMAT_YUV);
                MXImage cropedYUVImage = MXImages.crop(srcImage, new Rect(140, 90, 640 - 120, 480 - 90));
                MXImage rotatedYUVImage = MXImages.rotate(cropedYUVImage, 180);
                MXImage bgrImage = MXImages.yuv2BGR(rotatedYUVImage);
                bgrImage.setTag(srcImage);
                List<FaceInfo> faceInfoList = mxLiveDetectApi.faceDetect(bgrImage);
                if (faceInfoList == null) {
                    return;
                }
                int faceNumber = faceInfoList.size();
                if (faceNumber <= 0) {
                    Log.e("asd", "人脸不为1");
    //                if (!inIgnoreErrorTime()) {
    //                    if (isErrorFlow()) {
    //                        return faceNumber == 0 ? ERROR_NO_FACE : ERROR_MORE_FACE;
    //                    } else {
    //                        addErrorFrameCount();
    //                    }
    //                }
                    return;
                } else {
                    FaceInfo faceInfo = faceInfoList.get(0);
                    int maxFaceLength = Math.abs(faceInfo.getArea().right - faceInfo.getArea().left);
                    for (FaceInfo v : faceInfoList) {
                        int faceLength = Math.abs(v.getArea().right - v.getArea().left);
                        if (faceLength > maxFaceLength) {
                            maxFaceLength = faceLength;
                            faceInfo = v;
                        }
                    }
    //                if (livenessDone) return;
                    FaceQuality quality = faceInfo.getQuality();
                    if (quality.eyeDistance >= 120) {    //
                        Message message = handler.obtainMessage(MSG_LIVENESS_HINT);
                        message.obj = "请远离屏幕";
                        handler.sendMessage(message);
                        Log.e("asd", "太近");
                        return;
                    } else if (quality.eyeDistance <= 50) {   //
                        Message message = handler.obtainMessage(MSG_LIVENESS_HINT);
                        message.obj = "请靠近屏幕";
                        handler.sendMessage(message);
                        Log.e("asd", "太远");
                        return;
                    }
    //                else if (quality.pitch <= 34) {   //27
    //                    Log.e("asd", "太暗");
    //                    return;
    //                }
                    else if (quality.quality <= config.getLivenessQualityScore()) {
                        Message message = handler.obtainMessage(MSG_LIVENESS_HINT);
                        message.obj = "请对准提示框";
                        handler.sendMessage(message);
                        Log.e("asd", "太丑");
                        return;
                    }
                    int result = mxLiveDetectApi.blinkDetect(bgrImage, faceInfo);
    //                if (nearbyImageQueue.size() > 10) {
    //                    nearbyImageQueue.poll();
    //                }
    //                nearbyImageQueue.add(new NearbyFace(data, quality.quality));
                    if (livenessDone) return;
                    if (result == 2 && quality.quality > bestQuality) {
                        bestQuality = quality.quality;
                        bestImage = data;
                    }
                    //  int result = getMXLiveDetectApi().blinkDetect(image, faceInfo);
                    if (result == 1) {
    //                    byte[] bestImage;
    //                    if (nearbyImageQueue.isEmpty()) {
    //                        bestImage = data;
    //                    } else {
    //                        int maxQuality = 0;
    //                        bestImage = nearbyImageQueue.element().getData();
    //                        for (NearbyFace nearbyFace : nearbyImageQueue) {
    //                            if (nearbyFace.getQuality() > maxQuality) {
    //                                bestImage = nearbyFace.getData();
    //                            }
    //                        }
    //                    }
                        if (bestImage == null) {
                            bestImage = data;
                        }
                        Message message = handler.obtainMessage(MSG_LIVENESS_HINT);
                        message.obj = "活检通过";
                        handler.sendMessage(message);
                        message = handler.obtainMessage(MSG_LIVENESS_DISMISS_FACE_BOX);
                        handler.sendMessage(message);
                        livenessDone = true;
                        Log.e("asd", "活检成功");
                        detectFlag = true;
                        int[] pFaceNum = new int[1];
                        pFaceNum[0] = MAX_FACE_NUM;
                        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[MAX_FACE_NUM];
                        for (int i = 0; i < MAX_FACE_NUM; i++) {
                            pFaceBuffer[i] = new MXFaceInfoEx();
                        }
                        byte[] rgbData = new byte[PRE_WIDTH * PRE_HEIGHT * 3];
                        mxImageTool.YUV2RGB(bestImage, PRE_WIDTH, PRE_HEIGHT, rgbData);
                        byte[] zoomedRgbData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3];
                        int re = mxImageTool.Zoom(rgbData, PRE_WIDTH, PRE_HEIGHT, 3, ZOOM_WIDTH, ZOOM_HEIGHT, zoomedRgbData);
                        if (re != 1) {
                            Log.e(TAG, "mxImageTool.Zoom = " + re);
                        }
                        re = mxImageTool.ImageRotate(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, 180, zoomedRgbData, new int[1], new int[1]);
                        if (re != 1) {
                            return;
                        }
                        synchronized (lock2) {
                            long t1 = System.currentTimeMillis();
                            re = mxFaceAPI.mxDetectFace(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum, pFaceBuffer);
                        }
                        if (re == 0 && pFaceNum[0] > 0) {
                            eventBus.post(new DrawRectEvent(pFaceNum[0], pFaceBuffer));
                            re = mxFaceAPI.mxFaceQuality(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum[0], pFaceBuffer);
                            if (re == 0 && pFaceBuffer[0].quality > config.getQualityScore() && undocumentedFlag) {
                                undocumentedFlag = false;
                                byte[] bmpFaceData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3 + 10000];
                                int[] bmpDataLen = new int[1];
                                result = mxImageTool.ImageEncode(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, ".jpg", bmpFaceData, bmpDataLen);
                                if (result == 1) {
                                    byte[] faceImg = new byte[bmpDataLen[0]];
                                    System.arraycopy(bmpFaceData, 0, faceImg, 0, bmpDataLen[0]);
                                    eventBus.post(new UndocumentedEvent(Base64.encodeToString(faceImg, Base64.NO_WRAP)));
                                }
                                return;
                            }
                            if (re == 0 && pFaceBuffer[0].quality > config.getQualityScore() && !isExtractWorking && extractFlag) {
                                isExtractWorking = true;
                                ExtractAndMatch matchRunnable = new ExtractAndMatch(zoomedRgbData, pFaceBuffer[0]);
                                executorService.submit(matchRunnable);
                            }
                        } else {
                            eventBus.post(new DrawRectEvent(0, null));
                        }
                        ExtractAndMatch matchRunnable = new ExtractAndMatch(zoomedRgbData, pFaceBuffer[0]);
                        executorService.submit(matchRunnable);
                        return;
                    }
                    Message message = handler.obtainMessage(MSG_LIVENESS_HINT);
                    message.obj = "请缓慢眨眼";
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        int[] pFaceNum = new int[1];
        pFaceNum[0] = MAX_FACE_NUM;
        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[MAX_FACE_NUM];
        for (
                int i = 0;
                i < MAX_FACE_NUM; i++) {
            pFaceBuffer[i] = new MXFaceInfoEx();
        }

        //        byte[] rotateData = YuvUtil.rotateYUV420Degree180(data, PRE_WIDTH, PRE_HEIGHT);
        byte[] rgbData = new byte[PRE_WIDTH * PRE_HEIGHT * 3];
        mxImageTool.YUV2RGB(data, PRE_WIDTH, PRE_HEIGHT, rgbData);
        byte[] zoomedRgbData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3];
        int re = mxImageTool.Zoom(rgbData, PRE_WIDTH, PRE_HEIGHT, 3, ZOOM_WIDTH, ZOOM_HEIGHT, zoomedRgbData);
        if (re != 1) {
            Log.e(TAG, "mxImageTool.Zoom = " + re);
        }

        re = mxImageTool.ImageRotate(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, 180, zoomedRgbData, new int[1], new int[1]);
        if (re != 1) {
            return;
        }

        synchronized (lock2) {
            long t1 = System.currentTimeMillis();
//            re = mxFaceAPI.mxDetectFaceYUV(rotateData, PRE_WIDTH, PRE_HEIGHT, pFaceNum, pFaceBuffer);
            re = mxFaceAPI.mxDetectFace(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum, pFaceBuffer);
//            Log.e(TAG, "mxDetectFace 耗时：" + (System.currentTimeMillis() - t1));
        }
        if (re == 0 && pFaceNum[0] > 0) {
            eventBus.post(new DrawRectEvent(pFaceNum[0], pFaceBuffer));
            re = mxFaceAPI.mxFaceQuality(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, pFaceNum[0], pFaceBuffer);
            if (re == 0 && pFaceBuffer[0].quality > config.getQualityScore() && undocumentedFlag) {
                undocumentedFlag = false;
                byte[] bmpFaceData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3 + 10000];
                int[] bmpDataLen = new int[1];
                int result = mxImageTool.ImageEncode(zoomedRgbData, ZOOM_WIDTH, ZOOM_HEIGHT, ".jpg", bmpFaceData, bmpDataLen);
                if (result == 1) {
                    byte[] faceImg = new byte[bmpDataLen[0]];
                    System.arraycopy(bmpFaceData, 0, faceImg, 0, bmpDataLen[0]);
                    eventBus.post(new UndocumentedEvent(Base64.encodeToString(faceImg, Base64.NO_WRAP)));
                }
                return;
            }
            if (re == 0 && pFaceBuffer[0].quality > config.getQualityScore() && !isExtractWorking && extractFlag) {
                isExtractWorking = true;
                ExtractAndMatch matchRunnable = new ExtractAndMatch(zoomedRgbData, pFaceBuffer[0]);
                executorService.submit(matchRunnable);
            }
        } else {
            eventBus.post(new DrawRectEvent(0, null));
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDrawRectEvent(DrawRectEvent e) {
        Canvas canvas = shRect.lockCanvas(null);
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (e.getFaceNum() != 0) {
            drawFaceRect(e.getFaceInfos(), canvas, e.getFaceNum());
        }
        shRect.unlockCanvasAndPost(canvas);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onResultEvent(ResultEvent e) {
        if (noCardFlag && !localFlag) {
            eventBus.cancelEventDelivery(e);
            return;
        }
        Record record = e.getRecord();
        switch (e.getResult()) {
            case ResultEvent.FACE_SUCCESS:
                if (config.getVerifyMode() == Config.MODE_FACE_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST) {
                    record.setStatus("人脸通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    record.setStatus("人脸、指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                    record.setStatus("人脸通过");
                } else {
                    return;
                }
                playSound(Constants.SOUND_SUCCESS);
                break;
            case ResultEvent.FINGER_SUCCESS:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    record.setStatus("人脸、指纹通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    runOnUiThread(() -> {
                        if (config.getLivenessFlag()) {
                            tvLivenessHint.setText("请缓慢眨眼");
                            tvLivenessHint.setVisibility(View.VISIBLE);
                            ivFaceBox.setVisibility(View.VISIBLE);
                            playSound(Constants.PLEASE_BLINK);
                        }
                        detectFlag = true;
                        extractFlag = true;
                        matchFlag = true;
                    });
                    return;
                } else {
                    return;
                }
                playSound(Constants.SOUND_SUCCESS);
                break;
            case ResultEvent.FACE_FAIL:
                if (config.getVerifyMode() == Config.MODE_FACE_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FINGER_FIRST) {
                    record.setStatus("人脸不通过");
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST
                        || config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    record.setStatus("人脸不通过");
                } else if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                    record.setStatus("人脸不通过");
                } else {
                    return;
                }
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.FINGER_FAIL:
                if (config.getVerifyMode() == Config.MODE_FINGER_ONLY
                        || config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                    record.setStatus("指纹不通过");
                    playSound(Constants.SOUND_FAIL);
                } else if (config.getVerifyMode() == Config.MODE_TWO_FINGER_FIRST) {
                    if (TextUtils.isEmpty(record.getFinger0()) || TextUtils.isEmpty(record.getFinger1())) {
                        runOnUiThread(() -> {
                            if (config.getLivenessFlag()) {
                                tvLivenessHint.setText("请缓慢眨眼");
                                tvLivenessHint.setVisibility(View.VISIBLE);
                                ivFaceBox.setVisibility(View.VISIBLE);
                                playSound(Constants.PLEASE_BLINK);
                            }
                            detectFlag = true;
                            extractFlag = true;
                            matchFlag = true;
                        });
                        return;
                    }
                    record.setStatus("指纹不通过");
                    playSound(Constants.SOUND_FAIL);
                } else if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    if (TextUtils.isEmpty(record.getFinger0()) || TextUtils.isEmpty(record.getFinger1())) {
                        record.setStatus("人脸通过");
                        playSound(Constants.SOUND_SUCCESS);
                    } else {
                        record.setStatus("指纹不通过");
                        playSound(Constants.SOUND_FAIL);
                    }
                } else {
                    runOnUiThread(() -> {
                        if (config.getLivenessFlag()) {
                            tvLivenessHint.setText("请缓慢眨眼");
                            tvLivenessHint.setVisibility(View.VISIBLE);
                            ivFaceBox.setVisibility(View.VISIBLE);
                            playSound(Constants.PLEASE_BLINK);
                        }
                        detectFlag = true;
                        extractFlag = true;
                        matchFlag = true;
                    });
                    return;
                }
                break;
            case ResultEvent.WHITE_LIST_FAIL:
                record.setStatus("白名单检验失败");
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.BLACK_LIST_FAIL:
                record.setStatus("黑名单检验失败");
                playSound(Constants.SOUND_FAIL);
                break;
            case ResultEvent.VERIFY_FINGER:
                if (TextUtils.isEmpty(record.getFinger0()) && TextUtils.isEmpty(record.getFinger1())) {
                    return;
                }
                if (record.getFingerPosition0() >= 11
                        && record.getFingerPosition0() <= 20
                        && record.getFingerPosition1() >= 11
                        && record.getFingerPosition1() <= 20) {
                    playSound(Constants.PLEASE_PRESS, record.getFingerPosition1(), Constants.SOUND_OR, record.getFingerPosition0());
                } else {
                    playSound(Constants.SOUND_OTHER_FINGER);
                }
                return;
            case ResultEvent.VALIDATE_FAIL:
                playSound(Constants.SOUND_VALIDATE_FAIL);
                record.setStatus("身份证过期");
                break;
            default:
                return;
        }
        record.setCreateDate(new Date());
        record.setDevsn(MyUtil.getSerialNumber());
        record.setBusEntity(config.getOrgName());
        record.setLocation(location);
        record.setLatitude(latitude + "");
        record.setLongitude(longitude + "");
        if (config.getQueryFlag()) {
            FileUtil.saveRecordImg(record, this);
            recordDao.insert(record);
        }
//        startAD();
        if (config.getNetFlag()) {
            if (config.getResultFlag() ||
                    (TextUtils.equals(record.getStatus(), "人脸通过")
                            || TextUtils.equals(record.getStatus(), "指纹通过")
                            || TextUtils.equals(record.getStatus(), "人脸、指纹通过"))) {
                UpLoadRecordService.startActionUpLoad(this, record, config);
            }
        }
    }

    /* 处理 时间变化 事件， 实时更新时间*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeEvent(TimeChangeEvent e) {
        DateFormat dateFormat = new SimpleDateFormat("E  yyyy-MM-dd");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        tvTime.setText(time);
        tvDate.setText(date);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoCardEvent(NoCardEvent e) {
        if (undocumentedFlag) {
            return;
        }
        if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
            onDrawRectEvent(new DrawRectEvent(0, null));
        } else {
            tvPass.setVisibility(View.VISIBLE);
            ivFaceBox.setVisibility(View.INVISIBLE);
            tvLivenessHint.setVisibility(View.INVISIBLE);
            detectFlag = false;
            extractFlag = false;
            matchFlag = false;
            idFaceFeature = null;
            curFaceFeature = null;
            livenessDone = false;
//            nearbyImageQueue.clear();
            bestImage = null;
            bestQuality = 0;
            onDrawRectEvent(new DrawRectEvent(0, null));
            closeLed();
        }
        continuePlaySoundFlag = false;
        soundPool.stop(mCurSoundId);
        if (undocumentedFlag) {
            detectFlag = true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHasCardEvent(HasCardEvent e) {
        tvPass.setVisibility(View.GONE);
        if (advertiseDialog.isAdded() || advertiseDialog.isVisible()) {
            restartCamera();
            advertiseDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConfig();
        noActionSecond = 0;
//        startAD();
    }

    @Override
    protected void onPause() {
        super.onPause();
        monitorFlag = false;
        readIdFlag = false;
        localFlag = false;
        humanInductionFlag = false;
//        if (countDownTimer != null){
//            countDownTimer.cancel();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
        unregisterReceiver(timeReceiver);
        Face_App.getInstance().onTerminate();
        System.exit(0);
//        if (countDownTimer != null){
//            countDownTimer.cancel();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Constants.RESULT_CODE_FINISH) {
                finish();
            }
        }
    }

    /* 画人脸框 */
    private void drawFaceRect(MXFaceInfoEx[] faceInfos, Canvas canvas, int len) {
        float[] startArrayX = new float[len];
        float[] startArrayY = new float[len];
        float[] stopArrayX = new float[len];
        float[] stopArrayY = new float[len];
        for (int i = 0; i < len; i++) {
            startArrayX[i] = (CP_WIDTH - faceInfos[i].x * zoomRate);
            startArrayY[i] = (faceInfos[i].y * zoomRate);
            stopArrayX[i] = (CP_WIDTH - faceInfos[i].x * zoomRate - faceInfos[i].width * zoomRate);
            stopArrayY[i] = (faceInfos[i].y * zoomRate + faceInfos[i].height * zoomRate);
        }
        canvasDrawLine(canvas, len, startArrayX, startArrayY, stopArrayX, stopArrayY);
    }

    /* 画线 */
    private void canvasDrawLine(Canvas canvas, int iNum, float[] startArrayX, float[] startArrayY, float[] stopArrayX, float[] stopArrayY) {
        int iLen = 50;
        Paint mPaint = new Paint();
        mPaint.setColor(white);
        float startX, startY, stopX, stopY;
        for (int i = 0; i < iNum; i++) {
            startX = startArrayX[i];
            startY = startArrayY[i];
            stopX = stopArrayX[i];
            stopY = stopArrayY[i];
            mPaint.setStrokeWidth(6);// 设置画笔粗细
            canvas.drawLine(startX, startY, startX - iLen, startY, mPaint);
            canvas.drawLine(stopX + iLen, startY, stopX, startY, mPaint);
            canvas.drawLine(startX, startY, startX, startY + iLen, mPaint);
            canvas.drawLine(startX, stopY - iLen, startX, stopY, mPaint);
            canvas.drawLine(stopX, stopY, stopX, stopY - iLen, mPaint);
            canvas.drawLine(stopX, startY + iLen, stopX, startY, mPaint);
            canvas.drawLine(stopX, stopY, stopX + iLen, stopY, mPaint);
            canvas.drawLine(startX - iLen, stopY, startX, stopY, mPaint);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                latitude = aMapLocation.getLatitude();
                longitude = aMapLocation.getLongitude();
                location = aMapLocation.getAddress();
                queryWeather(aMapLocation.getCity());
            }
        } else {
            tvWeather.setText("无天气信息");
        }
    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i == 1000) {
            if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();
                tvWeather.setText(String.format("%s%s℃", weatherLive.getWeather(), weatherLive.getTemperature()));
            } else {
                tvWeather.setText("无天气信息");
            }
        } else {
            tvWeather.setText("无天气信息");
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
    }

    private void queryWeather(String city) {
        WeatherSearchQuery mQuery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        WeatherSearch mWeatherSearch = new WeatherSearch(this);
        mWeatherSearch.setOnWeatherSearchListener(this);
        mWeatherSearch.setQuery(mQuery);
        mWeatherSearch.searchWeatherAsyn(); //异步搜索
    }

    private void openLed() {
        try {
            Thread.sleep(GPIO_INTERVAL);
//            Face_App.getInstance().igpioControlDemo.setGpio(3, true);
            smdtManager.smdtSetGpioValue(3, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeLed() {
        try {
            Thread.sleep(GPIO_INTERVAL);
//            Face_App.getInstance().igpioControlDemo.setGpio(3, false);
            smdtManager.smdtSetGpioValue(3, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 解析身份证id 字符串 */
    private String getCardIdStr(byte[] cardId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cardId.length; i++) {
            sb.append(String.format("%02x", cardId[i]));
        }
        String data = sb.toString();
        String cardIdStr = data.substring(0, 16);
        String errorCode = data.substring(16, 20);
        if (TextUtils.equals(errorCode, "9000")) {
            return cardIdStr;
        } else {
            return "";
        }
    }

    private String isGreenCard(byte[] bCardInfo) {
        byte[] id_isGreen = new byte[2];
        id_isGreen[0] = bCardInfo[248];
        id_isGreen[1] = bCardInfo[249];
        return MyUtil.unicode2String(id_isGreen).trim();
    }

    private Bitmap getBitmap2(byte[] wlt) {
        WLTService dw = new WLTService();
        byte[] buffer = new byte[38556];
        int result = dw.wlt2Bmp(wlt, buffer);
        if (result == 1) {
            return IDPhotoHelper.Bgr2Bitmap(buffer);
        }
        return null;
    }

    /* 读身份证 */
    private void readCard() throws Exception {
        byte[] bCardFullInfo = new byte[256 + 1024 + 1024];
        long t1 = System.currentTimeMillis();
        int re = idCardDriver.mxReadCardFullInfo(bCardFullInfo);
        long t2 = System.currentTimeMillis();
        if (WRITE_TIME) {
            LogUtil.writeLog("读卡耗时：" + (t2 - t1));
        }
        String type = isGreenCard(bCardFullInfo);
        if (re == 1) {
            if ("".equals(type)) {
                analysisIdCardInfo(bCardFullInfo);
            } else if ("I".equals(type)) {
                analysisGreenCard(bCardFullInfo);
            } else if ("J".equals(type)) {
                analysiGATCardInfo(bCardFullInfo);
            }
        } else if (re == 0) {
            if ("".equals(type)) {
                analysisIdCardInfo(bCardFullInfo);
            } else if ("I".equals(type)) {
                analysisGreenCard(bCardFullInfo);
            } else if ("J".equals(type)) {
                analysiGATCardInfo(bCardFullInfo);
            }
            byte[] bFingerData0 = new byte[mFingerDataSize];
            byte[] bFingerData1 = new byte[mFingerDataSize];
            int iLen = 256 + 1024;
            System.arraycopy(bCardFullInfo, iLen, bFingerData0, 0, bFingerData0.length);
            iLen += 512;
            System.arraycopy(bCardFullInfo, iLen, bFingerData1, 0, bFingerData1.length);
            mRecord.setFingerPosition0(bFingerData0[5]);
            mRecord.setFinger0(Base64.encodeToString(bFingerData0, Base64.DEFAULT));
            mRecord.setFingerPosition1(bFingerData1[5]);
            mRecord.setFinger1(Base64.encodeToString(bFingerData1, Base64.DEFAULT));
        } else {
            throw new Exception("读卡失败");
        }
        eventBus.post(new ResultEvent(ResultEvent.ID_PHOTO, mRecord));
    }

    /* 解析身份证信息 */
    private void analysisIdCardInfo(byte[] bCardInfo) {
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 民族
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] _RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        mRecord.setType(" ");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
        int iRev = Integer.parseInt(MyUtil.unicode2String(id_Rev));
        mRecord.setRace(Constants.FOLK[iRev - 1]);

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        mRecord.setAddress(MyUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, _RegOrg, 0, _RegOrg.length);
        iLen = iLen + _RegOrg.length;
        mRecord.setRegOrg(MyUtil.unicode2String(_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = MyUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = MyUtil.unicode2String(id_ValidPeriodEnd).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }

    /* 解析身份证信息 */
    private void analysiGATCardInfo(byte[] bCardInfo) {
        byte[] id_Name = new byte[30]; // 姓名
        byte[] id_Sex = new byte[2]; // 性别 1为男 其他为女
        byte[] id_Rev = new byte[4]; // 预留区
        byte[] id_Born = new byte[16]; // 出生日期
        byte[] id_Home = new byte[70]; // 住址
        byte[] id_Code = new byte[36]; // 身份证号
        byte[] id_RegOrg = new byte[30]; // 签发机关
        byte[] id_ValidPeriodStart = new byte[16]; // 有效日期 起始日期16byte 截止日期16byte
        byte[] id_ValidPeriodEnd = new byte[16];
//        byte[] id_NewAddr = new byte[36]; // 预留区域
        byte[] id_PassNum = new byte[18];
        byte[] id_IssueNum = new byte[4];
        byte[] id_NewAddr = new byte[14];
        byte[] id_pImage = new byte[1024]; // 图片区域
        int iLen = 0;
        mRecord.setType("J");
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_Rev, 0, id_Rev.length);
        iLen = iLen + id_Rev.length;
//        int iRev = Integer.parseInt(MyUtil.unicode2String(id_Rev));
        mRecord.setRace("");

        System.arraycopy(bCardInfo, iLen, id_Born, 0, id_Born.length);
        iLen = iLen + id_Born.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_Born));

        System.arraycopy(bCardInfo, iLen, id_Home, 0, id_Home.length);
        iLen = iLen + id_Home.length;
        mRecord.setAddress(MyUtil.unicode2String(id_Home).trim());

        System.arraycopy(bCardInfo, iLen, id_Code, 0, id_Code.length);
        iLen = iLen + id_Code.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_Code).trim());

        System.arraycopy(bCardInfo, iLen, id_RegOrg, 0, id_RegOrg.length);
        iLen = iLen + id_RegOrg.length;
        mRecord.setRegOrg(MyUtil.unicode2String(id_RegOrg).trim());

        System.arraycopy(bCardInfo, iLen, id_ValidPeriodStart, 0, id_ValidPeriodStart.length);
        iLen = iLen + id_ValidPeriodStart.length;
        System.arraycopy(bCardInfo, iLen, id_ValidPeriodEnd, 0, id_ValidPeriodEnd.length);
        iLen = iLen + id_ValidPeriodEnd.length;
        String validateStart = MyUtil.unicode2String(id_ValidPeriodStart).trim();
        String validateEnd = MyUtil.unicode2String(id_ValidPeriodEnd).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_PassNum, 0, id_PassNum.length);
        iLen = iLen + id_PassNum.length;
        mRecord.setPassNum(MyUtil.unicode2String(id_PassNum).trim());

        System.arraycopy(bCardInfo, iLen, id_IssueNum, 0, id_IssueNum.length);
        iLen = iLen + id_IssueNum.length;
        mRecord.setIssueNum(MyUtil.unicode2String(id_IssueNum).trim());

        System.arraycopy(bCardInfo, iLen, id_NewAddr, 0, id_NewAddr.length);
        iLen = iLen + id_NewAddr.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }

    private void analysisGreenCard(byte[] bCardInfo) throws Exception {
        byte[] id_Name = new byte[120];    // 姓名
        byte[] id_Sex = new byte[2];      // 性别 1为男 其他为女
        byte[] id_cardNo = new byte[30];     // 永久居留证号码
        byte[] id_nation = new byte[6];      // 国籍或所在地区代码
        byte[] id_chinese_name = new byte[30];     // 中文姓名
        byte[] id_start_date = new byte[16];     // 证件签发日期
        byte[] id_end_date = new byte[16];     // 证件终止日期
        byte[] id_birthday = new byte[16];     // 出生日期
        byte[] id_version = new byte[4];      // 证件版本号
        byte[] id_reg_org = new byte[8];      // 当前申请受理机关代码
        byte[] id_type = new byte[2];      // 证件类型标识
        byte[] id_remark = new byte[6];      // 预留项
        byte[] id_pImage = new byte[1024];   // 照片
        mRecord.setType("I");
        int iLen = 0;
        System.arraycopy(bCardInfo, iLen, id_Name, 0, id_Name.length);
        iLen = iLen + id_Name.length;
        mRecord.setName(MyUtil.unicode2String(id_Name).trim());

        System.arraycopy(bCardInfo, iLen, id_Sex, 0, id_Sex.length);
        iLen = iLen + id_Sex.length;

        if (id_Sex[0] == '1') {
            mRecord.setSex("男");
        } else {
            mRecord.setSex("女");
        }

        System.arraycopy(bCardInfo, iLen, id_cardNo, 0, id_cardNo.length);
        iLen += id_cardNo.length;
        mRecord.setCardNo(MyUtil.unicode2String(id_cardNo));

        System.arraycopy(bCardInfo, iLen, id_nation, 0, id_nation.length);
        iLen += id_nation.length;
        mRecord.setRace(MyUtil.unicode2String(id_nation));

        System.arraycopy(bCardInfo, iLen, id_chinese_name, 0, id_chinese_name.length);
        iLen = iLen + id_chinese_name.length;
        mRecord.setChineseName(MyUtil.unicode2String(id_chinese_name).trim());

        System.arraycopy(bCardInfo, iLen, id_start_date, 0, id_start_date.length);
        iLen = iLen + id_start_date.length;
        System.arraycopy(bCardInfo, iLen, id_end_date, 0, id_end_date.length);
        iLen = iLen + id_end_date.length;
        String validateStart = MyUtil.unicode2String(id_start_date).trim();
        String validateEnd = MyUtil.unicode2String(id_end_date).trim();
        mRecord.setValidate(validateStart + "-" + validateEnd);

        System.arraycopy(bCardInfo, iLen, id_birthday, 0, id_birthday.length);
        iLen = iLen + id_birthday.length;
        mRecord.setBirthday(MyUtil.unicode2String(id_birthday));

        System.arraycopy(bCardInfo, iLen, id_version, 0, id_version.length);
        iLen = iLen + id_version.length;
//        curId.setVersion(CommonUtil.unicode2String(id_version));

        System.arraycopy(bCardInfo, iLen, id_reg_org, 0, id_reg_org.length);
        iLen += id_reg_org.length;
        mRecord.setRegOrg(MyUtil.unicode2String(id_reg_org));

        System.arraycopy(bCardInfo, iLen, id_type, 0, id_type.length);
        iLen += id_type.length;

        System.arraycopy(bCardInfo, iLen, id_remark, 0, id_remark.length);
        iLen += id_remark.length;

        System.arraycopy(bCardInfo, iLen, id_pImage, 0, id_pImage.length);
        Bitmap bitmap = getBitmap2(id_pImage);
        if (bitmap != null) {
            mRecord.setCardImgData(MyUtil.getBytesByBitmap(bitmap));
            String cardImgFilePath = FileUtil.getAvailableImgPath(this) + File.separator + mRecord.getCardNo() + "_" + mRecord.getName() + System.currentTimeMillis() + ".bmp";
            MyUtil.saveBitmap(cardImgFilePath, bitmap);
            mRecord.setCardImg(cardImgFilePath);
        } else {
            LogUtil.writeLog("decodeIdPhoto failed");
        }

    }


    /* 提取特征 */
    private byte[] extractFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            detectFlag = false;
            int re = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            detectFlag = true;
            if (re == 0) {
                return feature;
            }
            return null;
        }
    }

    /* 提取特征 */
    private byte[] extractIdCardFeature(byte[] pImage, int width, int height, MXFaceInfoEx faceInfo) {
        synchronized (lock1) {
            byte[] feature = new byte[mxFaceAPI.mxGetFeatureSize()];
            detectFlag = false;
            int re = mxFaceAPI.mxFeatureExtract(pImage, width, height, 1, new MXFaceInfoEx[]{faceInfo}, feature);
            if (re == 0) {
                return feature;
            }
            return null;
        }
    }

    /* 获取身份证照片特征 */
    private void getIdPhotoFeature() {
//        /** 加载图像 */
//        int re = -1;
//        int[] oX = new int[1];
//        int[] oY = new int[1];
//        // 获取图像大小
//        String availablePath = FileUtil.getAvailableImgPath(this);
//        File f = new File(mRecord.getCardImg());
//        if (!f.exists()) {
//            LogUtil.writeLog("身份证照片不存在！ 路径：" + f.getAbsolutePath());
//            return;
//        }
//        re = dtload.LoadFaceImage(f.getPath(), null, null, oX, oY);
//        if (re != 1) {
//            LogUtil.writeLog("第一次加载图片失败 dtload.LoadFaceImage = " + re + "_" + mRecord.getCardImg());
//            return;
//        }
//        byte[] pGrayBuff = new byte[oX[0] * oY[0]];
//        byte[] pRGBBuff = new byte[oX[0] * oY[0] * 3];
//        re = dtload.LoadFaceImage(f.getPath(), pRGBBuff, pGrayBuff, oX, oY);
//        if (re != 1) {
//            LogUtil.writeLog("第二次加载图片失败 dtload.LoadFaceImage = " + re + "_" + mRecord.getCardImg());
//            return;
//        }
//        /** 检测人脸 */
//        int[] pFaceNum = new int[1];
//        pFaceNum[0] = 1;                //身份证照片只可能检测到一张人脸
//        MXFaceInfoEx[] pFaceBuffer = new MXFaceInfoEx[1];
//        pFaceBuffer[0] = new MXFaceInfoEx();
//        int iX = oX[0];
//        int iY = oY[0];
////        detectFlag = false;
//        synchronized (lock2) {
//            long t1 = System.currentTimeMillis();
//            re = mxFaceAPI.mxDetectFace(pRGBBuff, iX, iY, pFaceNum, pFaceBuffer);
//            long t2 = System.currentTimeMillis();
//            if (WRITE_TIME) {
//                LogUtil.writeLog("检测身份证照片人脸耗时：" + (t2 - t1));
//            }
//        }
////        detectFlag = true;
//        if (re != 0) {
//            LogUtil.writeLog("mxDetectFace = " + re + "_" + mRecord.getCardImg());
//            return;
//        }
//        long t1 = System.currentTimeMillis();
//        idFaceFeature = extractIdCardFeature(pRGBBuff, 102, 126, pFaceBuffer[0]);
//        boolean deleteResult = f.delete();
//        Log.e("asd", "缓存文件删除" + deleteResult);
//        if (WRITE_TIME) {
//            LogUtil.writeLog("提取身份证照片耗时：" + (System.currentTimeMillis() - t1));
//        }
    }

    /* 读卡的完成后，和预提取的特征进行比对 */
    private void preExtractAndMatch() {
        if (config.getLivenessFlag()) {
//            detectFlag = true;
//            extractFlag = true;
            matchFlag = true;
            return;
        }
        Log.e(TAG, "preExtractAndMatch");
        if (curFaceFeature != null && idFaceFeature != null) {
            float[] fScore = new float[1];
            long t1 = System.currentTimeMillis();
            int re = mxFaceAPI.mxFeatureMatch(idFaceFeature, curFaceFeature, fScore);
            long t2 = System.currentTimeMillis();
//            if (WRITE_TIME) {
//                LogUtil.writeLog("比对耗时：" + (t2 - t1) + " 得分：" + fScore[0]);
//                LogUtil.writeLog("一次比对 总耗时：" + (t2 - at1));
//                eventBus.post(new ToastEvent("一次比对 总耗时：" + (t2 - at1)));
//            }
            if (re == 0 && fScore[0] >= config.getVerifyScore()) {
//                mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                mRecord.setFaceImgData(curCameraImg);
                mRecord.setScore(fScore[0] + "");
                matchFlag = false;
                extractFlag = false;
                eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                    eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord, curFaceInfo));
                }
            } else {
                extractFlag = true;
                matchFlag = true;
            }
            curFaceFeature = null;
            curCameraImg = null;
        } else {
            extractFlag = true;
            matchFlag = true;
        }
    }

    @OnClick(R.id.iv_record)
    void onRecordClick() {
        toType = 1;
        etPwd.setVisibility(View.VISIBLE);
        advertiseLock = false;
        btnCancel.setVisibility(View.VISIBLE);
        btnConfirm.setVisibility(View.VISIBLE);
        tvWelMsg.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_title)
    void onTitleClick() {
        long secondTime = System.currentTimeMillis();
        if ((secondTime - firstTime) > 1500) {
            mState = 0;
        } else {
            mState++;
        }
        firstTime = secondTime;
        if (mState > 4) {
            toType = 0;
            etPwd.setVisibility(View.VISIBLE);
            advertiseLock = false;
            btnCancel.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            tvWelMsg.setVisibility(View.GONE);
        } else {
            onCancel();
        }
    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        hideInputMethod();
        etPwd.setText(null);
        etPwd.setVisibility(View.GONE);
        noActionSecond = 0;
        advertiseLock = true;
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        tvWelMsg.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        hideInputMethod();
        String pwd = etPwd.getText().toString();
        if (pwd.equals(config.getPassword())) {
            etPwd.setText(null);
            etPwd.setVisibility(View.GONE);
            noActionSecond = 0;
            advertiseLock = true;
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            tvWelMsg.setVisibility(View.VISIBLE);
            if (toType == 0) {
                startActivity(new Intent(this, SettingActivity.class));
            } else if (toType == 1) {
                startActivity(new Intent(this, RecordActivity.class));
            }
        } else {
            Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
            etPwd.setText(null);
        }
    }

    private boolean loadLocalFeature() {
        localFeatureList = Face_App.getInstance().getDaoSession().getLocalFeatureDao().loadAll();
        for (int i = 0; i < localFeatureList.size(); i++) {
            LocalFeature local = localFeatureList.get(i);
            try {
                byte[] bFeature = FileUtil.readFileToBytes(local.getFilePath());
                local.setFeature(bFeature);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @SuppressLint("CheckResult")
    private void checkConfig() {
        final ProgressDialog pdCheck = new ProgressDialog(this);
        pdCheck.setCancelable(false);
        pdCheck.setMessage("正在加载设置...");
        pdCheck.show();
        Observable
                .create(new ObservableOnSubscribe<Config>() {
                    @Override
                    public void subscribe(ObservableEmitter<Config> e) throws Exception {
                        config = Face_App.getInstance().getDaoSession().getConfigDao().loadByRowId(1L);
                        if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                            readIdFlag = false;
                            localFlag = true;
                            if (!loadLocalFeature()) {
                                throw new Exception("加载本地特征失败！");
                            }
                            matchFlag = true;
                            detectFlag = true;
                            extractFlag = true;
                            humanInductionFlag = true;
                        }
                        if (config.getWhiteFlag()) {
                            whiteItemList = Face_App.getInstance().getDaoSession().getWhiteItemDao().loadAll();
                        }
                        e.onNext(config);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Config>() {
                    @Override
                    public void accept(Config config) {
                        if (config.getVerifyMode() == Config.MODE_LOCAL_FEATURE) {
                            tvPass.setVisibility(View.GONE);
                            startCount();
                            startHumanInduction();
                        } else {
                            tvPass.setVisibility(View.VISIBLE);
                            ivFaceBox.setVisibility(View.INVISIBLE);
                            tvLivenessHint.setVisibility(View.INVISIBLE);
                            readIdFlag = true;
                            extractFlag = false;
                        }
                        if (config.getDocumentFlag()) {
                            ivCloudDown.setVisibility(View.VISIBLE);
                        } else {
                            ivCloudDown.setVisibility(View.GONE);
                        }
                        if (config.getQueryFlag()) {
                            ivRecord.setVisibility(View.VISIBLE);
                        } else {
                            ivRecord.setVisibility(View.GONE);
                        }
                        if (config.getWhiteFlag()) {
                            ivImportFromU.setVisibility(View.VISIBLE);
                        } else {
                            ivImportFromU.setVisibility(View.GONE);
                        }
                        if (config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET || config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET_AND_LOCAL) {
                            AdvertisePresenter.downloadAdvertiseUrl(config.getAdvertisementUrl());
                        }
                        advertiseDialog = AdvertiseDialogFragment.newInstance(config.getAdvertisementMode(), position -> {
                            noActionSecond = 0;
                            restartCamera();
                            advertiseDialog.dismiss();
                        });
                        tvWelMsg.setText(config.getTitleStr());
                        monitorFlag = true;
                        pdCheck.dismiss();
                        startReadId();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        pdCheck.setMessage(throwable.getMessage());
                        pdCheck.setCancelable(true);
                        startReadId();
                    }
                });
    }

    private void startCount() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (localFlag) {
                    try {
                        Thread.sleep(6000);
                        detectFlag = true;
                        matchFlag = true;
                        extractFlag = true;
                        EventBus.getDefault().post(new NoCardEvent());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    private void startHumanInduction() {
        Thread t = new HumanInductionThread();
        t.start();
    }

    @OnClick(R.id.iv_import_from_u)
    void onImportClicked() {
        Flowable
                .create(new FlowableOnSubscribe<WhiteItem>() {
                    @Override
                    public void subscribe(FlowableEmitter<WhiteItem> e) throws Exception {
                        String whiteContent = FileUtil.readFromUSBPath(MainActivity.this, "白名单.txt");
                        if (TextUtils.isEmpty(whiteContent)) {
                            File whiteTxtFile = FileUtil.searchFileFromU(MainActivity.this, "白名单.txt");
                            if (whiteTxtFile != null) {
                                whiteContent = FileUtil.readFileToString(whiteTxtFile);
                            }
                        }
                        if (TextUtils.isEmpty(whiteContent)) {
                            throw new Exception("加载名单失败！请检查U盘和文件是否存在");
                        }
                        whiteContent = whiteContent.replace(" ", "");
                        String[] aWhites = whiteContent.split(",");
                        max = aWhites.length;
                        if (max > 0) {
                            whiteItemList.clear();
                            whiteItemDao.deleteAll();
                            EventBus.getDefault().post(new LoadProgressEvent(max, whiteItemList.size()));
                            for (String aWhite : aWhites) {
                                e.onNext(new WhiteItem(aWhite));
                            }
                        } else {
                            throw new Exception("加载名单失败！白名单内容为空，或格式错误");
                        }
                    }
                }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<WhiteItem>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);
                        mSubscription = s;
                    }

                    @Override
                    public void onNext(WhiteItem whiteItem) {
                        whiteItemList.add(whiteItem);
                        if (whiteItemList.size() == max) {
                            whiteItemDao.insertInTx(whiteItemList);
                        }
                        EventBus.getDefault().post(new LoadProgressEvent<>(max, whiteItemList.size()));
                        if (mSubscription != null) {
                            mSubscription.request(1);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        AlertDialog a = new AlertDialog();
                        a.setAdContent(t.getMessage());
                        a.show(getSupportFragmentManager(), "a");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadProgressEvent(LoadProgressEvent<WhiteItem> e) {
        if (!monitorFlag) {     //用monitorFlag判断 MainActivity是否显示
            return;
        }
        if (mSubscription == null) {
            return;
        }
        if (loadingDialog != null && !loadingDialog.isAdded() && !loadingDialog.isVisible()) {
            loadingDialog.show(getFragmentManager(), "loading");
            getFragmentManager().executePendingTransactions();
            loadingDialog.setMessage("正在导入...");
            loadingDialog.setButtonName(R.string.cancel);
            loadingDialog.setCancelable(false);
        }
        if (e.getMax() == 0) {
            return;
        }
        loadingDialog.setMax(e.getMax());
        loadingDialog.setProgress(e.getProgress());
        if (e.getMax() == e.getProgress()) {
            loadingDialog.setMessage("导入完成！");
            loadingDialog.setButtonName(R.string.confirm);
            loadingDialog.setCancelable(true);
        }
    }

    /* 连续播放4段音频 提示按指纹的 指位*/
    private void playSound(final int soundId0, final int soundId1, final int soundId2, final int soundId3) {
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

    /* 线程 循环读身份证id 读到id代表有身份证，开始比对流程， 读不到代表没有身份证 */
    private class ReadIdThread extends Thread {
        @Override
        public void run() {
            byte[] lastCardId = null;
            byte[] curCardId;
            int re;
            while (!interrupted()) {
                if (!readIdFlag || localFlag) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                curCardId = new byte[64];
                re = idCardDriver.mxReadCardId(curCardId);
                if (!readIdFlag || undocumentedFlag) {
                    continue;
                }
                switch (re) {
                    case GET_CARD_ID:
                        noCardFlag = false;
                        openLed();
                        noActionSecond = 0;
                        if (!Arrays.equals(lastCardId, curCardId)) {
                            eventBus.post(new HasCardEvent());
                            at1 = System.currentTimeMillis();
                            mRecord = new Record();
                            try {
                                mRecord.setCardId(getCardIdStr(curCardId));
                                switch (config.getVerifyMode()) {
                                    case Config.MODE_FACE_ONLY:
                                    case Config.MODE_ONE_FACE_FIRST:
                                    case Config.MODE_TWO_FACE_FIRST:
                                        readCard();
                                        if (checkIsOutValidate()) {
                                            eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                            break;
                                        }
                                        if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                            eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                            break;
                                        }
                                        getIdPhotoFeature();
                                        runOnUiThread(() -> {
                                            if (config.getLivenessFlag()) {
                                                tvLivenessHint.setText("请缓慢眨眼");
                                                tvLivenessHint.setVisibility(View.VISIBLE);
                                                ivFaceBox.setVisibility(View.VISIBLE);
                                                playSound(Constants.PLEASE_BLINK);
                                            }
                                            detectFlag = true;
                                            extractFlag = true;
                                        });
                                        preExtractAndMatch();
                                        break;
                                    case Config.MODE_FINGER_ONLY:
                                        readCard();
                                        if (checkIsOutValidate()) {
                                            eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                            break;
                                        }
                                        if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                            eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                            break;
                                        }
                                        eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                                        break;
                                    case Config.MODE_ONE_FINGER_FIRST:
                                    case Config.MODE_TWO_FINGER_FIRST:
                                        readCard();
                                        if (checkIsOutValidate()) {
                                            eventBus.post(new ResultEvent(ResultEvent.VALIDATE_FAIL, mRecord, null));
                                            break;
                                        }
                                        if (config.getWhiteFlag() && !checkInWhiteList(mRecord.getCardNo())) {
                                            eventBus.post(new ResultEvent(ResultEvent.WHITE_LIST_FAIL, mRecord, null));
                                            break;
                                        }
                                        getIdPhotoFeature();
                                        eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                                        break;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        lastCardId = curCardId;
                        break;
                    case NO_CARD:
                        if (!undocumentedFlag) {
                            noCardFlag = true;
                            lastCardId = null;
                            eventBus.post(new NoCardEvent());
                        }
                        break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* 线程 监控视频流回调 onPreviewFrame  是否有数据返回，设置时间内无数据返回 重启摄像头 */
    private class MonitorThread extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    Thread.sleep(1000);
                    noActionSecond++;
                    if (monitorFlag) {
                        long cur = System.currentTimeMillis();
                        if ((cur - lastCameraCallBackTime) >= config.getIntervalTime() * 1000) {
                            LogUtil.writeLog("开始修复视频卡顿");
                            closeCamera();
                            int re = smdtManager.smdtSetGpioValue(2, false);
                            LogUtil.writeLog("下电 re = " + re);
                            Thread.sleep(1000);
                            re = smdtManager.smdtSetGpioValue(2, true);
                            LogUtil.writeLog("上电 re = " + re);
                            Thread.sleep(1000);
                            openCamera();
                            LogUtil.writeLog("修复视频卡顿结束");
                        }
                        if (config.getAdvertiseFlag() && advertiseLock && noActionSecond >= config.getAdvertiseDelayTime()) {
                            if (!advertiseDialog.isAdded()) {
                                try {
                                    if (cameraLock.isLocked()) {
                                        return;
                                    }
                                    cameraLock.lock();
                                    advertiseDialog.show(getSupportFragmentManager(), "ad");
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!advertiseDialog.isAdded()) {
                                        return;
                                    }
                                    shMain.addCallback(MainActivity.this);
                                    closeCamera();
                                    smdtManager.smdtSetGpioValue(2, false);
                                    monitorFlag = false;
                                    Thread.sleep(800);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    cameraLock.unlock();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LogUtil.writeLog("修复视频卡顿线程 异常" + e.getMessage());
                }
            }
        }
    }

    /* 线程 人体感应线程 控制led灯开关 在本地特征比对模式下启用 */
    private class HumanInductionThread extends Thread {
        @Override
        public void run() {
            while (humanInductionFlag) {
                try {
                    Thread.sleep(GPIO_INTERVAL);
                    if (smdtManager.smdtReadGpioValue(1) == 1) {
                        openLed();
                    } else {
                        closeLed();
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    /* 线程 从视频流中提取特征并比对 */
    private class ExtractAndMatch implements Runnable {
        private byte[] pCameraData = null;
        private MXFaceInfoEx pFaceBuffer = null;

        ExtractAndMatch(byte[] pCameraData, MXFaceInfoEx pFaceBuffer) {
            this.pCameraData = pCameraData;
            this.pFaceBuffer = pFaceBuffer;
        }

        @Override
        public void run() {
            long tc = System.currentTimeMillis();
            int w = 0;
            int k = 0;
            curFaceInfo = pFaceBuffer;
            curFaceFeature = extractFeature(pCameraData, ZOOM_WIDTH, ZOOM_HEIGHT, curFaceInfo);
            if (WRITE_TIME) {
                LogUtil.writeLog("视频提取特征耗时：" + (System.currentTimeMillis() - tc));
            }
            byte[] bmpFaceData = new byte[ZOOM_WIDTH * ZOOM_HEIGHT * 3 + 10000];
            int[] bmpDataLen = new int[1];
            int re = mxImageTool.ImageEncode(pCameraData, ZOOM_WIDTH, ZOOM_HEIGHT, ".jpg", bmpFaceData, bmpDataLen);
            if (re == 1) {
                curCameraImg = new byte[bmpDataLen[0]];
                System.arraycopy(bmpFaceData, 0, curCameraImg, 0, bmpDataLen[0]);
//                FileUtil.writeBytesToFile(curCameraImg, FileUtil.getAvailablePath(MainActivity.this), "ImageEncode.jpg");
//                Log.e(TAG, "====== ImageEncode ======");
            } else {
                curCameraImg = pCameraData;
            }
            extractFlag = false;
            if (curFaceFeature != null && matchFlag && !localFlag) {
                float[] fScore = new float[1];
                if (idFaceFeature != null) {
                    long t1 = System.currentTimeMillis();
                    re = mxFaceAPI.mxFeatureMatch(idFaceFeature, curFaceFeature, fScore);
                    long t2 = System.currentTimeMillis();
//                    if (WRITE_TIME) {
//                        LogUtil.writeLog("比对耗时：" + (t2 - t1));
//                        LogUtil.writeLog("二次 比对总耗时：" + (t2 - at1));
//                        eventBus.post(new ToastEvent("二次 比对总耗时：" + (t2 - at1)));
//                    }
//                    mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                    mRecord.setFaceImgData(curCameraImg);
                    if (re == 0 && fScore[0] >= config.getVerifyScore()) {
                        mRecord.setScore(fScore[0] + "");
                        eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                        if (config.getVerifyMode() == Config.MODE_TWO_FACE_FIRST) {
                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                        }
                    } else {
                        eventBus.post(new ResultEvent(ResultEvent.FACE_FAIL, mRecord, curFaceInfo));
                        if (config.getVerifyMode() == Config.MODE_ONE_FACE_FIRST) {
                            eventBus.post(new ResultEvent(ResultEvent.VERIFY_FINGER, mRecord));
                        }
                    }
                    matchFlag = false;
                    curFaceFeature = null;
                    curCameraImg = null;
                }
            } else if (localFlag && matchFlag) {
                float[] fScore = new float[1];
                for (int i = 0; i < localFeatureList.size(); i++) {
                    long t1 = System.currentTimeMillis();
                    re = mxFaceAPI.mxFeatureMatch(localFeatureList.get(i).getFeature(), curFaceFeature, fScore);
                    long t2 = System.currentTimeMillis();
                    Log.e(TAG, "比对耗时：" + (t2 - t1));
                    if (re == 0 && fScore[0] >= config.getVerifyScore()) {
                        Log.e("passFeature", localFeatureList.get(i).getName() + " _ " + localFeatureList.get(i).getFilePath());
                        mRecord = new Record();
                        mRecord.setFaceImgData(curCameraImg);
                        mRecord.setScore(fScore[0] + "");
                        eventBus.post(new ResultEvent(ResultEvent.FACE_SUCCESS, mRecord, curFaceInfo));
                        isExtractWorking = false;

                        return;
                    }
                }
                mRecord = new Record();
//                mRecord.setFaceImgData(MyUtil.getYUV2JPEGBytes(curCameraImg, mCamera.getParameters().getPreviewFormat()));
                mRecord.setFaceImgData(curCameraImg);
                eventBus.post(new ResultEvent(ResultEvent.FACE_FAIL, mRecord, curFaceInfo));
                matchFlag = false;
                curFaceFeature = null;
                curCameraImg = null;
            }
            isExtractWorking = false;
        }

    }

    /**
     * 检查身份证是否已经过期
     *
     * @return true - 已过期 false - 未过期
     */
    private boolean checkIsOutValidate() {
        try {
            SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
            Date validEndDate = myFmt.parse(mRecord.getValidate().split("-")[1]);
            return validEndDate.getTime() < System.currentTimeMillis();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查身份证号码是否在白名单内
     *
     * @param cardNo 身份证号码
     * @return true - 在白名单内  false - 不在白名单内
     */
    private boolean checkInWhiteList(String cardNo) {
        for (WhiteItem item : whiteItemList) {
            if (item.getCardNo().equals(cardNo)) {
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUndocumentedEvent(UndocumentedEvent undocumentedEvent) {
        if (undocumented != null && undocumentedEvent.getFaceImage() != null) {
//            undocumented.setFaceImage(undocumentedEvent.getFaceImage());
            if (undocumentedCount != null) {
                undocumentedCount.dispose();
            }
            UpLoadRecordService.startActionUploadUndocumented(Face_App.getInstance().getApplicationContext(), undocumented, config);
            undocumented = null;
            noActionSecond = 0;
            advertiseLock = true;
            detectFlag = false;
            closeLed();
            tvPass.setVisibility(View.VISIBLE);
            ivFaceBox.setVisibility(View.INVISIBLE);
            tvLivenessHint.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadResultEvent(UploadResultEvent uploadResultEvent) {
        if (uploadResultEvent.isResult()) {
            playSoundAr(Constants.HAS_UPLOAD);
        } else {
            playSoundAr(Constants.UPLOAD_FAILED);
        }
    }

}
