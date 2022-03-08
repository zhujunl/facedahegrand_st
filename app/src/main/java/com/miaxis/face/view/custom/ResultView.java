package com.miaxis.face.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miaxis.face.BuildConfig;
import com.miaxis.face.R;
import com.miaxis.face.app.GlideApp;
import com.miaxis.face.constant.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultView extends LinearLayout {

    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.iv_finger_result)
    ImageView ivFingerResult;
    @BindView(R.id.iv_camera_photo)
    ImageView ivCameraPhoto;
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.iv_id_photo)
    ImageView ivIdPhoto;

    public ResultView(Context context) {
        super(context);
        init();
    }

    public ResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v = Constants.VERSION?BuildConfig.EQUIPMENT_TYPE==1? inflate(getContext(), R.layout.view_result, this):inflate(getContext(), R.layout.view_result2, this)
                :inflate(getContext(), R.layout.view_result_860s, this);
        ButterKnife.bind(this, v);
        bringToFront();
        GlideApp.with(this).load(R.raw.put_finger).into(ivFingerResult);
        setVisibility(INVISIBLE);
    }

    public void clear() {
        GlideApp.with(this).load(R.raw.put_finger).into(ivFingerResult);
        GlideApp.with(this).clear(ivResult);
        tvResult.setText("");
        showCardImage(null);
        showCameraImage(null);
    }

    public void setFingerMode(boolean mode) {
        ivFingerResult.setVisibility(mode ? View.VISIBLE : View.GONE);
    }

    public void setResultMessage(String message) {
        tvResult.setText(message);
    }

    public void showCardImage(Bitmap bitmap) {
        if (bitmap != null) {
            GlideApp.with(this).load(bitmap).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivIdPhoto);
        } else {
            GlideApp.with(this).clear(ivIdPhoto);
        }
    }

    public void showCameraImage(Bitmap bitmap) {
        if (bitmap != null) {
            GlideApp.with(this).load(bitmap).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(ivCameraPhoto);
        } else {
            GlideApp.with(this).clear(ivCameraPhoto);
        }
    }

    public void setFaceResult(boolean result) {
        GlideApp.with(this).load(result ? R.drawable.result_true : R.drawable.result_false).into(ivResult);
    }

    public void setFingerResult(boolean result) {
        GlideApp.with(this).load(result ? R.drawable.finger_succes : R.drawable.finger_fail).into(ivFingerResult);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
