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

/**
 * Created by wyb on 2017/4/25.
 */

public class ChangeKeyPresenterImpl extends BasePresenter<IChangekeyPresenter.View> implements IChangekeyPresenter.Presenter {


    @Override
    public void sendRequest(String loginName,String oldKey,String newKey) {
        ChangeKeyRequest request=new ChangeKeyRequest(CommandTypeConstant.CHANGE_KEY_REQUEST);
        request.setUserID(0);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.CHANGE_KEY_REQ_ID);
        request.setLoginName(loginName);
        request.setUserOldKey(oldKey);
        request.setUserNewKey(newKey);
        invoke(TcpUtils.send(request), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                System.out.println("修改密码发送失败");
                mView.refreshView(CommandTypeConstant.NONE_FAIL);
                this.unsubscribe();
            }
            @Override
            public void onCompleted() {
                System.out.println("修改密码发送完成");
                this.unsubscribe();
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        Subscription changkeySubscription = RxBus.getDefault().toObservable(AppConstants.CHANGE_KEY_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte s) {
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
