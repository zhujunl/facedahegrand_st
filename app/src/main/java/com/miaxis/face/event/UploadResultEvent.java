package com.miaxis.face.event;

public class UploadResultEvent {

    private boolean result;

    public UploadResultEvent(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
