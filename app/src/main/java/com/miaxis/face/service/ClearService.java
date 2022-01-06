package com.miaxis.face.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.miaxis.face.R;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.IDCardRecordDao;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ClearService extends IntentService {
    private static final String ACTION_CLEAR = "com.miaxis.face.service.action.CLEAR";

    public ClearService() {
        super("ClearService");
    }

    public static void startActionClear(Context context) {
        Intent intent = new Intent(context, ClearService.class);
        intent.setAction(ACTION_CLEAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLEAR.equals(action)) {
                handleActionClear(this);
            }
        }
    }

    public static void handleActionClear(Context context) {
        int type = FileUtil.getAvailablePathType(context);
        IDCardRecordDao recordDao = DaoManager.getInstance().getDaoSession().getIDCardRecordDao();
        switch (type) {
            case Constants.PATH_TF_CARD:
                long sdAll = FileUtil.getSDAllSize(FileUtil.getAvailablePath(context));
                long sdFree = FileUtil.getSDFreeSize(FileUtil.getAvailablePath(context));
                double c = (double) sdFree / sdAll;
                if (c <= 0.30d) {
                    List<IDCardRecord> recordList = recordDao.queryBuilder().offset(0).limit(1000).orderAsc(IDCardRecordDao.Properties.Id).list();
                    for (int i=0; i<recordList.size(); i ++) {
                        FileUtil.deleteImg(recordList.get(i).getFacePhotoPath());
                        FileUtil.deleteImg(recordList.get(i).getCardPhotoPath());
                    }
                    recordDao.deleteInTx(recordList);
                    LogUtil.writeLog("清理记录" + recordList.size() + "条");
                }
                break;
            case Constants.PATH_LOCAL:
                long count = recordDao.count();
                if (count >= 20000) {
                    List<IDCardRecord> recordList = recordDao.queryBuilder().offset(0).limit(1000).orderAsc(IDCardRecordDao.Properties.Id).list();
                    for (int i=0; i<recordList.size(); i ++) {
                        FileUtil.deleteImg(recordList.get(i).getFacePhotoPath());
                        FileUtil.deleteImg(recordList.get(i).getCardPhotoPath());
                    }
                    recordDao.deleteInTx(recordList);
                    LogUtil.writeLog("清理记录" + recordList.size() + "条");
                }
                break;
        }

    }
    private NotificationManager notificationManager;
    private static final String NOTIFICATION_ID = "channedId";
    private static final String NOTIFICATION_NAME = "channedId";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ClearService", "service oncreate");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1,getNotification());
//        handler = new MyHandler();
    }
    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("测试服务")
                .setContentText("我正在运行");
        //设置Notification的ChannelID,否则不能正常显示
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_ID);
        }
        Notification notification = builder.build();
        return notification;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearService", "5s onDestroy");
        Log.d("ClearService", "this service destroy");
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
