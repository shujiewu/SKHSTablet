package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {
        sendRequest();
    }

    public void sendRequest()
    {
        invoke(TcpUtils.send("hello worldx!"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    List<PatientDetail> mData=new ArrayList<>();
    /*public void registerFetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        mData.add(s);
                        mView.refreshView(mData);
                    }
                });
        if (this.mutiSubscription == null) {
            mutiSubscription = new CompositeSubscription();
        }
        mutiSubscription.add(mSubscription);
    }*/
    @Inject
    public MutiMonPresenterImpl()
    {

    }
}
