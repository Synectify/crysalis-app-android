package com.tangem.data.network;

import com.tangem.data.network.model.CardVerifyBody;
import com.tangem.data.network.model.CardVerifyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TangemApi {
    @Headers("Content-Type: application/json")
    @POST(Server.ApiTangem.Method.VERIFY)
    Call<CardVerifyResponse> getCardVerify(@Body CardVerifyBody body);
}