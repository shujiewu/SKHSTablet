package cn.sk.skhstablet.presenter.impl;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IChangekeyPresenter;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.up.ChangeKeyRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Subscription;
import rx.functions.Action1;

import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

/**
 * Created by wyb on 2017/4/25.
 * 修改密码的Presenter实现,对应于changkey的fragment
 */

public class ChangeKeyPresenterImpl extends BasePresenter<IChangekeyPresenter.View> implements IChangekeyPresenter.Presenter {

    //发送修改密码请求
    @Override
    public void sendRequest(String loginName,String oldKey,String newKey) {
        ChangeKeyRequest request=new ChangeKeyRequest(CommandTypeConstant.CHANGE_KEY_REQUEST);
        request.setUserID(0);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.CHANGE_KEY_REQ_ID);
        request.setLoginName(loginName);
        request.setUserOldKey(oldKey);
        request.setUserNewKey(newKey);
        //如果还未初始化链路，则直接返回错误
        if(TcpUtils.send(request)==null)
        {
            mView.refreshView(CommandTypeConstant.NO_LOGIN);
        }
        else
        {
            invoke(TcpUtils.send(request), new Callback<Void>() {
                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    System.out.println("修改密码发送失败");
                    //发送失败直接弹出未知错误
                    mView.refreshView(CommandTypeConstant.NO_LOGIN);
                    this.unsubscribe();
                    setConnDisable();
                }
                @Override
                public void onCompleted() {
                    System.out.println("修改密码发送完成");
                    this.unsubscribe();
                }
            });
        }
    }
    //注册修改密码状态返回的观察者
    @Override
    public void registerFetchResponse() {
        Subscription changkeySubscription = RxBus.getDefault().toObservable(AppConstants.CHANGE_KEY_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte s) {
                        //System.out.println("接收到修改密码的状态");
                        mView.refreshView(s);
                    }
                });
        ((LifeSubscription)mView).bindSubscription(changkeySubscription);
    }
    @Inject
    public ChangeKeyPresenterImpl()
    {

    }
}
