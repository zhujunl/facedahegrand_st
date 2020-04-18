package com.miaxis.face.view.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Created by xu.nan on 2017/6/6.
 */

public class BaseDialogFragment extends AppCompatDialogFragment {


    protected void hideNavigationBar() {
        final View decorView = getDialog().getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        getDialog().getWindow().setAttributes(params);
    }

    public void hideInputMethod(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
