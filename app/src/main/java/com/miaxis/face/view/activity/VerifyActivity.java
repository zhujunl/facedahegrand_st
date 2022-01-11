package com.miaxis.face.view.activity;

import static com.miaxis.face.constant.Constants.LEFT_VOLUME;
import static com.miaxis.face.constant.Constants.LOOP;
import static com.miaxis.face.constant.Constants.PRIORITY;
import static com.miaxis.face.constant.Constants.RIGHT_VOLUME;
import static com.miaxis.face.constant.Constants.SOUND_RATE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amap.api.services.weather.LocalWeatherLive;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.miaxis.face.BuildConfig;
import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.Task;
import com.miaxis.face.bean.Undocumented;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.manager.AdvertManager;
import com.miaxis.face.manager.CameraManager;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.ServerManager;
import com.miaxis.face.manager.TTSManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.manager.WatchDogManager;
import com.miaxis.face.presenter.UpdatePresenter;
import com.miaxis.face.presenter.VerifyPresenter;
import com.miaxis.face.view.custom.AdvertiseDialogFragment;
import com.miaxis.face.view.custom.RectSurfaceView;
import com.miaxis.face.view.custom.ResultView;
import com.miaxis.face.view.fragment.UndocumentedDialogFragment;
import com.miaxis.livedetect.jni.MXLiveDetectApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceInfoEx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyActivity extends BaseActivity {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_cloud_down)
    ImageView ivCloudDown;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.iv_import_from_u)
    ImageView ivImportFromU;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.tv_wel_msg)
    TextView tvWelMsg;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.tv_camera)
    TextureView tvCamera;
    @BindView(R.id.rsv_rect)
    RectSurfaceView rsvRect;
    @BindView(R.id.fl_camera_root)
    FrameLayout flCameraRoot;
    @BindView(R.id.iv_face_box)
    ImageView ivFaceBox;
    @BindView(R.id.tv_liveness_hint)
    TextView tvLivenessHint;
    @BindView(R.id.tv_pass)
    TextView tvPass;
    @BindView(R.id.rv_result)
    ResultView rvResult;
    @BindView(R.id.tv_upload_hint)
    TextView tvUploadHint;
    @BindView(R.id.iv_gather_finger)
    ImageView ivGatherFinger;
    @BindView(R.id.tv_gather_finger_hint)
    TextView tvGatherFingerHint;
    @BindView(R.id.rl_gather_finger)
    RelativeLayout rlGatherFinger;
    @BindView(R.id.tv_face_tip)
    TextView tv_face_tip;

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;

    private Config config;
    private VerifyPresenter presenter;
    private UpdatePresenter updatePresenter;

    private AdvertiseDialogFragment advertiseDialog;
    private HandlerThread handlerThread;
    private Handler asyncHandler;

    private volatile boolean advertiseFlag = false;
    private AtomicInteger advertiseDelay = new AtomicInteger(15);
    private ReentrantLock advertiseLock = new ReentrantLock();

    private long cameraOpenTime = 0;
    private int mState = 0;         // 记录点击次数
    private long firstTime = 0;
    private int toType;             // 0 SettingActivity   1 RecordActivity



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("VerifyActivity",":onCreate");
        if(BuildConfig.EQUIPMENT_TYPE==1){
            setContentView(R.layout.activity_verify);
        }else {
            setContentView(R.layout.activity_verify2);
        }
        //String tip=CameraManager.getInstance().setORIENTATION();
        ButterKnife.bind(this);
        initWindow();
        initDialog();
        initData();
        initView();
        initTimeReceiver();
        updatePresenter.checkUpdateSync();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("VerifyActivity",":onStart");
        GpioManager.getInstance().setSmdtStatusBar(this, false);
        CardManager.getInstance().startReadCard();
        AdvertManager.getInstance().updateAdvertise();
        initWithConfig();
        WatchDogManager.getInstance().startANRWatchDog();
        ServerManager.getInstance().startHeartBeat();
        if (presenter != null) {
            ServerManager.getInstance().setListener(presenter.taskListener);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("VerifyActivity",":onResume");
//        if(!Constants.VERSION) App.getInstance().sendBroadcast(Constants.TYPE_ID_FP,true);
//        if(!Constants.VERSION)  App.getInstance().sendBroadcast(Constants.TYPE_CAMERA,true);
        advertiseFlag = true;
        sendAdvertiseDelaySignal();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("VerifyActivity",":onPause");
        advertiseFlag = false;
        asyncHandler.removeCallbacks(advertiseRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("VerifyActivity",":OnStop");
//        MXLiveDetectApi mxLiveDetectApi;
//        mxLiveDetectApi = MXLiveDetectApi.INSTANCE;
//        mxLiveDetectApi.free();
//        CameraManager.getInstance().setSurfaceTexture();
        if(!Constants.VERSION)App.getInstance().sendBroadcast(Constants.TYPE_LED,false);
        CardManager.getInstance().closeReadCard();
        EventBus.getDefault().unregister(this);
        ServerManager.getInstance().stopHeartBeat();
        ServerManager.getInstance().setListener(null);
        WatchDogManager.getInstance().stopANRWatchDog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("VerifyActivity",":onDestroy");
        if (presenter != null) {
            presenter.doDestroy();
            presenter = null;
        }
        if (updatePresenter != null) {
            updatePresenter.doDestroy();
            updatePresenter = null;
        }
//        if(!Constants.VERSION){
//            App.getInstance().sendBroadcast(Constants.TYPE_CAMERA,false);
//            App.getInstance().sendBroadcast(Constants.TYPE_ID_FP,false);
//        }
        tvCamera.setDrawingCacheEnabled(false);
        CameraManager.getInstance().closeCamera();
        ServerManager.getInstance().stopServer();
        asyncHandler.removeCallbacks(advertiseRunnable);
        GpioManager.getInstance().closeLed();
        unregisterReceiver(timeReceiver);
        GpioManager.getInstance().setSmdtStatusBar(this, true);
        System.exit(0);
    }

    protected void initData() {
        DaoManager.getInstance().initDbHelper(getApplicationContext(), "FaceDahe_New.db");
        ConfigManager.getInstance().checkConfig();
        config = ConfigManager.getInstance().getConfig();
        presenter = new VerifyPresenter(this, config);
        updatePresenter = new UpdatePresenter(this);
        advertiseDelay.set(config.getAdvertiseDelayTime());
        handlerThread = new HandlerThread("advertise_thread");
        handlerThread.start();
        asyncHandler = new Handler(handlerThread.getLooper());
    }

    protected void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        etPwd.setHint(ServerManager.getInstance().getHost());
        tvCamera.getViewTreeObserver().addOnGlobalLayoutListener(globalListener);
        tvCamera.setRotationY(Constants.VERSION?180:0);
        rsvRect.bringToFront();
    }

    private void initWithConfig() {
        advertiseDialog = AdvertiseDialogFragment.newInstance(config.getAdvertisementMode(), position -> {
            controlAdvertDialog(false);
            sendAdvertiseDelaySignal();
        });
        ivCloudDown.setVisibility(config.getDocumentFlag() ? View.VISIBLE : View.GONE);
        ivRecord.setVisibility(config.getQueryFlag() ? View.VISIBLE : View.GONE);
        ivImportFromU.setVisibility(config.getWhiteFlag() ? View.VISIBLE : View.GONE);
        tvWelMsg.setText(config.getTitleStr());
    }

    private ViewTreeObserver.OnGlobalLayoutListener globalListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            tvCamera.getViewTreeObserver().removeOnGlobalLayoutListener(globalListener);
            CameraManager.getInstance().openCamera(tvCamera, cameraListener);
        }
    };
    Handler handler=new Handler(Looper.getMainLooper());
    private CameraManager.OnCameraOpenListener cameraListener = new CameraManager.OnCameraOpenListener() {
        @Override
        public void onCameraOpen(Camera.Size previewSize, String message) {
            if (previewSize == null) {
                ToastManager.toast("摄像头打开失败"+message);
                App.getInstance().getThreadExecutor().execute(()->{
                    controlAdvertDialog(true);
                    controlAdvertDialog(false);
                });
            } else {
                int rootWidth = flCameraRoot.getWidth();
                int rootHeight = flCameraRoot.getHeight() * previewSize.width / previewSize.height;
                resetLayoutParams(tvCamera, rootWidth, rootHeight);
                resetLayoutParams(rsvRect, rootWidth, rootHeight);
                rsvRect.setRootSize(rootWidth, rootHeight);
                rsvRect.setZoomRate((float) rootWidth / FaceManager.zoomWidth);
                handler.postDelayed(() -> {
                    try {
                        if (advertiseDialog.isVisible()) {
                            advertiseDialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.e("advertiseDialog:","="+e.toString());
                        e.printStackTrace();
                    }
                }, 500);
            }
            if (presenter != null && presenter.isOnTask()) {
                presenter.handleTask();
            }
        }

        @Override
        public void onCameraError() {
//            runOnUiThread(() -> {
                try {
                    while (advertiseLock.isLocked()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    advertiseLock.lock();
                    Log.e("asd", "开始修复摄像头卡顿");
                    CameraManager.getInstance().closeCamera();
                    GpioManager.getInstance().closeCameraGpio();
                    Thread.sleep(800);
                    GpioManager.getInstance().openCameraGpio();
                    CameraManager.getInstance().openCamera(tvCamera, cameraListener);
                    Log.e("asd", "结束修复摄像头卡顿");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    advertiseLock.unlock();
                }
//            });
        }
    };

    public void controlAdvertDialog(boolean show) {
//        runOnUiThread(() -> {
        Log.e("111controlAdvertDialog",Thread.currentThread().getName()+ "----------show:"+show);
  //      App.getInstance().getThreadExecutor().execute(() -> {
            Log.e("controlAdvertDialog",Thread.currentThread().getName()+ "----------show:"+show);
            if (show) {
                try {
                    if (advertiseLock.isLocked()) {
                        return;
                    }
                    advertiseLock.lock();
                    runOnUiThread(()->{
                        if (!advertiseDialog.isAdded()) {
                            advertiseDialog.show(getSupportFragmentManager(), "ad");
                        }
                    });
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    CameraManager.getInstance().closeCamera();
                    GpioManager.getInstance().closeCameraGpio();
                    Thread.sleep(800);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    advertiseLock.unlock();
                }
            } else {
                if (System.currentTimeMillis() - cameraOpenTime < 3000) return;
                try {
                    cameraOpenTime = System.currentTimeMillis();
                    while (advertiseLock.isLocked()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    advertiseLock.lock();
                    GpioManager.getInstance().openCameraGpio();
                    //runOnUiThread(() -> {
                        CameraManager.getInstance().openCamera(tvCamera, cameraListener);
                        cameraOpenTime = System.currentTimeMillis();
                   // });
                    Thread.sleep(800);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    advertiseLock.unlock();
                }
            }
    //    });
    }

    public void onCardEvent(CardManager.CardStatus cardStatus, IDCardRecord idCardRecord) {
        runOnUiThread(() -> {
            switch (cardStatus) {
                case NoCard:
                    advertiseDelay.set(config.getAdvertiseDelayTime());
                    advertiseFlag = true;
                    tvPass.setText("请放身份证");
                    if(tvLivenessHint.getVisibility()==View.VISIBLE){
                        tvLivenessHint.setVisibility(View.GONE);
                    }
                    Log.d("Viewv==","="+(tvLivenessHint.getVisibility()==View.VISIBLE));
                    if(!Constants.VERSION)App.getInstance().sendBroadcast(Constants.TYPE_LED,false);
                    tvPass.setVisibility(View.VISIBLE);
                    rvResult.setVisibility(View.GONE);
                    ivFaceBox.setVisibility(View.INVISIBLE);
                    tvLivenessHint.setVisibility(View.INVISIBLE);
                    rlGatherFinger.setVisibility(View.GONE);
                    ivGatherFinger.setImageBitmap(null);
                    tvGatherFingerHint.setText("指纹采集");
                    tvUploadHint.setVisibility(View.INVISIBLE);
                    rsvRect.clearDraw();
                    sendAdvertiseDelaySignal();
                    break;
                case FindCard:
                    advertiseFlag = false;
                    rsvRect.clearDraw();
                    asyncHandler.removeCallbacks(advertiseRunnable);
                    if (advertiseDialog.isVisible()) {
                        controlAdvertDialog(false);
                    }
                    break;
                case ReadCard:
                    if (idCardRecord != null) {
                        tvPass.setVisibility(View.GONE);
                        tvLivenessHint.setVisibility(View.VISIBLE);
                        if(!Constants.VERSION)App.getInstance().sendBroadcast(Constants.TYPE_LED,true);
                        ivFaceBox.setVisibility(View.VISIBLE);
                        rvResult.clear();
                        rvResult.showCardImage(idCardRecord.getCardBitmap());
                        rvResult.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });
    }

    public void overdue() {
        runOnUiThread(() -> {
            rvResult.setResultMessage("已过期");
            rvResult.setFingerMode(false);
            rvResult.setVisibility(View.VISIBLE);
        });
    }

    public void outWhiteList() {
        runOnUiThread(() -> {
            rvResult.setResultMessage("不在名单内");
            rvResult.setFingerMode(false);
            rvResult.setVisibility(View.VISIBLE);
        });
    }

    public void verifyMode(boolean verifyMode) {
        runOnUiThread(() -> {
            rvResult.setFingerMode(!verifyMode);
            if (verifyMode) {
                if (config.getLivenessFlag()) {
                    tvLivenessHint.setText("请缓慢眨眼");
                    tvLivenessHint.setVisibility(View.VISIBLE);
                    ivFaceBox.setVisibility(View.VISIBLE);
                }
            } else {
                tvLivenessHint.setVisibility(View.INVISIBLE);
                ivFaceBox.setVisibility(View.VISIBLE);
            }
        });
    }

    public void drawFaceRect(MXFaceInfoEx[] faceInfo, int faceNum) {
        rsvRect.drawRect(faceInfo, faceNum);
    }

    public void actionLiveHint(String message) {
        runOnUiThread(() -> {
            tvLivenessHint.setText(message);
        });
    }

    public void showFaceTips(String message) {
        runOnUiThread(() -> {
            tvLivenessHint.setVisibility(TextUtils.isEmpty(message)?View.GONE:View.VISIBLE);
            tvLivenessHint.setText(message==null?"":message);
            if (tvPass.getVisibility()==View.VISIBLE) tvLivenessHint.setVisibility(View.GONE);
        });
    }
    public void faceVerifyResult(boolean result, Bitmap bitmap, String message) {
        runOnUiThread(() -> {
            rsvRect.clearDraw();
            rvResult.setFaceResult(result);
            rvResult.showCameraImage(bitmap);
            rvResult.setResultMessage(message);
        });
    }

    public void fingerVerifyResult(boolean result, String message) {
        runOnUiThread(() -> {
            rvResult.setFingerResult(result);
            rvResult.setResultMessage(message);
        });
    }

    public void undocumentedResult(int status) {
        runOnUiThread(() -> {
            if (status == 0) {
                rsvRect.clearDraw();
                tvLivenessHint.setVisibility(View.INVISIBLE);
                ivFaceBox.setVisibility(View.VISIBLE);
            } else if (status == 1) {
                tvPass.setVisibility(View.INVISIBLE);
                ivFaceBox.setVisibility(View.VISIBLE);
                if (config.getLivenessFlag()) {
                    tvLivenessHint.setText("请缓慢眨眼");
                    tvLivenessHint.setVisibility(View.VISIBLE);
                    ivFaceBox.setVisibility(View.VISIBLE);
                }
            } else if (status == 2) {
                tvPass.setVisibility(View.VISIBLE);
                tvLivenessHint.setVisibility(View.GONE);
                ivFaceBox.setVisibility(View.INVISIBLE);
                tvUploadHint.setVisibility(View.INVISIBLE);
                advertiseFlag = true;
                sendAdvertiseDelaySignal();
            }
        });
    }

    public void gatherFingerResult(boolean open, Bitmap finger, String message) {
        runOnUiThread(() -> {
            if (open) {
                if (finger == null) {
                    showGif(R.raw.put_finger, ivGatherFinger);
                } else {
                    ivGatherFinger.setImageBitmap(finger);
                }
                tvGatherFingerHint.setText(message);
                rlGatherFinger.setVisibility(View.VISIBLE);
            } else {
                rlGatherFinger.setVisibility(View.GONE);
                ivGatherFinger.setImageResource(R.drawable.finger_null);
                tvGatherFingerHint.setText("指纹采集");
            }
        });
    }

    public void uploadStatus(String message) {
        runOnUiThread(() -> {
            tvLivenessHint.setVisibility(View.INVISIBLE);
            ivFaceBox.setVisibility(View.VISIBLE);
            tvUploadHint.setText(message);
            tvUploadHint.setVisibility(View.VISIBLE);
        });
    }

    public void onTaskResult(Task task, int status) {
        runOnUiThread(() -> {
            if (status == 0) {
                advertiseFlag = false;
                asyncHandler.removeCallbacks(advertiseRunnable);
                tvPass.setVisibility(View.INVISIBLE);
                tvLivenessHint.setVisibility(View.GONE);
                ivFaceBox.setVisibility(View.VISIBLE);
                if(!Constants.VERSION)App.getInstance().sendBroadcast(Constants.TYPE_LED,false);
                if (CameraManager.getInstance().getCamera() == null) {
                    controlAdvertDialog(false);
                } else {
                    if (presenter != null) {
                        presenter.handleTask();
                    }
                }
            } else if (status == 1) {
                rsvRect.clearDraw();
                rvResult.setVisibility(View.INVISIBLE);
                tvLivenessHint.setVisibility(View.INVISIBLE);
                ivFaceBox.setVisibility(View.INVISIBLE);
                tvPass.setVisibility(View.VISIBLE);
                if(!Constants.VERSION)App.getInstance().sendBroadcast(Constants.TYPE_LED,false);
                tvUploadHint.setVisibility(View.INVISIBLE);
                advertiseFlag = true;
                sendAdvertiseDelaySignal();
                if (presenter != null) {
                    presenter.onTaskDone();
                }
            } else if (status == 2) {
                if (TextUtils.equals(task.getTasktype(), "1001")) {
                } else if (TextUtils.equals(task.getTasktype(), "1002")) {
                    rvResult.clear();
                    rvResult.setFingerMode(false);
                    rvResult.showCardImage(task.getCardBitmap());
                    rvResult.setVisibility(View.VISIBLE);
                    if (config.getLivenessFlag()) {
                        tvLivenessHint.setText("请缓慢眨眼");
                        tvLivenessHint.setVisibility(View.VISIBLE);
                        ivFaceBox.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void sendAdvertiseDelaySignal() {
        advertiseDelay.set(config.getAdvertiseDelayTime());
        asyncHandler.post(advertiseRunnable);
    }

    private Runnable advertiseRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                advertiseDelay.decrementAndGet();
                if (!advertiseFlag || advertiseDelay.get() > 0) {
                    asyncHandler.post(advertiseRunnable);
                    return;
                }
                if (config.getAdvertiseFlag() && !advertiseDialog.isVisible()) {
                    controlAdvertDialog(true);
                } else {
                    advertiseDelay.set(config.getAdvertiseDelayTime());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content("请稍后")
                .cancelable(false)
                .autoDismiss(false)
                .build();
        resultDialog = new MaterialDialog.Builder(this)
                .content("")
                .positiveText("确认")
                .build();
    }

    public void showWaitDialog(String message) {
        runOnUiThread(() -> {
            waitDialog.getContentView().setText(message);
            waitDialog.show();
        });
    }

    public void dismissWaitDialog() {
        runOnUiThread(() -> {
            if (waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        });
    }

    public void showResultDialog(String message) {
        runOnUiThread(() -> {
            resultDialog.getContentView().setText(message);
            resultDialog.show();
        });
    }

    public void dismissResultDialog() {
        runOnUiThread(() -> {
            if (resultDialog.isShowing()) {
                resultDialog.dismiss();
            }
        });
    }

    private void resetLayoutParams(View view, int fixWidth, int fixHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = fixWidth;
        layoutParams.height = fixHeight;
        view.setLayoutParams(layoutParams);
    }

    @OnClick(R.id.iv_record)
    void onRecordClick() {
        toType = 1;
        etPwd.setVisibility(View.VISIBLE);
        advertiseFlag = false;
        asyncHandler.removeCallbacks(advertiseRunnable);
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
            advertiseFlag = false;
            asyncHandler.removeCallbacks(advertiseRunnable);
            btnCancel.setVisibility(View.VISIBLE);
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
        advertiseFlag = true;
        sendAdvertiseDelaySignal();
        btnCancel.setVisibility(View.GONE);
        btnConfirm.setVisibility(View.GONE);
        tvWelMsg.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        hideInputMethod();
        String pwd = etPwd.getText().toString();
        if (TextUtils.equals(pwd, config.getPassword())) {
            etPwd.setText(null);
            etPwd.setVisibility(View.GONE);
            advertiseFlag = true;
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

    @OnClick(R.id.iv_import_from_u)
    void onImportWhiteList() {
        if (presenter != null) {
            presenter.importWhiteList();
        }
    }

    @OnClick(R.id.iv_cloud_down)
    void onUndocumented() {
        if (presenter != null) {
            if (!presenter.isOnVerify()) {
                advertiseFlag = false;
                UndocumentedDialogFragment.newInstance(new UndocumentedDialogFragment.OnDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        advertiseFlag = true;
                        sendAdvertiseDelaySignal();
                    }

                    @Override
                    public void onConfirm(String name, String cardNumber, String nation) {
                        Undocumented undocumented = new Undocumented.Builder()
                                .name(name)
                                .cardNumber(cardNumber)
                                .nation(nation)
                                .build();
                        if (presenter != null) {
                            presenter.undocumented(undocumented);
                        }
                    }
                }).show(getSupportFragmentManager(), "undocumented");
            } else {
                ToastManager.toast("请先拿开身份证");
            }
        }
    }

    /**
     * 日期时间
     */
    private void initTimeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, filter);
        onTimeEvent();
    }

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                onTimeEvent();//每一分钟更新时间
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeatherChanged(LocalWeatherLive localWeatherLive) {
        tvWeather.setText(String.format("%s %s℃", localWeatherLive.getWeather(), localWeatherLive.getTemperature()));
    }

    /**
     * 处理 时间变化 事件， 实时更新时间
     */
    private void onTimeEvent() {
        String date = dateFormat.format(new Date());
        String time = timeFormat.format(new Date());
        tvTime.setText(time);
        tvDate.setText(date);
    }

    private void showGif(int rawId, ImageView view) {
        Glide.with(this).load(rawId).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        })
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view);
    }
}
