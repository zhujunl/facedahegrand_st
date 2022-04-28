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
    private String uploadRecordUrl;
    private String advertisementUrl;

    private String deviceSerialNumber;
    private String account;
    private String clientId;
    private boolean encrypt;

    private int verifyMode;
    private boolean netFlag;
    private boolean resultFlag;
    private boolean sequelFlag;
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
    private int qualityScore;
    private int livenessQualityScore;

    private String titleStr;
    private String password;

    private String upTime;
    private int intervalTime;
    private String orgName;
    private int advertiseDelayTime;
    private Integer version_delay;
    private Integer version_position;

    private Float headerAngle;
    private Float headerDistance;

@Generated(hash = 1292882712)
public Config(long id, String updateUrl, String uploadRecordUrl, String advertisementUrl,
        String deviceSerialNumber, String account, String clientId, boolean encrypt,
        int verifyMode, boolean netFlag, boolean resultFlag, boolean sequelFlag,
        boolean saveLocalFlag, boolean documentFlag, boolean livenessFlag,
        boolean queryFlag, boolean whiteFlag, boolean blackFlag, int gatherFingerFlag,
        boolean advertiseFlag, int advertisementMode, float verifyScore, int qualityScore,
        int livenessQualityScore, String titleStr, String password, String upTime,
        int intervalTime, String orgName, int advertiseDelayTime, Integer version_delay,
        Integer version_position, Float headerAngle, Float headerDistance) {
    this.id = id;
    this.updateUrl = updateUrl;
    this.uploadRecordUrl = uploadRecordUrl;
    this.advertisementUrl = advertisementUrl;
    this.deviceSerialNumber = deviceSerialNumber;
    this.account = account;
    this.clientId = clientId;
    this.encrypt = encrypt;
    this.verifyMode = verifyMode;
    this.netFlag = netFlag;
    this.resultFlag = resultFlag;
    this.sequelFlag = sequelFlag;
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
    this.qualityScore = qualityScore;
    this.livenessQualityScore = livenessQualityScore;
    this.titleStr = titleStr;
    this.password = password;
    this.upTime = upTime;
    this.intervalTime = intervalTime;
    this.orgName = orgName;
    this.advertiseDelayTime = advertiseDelayTime;
    this.version_delay = version_delay;
    this.version_position = version_position;
    this.headerAngle = headerAngle;
    this.headerDistance = headerDistance;
}
    @Generated(hash = 589037648)
    public Config() {
    }
    private Config(Builder builder) {
        setId(builder.id);
        setUpdateUrl(builder.updateUrl);
        setUploadRecordUrl(builder.uploadRecordUrl);
        setAdvertisementUrl(builder.advertisementUrl);
        setDeviceSerialNumber(builder.deviceSerialNumber);
        setAccount(builder.account);
        setClientId(builder.clientId);
        setEncrypt(builder.encrypt);
        setVerifyMode(builder.verifyMode);
        setNetFlag(builder.netFlag);
        setResultFlag(builder.resultFlag);
        setSequelFlag(builder.sequelFlag);
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
        setQualityScore(builder.qualityScore);
        setLivenessQualityScore(builder.livenessQualityScore);
        setTitleStr(builder.titleStr);
        setPassword(builder.password);
        setUpTime(builder.upTime);
        setIntervalTime(builder.intervalTime);
        setOrgName(builder.orgName);
        setAdvertiseDelayTime(builder.advertiseDelayTime);
        setVersion_delay(builder.version_delay);
        setVersion_position(builder.version_position);
        setHeaderAngle(builder.headerAngle);
        setHeaderDistance(builder.headerDistance);
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
    public String getUploadRecordUrl() {
        return this.uploadRecordUrl;
    }
    public void setUploadRecordUrl(String uploadRecordUrl) {
        this.uploadRecordUrl = uploadRecordUrl;
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
    public boolean getSequelFlag() {
        return this.sequelFlag;
    }
    public void setSequelFlag(boolean sequelFlag) {
        this.sequelFlag = sequelFlag;
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

    public boolean isEncrypt() {
        return encrypt;
    }

    public boolean isNetFlag() {
        return netFlag;
    }

    public boolean isResultFlag() {
        return resultFlag;
    }

    public boolean isSequelFlag() {
        return sequelFlag;
    }

    public boolean isSaveLocalFlag() {
        return saveLocalFlag;
    }

    public boolean isDocumentFlag() {
        return documentFlag;
    }

    public boolean isLivenessFlag() {
        return livenessFlag;
    }

    public boolean isQueryFlag() {
        return queryFlag;
    }

    public boolean isWhiteFlag() {
        return whiteFlag;
    }

    public boolean isBlackFlag() {
        return blackFlag;
    }

    public boolean isAdvertiseFlag() {
        return advertiseFlag;
    }

    public Integer getVersion_delay() {
        return version_delay;
    }

    public void setVersion_delay(Integer version_delay) {
        this.version_delay = version_delay;
    }

    public Integer getVersion_position() {
        return version_position;
    }

    public void setVersion_position(Integer version_position) {
        this.version_position = version_position;
    }


    public Float getHeaderAngle() {
        return headerAngle;
    }

    public void setHeaderAngle(Float headerAngle) {
        this.headerAngle = headerAngle;
    }

    public Float getHeaderDistance() {
        return headerDistance;
    }

    public void setHeaderDistance(Float headerDistance) {
        this.headerDistance = headerDistance;
    }

    public static final class Builder {
        private long id;
        private String updateUrl;
        private String uploadRecordUrl;
        private String advertisementUrl;
        private String deviceSerialNumber;
        private String account;
        private String clientId;
        private boolean encrypt;
        private int verifyMode;
        private boolean netFlag;
        private boolean resultFlag;
        private boolean sequelFlag;
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
        private int qualityScore;
        private int livenessQualityScore;
        private String titleStr;
        private String password;
        private String upTime;
        private int intervalTime;
        private String orgName;
        private int advertiseDelayTime;
        private Integer version_delay;
        private Integer version_position;

        private Float headerAngle;
        private Float headerDistance;

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

        public Builder uploadRecordUrl(String val) {
            uploadRecordUrl = val;
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

        public Builder sequelFlag(boolean val) {
            sequelFlag = val;
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

        public Builder qualityScore(int val) {
            qualityScore = val;
            return this;
        }

        public Builder livenessQualityScore(int val) {
            livenessQualityScore = val;
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

        public Builder version_delay(Integer val) {
            this.version_delay = val;
            return this;
        }

        public Builder version_position(Integer val){
            this.version_position=val;
            return  this;
        }

        public Builder headerDistance(Float val){
            this.headerDistance=val;
            return this;
        }

        public Builder headerAngle(Float val){
            this.headerAngle=val;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", updateUrl='" + updateUrl + '\'' +
                ", uploadRecordUrl='" + uploadRecordUrl + '\'' +
                ", advertisementUrl='" + advertisementUrl + '\'' +
                ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
                ", account='" + account + '\'' +
                ", clientId='" + clientId + '\'' +
                ", encrypt=" + encrypt +
                ", verifyMode=" + verifyMode +
                ", netFlag=" + netFlag +
                ", resultFlag=" + resultFlag +
                ", sequelFlag=" + sequelFlag +
                ", saveLocalFlag=" + saveLocalFlag +
                ", documentFlag=" + documentFlag +
                ", livenessFlag=" + livenessFlag +
                ", queryFlag=" + queryFlag +
                ", whiteFlag=" + whiteFlag +
                ", blackFlag=" + blackFlag +
                ", gatherFingerFlag=" + gatherFingerFlag +
                ", advertiseFlag=" + advertiseFlag +
                ", advertisementMode=" + advertisementMode +
                ", verifyScore=" + verifyScore +
                ", qualityScore=" + qualityScore +
                ", livenessQualityScore=" + livenessQualityScore +
                ", titleStr='" + titleStr + '\'' +
                ", password='" + password + '\'' +
                ", upTime='" + upTime + '\'' +
                ", intervalTime=" + intervalTime +
                ", orgName='" + orgName + '\'' +
                ", advertiseDelayTime=" + advertiseDelayTime +
                ", version_delay=" + version_delay +
                ", version_position=" + version_position +
                ", headerAngle=" + headerAngle +
                ", headerDistance=" + headerDistance +
                '}';
    }
}
