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

import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

/**
 * Created by wyb on 2017/4/25.
 */

public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.View> implements ILoginPresenter.Presenter {

    @Override
    public void registerFetchResponse() {
        //注册登录状态的观察者
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {

                        mView.refreshView(s);

                    }
                });
        /*//注册重连状态的观察者
        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s)
                            mView.reSendRequest();//接收到重连成功的状态
                        else
                        {
                            mView.setLoginDisable();//重连失败
                        }
                    }
                });*/
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        //((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);
    }

    public void sendVerify(String loginName, String key)
    {
        LoginRequest request=new LoginRequest(CommandTypeConstant.LOGIN_REQUEST);
        request.setUserID(0);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.LOGIN_REQ_ID);
        request.setLoginName(loginName);
        request.setLoginKey(key);
        if(TcpUtils.send(request)==null)//说明连接未建立成功
        {
            mView.setLoginDisable();
        }
        else
        {
            invoke(TcpUtils.send(request),  new Callback<Void>() {
                @Override
                public void onCompleted() {
                    //super.onCompleted();
                    System.out.println("登录验证发送完成");
                    this.unsubscribe();
                }
                @Override
                public void onError(Throwable e) {
                    //super.onError(e);
                    //netState= AppConstants.STATE_DIS_CONN;
                    System.out.println("登录验证发送失败");
                    mView.setLoginDisable();
                    this.unsubscribe();
                    setConnDisable();
                }
            });
        }
    }


    @Inject
    public LoginPresenterImpl()
    {
    }
}
