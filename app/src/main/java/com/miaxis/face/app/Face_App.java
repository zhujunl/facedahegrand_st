package com.miaxis.face.app;

import android.app.Application;
import android.app.smdt.SmdtManager;
import android.widget.Toast;

import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.Record;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.TimerResetEvent;
import com.miaxis.face.greendao.gen.DaoSession;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.presenter.AdvertisePresenter;
import com.miaxis.face.service.ClearService;
import com.miaxis.face.service.UpLoadRecordService;
import com.miaxis.face.util.FileUtil;
import com.miaxis.livedetect.jni.MXLiveDetectApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zz.api.MXFaceAPI;


import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Administrator on 2017/5/16 0016.
 */
public class Face_App extends Application {

    private static MXFaceAPI mxAPI;
    private static MXLiveDetectApi mxLiveDetectApi;
    private SmdtManager smdtManager;
    private EventBus eventBus;
    private static Config config;
    private static Timer timer;
    public static TimerTask timerTask;
    public static final int GROUP_SIZE = 100;
    private DaoSession mDaoSession;
    private static Face_App app;
    private boolean feedFlag;
    private Thread tFeedDog;
//    public IGPIOControl igpioControlDemo;
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //连接后拿到 Binder，转换成 AIDL，在不同进程会返回个代理
//            igpioControlDemo = IGPIOControl.Stub.asInterface(service);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            igpioControlDemo = null;
//        }
//    };

    @Override
    public void onCreate() {
        super.onCreate();
//        Intent intent1 = new Intent(getApplicationContext(), GPIOService.class);
//        bindService(intent1, mConnection, BIND_AUTO_CREATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
//                initData();
//                initGPIO();
//                initDbHelp();
//                initConfig();
//                initDirectory();
//                initDefaultPicture();
//                initHsIdPhotoDecodeLib();
                startTask();
//                initCW();
//                enableDog();
            }
        }).start();

    }

//    private void initData() {
//        app = this;
//        FileDownloader.setup(this);
//        eventBus = EventBus.getDefault();
//        eventBus.register(this);
//        mxAPI = new MXFaceAPI();
//        mxLiveDetectApi = MXLiveDetectApi.INSTANCE;
//        smdtManager = new SmdtManager(this);
//    }

//    private void initCW() {
//        final String sLicence = FileUtil.readLicence();
//        if (TextUtils.isEmpty(sLicence)) {
//            eventBus.postSticky(new InitCWEvent(InitCWEvent.ERR_LICENCE));
//            return;
//        }
//        int re = initFaceModel();
//        if (re == 0) {
//            re = mxAPI.mxInitAlg(getApplicationContext(), FileUtil.getFaceModelPath(), sLicence);
//        }
//        if (re == 0) {
//            re = mxLiveDetectApi.initialize(FileUtil.getFaceModelPath());
//        }
//        eventBus.postSticky(new InitCWEvent(re));
//    }

//    private void initDbHelp() {
////        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(new GreenDaoContext(this), "FaceDahe_ST.db", null);
//        MyOpenHelper helper = new MyOpenHelper(new GreenDaoContext(this), "FaceDahe_ST.db", null);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        DaoMaster daoMaster = new DaoMaster(db);
//        mDaoSession = daoMaster.newSession();
//    }

//    public void initConfig() {
//        try {
//            ConfigDao configDao = mDaoSession.getConfigDao();
//            config = configDao.loadByRowId(1);
//            if (config == null) {
//                config = new Config();
//                config.setId(1);
//                config.setHost(Constants.DEFAULE_HOST);
//                config.setResultFlag(Constants.DEFAULT_RESULT_FLAG);
//                config.setDocumentFlag(Constants.DEFAULT_DOCUMENT_FLAG);
//                config.setIntervalTime(Constants.DEFAULT_INTERVAL);
//                config.setBanner(Constants.DEFAULT_BANNER);
//                config.setUpTime(Constants.DEFAULT_UPTIME);
//                config.setPassScore(Constants.DEFAULT_SCORE);
//                config.setVerifyMode(Config.MODE_FACE_ONLY);
//                config.setNetFlag(Constants.DEFAULT_NET);
//                config.setQueryFlag(Constants.DEFAULT_QUERY);
//                config.setPassword(Constants.DEFAULT_PASSWORD);
//                config.setAdvertiseFlag(Constants.DEFAULT_ADVERTISE_FLAG);
//                config.setAdvertiseDelayTime(Constants.DEFAULT_ADVERTISE_DELAY_TIME);
//                config.setAdvertisementUrl(Constants.DEFAULT_ADVERTISEMENT_URL);
//                config.setAdvertisementMode(Constants.DEFAULT_ADVERTISEMENT_MODE);
//                config.setUpdateUrl(Constants.UPDATE_URL);
//                config.setQualityScore(Constants.DEFAULT_QUALITY_SCORE);
//                config.setLiveness(Constants.DEFAULT_LIVENESS);
//                config.setLivenessQualityScore(Constants.DEFAULT_LIVENESS_QUALITY_SCORE);
//                config.setSaveLocal(Constants.DEFAULT_SAVE_LOCAL);
//                configDao.insert(config);
//            } else {
//                if (!checkHasFingerDevice() && config.getVerifyMode() != Config.MODE_LOCAL_FEATURE) {
//                    config.setVerifyMode(Config.MODE_FACE_ONLY);
//                    configDao.insertOrReplace(config);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onTerminate() {
//        eventBus.unregister(this);
//        mxAPI.mxFreeAlg();
//        super.onTerminate();
//    }
//
//    public static MXFaceAPI getMxAPI() {
//        return mxAPI;
//    }
//
//    public static MXLiveDetectApi getMxLiveDetectApi() {
//        return mxLiveDetectApi;
//    }

    private void upLoad() {
//        RecordDao recordDao = mDaoSession.getRecordDao();
//        long count = recordDao.count();
//        long page = (count % GROUP_SIZE == 0) ? count / GROUP_SIZE : (count / GROUP_SIZE + 1);
//        for (int i = 0; i < page; i++) {
//            List<Record> recordList = recordDao.queryBuilder().offset(i * GROUP_SIZE).limit(GROUP_SIZE).orderAsc(RecordDao.Properties.Id).list();
//            for (int j = 0; j < recordList.size(); j++) {
//                Record record = recordList.get(j);
//                if (!record.isHasUp()) {
//                    UpLoadRecordService.startActionUpLoad(getApplicationContext(), record, config);
//                }
//            }
//        }
    }

    private void startTask() {
        initTask();
        Date start = new Date();
        start.setHours(Integer.valueOf(config.getUpTime().split(" : ")[0]));
        start.setMinutes(Integer.valueOf(config.getUpTime().split(" : ")[1]));
        long tStart = start.getTime();
        long t1 = new Date().getTime();
        if (tStart < t1) {
            start.setDate(new Date().getDate() + 1);
        }
        timer.schedule(timerTask, start, Constants.TASK_DELAY);
    }

//    private void initGPIO() {
//        Log.e("initGPIO", "initGPIO");
//        try {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//                smdtManager.smdtSetGpioDirection(1, 0);         // value  0 读 1 写
//                Thread.sleep(GPIO_INTERVAL);
//                smdtManager.smdtSetGpioDirection(2, 1);
//                Thread.sleep(GPIO_INTERVAL);
////                smdtManager.smdtSetGpioDirection(3, 1);
////                Thread.sleep(GPIO_INTERVAL);
//            }
////            smdtManager.smdtSetExtrnalGpioValue(2, false);
////            smdtManager.smdtSetExtrnalGpioValue(3, false);
//            for (int i = 0; i < 3; i++) {
//                Thread.sleep(GPIO_INTERVAL);
//                int re = smdtManager.smdtSetGpioValue(2, true);
//                if (re == 0) {
//                    break;
//                }
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private void initTask() {
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (config.getNetFlag()) {
//                    upLoad();
                }
                if (config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET || config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET_AND_LOCAL) {
                    AdvertisePresenter.downloadAdvertiseUrl(config.getAdvertisementUrl());
                }
                ClearService.startActionClear(getApplicationContext());
            }
        };
    }

    public void reSetTimer() {
        timerTask.cancel();
        initTask();
        timer.cancel();
        timer.purge();
        timer = new Timer();
        Date start = new Date();
        start.setHours(Integer.valueOf(config.getUpTime().split(" : ")[0]));
        start.setMinutes(Integer.valueOf(config.getUpTime().split(" : ")[1]));
        start.setSeconds(0);
        long tStart = start.getTime();
        long t1 = new Date().getTime();
        if (tStart < t1) {
            start.setDate(new Date().getDate() + 1);
        }
        timer.schedule(timerTask, start, Constants.TASK_DELAY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerResetEvent(TimerResetEvent e) {
        reSetTimer();
    }

    /**
     * @return
     */
//    private int initFaceModel() {
//        String hsLibDirName = "zzFaceModel";
//        String modelFile1 = "MIAXIS_V5.0.0_FaceDetect.model";
//        String modelFile2 = "MIAXIS_V5.0.0_FaceMaskAlign.model";
//        String modelFile3 = "MIAXIS_V5.0.0_FaceMaskDetect.model";
//        String modelFile4 = "MIAXIS_V5.0.0_FaceMaskRecog.model";
//        String modelFile5 = "MIAXIS_V5.0.0_FaceQuality.model";
//        String modelFile6 = "MIAXIS_V5.0.0_FaceRecog.model";
//        String modelFile7 = "MIAXIS_V5.0.0_LivenessDetect.model";
//        String modelFile8 = "mx_eyeblink_detect.pb";
//        File modelDir = new File(FileUtil.getFaceModelPath());
//        if (modelDir.exists()) {
//            if (!new File(modelDir + File.separator + modelFile1).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile1, modelDir + File.separator + modelFile1);
//            }
//            if (!new File(modelDir + File.separator + modelFile2).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile2, modelDir + File.separator + modelFile2);
//            }
//            if (!new File(modelDir + File.separator + modelFile3).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile3, modelDir + File.separator + modelFile3);
//            }
//            if (!new File(modelDir + File.separator + modelFile4).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile4, modelDir + File.separator + modelFile4);
//            }
//            if (!new File(modelDir + File.separator + modelFile5).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile5, modelDir + File.separator + modelFile5);
//            }
//            if (!new File(modelDir + File.separator + modelFile6).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile6, modelDir + File.separator + modelFile6);
//            }
//            if (!new File(modelDir + File.separator + modelFile7).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile7, modelDir + File.separator + modelFile7);
//            }
//            if (!new File(modelDir + File.separator + modelFile8).exists()) {
//                FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + modelFile8, modelDir + File.separator + modelFile8);
//            }
//            return 0;
//        } else {
//            return -1;
//        }
//    }

//    private void initDefaultPicture() {
//        String filename = "default_picture.jpg";
//        String assetsFilepath = "default" + File.separator + filename;
//        File adFileDir = new File(FileUtil.getAdvertisementFilePath());
////        if(adFileDir.exists() && !AdvertiseDialog.isAdExist()) {
//            FileUtil.copyAssetsFile(this, assetsFilepath, adFileDir + File.separator + filename);
////        }
//    }

//    private void initDirectory() {
//        File modelDir = new File(FileUtil.getFaceModelPath());
//        if (!modelDir.exists()) {
//            modelDir.mkdirs();
//        }
//        File zzFacesDir = new File(FileUtil.getAvailableImgPath(this));
//        if (!zzFacesDir.exists()) {
//            zzFacesDir.mkdirs();
//        }
//        File adFileDir = new File(FileUtil.getAdvertisementFilePath());
//        if (!adFileDir.exists()) {
//            adFileDir.mkdirs();
//        }
//        File wltlibDir = new File(FileUtil.getAvailableWltPath(this));
//        if (!wltlibDir.exists()) {
//            wltlibDir.mkdirs();
//        }
//        File cacheDir = new File(FileUtil.getAdvertisementCachePath());
//        if (!cacheDir.exists()) {
//            cacheDir.mkdirs();
//        }
//    }

//    /**
//     * 复制宇松二代证解码库的授权文件到指定目录
//     *
//     * @return
//     */
//    private int initHsIdPhotoDecodeLib() {
//        String hsLibDirName = "wltlib";
//        String hsFile1 = "base.dat";
//        String hsFile2 = "license.lic";
//        String hsFile3 = "test.dat";
//        String hsFile4 = "zp.wlt";
//        File wltlibDir = new File(FileUtil.getAvailableWltPath(this));
//        if (!wltlibDir.exists()) {
//            if (!wltlibDir.mkdirs()) {
//                return -1;
//            }
//        }
//        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile1, wltlibDir + File.separator + hsFile1);
//        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile2, wltlibDir + File.separator + hsFile2);
//        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile3, wltlibDir + File.separator + hsFile3);
//        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile4, wltlibDir + File.separator + hsFile4);
//        return 0;
//    }

    public static Face_App getInstance() {
        return app;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onToastEvent(ToastEvent e) {
//        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//    }

//    private boolean checkHasFingerDevice() {
//        int re;
//        int pid = 0x0202;
//        int vid = 0x821B;
//        MXFingerDriver fingerDriver = new MXFingerDriver(getApplicationContext(), pid, vid);
//        for (int i = 0; i < 20; i++) {
//            re = fingerDriver.mxGetDevVersion(new byte[120]);
//            if (re == 0) {
//                return true;
//            }
//        }
//        return false;
//    }

//    class FeedDogThread extends Thread {
//        @Override
//        public void run() {
//            while (feedFlag) {
//                smdtManager.smdtWatchDogFeed();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void enableDog() {
//        char c = 1;
//        feedFlag = true;
//        smdtManager.smdtWatchDogEnable(c);
//        if (tFeedDog == null) {
//            tFeedDog = new FeedDogThread();
//            tFeedDog.start();
//        }
//    }
//
//    public void unableDog() {
//        char c = 0;
//        feedFlag = false;
//        smdtManager.smdtWatchDogEnable(c);
//        if (tFeedDog != null) {
//            tFeedDog.interrupt();
//            tFeedDog = null;
//        }
//    }

}