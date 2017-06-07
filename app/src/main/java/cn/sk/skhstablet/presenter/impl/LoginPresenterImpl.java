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

        sendVerify(userID,key);
        //TcpUtils.connect(AppConstants.url, AppConstants.port);
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

    public void sendVerify(String loginName, String key)
    {
        LoginRequest request=new LoginRequest(CommandTypeConstant.LOGIN_REQUEST);
        request.setUserID(0);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.LOGIN_REQ_ID);
        //AppConstants.USER_ID=request.getUserID();
        /*byte [] password=new byte[16];
        int i;
        for(i=0;i<key.length();i++)
        {
            password[i]=(byte) key.charAt(i);
        }
        if(i!=16)
            password[i]='#';*/
        //request.setLoginKeyLength((byte) key.length());
        //request.setLoginNameLength((byte) loginName.length());
        request.setLoginName(loginName);
        request.setLoginKey(key);
        invoke(TcpUtils.send(request),  new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("验证发送完成");
                this.unsubscribe();
            }
        });
    }
    @Override
    public void sendFormatRequest()
    {
        /*DevNameRequest devNameRequest=new DevNameRequest(CommandTypeConstant.DEV_NAME_REQUEST);
        devNameRequest.setUserID(AppConstants.USER_ID);
        invoke(TcpUtils.send(devNameRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });*/
        SportDevFormRequest sportDevFormRequest=new SportDevFormRequest(CommandTypeConstant.SPORT_DEV_FORM_REQUEST);
        sportDevFormRequest.setUserID(AppConstants.USER_ID);
        System.out.println("AppConstants.USER_ID"+AppConstants.USER_ID);
        sportDevFormRequest.setDeviceType(AppConstants.DEV_TYPE);
        sportDevFormRequest.setRequestID(AppConstants.SPORT_FORM_REQ_ID);
        MonitorDevFormRequest monitorDevFormRequest=new MonitorDevFormRequest(CommandTypeConstant.MONITOR_DEV_FORM_REQUEST);
        monitorDevFormRequest.setUserID(AppConstants.USER_ID);
        monitorDevFormRequest.setDeviceType(AppConstants.DEV_TYPE);
        monitorDevFormRequest.setRequestID(AppConstants.PHY_FORM_REQ_ID);
        invoke(TcpUtils.send(sportDevFormRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("运动设备格式发送完成");
                this.unsubscribe();
            }
        });
        invoke(TcpUtils.send(monitorDevFormRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("监护格式发送完成");
                this.unsubscribe();
            }
        });
    }


    @Inject
    public LoginPresenterImpl()
    {
    }
}
