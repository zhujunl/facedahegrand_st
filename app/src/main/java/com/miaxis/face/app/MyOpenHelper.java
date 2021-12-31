package com.miaxis.face.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.ConfigDao;
import com.miaxis.face.greendao.gen.DaoMaster;
import com.miaxis.face.greendao.gen.DaoSession;
import com.miaxis.face.greendao.gen.RecordDao;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.DaoManager;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * Created by tang.yf on 2018/8/30.
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            }, RecordDao.class,ConfigDao.class);
        }
    }
}
