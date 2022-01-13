package com.miaxis.face.view.activity;

import static com.miaxis.face.constant.Constants.DELAYList;

import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.miaxis.face.R;
import com.miaxis.face.app.App;
import com.miaxis.face.bean.Config;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.greendao.gen.IDCardRecordDao;
import com.miaxis.face.manager.ConfigManager;
import com.miaxis.face.manager.DaoManager;
import com.miaxis.face.manager.FingerManager;
import com.miaxis.face.manager.GpioManager;
import com.miaxis.face.manager.TaskManager;
import com.miaxis.face.manager.ToastManager;
import com.miaxis.face.net.FaceNetApi;
import com.miaxis.face.presenter.UpdatePresenter;
import com.miaxis.face.util.MyUtil;
import com.miaxis.face.util.PatternUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.et_update_url)
    EditText etUpdateUrl;
    @BindView(R.id.et_upload_url)
    EditText etUploadUrl;
    @BindView(R.id.et_advertisement_url)
    EditText etAdvertisementUrl;
    @BindView(R.id.tv_device_serial)
    TextView tvDeviceSerial;
    @BindView(R.id.et_client_id)
    EditText etClientId;
    @BindView(R.id.s_verify_mode)
    Spinner sVerifyMode;
    @BindView(R.id.rb_net_on)
    RadioButton rbNetOn;
    @BindView(R.id.rb_net_off)
    RadioButton rbNetOff;
    @BindView(R.id.rg_net)
    RadioGroup rgNet;
    @BindView(R.id.rb_encrypt_on)
    RadioButton rbEncryptOn;
    @BindView(R.id.rb_encrypt_off)
    RadioButton rbEncryptOff;
    @BindView(R.id.rg_encrypt)
    RadioGroup rgEncrypt;
    @BindView(R.id.rb_result_on)
    RadioButton rbResultOn;
    @BindView(R.id.rb_result_off)
    RadioButton rbResultOff;
    @BindView(R.id.rg_result)
    RadioGroup rgResult;
    @BindView(R.id.rb_gather_on_one)
    RadioButton rbGatherOnOne;
    @BindView(R.id.rb_gather_on_two)
    RadioButton rbGatherOnTwo;
    @BindView(R.id.rb_gather_off)
    RadioButton rbGatherOff;
    @BindView(R.id.rg_gather)
    RadioGroup rgGather;
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
    @BindView(R.id.rb_advertise_local)
    RadioButton rbAdvertiseLocal;
    @BindView(R.id.rb_advertise_net)
    RadioButton rbAdvertiseNet;
    @BindView(R.id.rb_advertise_net_and_local)
    RadioButton rbAdvertiseNetAndLocal;
    @BindView(R.id.rg_advertise_mode)
    RadioGroup rgAdvertiseMode;
    @BindView(R.id.et_verify_score)
    EditText etVerifyScore;
    @BindView(R.id.et_quality_score)
    EditText etQualityScore;
    @BindView(R.id.et_liveness_quality_score)
    EditText etLivenessQualityScore;
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
    @BindView(R.id.rb_sequel_on)
    RadioButton rbSequelOn;
    @BindView(R.id.rb_sequel_off)
    RadioButton rbSequelOff;
    @BindView(R.id.rg_sequel)
    RadioGroup rgSequel;
    @BindView(R.id.et_version_delay_time)
    Spinner et_version_delay_time;

    private Config config;
    private boolean hasFingerDevice;

    private UpdatePresenter updatePresenter;

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
        GpioManager.getInstance().setSmdtStatusBar(this, false);
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


        List<String> versionList=Arrays.asList(getResources().getStringArray(R.array.versiondelay));
        ArrayAdapter<String> versionAdapter=new ArrayAdapter<>(this,R.layout.spinner_style_display,R.id.tvDisplay,versionList);
        et_version_delay_time.setAdapter(versionAdapter);
        et_version_delay_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                config.setVersion_position(position);
                config.setVersion_delay(DELAYList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initData() {
        config = ConfigManager.getInstance().getConfig();
        hasFingerDevice = FingerManager.getInstance().checkHasFingerDevice();
        updatePresenter = new UpdatePresenter(this);
    }

    private void initView() {
//        GpioManager.getInstance().setSmdtStatusBar(this, false);
        tvVersion.setText(MyUtil.getCurVersion(this));
        etUpdateUrl.setText(config.getUpdateUrl());
        etUploadUrl.setText(config.getUploadRecordUrl());
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
        rbSequelOn.setChecked(config.getSequelFlag());
        rbSequelOff.setChecked(!config.getSequelFlag());
        rbEncryptOn.setChecked(config.getEncrypt());
        rbEncryptOff.setChecked(!config.getEncrypt());
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
        etQualityScore.setText(String.valueOf(config.getQualityScore()));
        etLivenessQualityScore.setText(String.valueOf(config.getLivenessQualityScore()));
        tvSelectTime.setText(config.getUpTime());
        etMonitorInterval.setText(String.valueOf(config.getIntervalTime()));
        et_version_delay_time.setSelection(config.getVersion_position()==null?2:config.getVersion_position());
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
        config.setUploadRecordUrl(etUploadUrl.getText().toString());
        config.setAdvertisementUrl(etAdvertisementUrl.getText().toString());
        config.setClientId(etClientId.getText().toString());
        config.setNetFlag(rbNetOn.isChecked());
        config.setSequelFlag(rbSequelOn.isChecked());
        config.setEncrypt(rbEncryptOn.isChecked());
        config.setResultFlag(rbResultOn.isChecked());
        config.setSaveLocalFlag(rbSaveLocalOn.isChecked());
        config.setDocumentFlag(rbDocumentOn.isChecked());
        config.setLivenessFlag(rbLivenessOn.isChecked());
        config.setQueryFlag(rbQueryOn.isChecked());
        config.setAdvertiseFlag(rbAdvertiseOn.isChecked());
        if(TextUtils.isEmpty(etVerifyScore.getText().toString().trim())){
            ToastManager.toast("保存失败，请设置比对阈值");
            return;
        }
        if(Float.parseFloat(etVerifyScore.getText().toString().trim())>1.0F){
            ToastManager.toast("保存失败，比对阈值最高为1");
            return;
        }
        config.setVerifyScore(Float.parseFloat(etVerifyScore.getText().toString()));
        if(TextUtils.isEmpty(etQualityScore.getText().toString().trim())){
            ToastManager.toast("保存失败，请设置质量阈值");
            return;
        }
        config.setQualityScore(Integer.parseInt(etQualityScore.getText().toString()));
        if(TextUtils.isEmpty(etLivenessQualityScore.getText().toString().trim())){
            ToastManager.toast("保存失败，请设置活体质量阈值");
            return;
        }
        config.setLivenessQualityScore(Integer.parseInt(etLivenessQualityScore.getText().toString()));
        config.setTitleStr(etTitleStr.getText().toString());
        config.setPassword(etPwd.getText().toString());
        config.setUpTime(tvSelectTime.getText().toString());
        if(TextUtils.isEmpty(etMonitorInterval.getText().toString().trim())){
            ToastManager.toast("保存失败，请设置监控间隔");
            return;
        }
        config.setIntervalTime(Integer.parseInt(etMonitorInterval.getText().toString()));
        config.setOrgName(etOrgName.getText().toString());
        if(TextUtils.isEmpty(etAdvertiseDelayTime.getText().toString().trim())){
            ToastManager.toast("保存失败，请设置广告显示延迟");
            return;
        }
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
                Log.e("message===",message);
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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Face_App.timerTask.run();
//            }
//        }).start();
        App.getInstance().getThreadExecutor().execute(() -> TaskManager.getInstance().task(this));
        btnClearNow.setEnabled(false);
        ToastManager.toast("任务已开始执行");
    }

    @OnClick(R.id.btn_update)
    void update() {
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
        updatePresenter.checkUpdateWithDialog(urlStr);
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
        if (updatePresenter != null) {
            updatePresenter.doDestroy();
        }
    }


}
