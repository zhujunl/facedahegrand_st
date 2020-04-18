package com.miaxis.face.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.miaxis.face.app.GreenDaoContext;
import com.miaxis.face.app.MyOpenHelper;
import com.miaxis.face.greendao.gen.DaoMaster;
import com.miaxis.face.greendao.gen.DaoSession;

public class DaoManager {

    private DaoManager() {}

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
    }

}
