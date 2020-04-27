package com.miaxis.face.bean;

import java.util.List;

public class ResponseEntity<T> {
    private String code;
    private String message;
    private T data;

    public ResponseEntity() {
    }

    public ResponseEntity(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseEntity(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
