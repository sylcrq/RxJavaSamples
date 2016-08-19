package com.rengwuxian.rxjavasamples.module.github_7;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.model.Repo;
import com.rengwuxian.rxjavasamples.network.Network;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GitHubFragment extends BaseFragment {

    //    private Context mContext;
    public static final String TAG = GitHubFragment.class.getSimpleName();

    private Subscriber<String> mSubscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "onError");
        }

        @Override
        public void onNext(String s) {
            Log.d(TAG, "onNext # " + s);
        }
    };

    public GitHubFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_git_hub, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.github_go)
    public void go(View view) {
        final Context context = view.getContext();

        Network.getGitHubApi().listRepo("octocat").enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Call<List<Repo>> call, Response<List<Repo>> response) {
//                Toast.makeText(context, "" + response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Repo>> call, Throwable t) {
//                Toast.makeText(context, "" + t, Toast.LENGTH_SHORT).show();
            }
        });

        // 1.RxJava Hello World
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(mSubscriber);

        // 2.Simpler Code
        Observable.just("hello world 2").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "simpler # " + s);
            }
        });

        // 3.Transformation
        Observable.just("hello world 3")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s + " from syl";
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "transform # " + s);
                    }
                });

        // 4. from
        List<String> list = new ArrayList<>();
        list.add("x");
        list.add("xx");
        list.add("xxx");

        Observable.from(list)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "" + s);
                    }
                });

        // 5.flatMap
//        Network.getGitHubApi().rxListRepo("octocat")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<Repo>>() {
//                    @Override
//                    public void call(List<Repo> repos) {
//                        for (Repo x : repos) {
//                            Log.d(TAG, "Repo # " + x.getFull_name());
//                        }
//                    }
//                });

        Network.getGitHubApi().rxListRepo("octocat")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<Repo>, Observable<Repo>>() {
                    @Override
                    public Observable<Repo> call(List<Repo> repos) {
                        return Observable.from(repos);
                    }
                })
                .filter(new Func1<Repo, Boolean>() {
                    @Override
                    public Boolean call(Repo repo) {
                        return repo != null;
                    }
                })
                .take(3)
                .subscribe(new Action1<Repo>() {
                    @Override
                    public void call(Repo repo) {
                        Log.d(TAG, "" + repo.getFull_name());
                    }
                });

        // 6.Error Handling
        Observable.just("Hello Error")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return potentialException();
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "" + e);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "" + s);
                    }
                });

        // 7.Subscription
        Subscription subscription = Observable.just("test")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "" + s);
                    }
                });
        subscription.unsubscribe();
    }

    private String potentialException() {
        throw new RuntimeException("potentialException");
    }

    @Override
    protected int getDialogRes() {
        return 0;
    }

    @Override
    protected int getTitleRes() {
        return 0;
    }
}
