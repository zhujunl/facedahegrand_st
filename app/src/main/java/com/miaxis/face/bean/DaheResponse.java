package com.miaxis.face.bean;

public class DaheResponse {

    private String code;
    private String msg;

    public DaheResponse() {
    }

    public DaheResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
