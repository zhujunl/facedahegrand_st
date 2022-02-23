package com.miaxis.face.view.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.miaxis.face.manager.ToastManager;


public class MyRadioGroup extends RadioGroup {

    private int checkId;
    private AlertDialog dialog;

    public MyRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        final EditText et = new EditText(context);
        et.setInputType(0x00000012);//键盘设置为密码键盘
        dialog = new AlertDialog.Builder(getContext())
                .setTitle("请输入二次密码")
                .setView(et)
                .setCancelable(false)
                .setPositiveButton("确定", (anInterface, i) -> {
                    if(et.getText().toString().trim().equals("556677")){
                        check(checkId);
                    }else {
                        ToastManager.toast("二次密码错误");
                    }
                    et.setText("");
                    hide_keyboard_from(context,et);
                })
                .setNegativeButton("取消", (anInterface, i) -> {
                    et.setText("");
                    hide_keyboard_from(context, et);
                })
                .create();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int count = getChildCount();
        for (int i=1;i<count;i=i+2) {
            RadioButton child = (RadioButton) getChildAt(i);
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getTop();
            int bottom = child.getBottom();
            if (x < right && x > left && y > top && y < bottom) {
                checkId = child.getId();
                break;
            }
        }
        if (getCheckedRadioButtonId() != checkId) {
            dialog.show();
        }
        return true;
    }

    public void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}