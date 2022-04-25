package com.miaxis.face.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.CrashExceptionManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.manager.TTSManager;
import com.miaxis.face.manager.WatchDogManager;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class App extends Application {

    private static App instance;

    private ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    List<Activity> activities=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Loadin_App","onCreate");
        instance = this;
        MultiDex.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getInstance() {
        return instance;
    }

    public void initApplication(@NonNull OnAppInitListener listener) {
        Log.e("Loadin_App","initApplication");
        try {
            FileUtil.initDirectory(this);
            FileUtil.ClearAPK(this);
            DaoManager.getInstance().initDbHelper(getApplicationContext(), "FaceDahe_New.db");
            WatchDogManager.getInstance().init(this);
            CrashExceptionManager.getInstance().init(this);
            FileDownloader.setup(this);
            SoundManager.getInstance().init();
            CardManager.getInstance().init();
            FingerManager.getInstance(). init(this);
            ConfigManager.getInstance().checkConfig();
            FaceNetApi.rebuildRetrofit();
            initHsIdPhotoDecodeLib();
            TTSManager.getInstance().init(this);
//            AdvertManager.getInstance().init();
            //TODO:定时续传日志，清理
            int result = FaceManager.getInstance().initFaceST_11(this);
            if(Constants.VERSION){
                GpioManager.getInstance().init(this);
            }else{
                SystemClock.sleep(2000);
                sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_ID_FP,true);
                sendBroadcast(Constants.MOLD_POWER,Constants.TYPE_CAMERA,true);
                SystemClock.sleep(1000);
            }
            listener.onInit(result == FaceManager.INIT_SUCCESS, FaceManager.getFaceInitResultDetail(result));
//            listener.onInit(true, "");
        } catch (Exception e) {
            e.printStackTrace();
            listener.onInit(false, e.getMessage());
        }
    }

    public interface OnAppInitListener {
        void onInit(boolean result, String message);
    }

    public ExecutorService getThreadExecutor() {
        return threadExecutor;
    }

    /**
     * 复制宇松二代证解码库的授权文件到指定目录
     *
     * @return
     */
    private int initHsIdPhotoDecodeLib() {
        String hsLibDirName = "wltlib";
        String hsFile1 = "base.dat";
        String hsFile2 = "license.lic";
        String hsFile3 = "test.dat";
        String hsFile4 = "zp.wlt";
        File wltlibDir = new File(FileUtil.getAvailableWltPath(this));
        if (!wltlibDir.exists()) {
            if (!wltlibDir.mkdirs()) {
                return -1;
            }
        }
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile1, wltlibDir + File.separator + hsFile1);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile2, wltlibDir + File.separator + hsFile2);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile3, wltlibDir + File.separator + hsFile3);
        FileUtil.copyAssetsFile(this, hsLibDirName + File.separator + hsFile4, wltlibDir + File.separator + hsFile4);
        return 0;
    }

    public void sendBroadcast(String mold,int type,boolean value){
        if(!Constants.VERSION){
            Intent intent = new Intent(mold);
            if(type!=-1)intent.putExtra("type",type);
            intent.putExtra("value",value);
            getInstance().sendBroadcast(intent);
        }

    }
}
