package com.miaxis.face.app;

import android.content.Context;
import android.widget.ImageView;

import com.miaxis.face.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by tang.yf on 2018/11/5.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        GlideApp.with(context).load(path).placeholder(R.mipmap.default_picture).error(R.mipmap.default_picture).into(imageView);
    }

}
