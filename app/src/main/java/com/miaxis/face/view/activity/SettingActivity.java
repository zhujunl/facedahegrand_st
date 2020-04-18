package com.miaxis.face.view.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.UpdateData;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.IDCardRecordDao;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.TaskManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.net.UpdateNet;
import com.miaxis.face.util.FileUtil;
import com.miaxis.face.util.LogUtil;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.util.PatternUtil;
import com.miaxis.face.view.fragment.UpdateDialog;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.et_update_url)
    EditText etUpdateUrl;
    @BindView(R.id.et_upload_url_1)
    EditText etUploadUrl1;
    @BindView(R.id.et_upload_url_2)
    EditText etUploadUrl2;
    @BindView(R.id.et_advertisement_url)
    EditText etAdvertisementUrl;
    @BindView(R.id.s_verify_mode)
    Spinner sVerifyMode;
    @BindView(R.id.rb_net_on)
    RadioButton rbNetOn;
    @BindView(R.id.rb_net_off)
    RadioButton rbNetOff;
    @BindView(R.id.rg_net)
    RadioGroup rgNet;
    @BindView(R.id.rb_result_on)
    RadioButton rbResultOn;
    @BindView(R.id.rb_result_off)
    RadioButton rbResultOff;
    @BindView(R.id.rg_result)
    RadioGroup rgResult;
    @BindView(R.id.rb_save_local_on)
    RadioButton rbSaveLocalOn;
    @BindView(R.id.rb_save_local_off)
    RadioButton rbSaveLocalOff;
    @BindView(R.id.rg_save_local)
    RadioGroup rgSaveLocal;
    @BindView(R.id.rb_document_on)
    RadioButton rbDocumentOn;
    @BindView(R.id.rb_document_off)
    RadioButton rbDocumentOff;
    @BindView(R.id.rg_document)
    RadioGroup rgDocument;
    @BindView(R.id.rb_liveness_on)
    RadioButton rbLivenessOn;
    @BindView(R.id.rb_liveness_off)
    RadioButton rbLivenessOff;
    @BindView(R.id.rg_liveness)
    RadioGroup rgLiveness;
    @BindView(R.id.rb_query_on)
    RadioButton rbQueryOn;
    @BindView(R.id.rb_query_off)
    RadioButton rbQueryOff;
    @BindView(R.id.rg_query)
    RadioGroup rgQuery;
    @BindView(R.id.rb_advertise_on)
    RadioButton rbAdvertiseOn;
    @BindView(R.id.rb_advertise_off)
    RadioButton rbAdvertiseOff;
    @BindView(R.id.rg_advertise)
    RadioGroup rgAdvertise;
    @BindView(R.id.et_verify_score)
    EditText etVerifyScore;
    @BindView(R.id.et_mask_verify_score)
    EditText etMaskVerifyScore;
    @BindView(R.id.et_quality_score)
    EditText etQualityScore;
    @BindView(R.id.et_liveness_quality_score)
    EditText etLivenessQualityScore;
    @BindView(R.id.et_mask_score)
    EditText etMaskScore;
    @BindView(R.id.tv_select_time)
    TextView tvSelectTime;
    @BindView(R.id.btn_clear_now)
    Button btnClearNow;
    @BindView(R.id.tv_result_count)
    TextView tvResultCount;
    @BindView(R.id.et_monitor_interval)
    EditText etMonitorInterval;
    @BindView(R.id.et_title_str)
    EditText etTitleStr;
    @BindView(R.id.et_org_name)
    EditText etOrgName;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_advertise_delay_time)
    EditText etAdvertiseDelayTime;
    @BindView(R.id.rb_advertise_local)
    RadioButton rbAdvertiseLocal;
    @BindView(R.id.rb_advertise_net)
    RadioButton rbAdvertiseNet;
    @BindView(R.id.rb_advertise_net_and_local)
    RadioButton rbAdvertiseNetAndLocal;
    @BindView(R.id.rg_advertise_mode)
    RadioGroup rgAdvertiseMode;
    @BindView(R.id.btn_white_manage)
    Button btnWhiteManage;
    @BindView(R.id.btn_black_manage)
    Button btnBlackManage;
    @BindView(R.id.btn_local_feature_manage)
    Button btnLocalFeatureManage;
    @BindView(R.id.btn_save_config)
    Button btnSaveConfig;
    @BindView(R.id.btn_cancel_config)
    Button btnCancelConfig;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.tv_device_serial)
    TextView tvDeviceSerial;
    @BindView(R.id.et_client_id)
    EditText etClientId;
    @BindView(R.id.rb_gather_on_one)
    RadioButton rbGatherOnOne;
    @BindView(R.id.rb_gather_on_two)
    RadioButton rbGatherOnTwo;
    @BindView(R.id.rb_gather_off)
    RadioButton rbGatherOff;
    @BindView(R.id.rg_gather)
    RadioGroup rgGather;

    private Config config;
    private UpdateDialog updateDialog;
    private boolean hasFingerDevice;

    private MaterialDialog waitDialog;
    private MaterialDialog updateMaterialDialog;
    private MaterialDialog downloadProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long tim = System.currentTimeMillis();
        setContentView(R.layout.activity_setting);
        Log.e("asd", "asdadasdsadsa" + (System.currentTimeMillis() - tim));
        ButterKnife.bind(this);
        initWindow();
        initData();
        initModeSpinner();
        initView();
    }

    void initModeSpinner() {
        List<String> verifyModeList = Arrays.asList(getResources().getStringArray(R.array.verifyMode));
        if (!hasFingerDevice) {
            String faceOnly = verifyModeList.get(0);
//            String local = verifyModeList.get(6);
            verifyModeList = new ArrayList<>();
            verifyModeList.add(faceOnly);
//            verifyModeList.add(local);
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.spinner_style_display, R.id.tvDisplay, verifyModeList);
        sVerifyMode.setAdapter(myAdapter);
        sVerifyMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (hasFingerDevice) {
                    config.setVerifyMode(position);
                } else {
                    if (position == 1) {
                        config.setVerifyMode(Config.MODE_LOCAL_FEATURE);
                    } else {
                        config.setVerifyMode(Config.MODE_FACE_ONLY);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                config.setVerifyMode(0);
            }
        });
    }

    private void initData() {
        config = ConfigManager.getInstance().getConfig();
        hasFingerDevice = FingerManager.getInstance().checkHasFingerDevice();
    }

    private void initView() {
//        GpioManager.getInstance().setSmdtStatusBar(this, false);
        tvVersion.setText(MyUtil.getCurVersion(this).getVersion());
        etUpdateUrl.setText(config.getUpdateUrl());
        etUploadUrl1.setText(config.getUploadRecordUrl1());
        etUploadUrl2.setText(config.getUploadRecordUrl2());
        etAdvertisementUrl.setText(config.getAdvertisementUrl());
        tvDeviceSerial.setText(config.getDeviceSerialNumber());
        etClientId.setText(config.getClientId());
        if (hasFingerDevice) {
            sVerifyMode.setSelection(config.getVerifyMode());
        } else {
            sVerifyMode.setSelection(config.getVerifyMode() / 6);           //无指纹模块时， 验证模式 只有0 或 6
        }
        rbNetOn.setChecked(config.getNetFlag());
        rbNetOff.setChecked(!config.getNetFlag());
        rbResultOn.setChecked(config.getResultFlag());
        rbResultOff.setChecked(!config.getResultFlag());
        rbSaveLocalOn.setChecked(config.getSaveLocalFlag());
        rbSaveLocalOff.setChecked(!config.getSaveLocalFlag());
        rbDocumentOn.setChecked(config.getDocumentFlag());
        rbDocumentOff.setChecked(!config.getDocumentFlag());
        rbLivenessOn.setChecked(config.getLivenessFlag());
        rbLivenessOff.setChecked(!config.getLivenessFlag());
        rbQueryOn.setChecked(config.getQueryFlag());
        rbQueryOff.setChecked(!config.getQueryFlag());
        rbAdvertiseOn.setChecked(config.getAdvertiseFlag());
        rbAdvertiseOff.setChecked(!config.getAdvertiseFlag());
        rbGatherOnOne.setChecked(config.getGatherFingerFlag() == 0);
        rbGatherOnTwo.setChecked(config.getGatherFingerFlag() == 1);
        rbGatherOff.setChecked(config.getGatherFingerFlag() == 2);
        etVerifyScore.setText(String.valueOf(config.getVerifyScore()));
        etMaskVerifyScore.setText(String.valueOf(config.getMaskVerifyScore()));
        etQualityScore.setText(String.valueOf(config.getQualityScore()));
        etLivenessQualityScore.setText(String.valueOf(config.getLivenessQualityScore()));
        etMaskScore.setText(String.valueOf(config.getMaskScore()));
        tvSelectTime.setText(config.getUpTime());
        etMonitorInterval.setText(String.valueOf(config.getIntervalTime()));
        etTitleStr.setText(config.getTitleStr());
        etOrgName.setText(config.getOrgName());
        etPwd.setText(config.getPassword());
        etAdvertiseDelayTime.setText(String.valueOf(config.getAdvertiseDelayTime()));
        rbAdvertiseLocal.setChecked(config.getAdvertisementMode() == Constants.ADVERTISEMENT_LOCAL);
        rbAdvertiseNet.setChecked(config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET);
        rbAdvertiseNetAndLocal.setChecked(config.getAdvertisementMode() == Constants.ADVERTISEMENT_NET_AND_LOCAL);
        App.getInstance().getThreadExecutor().execute(() -> {
            IDCardRecordDao recordDao = DaoManager.getInstance().getDaoSession().getIDCardRecordDao();
            long notUpCount = recordDao.queryBuilder().where(IDCardRecordDao.Properties.Upload.eq(false)).count();
            long count = recordDao.count();
            runOnUiThread(() -> {
                tvResultCount.setText(notUpCount + " / " + count);
            });
        });
        updateDialog = new UpdateDialog();
        updateDialog.setContext(this);
    }

    @OnClick(R.id.tv_select_time)
    void onSelectTime(View view) {
        String[] strs = tvSelectTime.getText().toString().split(" : ");
        int h = Integer.valueOf(strs[0]);
        int m = Integer.valueOf(strs[1]);
        TimePickerDialog d = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
            String h1 = hourOfDay + "";
            String m1 = minute + "";
            if (hourOfDay < 10) {
                h1 = "0" + h1;
            }
            if (minute < 10) {
                m1 = "0" + m1;
            }
            tvSelectTime.setText(h1 + " : " + m1);
        }, h, m, true);
        d.show();
    }

    @OnClick(R.id.btn_save_config)
    void save() {
        if (etPwd.getText().length() != 6) {
            Toast.makeText(this, "请填写6位数字密码", Toast.LENGTH_SHORT).show();
            return;
        }
        config.setUpdateUrl(etUpdateUrl.getText().toString());
        config.setUploadRecordUrl1(etUploadUrl1.getText().toString());
        config.setUploadRecordUrl2(etUploadUrl2.getText().toString());
        config.setAdvertisementUrl(etAdvertisementUrl.getText().toString());
        config.setClientId(etClientId.getText().toString());
        config.setNetFlag(rbNetOn.isChecked());
        config.setResultFlag(rbResultOn.isChecked());
        config.setSaveLocalFlag(rbSaveLocalOn.isChecked());
        config.setDocumentFlag(rbDocumentOn.isChecked());
        config.setLivenessFlag(rbLivenessOn.isChecked());
        config.setQueryFlag(rbQueryOn.isChecked());
        config.setAdvertiseFlag(rbAdvertiseOn.isChecked());
        config.setVerifyScore(Float.parseFloat(etVerifyScore.getText().toString()));
        config.setMaskVerifyScore(Float.parseFloat(etMaskVerifyScore.getText().toString()));
        config.setQualityScore(Integer.parseInt(etQualityScore.getText().toString()));
        config.setLivenessQualityScore(Integer.parseInt(etLivenessQualityScore.getText().toString()));
        config.setMaskScore(Integer.parseInt(etMaskScore.getText().toString()));
        config.setTitleStr(etTitleStr.getText().toString());
        config.setPassword(etPwd.getText().toString());
        config.setUpTime(tvSelectTime.getText().toString());
        config.setIntervalTime(Integer.parseInt(etMonitorInterval.getText().toString()));
        config.setOrgName(etOrgName.getText().toString());
        config.setAdvertiseDelayTime(Integer.parseInt(etAdvertiseDelayTime.getText().toString()));
        if (rbGatherOnOne.isChecked()) {
            config.setGatherFingerFlag(0);
        } else if (rbGatherOnTwo.isChecked()) {
            config.setGatherFingerFlag(1);
        } else if (rbGatherOff.isChecked()) {
            config.setGatherFingerFlag(2);
        }
        if (rbAdvertiseLocal.isChecked()) {
            config.setAdvertisementMode(Constants.ADVERTISEMENT_LOCAL);
        } else if (rbAdvertiseNet.isChecked()) {
            config.setAdvertisementMode(Constants.ADVERTISEMENT_NET);
        } else if (rbAdvertiseNetAndLocal.isChecked()) {
            config.setAdvertisementMode(Constants.ADVERTISEMENT_NET_AND_LOCAL);
        }
        ConfigManager.getInstance().saveConfig(config, (result, message) -> {
            if (result) {
                FaceNetApi.rebuildRetrofit();
                TaskManager.getInstance().reSetTimer();
                finish();
            } else {
                ToastManager.toast("保存设置失败");
            }
        });
    }

    @OnClick(R.id.btn_cancel_config)
    void cancel() {
        finish();
    }

    @OnClick(R.id.btn_clear_now)
    void upLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Face_App.timerTask.run();
            }
        }).start();
    }

    @OnClick(R.id.btn_update)
    void update() {
        onUpdateClick();
    }

    @OnClick(R.id.btn_exit)
    void singOut() {
//        Face_App.getInstance().unableDog();
        GpioManager.getInstance().reduction(this);
        Intent intent = new Intent("com.miaxis.face.view.activity");
        intent.putExtra("closeAll", 1);
        sendBroadcast(intent);
    }

    @OnClick(R.id.btn_white_manage)
    void onWhiteManage() {
        startActivity(new Intent(this, WhiteActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onUpdateClick() {
        String urlStr = "";
        if (TextUtils.isEmpty(config.getUpdateUrl())) {
            if (!TextUtils.isEmpty(etUpdateUrl.getText().toString())) {
                if (!PatternUtil.isHttpFormat(etUpdateUrl.getText().toString())) {
                    Toast.makeText(SettingActivity.this, "应用更新URL校验失败，请输入 http://host:port/api 格式", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    urlStr = etUpdateUrl.getText().toString();
                }
            } else {
                return;
            }
        } else {
            urlStr = config.getUpdateUrl();
        }
        waitDialog = new MaterialDialog.Builder(SettingActivity.this)
                .cancelable(false)
                .autoDismiss(false)
                .content("请求服务器中，请稍后")
                .progress(true, 100)
                .show();
        Observable.just(urlStr)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap((Function<String, ObservableSource<ResponseEntity<UpdateData>>>) s -> {
                    URL url = new URL(s);
                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl("http://" + url.getHost() + ":" + url.getPort() + "/")
                            .build();
                    UpdateNet updateNet = retrofit.create(UpdateNet.class);
                    return updateNet.downUpdateVersion(url.getPath());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUpdateDataDown, throwable -> {
                    if (waitDialog != null) {
                        waitDialog.dismiss();
                    }
                    LogUtil.writeLog(throwable.getMessage());
                    Toast.makeText(SettingActivity.this, "更新请求Url连接失败", Toast.LENGTH_SHORT).show();
                });
    }

    private void onUpdateDataDown(ResponseEntity<UpdateData> responseEntity) {
        if (TextUtils.equals("200", responseEntity.getCode())) {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
            UpdateData updateData = responseEntity.getData();
            if (updateData == null) {
                Toast.makeText(SettingActivity.this, "更新请求Url返回数据错误", Toast.LENGTH_SHORT).show();
            }
            updateMaterialDialog = new MaterialDialog.Builder(SettingActivity.this)
                    .title("是否要下载更新文件？")
                    .content("设备当前版本：" + MyUtil.getCurVersion(this).getVersion() + "\n"
                            + "服务器当前版本：人证核验_" + updateData.getVersion())
                    .positiveText("确认下载")
                    .onPositive((dialog, which) -> downUpdateFile(updateData.getVersion(), updateData.getUrl()))
                    .negativeText("取消")
                    .show();
        } else {
            Toast.makeText(SettingActivity.this, "更新请求Url返回码并非200", Toast.LENGTH_SHORT).show();
        }
    }

    private void downUpdateFile(String version, String url) {
        downloadProgressDialog = new MaterialDialog.Builder(SettingActivity.this)
                .title("下载进度")
                .progress(false, 100)
                .positiveText("取消")
                .onPositive((dialog, which) -> FileDownloader.getImpl().pauseAll())
                .cancelable(false)
                .show();
        downloadFile(url, FileUtil.FACE_MAIN_PATH + File.separator + version + ".apk");
    }

    private void downloadFile(String url, String path) {
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downloadProgressDialog != null && downloadProgressDialog.isShowing()) {
                            int percent = (int) ((double) soFarBytes / (double) totalBytes * 100);
                            downloadProgressDialog.setProgress(percent);
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        onDownloadCompleted(task.getPath());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        onDownloadFinish("下载已取消");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        onDownloadFinish("下载时出现错误，已停止下载");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private void onDownloadFinish(String message) {
        if (downloadProgressDialog != null) {
            downloadProgressDialog.dismiss();
            Toast.makeText(SettingActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void onDownloadCompleted(String path) {
        if (downloadProgressDialog != null) {
            downloadProgressDialog.dismiss();
            Toast.makeText(SettingActivity.this, "下载已完成，正在准备安装", Toast.LENGTH_SHORT).show();
        }
        File file = new File(path);
        if (file.exists()) {
            installApk(file);
        } else {
            Toast.makeText(SettingActivity.this, "文件路径未找到，请尝试手动安装", Toast.LENGTH_SHORT).show();
        }
    }

    private void installApk(File file) {
        singOut();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

}
