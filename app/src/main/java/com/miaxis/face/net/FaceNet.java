package com.miaxis.face.net;

import com.miaxis.face.bean.Advertisement;
import com.miaxis.face.bean.DaheResponse;
import com.miaxis.face.bean.DaheResponseEntity;
import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.UpdateData;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FaceNet {

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST()
    Call<DaheResponseEntity> uploadRecord(@Url String url, @Body RequestBody body);

    @POST
    Call<ResponseEntity<UpdateData>> downUpdateVersion(@Url String url);

    @POST
    Call<ResponseEntity<List<Advertisement>>> downImageList(@Url String url);

}
