package com.miaxis.face.view.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
        App.getInstance().sendBroadcast(Constants.MOLD_STATUS,-1,false);
        App.getInstance().sendBroadcast(Constants.MOLD_NAV,-1,false);
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

    protected void initApplication(){
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

    int targetSdkVersion = 0;
    String[] PermissionString = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.KILL_BACKGROUND_PROCESSES,
            Manifest.permission.GET_PACKAGE_SIZE,
            Manifest.permission.VIBRATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.CAMERA};

    public void checkPermission() {
        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    //第 1 步: 检查是否有相应的权限
                    boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                    if (isAllGranted) {
                        initApplication();
                        return;
                    }
                    ActivityCompat.requestPermissions(this,PermissionString, 1);
                }
            }else {
                initApplication();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private AlertDialog mDialog;

    //申请权限结果返回处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                //已授权
                if (grantResults[i] ==  PackageManager.PERMISSION_GRANTED) {
                    if(i==permissions.length-1){
                        initApplication();
                    }
                    continue;
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //选择禁止
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("授权");
                    builder.setMessage("需要允许授权才可使用");
                    int finalI = i;
                    builder.setPositiveButton("去允许", (dialog, id) -> {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        ActivityCompat.requestPermissions(LoadingActivity.this, new String[]{permissions[finalI]}, 1);
                    });
                    mDialog = builder.create();
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
                else {
                    //选择禁止并勾选禁止后不再询问
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("授权");
                    builder.setMessage("需要允许授权才可使用");
                    builder.setPositiveButton("去授权", (dialog, id) -> {
                        if (mDialog != null && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        //调起应用设置页面
                        startActivityForResult(intent, 2);
                    });
                    mDialog = builder.create();
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
            }
        }else {
            initApplication();
        }
    }

}
