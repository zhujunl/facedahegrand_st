package com.miaxis.face.view.custom;

import android.app.Dialog;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.miaxis.face.R;
import com.miaxis.face.app.GlideImageLoader;
import com.miaxis.face.bean.Advertisement;
import com.miaxis.face.constant.Constants;
import com.miaxis.face.presenter.AdvertisePresenter;
import com.miaxis.face.view.fragment.BaseDialogFragment;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AdvertiseDialogFragment extends BaseDialogFragment {

    @BindView(R.id.banner)
    Banner banner;

    private Unbinder bind;
    private List<Advertisement> advertisementList;
    private OnViewClickListener listener;
    private Integer mode;
    private AdvertisePresenter advertisePresenter;

    public static AdvertiseDialogFragment newInstance(Integer mode, OnViewClickListener listener) {
        AdvertiseDialogFragment advertiseDialogFragment = new AdvertiseDialogFragment();
        advertiseDialogFragment.setListener(listener);
        advertiseDialogFragment.setMode(mode);
        return advertiseDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advertise_dialog, container, false);
        bind = ButterKnife.bind(this, view);
        banner.setOnBannerListener(listener::onClick);
        advertisePresenter = new AdvertisePresenter(inflater.getContext());
        hideNavigationBar();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        advertisePresenter.loadAdvertisement(mode, this::onImageList, this::onError);
    }

    private void onImageList(List<Advertisement> dataList) {
        this.advertisementList = dataList;
        List<String> imageList = Stream.of(dataList).map(Advertisement::getUrl).collect(Collectors.toList());
        List<String> titleList = Stream.of(dataList).map(Advertisement::getTitle).collect(Collectors.toList());
        if (mode != Constants.ADVERTISEMENT_LOCAL) {
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        }
        banner.setImageLoader(new GlideImageLoader());
        banner.update(imageList, titleList);
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageSelected(int i) {
                if (null != advertisementList) {
                    int delayTime = (advertisementList.get(i).getDelayTime() + 1) * 1000;
                    banner.setDelayTime(Math.min(delayTime, 60000));
                    banner.startAutoPlay();
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
        banner.start();
        banner.startAutoPlay();
    }

    private void onError(Throwable throwable) {
        advertisementList = null;
        banner.setImageLoader(new GlideImageLoader());
        List<Integer> defaultImage = new ArrayList<>();
        defaultImage.add(R.mipmap.default_picture);
        banner.setImages(defaultImage);
        banner.start();
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("AdvertiseDialogFragment",":onStopView");
        banner.stopAutoPlay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("AdvertiseDialogFragment",":onDestroyView");
        advertisePresenter.destroy();
        bind.unbind();
    }

    public void setListener(OnViewClickListener listener) {
        this.listener = listener;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public interface OnViewClickListener {
        void onClick(int position);
    }

}
