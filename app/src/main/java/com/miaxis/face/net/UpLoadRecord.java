package com.miaxis.face.net;


import com.miaxis.face.bean.DaheResponse;
import com.miaxis.face.bean.ResponseEntity;

import org.json.JSONObject;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/5/23 0023.
 */

public interface UpLoadRecord {

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST()
    Observable<DaheResponse> post(@Url String url, @Body RequestBody body);
}
