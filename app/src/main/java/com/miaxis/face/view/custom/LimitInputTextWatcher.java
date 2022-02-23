package com.miaxis.face.view.custom;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.miaxis.face.manager.ToastManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by junweiliu on 17/1/6.
 */
public class LimitInputTextWatcher implements TextWatcher {
    private boolean mIsMatch;
    private CharSequence mResult;
    private int mSelectionStart;
    private int mSelectionEnd;
    private EditText mPswEditText;
    public LimitInputTextWatcher() {};
    private boolean repalce=false;
    public LimitInputTextWatcher(EditText editText) {
        mPswEditText = editText;
    };

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        CharSequence charSequence = "";
        if ((mSelectionStart + count) <= s.length()) {
            charSequence = s.subSequence(mSelectionStart, mSelectionStart + count);
        }
        if (!mIsMatch) {
            mIsMatch = pswFilter(charSequence);
            String temp = s.toString();
            mResult = temp.replace(charSequence, "");
            mSelectionEnd =mIsMatch? start+count:start;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        mSelectionStart = mPswEditText.getSelectionStart();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!mIsMatch) {
            mIsMatch = true;
            mPswEditText.setText(mResult);
            mPswEditText.setSelection(mSelectionEnd);
        }
        ss(s);
        mIsMatch = false;
    }

    public void ss(Editable s){
        String st=s.toString();
        String regex = "[//，//。//、//；//‘//’//【//】//《//》//？//：//“//”//{//}//（//）]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (repalce){
            mPswEditText.removeTextChangedListener(this);
            st=st.replace("，",",");
            st=st.replace("。",".");
            st=st.replace("、","/");
            st=st.replace("？","?");
            st=st.replace("》",">");
            st=st.replace("《","<");
            st=st.replace("“","\"");
            st=st.replace("”","\"");
            st=st.replace("’","'");
            st=st.replace("‘","'");
            st=st.replace("；",";");
            st=st.replace("：",":");
            st=st.replace("】","[");
            st=st.replace("【","]");
            st=st.replace("}","{");
            st=st.replace("{","}");
            st=st.replace("（","(");
            st=st.replace("）",")");
            st=st.replace("！","!");
            st=st.replace("——","_");
            mPswEditText.setText(st);
            mPswEditText.setSelection(mSelectionEnd);
            mPswEditText.addTextChangedListener(this);
            repalce=false;
        }
    }

    private boolean pswFilter(CharSequence s) {
        if (TextUtils.isEmpty(s)) {
            return true;
        }
        String regex = "[A-Z0-9a-z!@#$%^&*.~///{//}|()'/\"?><,.`//+-=_//[//]:;]+";
//          String regex = "[A-Z0-9a-z!@#$%^&*.~///{//}|()'/\"?><,.`//+-=_//[//]:;//，//。//、//；//‘//’//【//】//《//》//？//：//“//”//{//}//（//）]+";
        String regex2 = "[//——//！//，//。//、//；//‘//’//【//】//《//》//？//：//“//”//{//}//（//）]+";
//        String regex = "[A-Z0-9a-z]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            return true;
        }else {
            pattern=Pattern.compile(regex2);
            matcher=pattern.matcher(s);
            if(matcher.matches()){
                repalce=true;
                return true;
            }else {
                ToastManager.toast("请输入英文字母或符号");
                return false;
            }
        }
    }
}