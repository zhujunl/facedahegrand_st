package com.miaxis.face.model;

import com.miaxis.face.app.App;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.util.FileUtil;

import java.io.File;

public class IDCardRecordModel {

    public static void saveIDCardRecord(IDCardRecord idCardRecord) throws Exception {
        String cardPhotoName = idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
        String facePhotoName = idCardRecord.getCardNumber() + "face" + System.currentTimeMillis() + ".jpg";
        if (idCardRecord.getCardBitmap() != null) {
            FileUtil.saveBitmap(idCardRecord.getCardBitmap(), FileUtil.getAvailableImgPath(App.getInstance()), cardPhotoName);
            idCardRecord.setCardPhotoPath(FileUtil.getAvailableImgPath(App.getInstance()) + File.separator + cardPhotoName);
        }
        if (idCardRecord.getFaceBitmap() != null) {
            FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), FileUtil.getAvailableImgPath(App.getInstance()), facePhotoName);
            idCardRecord.setFacePhotoPath(FileUtil.getAvailableImgPath(App.getInstance()) + File.separator + facePhotoName);
        }
        DaoManager.getInstance().getDaoSession().getIDCardRecordDao().insert(idCardRecord);
    }

    public static void updateRecord(IDCardRecord idCardRecord) {
        DaoManager.getInstance().getDaoSession().getIDCardRecordDao().update(idCardRecord);
    }

}
