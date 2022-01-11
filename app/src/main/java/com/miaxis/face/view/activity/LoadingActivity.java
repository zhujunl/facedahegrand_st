package com.miaxis.face.view.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.app.Face_App;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.event.InitCWEvent;
import com.miaxis.face.event.ReInitEvent;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.ServerManager;
import com.miaxis.face.manager.ToastManager;
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
        Log.e("Loadin","OnCreate");
        setContentView(R.layout.activity_loading);
        if (!Constants.VERSION&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ( !Settings.canDrawOverlays(this)) {
                //若未授权则请求权限
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                Log.e("Loadin==","startActivityForResult");
                startActivityForResult(intent, 0);
                Log.e("Loadin==","startActivityForResult++++++++");
            }
        }
        ButterKnife.bind(this);
        initWindow();
        initTitle();
        gifLoading.setMovieResource(R.raw.loading);
        App.getInstance().getThreadExecutor().execute(() -> {
            App.getInstance().initApplication((result, message) -> {
                runOnUiThread(() -> {
                    if (result) {
                        tvLoading.setText("初始化算法成功，正在启动Http服务");
                        App.getInstance().getThreadExecutor().execute(() -> {
                            ServerManager.getInstance().startServer(25841, () -> {
                                runOnUiThread(() -> {
                                    ToastManager.toast("创建Http服务成功");
                                    tvLoading.setText("初始化成功");
                                    startActivity(new Intent(this, VerifyActivity.class));
                                    finish();
                                });
                            });
                        });
                    } else {
                        tvLoading.setText(message);
                        LogUtil.writeLog(message);
                        llTop.setVisibility(View.VISIBLE);
                    }
                });
            });
        });
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
        Log.e("Loadin","onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.e("Loadin","onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e("Loadin","onStop");
        super.onStop();
    }

    @OnClick(R.id.tv_title)
    void onErrorExit() {
//        Face_App.getInstance().unableDog();
        GpioManager.getInstance().reduction(this);
        finish();
    }
}
