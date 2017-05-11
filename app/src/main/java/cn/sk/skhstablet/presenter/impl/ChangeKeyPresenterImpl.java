package cn.sk.skhstablet.presenter.impl;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IChangekeyPresenter;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class ChangeKeyPresenterImpl extends BasePresenter<IChangekeyPresenter.View> implements IChangekeyPresenter.Presenter {


    @Override
    public void sendRequest() {
        invoke(TcpUtils.send("changekey"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        Subscription changkeySubscription = RxBus.getDefault().toObservable(AppConstants.CHANGE_KEY_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
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
