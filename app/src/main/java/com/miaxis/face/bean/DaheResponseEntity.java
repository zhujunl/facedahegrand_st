package com.miaxis.face.bean;

public class DaheResponseEntity {

    private int errCode;
    private String errMsg;
    private boolean playVoice;
    private String voiceText;

    public DaheResponseEntity() {
    }

    public DaheResponseEntity(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public DaheResponseEntity(int errCode, String errMsg, boolean playVoice, String voiceText) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.playVoice = playVoice;
        this.voiceText = voiceText;
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

    public boolean isPlayVoice() {
        return playVoice;
    }

    public void setPlayVoice(boolean playVoice) {
        this.playVoice = playVoice;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }
}
