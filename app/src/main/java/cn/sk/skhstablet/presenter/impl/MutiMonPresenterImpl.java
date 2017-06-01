package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {
        sendRequest();
    }

    private List<PatientDetail> mDatas=new ArrayList<>();

    private int position=0;
    @Override
    public void registerFetchResponse() {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
                        {
                            int patientID=s.getPatientID();
                            if(hasMutiPatient.containsKey(patientID))
                            {
                                mDatas.set(hasMutiPatient.get(patientID),s);
                                mView.refreshView(s,hasMutiPatient.get(patientID));
                            }
                            else
                            {
                                mDatas.add(s);
                                hasMutiPatient.put(patientID,position++);
                                mView.refreshView(mDatas);
                            }
                        }//成功界面才更新，否则抛弃
                    }
                });

        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                            mView.reSendRequest();
                    }
                });


        Subscription mutiPageSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_REQ_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b== CommandTypeConstant.SUCCESS)
                        {
                            mView.setPageState(AppConstants.STATE_SUCCESS);
                            mDatas.clear();
                            hasMutiPatient.clear();
                            position=0;
                        }
                        else if(b==CommandTypeConstant.NONE_FAIL)
                        {
                            System.out.println("多人监控获取未知错误");
                        }
                        else {
                            System.out.println("未登录");
                        }
                    }
                });
        System.out.println("registermuti");
        ((LifeSubscription)mView).bindSubscription(mutiPageSubscription);

        ((LifeSubscription)mView).bindSubscription(mSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);

    }

    public void sendRequest()
    {
        /*invoke(TcpUtils.send("hello worldx!"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });*/
        if(lastMutiPatientID==null)
            return;
        MutiMonitorRequest mutiMonitorRequest=new MutiMonitorRequest(CommandTypeConstant.MUTI_MONITOR_REQUEST);
        mutiMonitorRequest.setUserID(AppConstants.USER_ID);
        mutiMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequest.setRequestID(AppConstants.MUTI_REQ_ID);
        mutiMonitorRequest.setPatientNumber((short) lastMutiPatientID.size());
        mutiMonitorRequest.setPatientID(lastMutiPatientID);
        //lastMutiPatientID=patientID;
        invoke(TcpUtils.send(mutiMonitorRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send mutiMonitorRequest!");
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
