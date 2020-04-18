package com.miaxis.face.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Undocumented implements Serializable {

    private static final long serialVersionUID = 2228713457659478253L;

    private String name;
    private String cardNumber;
    private String nation;
    private Bitmap faceImage;
    private String gatherFingerprint1;
    private Bitmap gatherFingerprintBitmap1;
    private String gatherFingerprint2;
    private Bitmap gatherFingerprintBitmap2;

    public Undocumented() {
    }

    private Undocumented(Builder builder) {
        setName(builder.name);
        setCardNumber(builder.cardNumber);
        setNation(builder.nation);
        setFaceImage(builder.faceImage);
        setGatherFingerprint1(builder.gatherFingerprint1);
        setGatherFingerprintBitmap1(builder.gatherFingerprintBitmap1);
        setGatherFingerprint2(builder.gatherFingerprint2);
        setGatherFingerprintBitmap2(builder.gatherFingerprintBitmap2);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Bitmap getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(Bitmap faceImage) {
        this.faceImage = faceImage;
    }

    public String getGatherFingerprint1() {
        return gatherFingerprint1;
    }

    public void setGatherFingerprint1(String gatherFingerprint1) {
        this.gatherFingerprint1 = gatherFingerprint1;
    }

    public Bitmap getGatherFingerprintBitmap1() {
        return gatherFingerprintBitmap1;
    }

    public void setGatherFingerprintBitmap1(Bitmap gatherFingerprintBitmap1) {
        this.gatherFingerprintBitmap1 = gatherFingerprintBitmap1;
    }

    public String getGatherFingerprint2() {
        return gatherFingerprint2;
    }

    public void setGatherFingerprint2(String gatherFingerprint2) {
        this.gatherFingerprint2 = gatherFingerprint2;
    }

    public Bitmap getGatherFingerprintBitmap2() {
        return gatherFingerprintBitmap2;
    }

    public void setGatherFingerprintBitmap2(Bitmap gatherFingerprintBitmap2) {
        this.gatherFingerprintBitmap2 = gatherFingerprintBitmap2;
    }

    public static final class Builder {
        private String name;
        private String cardNumber;
        private String nation;
        private Bitmap faceImage;
        private String gatherFingerprint1;
        private Bitmap gatherFingerprintBitmap1;
        private String gatherFingerprint2;
        private Bitmap gatherFingerprintBitmap2;

        public Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder cardNumber(String val) {
            cardNumber = val;
            return this;
        }

        public Builder nation(String val) {
            nation = val;
            return this;
        }

        public Builder faceImage(Bitmap val) {
            faceImage = val;
            return this;
        }

        public Builder gatherFingerprint1(String val) {
            gatherFingerprint1 = val;
            return this;
        }

        public Builder gatherFingerprintBitmap1(Bitmap val) {
            gatherFingerprintBitmap1 = val;
            return this;
        }

        public Builder gatherFingerprint2(String val) {
            gatherFingerprint2 = val;
            return this;
        }

        public Builder gatherFingerprintBitmap2(Bitmap val) {
            gatherFingerprintBitmap2 = val;
            return this;
        }

        public Undocumented build() {
            return new Undocumented(this);
        }
    }
}
