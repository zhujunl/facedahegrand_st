package com.miaxis.face.bean;

public class UpdateData {

    private String version;
    private String url;

    public UpdateData(String version, String url) {
        this.version = version;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
