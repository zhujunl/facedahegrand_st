package com.miaxis.face.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * @author Tank
 * @date 2021/7/26 5:29 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CameraTextureView extends TextureView {

    private static final String TAG = "CameraTextureView";
    //宽高比列
    private float mRatio = 1F;

    public CameraTextureView(Context context) {
        super(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //        @SuppressLint("CustomViewStyleable")
        //        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.preview_view);
        //        this.mRatio = array.getFloat(R.styleable.preview_view_ratio, 1F);
        //        array.recycle();
    }

    private Size mSize;
    public void setRawPreviewSize(Size size) {
        mSize=size;
        if (size != null && size.isLegal()) {
            //this.mRatio = size.width / (size.height * 1.0F);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mSize != null && mSize.isLegal()) {
            //是否宽大于高
            boolean widthIsMax=mSize.width>mSize.height;
            if (widthIsMax){
                //宽大于高
                mRatio=1F*width/mSize.width;
            }else {
                //高大于等于宽
                mRatio=1F*height/mSize.height;
            }
        }
        setMeasuredDimension(width, (int) (mRatio*mSize.height));
        setTranslationY(-((mRatio*mSize.height) - height) / 2F);
    }


    public static class Size {
        public final int width;
        public final int height;

        /**
         * @param width  水平方向长度
         * @param height 竖直方向长度
         */
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public boolean isLegal() {
            return this.width > 0 && this.height > 0
                    && (Math.max(this.width, this.height) / Math.min(this.width, this.height) < 2);
        }

        @Override
        public String toString() {
            return "Size{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }
    }

}
