package com.miaxis.face.manager;

import android.util.Log;

import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.IDCardRecordDao;
import com.miaxis.face.presenter.AdvertisePresenter;
import com.miaxis.face.presenter.UpdatePresenter;
import com.miaxis.face.service.ClearService;
import com.miaxis.face.service.UpLoadRecordService;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private TaskManager() {
        scheduledExecutorService= Executors.newScheduledThreadPool(1);
        config=ConfigManager.getInstance().getConfig();
    }

    public static TaskManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final TaskManager instance = new TaskManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public static final int GROUP_SIZE = 100;

    private static Timer timer;
    public static TimerTask timerTask;
    private Config config;
    private ScheduledExecutorService scheduledExecutorService;

    public void init() {
        startTask();
    }

    private void initTask() {
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                task();
            }
        };
    }

    public void task() {
        Log.d("Task====","task");
        Config config = ConfigManager.getInstance().getConfig();
        if (config.getNetFlag() && config.getSequelFlag()) {
            upLoad();
        }
        if (config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET || config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET_AND_LOCAL) {
            AdvertisePresenter.downloadAdvertiseUrl(config.getAdvertisementUrl());
        }
        App.getInstance().getThreadExecutor().execute(() -> {
            new UpdatePresenter(App.getInstance()).checkUpdateSync();
        });
        ClearService.startActionClear(App.getInstance());
    }

    private void startTask() {
        initTask();
        Date start = new Date();
        start.setHours(Integer.parseInt(config.getUpTime().split(" : ")[0]));
        start.setMinutes(Integer.parseInt(config.getUpTime().split(" : ")[1]));
        long tStart = start.getTime();
        long t1 = new Date().getTime();
        if (tStart < t1) {
            start.setDate(new Date().getDate() + 1);
        }
        timer.scheduleAtFixedRate(timerTask,config.getVersion_delay(),config.getVersion_delay());
    }

    public void reSetTimer() {
        timerTask.cancel();
        initTask();
        timer.cancel();
        timer.purge();
        timer = new Timer();
//        Date start = new Date();
//        start.setHours(Integer.parseInt(config.getUpTime().split(" : ")[0]));
//        start.setMinutes(Integer.parseInt(config.getUpTime().split(" : ")[1]));
//        start.setSeconds(0);
//        long tStart = start.getTime();
//        long t1 = new Date().getTime();
//        if (tStart < t1) {
//            start.setDate(new Date().getDate() + 1);
//        }
//        timer.schedule(timerTask, start, Constants.TASK_DELAY);
        timer.scheduleAtFixedRate(timerTask,config.getVersion_delay(),config.getVersion_delay());
    }

    private void upLoad() {
        App.getInstance().getThreadExecutor().execute(() -> {
            IDCardRecordDao recordDao = DaoManager.getInstance().getDaoSession().getIDCardRecordDao();
            long count = recordDao.count();
            long page = (count % GROUP_SIZE == 0) ? count / GROUP_SIZE : (count / GROUP_SIZE + 1);
            for (int i = 0; i < page; i++) {
                List<IDCardRecord> recordList = recordDao.queryBuilder().offset(i * GROUP_SIZE).limit(GROUP_SIZE).orderAsc(IDCardRecordDao.Properties.Id).list();
                for (int j = 0; j < recordList.size(); j++) {
                    IDCardRecord record = recordList.get(j);
                    if (!record.isUpload()) {
                        RecordManager.getInstance().uploadRecord(record, (result, message, playVoice, voiceText) -> {
                            Log.e("asd", result ? "续传成功" : "续传失败");
                            if (result) {
                                record.setUpload(true);
                                recordDao.update(record);
                            }
                        });
//                    UpLoadRecordService.startActionUpLoad(App.getInstance(), record, ConfigManager.getInstance().getConfig());
                    }
                }
            }
        });
    }

    public void close(){
        if(!scheduledExecutorService.isShutdown()){
            scheduledExecutorService.shutdown();
        }
    }

}
