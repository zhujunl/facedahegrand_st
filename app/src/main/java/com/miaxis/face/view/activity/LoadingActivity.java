package com.miaxis.face.view.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.event.InitCWEvent;
import com.miaxis.face.event.ReInitEvent;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.view.custom.GifView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingActivity extends BaseActivity {

    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.gif_loading)
    GifView gifLoading;

    @BindColor(R.color.white)
    int white;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.tv_wel_msg)
    TextView tvWelMsg;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_weather)
    TextView tvWeather;
    @BindView(R.id.ll_top)
    LinearLayout llTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        initWindow();
        initTitle();
        gifLoading.setMovieResource(R.raw.loading);
        new Thread(() -> {
            App.getInstance().initApplication((result, message) -> {
                runOnUiThread(() -> {
                    if (result) {
                        tvLoading.setText("初始化算法成功");
                        LogUtil.writeLog("初始化算法成功");
                        startActivity(new Intent(this, VerifyActivity.class));
                        finish();
                    } else {
                        tvLoading.setText(message);
                        LogUtil.writeLog(message);
                        llTop.setVisibility(View.VISIBLE);
                    }
                });
            });
        }).start();
    }

    void initTitle() {
        llTop.setVisibility(View.GONE);
        tvTitle.setText("退出");
        tvWelMsg.setVisibility(View.INVISIBLE);
        tvTime.setVisibility(View.INVISIBLE);
        tvWeather.setVisibility(View.INVISIBLE);
        tvDate.setVisibility(View.INVISIBLE);
        ivRecord.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.tv_title)
    void onErrorExit() {
//        Face_App.getInstance().unableDog();
        GpioManager.getInstance().reduction(this);
        finish();
    }
}
