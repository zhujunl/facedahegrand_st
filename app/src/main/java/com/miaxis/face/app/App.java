package com.miaxis.face.app;

import android.app.Application;

import androidx.annotation.NonNull;

import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.face.manager.AdvertManager;
import com.miaxis.face.manager.CardManager;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.CrashExceptionManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FaceManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.SoundManager;
import com.miaxis.face.manager.TaskManager;
import com.miaxis.face.manager.WatchDogManager;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.util.FileUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    private static App instance;

    private ExecutorService threadExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public void initApplication(@NonNull OnAppInitListener listener) {
        try {
            FileUtil.initDirectory(this);
            DaoManager.getInstance().initDbHelper(getApplicationContext(), "FaceDahe_New.db");
            WatchDogManager.getInstance().init(this);
            CrashExceptionManager.getInstance().init(this);
            GpioManager.getInstance().init(this);
            FileDownloader.setup(this);
            SoundManager.getInstance().init();
            CardManager.getInstance().init();
            FingerManager.getInstance().init(this);
            ConfigManager.getInstance().checkConfig();
            FaceNetApi.rebuildRetrofit();
//            AdvertManager.getInstance().init();
            TaskManager.getInstance().init();
            //TODO:定时续传日志，清理
            int result = FaceManager.getInstance().initFaceST(this);
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
}
