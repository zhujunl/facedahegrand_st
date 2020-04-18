package com.miaxis.face.app;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.miaxis.face.util.FileUtil;

import java.io.File;

/**
 * Created by tang.yf on 2018/8/22.
 */

/**
 * 设置缓存路径
 */
@GlideModule
public class MyGlideAppModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int memoryCacheSizeBytes = 1024 * 1024 * 100; // 100mb
        //        设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        File file = new File(FileUtil.getAdvertisementCachePath());
        if (!file.exists()) {
            file.mkdir();
        }
        //        设置硬盘缓存大小
        builder.setDiskCache(new DiskLruCacheFactory(file.getAbsolutePath(), memoryCacheSizeBytes));
    }

    //    针对V4用户可以提升速度
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

}
