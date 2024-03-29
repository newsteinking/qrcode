package com.seanlab.qrcode.mlkit.ghost.auth;

import androidx.annotation.NonNull;

import com.seanlab.qrcode.mlkit.ghost.network.ApiProvider;
import com.seanlab.qrcode.mlkit.ghost.network.GhostApiService;
import com.seanlab.qrcode.mlkit.ghost.network.GhostApiUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ProductionApiProvider implements ApiProvider {

    private final Retrofit mRetrofit;

    public ProductionApiProvider(@NonNull OkHttpClient httpClient, @NonNull String blogUrl) {
        mRetrofit = GhostApiUtils.INSTANCE.getRetrofit(blogUrl, httpClient);
    }

    @Override
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    @Override
    public GhostApiService getGhostApi() {
        return mRetrofit.create(GhostApiService.class);
    }

}
