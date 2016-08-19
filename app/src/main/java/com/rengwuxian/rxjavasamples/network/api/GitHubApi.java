package com.rengwuxian.rxjavasamples.network.api;

import com.rengwuxian.rxjavasamples.model.Repo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Shen YunLong on 2016/08/18.
 */
public interface GitHubApi {

    @GET("users/{user}/repos")
    Call<List<Repo>> listRepo(@Path("user") String user);

    @GET("users/{user}/repos")
    Observable<List<Repo>> rxListRepo(@Path("user") String user);
}
