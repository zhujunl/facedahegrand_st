package com.miaxis.face.event;

public class UndocumentedEvent {
    private String faceImage;

    public UndocumentedEvent(String faceImage) {
        this.faceImage = faceImage;
    }

    public String getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(String faceImage) {
        this.faceImage = faceImage;
    }
}
