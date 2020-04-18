package com.miaxis.face.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18 0018.
 */
@Entity
public class Config implements Serializable {

    public static final int MODE_FACE_ONLY = 0;
    public static final int MODE_FINGER_ONLY = 1;
    public static final int MODE_ONE_FACE_FIRST = 2;
    public static final int MODE_ONE_FINGER_FIRST = 3;
    public static final int MODE_TWO_FACE_FIRST = 4;
    public static final int MODE_TWO_FINGER_FIRST = 5;
    public static final int MODE_LOCAL_FEATURE = 6;

    private static final long serialVersionUID = 1L;

    @Id
    private long id;
    private String updateUrl;
    private String uploadRecordUrl1;
    private String uploadRecordUrl2;
    private String advertisementUrl;

    private String deviceSerialNumber;
    private String account;
    private String clientId;
    private boolean encrypt;

    private int verifyMode;
    private boolean netFlag;
    private boolean resultFlag;
    private boolean saveLocalFlag;
    private boolean documentFlag;
    private boolean livenessFlag;
    private boolean queryFlag;
    private boolean whiteFlag;
    private boolean blackFlag;
    private int gatherFingerFlag;
    private boolean advertiseFlag;
    private int advertisementMode;

    private float verifyScore;
    private float maskVerifyScore;
    private int qualityScore;
    private int livenessQualityScore;
    private int maskScore;

    private String titleStr;
    private String password;

    private String upTime;
    private int intervalTime;
    private String orgName;
    private int advertiseDelayTime;
    @Generated(hash = 2091216551)
    public Config(long id, String updateUrl, String uploadRecordUrl1,
            String uploadRecordUrl2, String advertisementUrl,
            String deviceSerialNumber, String account, String clientId,
            boolean encrypt, int verifyMode, boolean netFlag, boolean resultFlag,
            boolean saveLocalFlag, boolean documentFlag, boolean livenessFlag,
            boolean queryFlag, boolean whiteFlag, boolean blackFlag,
            int gatherFingerFlag, boolean advertiseFlag, int advertisementMode,
            float verifyScore, float maskVerifyScore, int qualityScore,
            int livenessQualityScore, int maskScore, String titleStr,
            String password, String upTime, int intervalTime, String orgName,
            int advertiseDelayTime) {
        this.id = id;
        this.updateUrl = updateUrl;
        this.uploadRecordUrl1 = uploadRecordUrl1;
        this.uploadRecordUrl2 = uploadRecordUrl2;
        this.advertisementUrl = advertisementUrl;
        this.deviceSerialNumber = deviceSerialNumber;
        this.account = account;
        this.clientId = clientId;
        this.encrypt = encrypt;
        this.verifyMode = verifyMode;
        this.netFlag = netFlag;
        this.resultFlag = resultFlag;
        this.saveLocalFlag = saveLocalFlag;
        this.documentFlag = documentFlag;
        this.livenessFlag = livenessFlag;
        this.queryFlag = queryFlag;
        this.whiteFlag = whiteFlag;
        this.blackFlag = blackFlag;
        this.gatherFingerFlag = gatherFingerFlag;
        this.advertiseFlag = advertiseFlag;
        this.advertisementMode = advertisementMode;
        this.verifyScore = verifyScore;
        this.maskVerifyScore = maskVerifyScore;
        this.qualityScore = qualityScore;
        this.livenessQualityScore = livenessQualityScore;
        this.maskScore = maskScore;
        this.titleStr = titleStr;
        this.password = password;
        this.upTime = upTime;
        this.intervalTime = intervalTime;
        this.orgName = orgName;
        this.advertiseDelayTime = advertiseDelayTime;
    }
    @Generated(hash = 589037648)
    public Config() {
    }

    private Config(Builder builder) {
        setId(builder.id);
        setUpdateUrl(builder.updateUrl);
        setUploadRecordUrl1(builder.uploadRecordUrl1);
        setUploadRecordUrl2(builder.uploadRecordUrl2);
        setAdvertisementUrl(builder.advertisementUrl);
        setDeviceSerialNumber(builder.deviceSerialNumber);
        setAccount(builder.account);
        setClientId(builder.clientId);
        setEncrypt(builder.encrypt);
        setVerifyMode(builder.verifyMode);
        setNetFlag(builder.netFlag);
        setResultFlag(builder.resultFlag);
        setSaveLocalFlag(builder.saveLocalFlag);
        setDocumentFlag(builder.documentFlag);
        setLivenessFlag(builder.livenessFlag);
        setQueryFlag(builder.queryFlag);
        setWhiteFlag(builder.whiteFlag);
        setBlackFlag(builder.blackFlag);
        setGatherFingerFlag(builder.gatherFingerFlag);
        setAdvertiseFlag(builder.advertiseFlag);
        setAdvertisementMode(builder.advertisementMode);
        setVerifyScore(builder.verifyScore);
        setMaskVerifyScore(builder.maskVerifyScore);
        setQualityScore(builder.qualityScore);
        setLivenessQualityScore(builder.livenessQualityScore);
        setMaskScore(builder.maskScore);
        setTitleStr(builder.titleStr);
        setPassword(builder.password);
        setUpTime(builder.upTime);
        setIntervalTime(builder.intervalTime);
        setOrgName(builder.orgName);
        setAdvertiseDelayTime(builder.advertiseDelayTime);
    }

    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUpdateUrl() {
        return this.updateUrl;
    }
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }
    public String getUploadRecordUrl1() {
        return this.uploadRecordUrl1;
    }
    public void setUploadRecordUrl1(String uploadRecordUrl1) {
        this.uploadRecordUrl1 = uploadRecordUrl1;
    }
    public String getUploadRecordUrl2() {
        return this.uploadRecordUrl2;
    }
    public void setUploadRecordUrl2(String uploadRecordUrl2) {
        this.uploadRecordUrl2 = uploadRecordUrl2;
    }
    public String getAdvertisementUrl() {
        return this.advertisementUrl;
    }
    public void setAdvertisementUrl(String advertisementUrl) {
        this.advertisementUrl = advertisementUrl;
    }
    public String getDeviceSerialNumber() {
        return this.deviceSerialNumber;
    }
    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getClientId() {
        return this.clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public boolean getEncrypt() {
        return this.encrypt;
    }
    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }
    public int getVerifyMode() {
        return this.verifyMode;
    }
    public void setVerifyMode(int verifyMode) {
        this.verifyMode = verifyMode;
    }
    public boolean getNetFlag() {
        return this.netFlag;
    }
    public void setNetFlag(boolean netFlag) {
        this.netFlag = netFlag;
    }
    public boolean getResultFlag() {
        return this.resultFlag;
    }
    public void setResultFlag(boolean resultFlag) {
        this.resultFlag = resultFlag;
    }
    public boolean getSaveLocalFlag() {
        return this.saveLocalFlag;
    }
    public void setSaveLocalFlag(boolean saveLocalFlag) {
        this.saveLocalFlag = saveLocalFlag;
    }
    public boolean getDocumentFlag() {
        return this.documentFlag;
    }
    public void setDocumentFlag(boolean documentFlag) {
        this.documentFlag = documentFlag;
    }
    public boolean getLivenessFlag() {
        return this.livenessFlag;
    }
    public void setLivenessFlag(boolean livenessFlag) {
        this.livenessFlag = livenessFlag;
    }
    public boolean getQueryFlag() {
        return this.queryFlag;
    }
    public void setQueryFlag(boolean queryFlag) {
        this.queryFlag = queryFlag;
    }
    public boolean getWhiteFlag() {
        return this.whiteFlag;
    }
    public void setWhiteFlag(boolean whiteFlag) {
        this.whiteFlag = whiteFlag;
    }
    public boolean getBlackFlag() {
        return this.blackFlag;
    }
    public void setBlackFlag(boolean blackFlag) {
        this.blackFlag = blackFlag;
    }
    public int getGatherFingerFlag() {
        return this.gatherFingerFlag;
    }
    public void setGatherFingerFlag(int gatherFingerFlag) {
        this.gatherFingerFlag = gatherFingerFlag;
    }
    public boolean getAdvertiseFlag() {
        return this.advertiseFlag;
    }
    public void setAdvertiseFlag(boolean advertiseFlag) {
        this.advertiseFlag = advertiseFlag;
    }
    public int getAdvertisementMode() {
        return this.advertisementMode;
    }
    public void setAdvertisementMode(int advertisementMode) {
        this.advertisementMode = advertisementMode;
    }
    public float getVerifyScore() {
        return this.verifyScore;
    }
    public void setVerifyScore(float verifyScore) {
        this.verifyScore = verifyScore;
    }
    public float getMaskVerifyScore() {
        return this.maskVerifyScore;
    }
    public void setMaskVerifyScore(float maskVerifyScore) {
        this.maskVerifyScore = maskVerifyScore;
    }
    public int getQualityScore() {
        return this.qualityScore;
    }
    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }
    public int getLivenessQualityScore() {
        return this.livenessQualityScore;
    }
    public void setLivenessQualityScore(int livenessQualityScore) {
        this.livenessQualityScore = livenessQualityScore;
    }
    public int getMaskScore() {
        return this.maskScore;
    }
    public void setMaskScore(int maskScore) {
        this.maskScore = maskScore;
    }
    public String getTitleStr() {
        return this.titleStr;
    }
    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUpTime() {
        return this.upTime;
    }
    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }
    public int getIntervalTime() {
        return this.intervalTime;
    }
    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }
    public String getOrgName() {
        return this.orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public int getAdvertiseDelayTime() {
        return this.advertiseDelayTime;
    }
    public void setAdvertiseDelayTime(int advertiseDelayTime) {
        this.advertiseDelayTime = advertiseDelayTime;
    }

    public static final class Builder {
        private long id;
        private String updateUrl;
        private String uploadRecordUrl1;
        private String uploadRecordUrl2;
        private String advertisementUrl;
        private String deviceSerialNumber;
        private String account;
        private String clientId;
        private boolean encrypt;
        private int verifyMode;
        private boolean netFlag;
        private boolean resultFlag;
        private boolean saveLocalFlag;
        private boolean documentFlag;
        private boolean livenessFlag;
        private boolean queryFlag;
        private boolean whiteFlag;
        private boolean blackFlag;
        private int gatherFingerFlag;
        private boolean advertiseFlag;
        private int advertisementMode;
        private float verifyScore;
        private float maskVerifyScore;
        private int qualityScore;
        private int livenessQualityScore;
        private int maskScore;
        private String titleStr;
        private String password;
        private String upTime;
        private int intervalTime;
        private String orgName;
        private int advertiseDelayTime;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder updateUrl(String val) {
            updateUrl = val;
            return this;
        }

        public Builder uploadRecordUrl1(String val) {
            uploadRecordUrl1 = val;
            return this;
        }

        public Builder uploadRecordUrl2(String val) {
            uploadRecordUrl2 = val;
            return this;
        }

        public Builder advertisementUrl(String val) {
            advertisementUrl = val;
            return this;
        }

        public Builder deviceSerialNumber(String val) {
            deviceSerialNumber = val;
            return this;
        }

        public Builder account(String val) {
            account = val;
            return this;
        }

        public Builder clientId(String val) {
            clientId = val;
            return this;
        }

        public Builder encrypt(boolean val) {
            encrypt = val;
            return this;
        }

        public Builder verifyMode(int val) {
            verifyMode = val;
            return this;
        }

        public Builder netFlag(boolean val) {
            netFlag = val;
            return this;
        }

        public Builder resultFlag(boolean val) {
            resultFlag = val;
            return this;
        }

        public Builder saveLocalFlag(boolean val) {
            saveLocalFlag = val;
            return this;
        }

        public Builder documentFlag(boolean val) {
            documentFlag = val;
            return this;
        }

        public Builder livenessFlag(boolean val) {
            livenessFlag = val;
            return this;
        }

        public Builder queryFlag(boolean val) {
            queryFlag = val;
            return this;
        }

        public Builder whiteFlag(boolean val) {
            whiteFlag = val;
            return this;
        }

        public Builder blackFlag(boolean val) {
            blackFlag = val;
            return this;
        }

        public Builder gatherFingerFlag(int val) {
            gatherFingerFlag = val;
            return this;
        }

        public Builder advertiseFlag(boolean val) {
            advertiseFlag = val;
            return this;
        }

        public Builder advertisementMode(int val) {
            advertisementMode = val;
            return this;
        }

        public Builder verifyScore(float val) {
            verifyScore = val;
            return this;
        }

        public Builder maskVerifyScore(float val) {
            maskVerifyScore = val;
            return this;
        }

        public Builder qualityScore(int val) {
            qualityScore = val;
            return this;
        }

        public Builder livenessQualityScore(int val) {
            livenessQualityScore = val;
            return this;
        }

        public Builder maskScore(int val) {
            maskScore = val;
            return this;
        }

        public Builder titleStr(String val) {
            titleStr = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder upTime(String val) {
            upTime = val;
            return this;
        }

        public Builder intervalTime(int val) {
            intervalTime = val;
            return this;
        }

        public Builder orgName(String val) {
            orgName = val;
            return this;
        }

        public Builder advertiseDelayTime(int val) {
            advertiseDelayTime = val;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
