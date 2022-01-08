package com.miaxis.face.manager;

import android.app.Application;
import android.app.smdt.SmdtManager;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import com.miaxis.face.app.App;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.constant.Constants;

public class GpioManager {

    private GpioManager() {
    }

    public static GpioManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final GpioManager instance = new GpioManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static final int GPIO_INTERVAL = 100;

    private SmdtManager smdtManager;
    private boolean humanInductionFlag = true;
    private Boolean mr860Ddevice;
    private boolean feedFlag;
    private Thread tFeedDog;

    public void init(Application application) {
        smdtManager = new SmdtManager(application);
        initGPIO();
        enableDog();
    }

    /**
     * 摄像头上电
     */
    private void initGPIO() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                smdtManager.smdtSetGpioDirection(1, 0);         // value  0 读 1 写
                Thread.sleep(GPIO_INTERVAL);
                smdtManager.smdtSetGpioDirection(2, 1);
                Thread.sleep(GPIO_INTERVAL);
            }
            for (int i = 0; i < 10; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, true);
                if (re == 0) {
                    break;
                }
            }
            closeLed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCameraGpio() {
        if(!Constants.VERSION){
            return;
        }
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, false);
                if (re == 0) {
                    break;
                }
            }
            Thread.sleep(GPIO_INTERVAL);
            for (int i = 0; i < 3; i++) {
                Thread.sleep(GPIO_INTERVAL);
                int re = smdtManager.smdtSetGpioValue(2, true);
                if (re == 0) {
                    break;
                }
            }
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void closeCameraGpio() {
        if(!Constants.VERSION){
            return;
        }
        try {
            for (int i = 0; i < 3; i++) {
                int result = smdtManager.smdtSetGpioValue(2, false);
                if (result == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开闪光灯
     */
    public void openLed() {
        if(!Constants.VERSION){
            return;
        }
        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭闪光灯
     */
    public void closeLed() {
        if(!Constants.VERSION){
            return;
        }

        try {
            Thread.sleep(GPIO_INTERVAL);
            smdtManager.smdtSetGpioValue(3, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSmdtStatusBar(Context context, boolean value) {
        if(Constants.VERSION){
            try {
                smdtManager.smdtSetStatusBar(context, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 线程 人体感应线程 */
    public class HumanInductionThread extends Thread {
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
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isMR860Device() {
        if (mr860Ddevice == null) {
            String version = smdtManager.getAndroidDisplay();
            mr860Ddevice = version != null && version.contains("MR-860");
            return mr860Ddevice;
        }
        return mr860Ddevice;
    }

    public SmdtManager getSmdtManager() {
        return smdtManager;
    }

    public void reduction(Context context) {
        if(Constants.VERSION){
            smdtManager.smdtSetStatusBar(context, true);
            smdtManager.smdtSetGpioValue(2, false);
            smdtManager.smdtSetGpioValue(3, false);
        }
        unableDog();
    }

    class FeedDogThread extends Thread {
        @Override
        public void run() {
            while (feedFlag) {
                if(Constants.VERSION)
                    smdtManager.smdtWatchDogFeed();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void enableDog() {
        char c = 1;
        feedFlag = true;
        if(Constants.VERSION)
            smdtManager.smdtWatchDogEnable(c);
        if (tFeedDog == null) {
            tFeedDog = new FeedDogThread();
            tFeedDog.start();
        }
    }

    private void unableDog() {
        char c = 0;
        feedFlag = false;
        if(Constants.VERSION)
            smdtManager.smdtWatchDogEnable(c);
        if (tFeedDog != null) {
            tFeedDog.interrupt();
            tFeedDog = null;
        }
    }

}
