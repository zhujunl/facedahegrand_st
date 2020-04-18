package com.miaxis.face.manager;

import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.presenter.AdvertisePresenter;

public class AdvertManager {

    private AdvertManager() {
    }

    public static AdvertManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final AdvertManager instance = new AdvertManager();
    }

    /** ================================ 静态内部类单例 ================================ **/

    public void updateAdvertise() {
        Config config = ConfigManager.getInstance().getConfig();
        if (config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET || config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET_AND_LOCAL) {
            AdvertisePresenter.downloadAdvertiseUrl(config.getAdvertisementUrl());
        }
    }

}
