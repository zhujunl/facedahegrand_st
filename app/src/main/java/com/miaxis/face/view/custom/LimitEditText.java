package com.miaxis.face.view.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;


public class LimitEditText extends android.support.v7.widget.AppCompatEditText {

    private boolean tage=false;
    private Context context;
    private Tage ta;
    private LimitEditText view;
    private String string="";

    public LimitEditText(Context context) {
        super(context);
    }

    public LimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;


    }

    /**
     * 输入法
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {

        return new mInputConnecttion(super.onCreateInputConnection(outAttrs),
                false);
    }


    private int count;
    class mInputConnecttion extends InputConnectionWrapper implements
            InputConnection {

        public mInputConnecttion(InputConnection target, boolean mutable) {
            super(target, mutable);
        }


        /**
         * 对输入的内容进行拦截
         *
         * @param text
         * @param newCursorPosition
         * @return
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            ta.setDialog(!getTage(), view);
            if(!getTage()) text=getString();
            return getTage()?super.commitText(text, newCursorPosition):getTage();
        }


        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if(event.getAction()==KeyEvent.ACTION_UP){
                ta.setDialog(!getTage(), view);
            }
            return getTage() ?super.sendKeyEvent(event):getTage();
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            ta.setDialog(!getTage(),view);
            return getTage() ? super.deleteSurroundingText(beforeLength, afterLength):getTage();
        }



        @Override
        public boolean setSelection(int start, int end) {

            return super.setSelection(start, end);
        }

    }

   public int editflag=0;
    public void setTageListener(Tage ta, LimitEditText view){
        this.ta=ta;
        this.view=view;
        setString(view.getText().toString().trim());
        this.view.addTextChangedListener(textWatcher);
    }
    private int ss=0;
    private int w=0;

    public TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            ss=count;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            w=count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            editflag++;
            if(ss!=w) {
                if (editflag==1){
                    ta.setDialog(!getTage(), view);
                }
            }else {
                editflag=0;
            }
        }
    };


    public void setTage(boolean tage){
        this.tage=tage;
    }

    public boolean getTage(){
        return tage;
    }

    public interface Tage {
        void setDialog(boolean bo, LimitEditText view);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
