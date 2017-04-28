package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {
        fetchResponse();
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
    public void fetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(1,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        mData.add(s);
                        mView.refreshView(mData);
                    }
                });
        /*Observable observable = Observable.create(new Observable.OnSubscribe<PatientDetail>() {
            @Override
            public void call(Subscriber<? super PatientDetail> subscriber) {
                subscriber.onNext(PatientDetailList.PATIENTS.get(0));
                //subscriber.onNext("Hi");
                //subscriber.onNext("Aloha");
                //subscriber.onCompleted();
            }
        });*/
        /*invoke(observable, new Callback<PatientDetail>() {
            @Override
            public void onResponse(PatientDetail data) {

                Log.e("second",data.getName());
                //if(data.equals("echo=> hello worldx!"))
                //{
                   // List<PatientDetail> mData =PatientDetailList.PATIENTS;
                   // mData.get(0).setName(data);
                   // mView.refreshView(data);
                //}
                /*for(int i=0;i<10;i++)
                {

                    try {
                        Thread.sleep(100);
                        //data.setName(String.valueOf(i));
                        mData.add(data);
                        mView.refreshView(mData);
                        mData.clear();

                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                sendRequest();
                mData.add(data);
                mView.refreshView(mData);
            }
        });*/
    }
    @Inject
    public MutiMonPresenterImpl()
    {

    }
}
