package com.miaxis.face.bean;

public class RecordDto {

    private String name;
    private String enname;
    private String paperType;
    private String number;
    private String sex;
    private String folk;
    private String country;
    private String address;
    private String birthday;
    private String signOrgan;
    private String valid;
    private String idCard;
    private String passNumber;
    private String changeNum;
    private String headImage;
    private String snapshot;
    private String similarity;
    private String isPass;
    private String fpFeatrue1;
    private String fpFeatrue2;
    private String sceneFP1;
    private String sceneFP2;
    private String fpImage1;
    private String fpImage2;
    private String deviceSN;

    public RecordDto() {
    }

    private RecordDto(Builder builder) {
        setName(builder.name);
        setEnname(builder.enname);
        setPaperType(builder.paperType);
        setNumber(builder.number);
        setSex(builder.sex);
        setFolk(builder.folk);
        setCountry(builder.country);
        setAddress(builder.address);
        setBirthday(builder.birthday);
        setSignOrgan(builder.signOrgan);
        setValid(builder.valid);
        setIdCard(builder.idCard);
        setPassNumber(builder.passNumber);
        setChangeNum(builder.changeNum);
        setHeadImage(builder.headImage);
        setSnapshot(builder.snapshot);
        setSimilarity(builder.similarity);
        setIsPass(builder.isPass);
        setFpFeatrue1(builder.fpFeatrue1);
        setFpFeatrue2(builder.fpFeatrue2);
        setSceneFP1(builder.sceneFP1);
        setSceneFP2(builder.sceneFP2);
        setFpImage1(builder.fpImage1);
        setFpImage2(builder.fpImage2);
        setDeviceSN(builder.deviceSN);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFolk() {
        return folk;
    }

    public void setFolk(String folk) {
        this.folk = folk;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignOrgan() {
        return signOrgan;
    }

    public void setSignOrgan(String signOrgan) {
        this.signOrgan = signOrgan;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    public String getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(String changeNum) {
        this.changeNum = changeNum;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    public String getIsPass() {
        return isPass;
    }

    public void setIsPass(String isPass) {
        this.isPass = isPass;
    }

    public String getFpFeatrue1() {
        return fpFeatrue1;
    }

    public void setFpFeatrue1(String fpFeatrue1) {
        this.fpFeatrue1 = fpFeatrue1;
    }

    public String getFpFeatrue2() {
        return fpFeatrue2;
    }

    public void setFpFeatrue2(String fpFeatrue2) {
        this.fpFeatrue2 = fpFeatrue2;
    }

    public String getSceneFP1() {
        return sceneFP1;
    }

    public void setSceneFP1(String sceneFP1) {
        this.sceneFP1 = sceneFP1;
    }

    public String getSceneFP2() {
        return sceneFP2;
    }

    public void setSceneFP2(String sceneFP2) {
        this.sceneFP2 = sceneFP2;
    }

    public String getFpImage1() {
        return fpImage1;
    }

    public void setFpImage1(String fpImage1) {
        this.fpImage1 = fpImage1;
    }

    public String getFpImage2() {
        return fpImage2;
    }

    public void setFpImage2(String fpImage2) {
        this.fpImage2 = fpImage2;
    }

    public String getDeviceSN() {
        return deviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        this.deviceSN = deviceSN;
    }

    public static final class Builder {
        private String name;
        private String enname;
        private String paperType;
        private String number;
        private String sex;
        private String folk;
        private String country;
        private String address;
        private String birthday;
        private String signOrgan;
        private String valid;
        private String idCard;
        private String passNumber;
        private String changeNum;
        private String headImage;
        private String snapshot;
        private String similarity;
        private String isPass;
        private String fpFeatrue1;
        private String fpFeatrue2;
        private String sceneFP1;
        private String sceneFP2;
        private String fpImage1;
        private String fpImage2;
        private String deviceSN;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder enname(String val) {
            enname = val;
            return this;
        }

        public Builder paperType(String val) {
            paperType = val;
            return this;
        }

        public Builder number(String val) {
            number = val;
            return this;
        }

        public Builder sex(String val) {
            sex = val;
            return this;
        }

        public Builder folk(String val) {
            folk = val;
            return this;
        }

        public Builder country(String val) {
            country = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder birthday(String val) {
            birthday = val;
            return this;
        }

        public Builder signOrgan(String val) {
            signOrgan = val;
            return this;
        }

        public Builder valid(String val) {
            valid = val;
            return this;
        }

        public Builder idCard(String val) {
            idCard = val;
            return this;
        }

        public Builder passNumber(String val) {
            passNumber = val;
            return this;
        }

        public Builder changeNum(String val) {
            changeNum = val;
            return this;
        }

        public Builder headImage(String val) {
            headImage = val;
            return this;
        }

        public Builder snapshot(String val) {
            snapshot = val;
            return this;
        }

        public Builder similarity(String val) {
            similarity = val;
            return this;
        }

        public Builder isPass(String val) {
            isPass = val;
            return this;
        }

        public Builder fpFeatrue1(String val) {
            fpFeatrue1 = val;
            return this;
        }

        public Builder fpFeatrue2(String val) {
            fpFeatrue2 = val;
            return this;
        }

        public Builder sceneFP1(String val) {
            sceneFP1 = val;
            return this;
        }

        public Builder sceneFP2(String val) {
            sceneFP2 = val;
            return this;
        }

        public Builder fpImage1(String val) {
            fpImage1 = val;
            return this;
        }

        public Builder fpImage2(String val) {
            fpImage2 = val;
            return this;
        }

        public Builder deviceSN(String val) {
            deviceSN = val;
            return this;
        }

        public RecordDto build() {
            return new RecordDto(this);
        }
    }
}
