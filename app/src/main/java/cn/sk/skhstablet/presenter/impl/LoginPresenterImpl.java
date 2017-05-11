package cn.sk.skhstablet.presenter.impl;

import android.util.Log;
import android.view.View;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ILoginPresenter;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Subscription;
import rx.functions.Action1;

import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;

/**
 * Created by wyb on 2017/4/25.
 */

public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.View> implements ILoginPresenter.Presenter {
    @Override
    public void fetchStateData() {
        //TcpUtils.connect(AppConstants.url, AppConstants.port);
        invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                Log.e("10.40","4");
                sendVerify();
                fetchData();
                //fetchVerifyState();
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        Log.e("login","succees1");
                        mView.refreshView(s);

                    }
                });
        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                            mView.reSendRequest();
                        else
                        {
                            mView.setLoginDisable();
                        }


                    }
                });
        Subscription mSubscriptionRequestFail = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST_FAIL,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                            mView.reSendRequest();
                    }
                });
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);
        ((LifeSubscription)mView). bindSubscription(mSubscriptionRequestFail);
    }

    public void sendVerify()
    {
        invoke(TcpUtils.send("hello world!"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    public void sendFormatRequest()
    {
        invoke(TcpUtils.send("devname"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
        invoke(TcpUtils.send("sportdevform"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
        invoke(TcpUtils.send("monitordevform"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }


    @Inject
    public LoginPresenterImpl()
    {
    }
}
