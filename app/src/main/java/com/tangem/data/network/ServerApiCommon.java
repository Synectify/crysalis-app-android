package com.tangem.data.network;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.tangem.App;
import com.tangem.tangemcard.data.network.TangemApi;
import com.tangem.tangemcard.data.network.model.CardVerifyAndGetInfo;
import com.tangem.data.network.model.RateInfoResponse;
import com.tangem.tangemcard.data.TangemCard;
import com.tangem.tangemcard.util.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerApiCommon {
    private static String TAG = ServerApiCommon.class.getSimpleName();

    /**
     * HTTP
     * Estimate fee
     */
    public static final int ESTIMATE_FEE_PRIORITY = 2;
    public static final int ESTIMATE_FEE_NORMAL = 3;
    public static final int ESTIMATE_FEE_MINIMAL = 6;
    private EstimateFeeListener estimateFeeListener;

    public interface EstimateFeeListener {
        void onSuccess(int blockCount, String estimateFeeResponse);
        void onFail(String message);
    }

    public void setEstimateFee(EstimateFeeListener listener) {
        estimateFeeListener = listener;
    }

    public void estimateFee(int blockCount) {
        EstimatefeeApi estimatefeeApi = App.getNetworkComponent().getRetrofitEstimatefee().create(EstimatefeeApi.class);

        Call<String> call;
        switch (blockCount) {
            case ESTIMATE_FEE_PRIORITY:
                call = estimatefeeApi.getEstimateFeePriority();
                break;

            case ESTIMATE_FEE_NORMAL:
                call = estimatefeeApi.getEstimateFeeNormal();
                break;

            case ESTIMATE_FEE_MINIMAL:
                call = estimatefeeApi.getEstimateFeeMinimal();
                break;

            default:
                call = estimatefeeApi.getEstimateFeeNormal();
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.code() == 200) {
                    estimateFeeListener.onSuccess(blockCount, response.body());
                    Log.i(TAG, "estimateFee         onResponse " + response.code() + "  " + response.body());
                } else
                    estimateFeeListener.onFail(response.body());
                Log.e(TAG, "estimateFee         onResponse " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                estimateFeeListener.onFail(t.getMessage());
                Log.e(TAG, "estimateFee onFailure " + t.getMessage());
            }
        });
    }

    /**
     * HTTP
     * Used in Crypto-currency course
     */
    private RateInfoDataListener rateInfoDataListener;

    public interface RateInfoDataListener {
        void onSuccess(RateInfoResponse rateInfoResponse);
    }

    public void setRateInfoData(RateInfoDataListener listener) {
        rateInfoDataListener = listener;
    }

    @SuppressLint("CheckResult")
    public void rateInfoData(String cryptoId) {
        CoinmarketApi coinmarketApi = App.getNetworkComponent().getRetrofitCoinmarketcap().create(CoinmarketApi.class);

        coinmarketApi.getRateInfoList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rateInfoModelList -> {
                            if (!rateInfoModelList.isEmpty()) {
                                for (RateInfoResponse rateInfoMode : rateInfoModelList) {
                                    if (rateInfoMode.getId().equals(cryptoId)) {
                                        rateInfoDataListener.onSuccess(rateInfoMode);
                                        Log.i(TAG, "rateInfoData        " + cryptoId + " onResponse " + "200");
                                    }
                                }
                            } else
                                Log.e(TAG, "rateInfoData        " + cryptoId + " onResponse " + "Empty");
                        },
                        // handle error
                        Throwable::printStackTrace);
    }

    /**
     * HTTP
     * Last version request from GitHub
     */
    private LastVersionListener lastVersionListener;

    public interface LastVersionListener {
        void onSuccess(String lastVersion);
    }

    public void setLastVersionListener(LastVersionListener listener) {
        lastVersionListener = listener;
    }

    public void requestLastVersion() {
        UpdateVersionApi updateVersionApi = App.getNetworkComponent().getRetrofitGithubusercontent().create(UpdateVersionApi.class);

        Call<ResponseBody> call = updateVersionApi.getLastVersion();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.i(TAG, "requestLastVersion  onResponse " + response.code());
                if (response.code() == 200) {
                    String stringResponse;
                    try {
                        stringResponse = response.body() != null ? response.body().string() : null;
                        lastVersionListener.onSuccess(stringResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "lastVersion onFailure " + t.getMessage());
            }
        });
    }

}