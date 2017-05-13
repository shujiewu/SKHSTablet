package cn.sk.skhstablet.presenter.impl;

import android.util.Log;
import android.view.View;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ILoginPresenter;
import cn.sk.skhstablet.protocol.up.DevNameRequest;
import cn.sk.skhstablet.protocol.up.LoginRequest;
import cn.sk.skhstablet.protocol.up.MonitorDevFormRequest;
import cn.sk.skhstablet.protocol.up.SportDevFormRequest;
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
    public void fetchStateData(final String userID, final String key) {
        //TcpUtils.connect(AppConstants.url, AppConstants.port);
        invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                //Log.e("10.40","4");
                fetchData();
                sendVerify(userID,key);
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

    public void sendVerify(String userID, String key)
    {
        LoginRequest request=new LoginRequest(CommandTypeConstant.LOGIN_REQUEST);
        request.setUserID(Integer.parseInt(userID));
        AppConstants.USER_ID=request.getUserID();
        byte [] password=new byte[16];
        int i;
        for(i=0;i<key.length();i++)
        {
            password[i]=(byte) key.charAt(i);
        }
        if(i!=16)
            password[i]='#';
        request.setUserKey(password);
        invoke(TcpUtils.send(request), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    @Override
    public void sendFormatRequest()
    {
        DevNameRequest devNameRequest=new DevNameRequest(CommandTypeConstant.DEV_NAME_REQUEST);
        devNameRequest.setUserID(AppConstants.USER_ID);
        SportDevFormRequest sportDevFormRequest=new SportDevFormRequest(CommandTypeConstant.SPORT_DEV_FORM_REQUEST);
        sportDevFormRequest.setUserID(AppConstants.USER_ID);
        MonitorDevFormRequest monitorDevFormRequest=new MonitorDevFormRequest(CommandTypeConstant.MONITOR_DEV_FORM_REQUEST);
        monitorDevFormRequest.setUserID(AppConstants.USER_ID);
        invoke(TcpUtils.send(devNameRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
        invoke(TcpUtils.send(sportDevFormRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
        invoke(TcpUtils.send(monitorDevFormRequest), new Action1<Void>() {
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
