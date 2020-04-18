package com.miaxis.face.net;

import com.miaxis.face.bean.Advertisement;
import com.miaxis.face.bean.DaheResponse;
import com.miaxis.face.bean.DaheResponseEntity;
import com.miaxis.face.bean.ResponseEntity;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

public class FaceNetApi extends BaseAPI {

    private static FaceNet getFaceNet() {
        return getRetrofit().create(FaceNet.class);
    }

    public static Call<DaheResponseEntity> uploadRecord(String url, String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        return getFaceNet().uploadRecord(url, body);
    }

    public static Call<ResponseEntity<List<Advertisement>>> downImageList(String url) {
        return getFaceNet().downImageList(url);
    }

}
