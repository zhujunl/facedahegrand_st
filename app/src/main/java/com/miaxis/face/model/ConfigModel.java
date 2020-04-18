package com.miaxis.face.model;

import com.miaxis.face.bean.Config;
import com.miaxis.face.manager.DaoManager;

public class ConfigModel {

    public static void saveConfig(Config config) {
        DaoManager.getInstance().getDaoSession().getConfigDao().deleteByKey(1L);
        DaoManager.getInstance().getDaoSession().getConfigDao().insert(config);
    }

    public static Config loadConfig() {
        return DaoManager.getInstance().getDaoSession().getConfigDao().load(1L);
    }

}
