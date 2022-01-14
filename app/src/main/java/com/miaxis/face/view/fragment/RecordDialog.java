package com.miaxis.face.view.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.miaxis.face.R;
import com.miaxis.face.bean.IDCardRecord;
import com.miaxis.face.bean.Record;
import com.miaxis.face.manager.ConfigManager;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xu.nan on 2016/10/14.
 */

public class RecordDialog extends BaseDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.iv_id_photo)
    ImageView ivIdPhoto;
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.iv_camera_photo)
    ImageView ivCameraPhoto;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_cardNo)
    TextView tvCardNo;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_has_up)
    TextView tvHasUp;
    @BindView(R.id.tv_address)
    TextView tvAddress;

    @BindColor(R.color.green_dark)
    int darkGreen;
    @BindColor(R.color.red)
    int red;

    private IDCardRecord idCardRecord;

    public void setIdCardRecord(IDCardRecord idCardRecord) {
        this.idCardRecord = idCardRecord;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.77), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        hideNavigationBar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_record, container);
        unbinder = ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {

        if (idCardRecord == null) {
            return;
        }

        tvName.setText(idCardRecord.getName());
        tvCardNo.setText(idCardRecord.getCardNumber());
        tvSex.setText(idCardRecord.getSex());
        tvResult.setText(idCardRecord.getDescribe());
        if (idCardRecord.isVerifyResult()) {
            ivResult.setImageResource(R.mipmap.result_true);
            tvResult.setTextColor(darkGreen);
        } else {
            ivResult.setImageResource(R.mipmap.result_false);
            tvResult.setTextColor(red);
        }
        if (idCardRecord.isUpload()) {
            tvHasUp.setText("已上传");
            tvHasUp.setTextColor(darkGreen);
        } else {
            tvHasUp.setText("未上传");
            tvHasUp.setTextColor(red);
        }
        if (!TextUtils.isEmpty(idCardRecord.getCardPhotoPath())) {
            Bitmap bmpId = BitmapFactory.decodeFile(idCardRecord.getCardPhotoPath());
            ivIdPhoto.setImageBitmap(bmpId);
        }
        if (!TextUtils.isEmpty(idCardRecord.getFacePhotoPath())) {
            Bitmap bmpCamera = BitmapFactory.decodeFile(idCardRecord.getFacePhotoPath());
            ivCameraPhoto.setImageBitmap(bmpCamera);
        }

        tvLocation.setText(idCardRecord.getLocation());
        tvAddress.setText(idCardRecord.getAddress());
        tvBirthday.setText(idCardRecord.getBirthday());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
