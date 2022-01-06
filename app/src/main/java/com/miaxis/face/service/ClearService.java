package com.miaxis.face.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Build;

import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.IDCardRecordDao;
import com.miaxis.face.manager.DaoManager;
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
                handleActionClear();
            }
        }
    }

    private void handleActionClear() {
        int type = FileUtil.getAvailablePathType(this);
        IDCardRecordDao recordDao = DaoManager.getInstance().getDaoSession().getIDCardRecordDao();
        switch (type) {
            case Constants.PATH_TF_CARD:
                long sdAll = FileUtil.getSDAllSize(FileUtil.getAvailablePath(this));
                long sdFree = FileUtil.getSDFreeSize(FileUtil.getAvailablePath(this));
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


}
