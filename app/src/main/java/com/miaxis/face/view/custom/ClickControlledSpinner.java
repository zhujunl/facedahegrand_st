package com.miaxis.face.view.custom;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ClickControlledSpinner extends android.support.v7.widget.AppCompatSpinner {

    private boolean dialog=true;

    public ClickControlledSpinner(Context context) {
        super(context);
    }

    public ClickControlledSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ClickControlledSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isMoved = false;
    private Point touchedPoint = new Point();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchedPoint.x = x;
                touchedPoint.y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    // 从上向下滑动
                    if (y - touchedPoint.y > 20) {
                    }
                    // 从下向上滑动
                    else if (touchedPoint.y - y > 20) {
                    }
                    // 滑动幅度小时，当作点击事件
                    else {
                        onClickMyListener.onClick();
                    }
                    isMoved = false;
                } else {
                    onClickMyListener.onClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            setDialog(true);
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            setDialog(true);
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    public void setDialog(boolean booean){
        dialog=booean;
    }

    public boolean getDialog(){
        return dialog;
    }


    private OnClickMyListener onClickMyListener;


    public void setOnClickMyListener(OnClickMyListener onClickMyListener) {
        this.onClickMyListener = onClickMyListener;
    }

    public interface OnClickMyListener {
         void onClick() ;
    }
}