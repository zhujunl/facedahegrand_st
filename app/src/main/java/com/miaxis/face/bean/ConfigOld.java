package com.miaxis.face.bean;


public class ConfigOld {
    private long id;
    private String host;
    private boolean resultFlag;
    private boolean documentFlag;
    private String upTime;
    private float  passScore;
    private String banner;
    private int intervalTime;
    private String orgId;
    private String orgName;
    private boolean netFlag;
    private boolean queryFlag;
    private String password;
    private int verifyMode;
    private boolean whiteFlag;      // 是否启用白名单验证
    private boolean blackFlag;      // 是否启用黑名单验证
    private Boolean advertiseFlag;  //是否启用广告
    private Integer advertiseDelayTime; //广告显示延迟
    private String advertisementUrl;
    private Integer advertisementMode;
    private String updateUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(boolean resultFlag) {
        this.resultFlag = resultFlag;
    }

    public boolean isDocumentFlag() {
        return documentFlag;
    }

    public void setDocumentFlag(boolean documentFlag) {
        this.documentFlag = documentFlag;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public float getPassScore() {
        return passScore;
    }

    public void setPassScore(float passScore) {
        this.passScore = passScore;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public boolean isNetFlag() {
        return netFlag;
    }

    public void setNetFlag(boolean netFlag) {
        this.netFlag = netFlag;
    }

    public boolean isQueryFlag() {
        return queryFlag;
    }

    public void setQueryFlag(boolean queryFlag) {
        this.queryFlag = queryFlag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }

    public boolean isWhiteFlag() {
        return whiteFlag;
    }

    public void setWhiteFlag(boolean whiteFlag) {
        this.whiteFlag = whiteFlag;
    }

    public boolean isBlackFlag() {
        return blackFlag;
    }

    public void setBlackFlag(boolean blackFlag) {
        this.blackFlag = blackFlag;
    }

    public Boolean getAdvertiseFlag() {
        return advertiseFlag;
    }

    public void setAdvertiseFlag(Boolean advertiseFlag) {
        this.advertiseFlag = advertiseFlag;
    }

    public Integer getAdvertiseDelayTime() {
        return advertiseDelayTime;
    }

    public void setAdvertiseDelayTime(Integer advertiseDelayTime) {
        this.advertiseDelayTime = advertiseDelayTime;
    }

    public String getAdvertisementUrl() {
        return advertisementUrl;
    }

    public void setAdvertisementUrl(String advertisementUrl) {
        this.advertisementUrl = advertisementUrl;
    }

    public Integer getAdvertisementMode() {
        return advertisementMode;
    }

    public void setAdvertisementMode(Integer advertisementMode) {
        this.advertisementMode = advertisementMode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    @Override
    public String toString() {
        return "ConfigOld{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", resultFlag=" + resultFlag +
                ", documentFlag=" + documentFlag +
                ", upTime='" + upTime + '\'' +
                ", passScore=" + passScore +
                ", banner='" + banner + '\'' +
                ", intervalTime=" + intervalTime +
                ", orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", netFlag=" + netFlag +
                ", queryFlag=" + queryFlag +
                ", password='" + password + '\'' +
                ", verifyMode=" + verifyMode +
                ", whiteFlag=" + whiteFlag +
                ", blackFlag=" + blackFlag +
                ", advertiseFlag=" + advertiseFlag +
                ", advertiseDelayTime=" + advertiseDelayTime +
                ", advertisementUrl='" + advertisementUrl + '\'' +
                ", advertisementMode=" + advertisementMode +
                ", updateUrl='" + updateUrl + '\'' +
                '}';
    }
}
