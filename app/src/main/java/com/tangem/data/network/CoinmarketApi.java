package com.tangem.data.network;

import com.tangem.data.network.model.RateInfoModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface CoinmarketApi {
    @GET(Server.ApiCoinmarket.Method.V1_TICKER_CONVERT)
    Observable<List<RateInfoModel>> getRateInfoList();
}