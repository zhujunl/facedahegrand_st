package com.miaxis.face.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.annimon.stream.function.Consumer;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Advertisement;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.exception.MyException;
import com.miaxis.face.greendao.gen.AdvertisementDao;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.net.AdvertiseNet;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.util.FileUtil;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdvertisePresenter {

    private Context context;

    public AdvertisePresenter(Context context) {
        this.context = context;
    }

    public static void downloadAdvertiseUrl(String imageListUrl) {
        Disposable disposable = Observable.just(imageListUrl)
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .map(s -> {
                    Call<ResponseEntity<List<Advertisement>>> call = FaceNetApi.downImageList(imageListUrl);
                    Response<ResponseEntity<List<Advertisement>>> execute = call.execute();
                    ResponseEntity<List<Advertisement>> body = execute.body();
                    if (body != null) {
                        return body;
                    }
                    throw new MyException("下载广告失败");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseEntity -> {
                    if (TextUtils.equals(responseEntity.getCode(), "200")) {
                        Log.e("asd", "下载完成");
                        DaoManager.getInstance().getDaoSession().getAdvertisementDao().deleteAll();
                        try {
                            DaoManager.getInstance().getDaoSession().getAdvertisementDao().insertInTx(responseEntity.getData());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, throwable -> Log.e("asd", "下载广告失败"));
    }

    public void loadAdvertisement(Integer mode, Consumer<List<Advertisement>> consumer, Consumer<Throwable> error) {
        Disposable disposable = Observable.create((ObservableOnSubscribe<List<Advertisement>>) e -> {
            switch (mode) {
                case 0:
                    e.onNext(loadAdvertisementOnFile());
                    break;
                case 1:
                    e.onNext(loadAdvertisementOnDatabase());
                    break;
                case 2:
                    List<Advertisement> advertisementList = loadAdvertisementOnDatabase();
                    advertisementList.addAll(loadAdvertisementOnFile());
                    e.onNext(advertisementList);
                    break;
            }
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(advertisements -> {
                    if (advertisements.isEmpty()) {
                        Advertisement advertisement = new Advertisement.Builder()
                                .title("请放身份证或轻击屏幕以唤醒系统")
                                .url("file:///android_asset/default/default_picture.jpg")
                                .delayTime(5)
                                .build();
                        advertisements.add(advertisement);
                    }
                })
                .subscribe(advertisements -> {
                    if (context != null) {
                        consumer.accept(advertisements);
                    }
                }, throwable -> {
                    if (context != null) {
                        error.accept(throwable);
                    }
                });
    }

    private List<Advertisement> loadAdvertisementOnDatabase() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String now = simpleDateFormat.format(new Date());
        return DaoManager.getInstance().getDaoSession().getAdvertisementDao().queryBuilder()
                .where(AdvertisementDao.Properties.StartDate.le(now))
                .where(AdvertisementDao.Properties.EndDate.ge(now))
                .orderAsc(AdvertisementDao.Properties.Id)
                .list();
    }

    private List<Advertisement> loadAdvertisementOnFile() {
        List<Advertisement> advertisementList = new ArrayList<>();
        File directory = new File(FileUtil.getAdvertisementFilePath());
        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                Advertisement advertisement = new Advertisement.Builder()
                        .title("")
                        .url(file.getAbsolutePath())
                        .delayTime(5)
                        .build();
                advertisementList.add(advertisement);
            }
        }
        return advertisementList;
    }

    public void destroy() {
        this.context = null;
    }

}
