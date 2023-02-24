package com.hakaninc.messengerapp.service;

import com.hakaninc.messengerapp.notifications.MyResponse;
import com.hakaninc.messengerapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAm0gPLXI:APA91bHtmOJzKdQv13SbEwHp8DXFIdEBkxrYHvIomEVdaBT37o6bJvMNeadlDQJi8sR0XMylaABIFAguvJiBgb_5FlhkITp48u3anX1kktj6JyOb5b3uXSl_jCJrJ2l8UcKvq__XL8Up"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
