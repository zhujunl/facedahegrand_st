package com.miaxis.face.bean;

import android.graphics.Bitmap;

public class Task {

    private String taskid;
    private String tasktype;
    private String taskparam;

    private Bitmap cardBitmap;
    private byte[] cardFeatureCache;

    public Task() {
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public String getTaskparam() {
        return taskparam;
    }

    public void setTaskparam(String taskparam) {
        this.taskparam = taskparam;
    }

    public Bitmap getCardBitmap() {
        return cardBitmap;
    }

    public void setCardBitmap(Bitmap cardBitmap) {
        this.cardBitmap = cardBitmap;
    }

    public byte[] getCardFeatureCache() {
        return cardFeatureCache;
    }

    public void setCardFeatureCache(byte[] cardFeatureCache) {
        this.cardFeatureCache = cardFeatureCache;
    }
}
