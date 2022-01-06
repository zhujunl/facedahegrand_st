package com.miaxis.face.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.miaxis.face.app.App;
import com.miaxis.face.app.GreenDaoContext;
import com.miaxis.face.app.MyOpenHelper;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.ConfigOld;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.DaoMaster;
import com.miaxis.face.greendao.gen.DaoSession;
import com.miaxis.face.model.ConfigModel;
import com.miaxis.face.util.FileUtil;

import java.io.File;

public class DaoManager {

    private DaoManager() {}

    public static final String path = FileUtil.getAvailablePath(App.getInstance())+ File.separator + "FaceDahe_ST.db";
    public static DaoManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DaoManager instance = new DaoManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private SQLiteDatabase sqLiteDatabase;

    public DaoSession getDaoSession() {
        daoSession.clear();
        return daoSession;
    }

    /**
     * 初始化数据库
     * @param context
     * @param name
     */
    public void initDbHelper(Context context, String name) {
        MyOpenHelper helper = new MyOpenHelper(new GreenDaoContext(context), name, null);
        sqLiteDatabase = helper.getWritableDatabase();
        daoMaster = new DaoMaster(sqLiteDatabase);
        daoSession = daoMaster.newSession();
        Config config=daoSession.getConfigDao().queryBuilder().unique();
        if (config==null)
            config=new Config();
        Log.d("config==","="+config.toString());
        ConfigOld old=getOldConfig(context);
        if(old!=null){
            Log.d("oldconfig==","="+old.toString());
            config.setId(old.getId());
            config.setUpdateUrl(old.getUpdateUrl());
            config.setUploadRecordUrl(old.getHost());
            config.setAdvertisementUrl(old.getAdvertisementUrl());
//            config.setDeviceSerialNumber();
//            config.setAccount();
//            config.setClientId();
//            config.setEncrypt();
            config.setVerifyMode(old.getVerifyMode());
            config.setNetFlag(old.isNetFlag());
            config.setResultFlag(old.isResultFlag());
//            config.setSequelFlag();
            config.setDocumentFlag(old.isDocumentFlag());
 //           config.setLivenessFlag();
            config.setQueryFlag(old.isQueryFlag());
            config.setWhiteFlag(old.isWhiteFlag());
            config.setBlackFlag(old.isBlackFlag());
//            config.setGatherFingerFlag();
            config.setAdvertiseFlag(old.getAdvertiseFlag());
            config.setAdvertisementMode(old.getAdvertisementMode());
            config.setVerifyScore(old.getPassScore());
 //           config.setQualityScore();
 //           config.setLivenessQualityScore();
            config.setTitleStr(old.getBanner());
            config.setPassword(old.getPassword());
            config.setUpTime(old.getUpTime());
            config.setIntervalTime(old.getIntervalTime());
            config.setOrgName(old.getOrgName());
            config.setAdvertiseDelayTime(old.getAdvertiseDelayTime());
            ConfigModel.saveConfig(config);
            Log.d("newconfig==",config.toString());
            delete(context,String.valueOf(old.getId()));
        }
    }

    public ConfigOld getOldConfig(Context context){
        ConfigOld configOld ;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
        try {
            Cursor cursor=db.rawQuery("select * from CONFIG", null);
            while (cursor.moveToNext()) {
                configOld= new ConfigOld();
                configOld.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                configOld.setHost(cursor.getString(cursor.getColumnIndex("HOST")));
                configOld.setResultFlag(cursor.getInt(cursor.getColumnIndex("RESULT_FLAG")) != 0);
                configOld.setDocumentFlag(cursor.getInt(cursor.getColumnIndex("DOCUMENT_FLAG")) != 0);
                configOld.setUpTime(cursor.getString(cursor.getColumnIndex("UP_TIME")));
                configOld.setPassScore(cursor.getFloat(cursor.getColumnIndex("PASS_SCORE")));
                configOld.setBanner(cursor.getString(cursor.getColumnIndex("BANNER")));
                configOld.setIntervalTime(cursor.getInt(cursor.getColumnIndex("INTERVAL_TIME")));
                configOld.setOrgId(cursor.getString(cursor.getColumnIndex("ORG_ID")));
                configOld.setOrgName(cursor.getString(cursor.getColumnIndex("ORG_NAME")));
                configOld.setNetFlag(cursor.getInt(cursor.getColumnIndex("NET_FLAG")) != 0);
                configOld.setQueryFlag(cursor.getInt(cursor.getColumnIndex("QUERY_FLAG")) != 0);
                configOld.setPassword(cursor.getString(cursor.getColumnIndex("PASSWORD")));
                configOld.setVerifyMode(cursor.getInt(cursor.getColumnIndex("VERIFY_MODE")));
                configOld.setWhiteFlag(cursor.getInt(cursor.getColumnIndex("WHITE_FLAG")) != 0);
                configOld.setBlackFlag(cursor.getInt(cursor.getColumnIndex("BLACK_FLAG")) != 0);
                configOld.setAdvertiseFlag(cursor.getInt(cursor.getColumnIndex("ADVERTISE_FLAG")) != 0);
                configOld.setAdvertiseDelayTime(cursor.getInt(cursor.getColumnIndex("ADVERTISE_DELAY_TIME")));
                configOld.setAdvertisementUrl(cursor.getString(cursor.getColumnIndex("ADVERTISEMENT_URL")));
                configOld.setAdvertisementMode(cursor.getInt(cursor.getColumnIndex("ADVERTISEMENT_MODE")));
                configOld.setUpdateUrl(cursor.getString(cursor.getColumnIndex("UPDATE_URL")));
                cursor.close();
                db.close();
                return configOld;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(Context context,String brandId) {
//        MyOpenHelper h = new MyOpenHelper(context, "FaceDahe_ST.db", null);
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
        db.delete("CONFIG", "_id = ? ", new String[]{brandId});
        db.close();
    }

}
