package com.miaxis.face.net;

import com.miaxis.face.bean.Advertisement;
import com.miaxis.face.bean.ResponseEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AdvertiseNet {

    @POST
    Observable<ResponseEntity<List<Advertisement>>> downImageList(@Url String url);

}
