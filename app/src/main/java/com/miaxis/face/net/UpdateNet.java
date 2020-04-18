package com.miaxis.face.net;

import com.miaxis.face.bean.ResponseEntity;
import com.miaxis.face.bean.UpdateData;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UpdateNet {

    @POST
    Observable<ResponseEntity<UpdateData>> downUpdateVersion(@Url String url);

}
