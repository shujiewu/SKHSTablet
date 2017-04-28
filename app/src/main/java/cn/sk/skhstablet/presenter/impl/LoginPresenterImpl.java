package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ILoginPresenter;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.View> implements ILoginPresenter.Presenter {
    @Override
    public void fetchStateData() {

        invoke(TcpUtils.connect("localhost", 60000), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                fetchData();
                sendVerify();
                fetchVerifyState();
            }
        });
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
    public void fetchVerifyState()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        Log.e("login","succees1");
                        mView.refreshView(s);

                    }
                });
    }
    @Inject
    public LoginPresenterImpl()
    {
    }
}
