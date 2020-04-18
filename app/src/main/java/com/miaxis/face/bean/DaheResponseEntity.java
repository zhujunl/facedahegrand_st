package com.miaxis.face.bean;

public class DaheResponseEntity {

    private int errCode;
    private String errMsg;

    public DaheResponseEntity() {
    }

    public DaheResponseEntity(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
