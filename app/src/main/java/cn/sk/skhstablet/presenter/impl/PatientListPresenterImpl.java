package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IPatientListPresenter;
import cn.sk.skhstablet.protocol.up.LoginRequest;
import cn.sk.skhstablet.protocol.up.LogoutRequest;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.protocol.up.PatientListRequest;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.fragment.SingleMonitorFragment;
import rx.Subscription;
import rx.functions.Action1;

import static cn.sk.skhstablet.app.AppConstants.LOGIN_KEY;
import static cn.sk.skhstablet.app.AppConstants.LOGIN_NAME;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.app.AppConstants.lastSinglePatientID;

/**
 * Created by wyb on 2017/4/25.
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {

    public HashMap<Integer,Integer> hasPatient=new HashMap<>();
    public List<Patient> mDatas=new ArrayList<>();
    private int position=0;
    @Override
    public void sentPatientListRequest() {
        //mDatas= PatientList.PATIENTS;
        //mView.refreshView(mDatas);

        //在这里写发送
        PatientListRequest patientListRequest=new PatientListRequest(CommandTypeConstant.PATIENT_LIST_REQUEST);
        patientListRequest.setUserID(AppConstants.USER_ID);
        patientListRequest.setDeviceType(AppConstants.DEV_TYPE);
        patientListRequest.setRequestID(AppConstants.PATIENT_LIST_REQ_ID);
        //System.out.println("send patientListRequest!");
        invoke(TcpUtils.send(patientListRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send patientListRequest!");
            }
        });
    }

    @Override
    public void sendMutiMonitorRequest(List<Integer> patientID) {
        MutiMonitorRequest mutiMonitorRequest=new MutiMonitorRequest(CommandTypeConstant.MUTI_MONITOR_REQUEST);
        mutiMonitorRequest.setUserID(AppConstants.USER_ID);
        mutiMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequest.setRequestID(AppConstants.MUTI_REQ_ID);
        mutiMonitorRequest.setPatientNumber((short) patientID.size());
        mutiMonitorRequest.setPatientID(patientID);
        lastMutiPatientID=patientID;
        invoke(TcpUtils.send(mutiMonitorRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send mutiMonitorRequest!");
            }
        });
    }

    @Override
    public void sendCancelSingleMonitorReq() {
        SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        singleMonitorRequest.setUserID(AppConstants.USER_ID);
        singleMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        singleMonitorRequest.setRequestID(AppConstants.SINGLE_REQ_ID);
        singleMonitorRequest.setPatientNumber((short) 1);
        singleMonitorRequest.setPatientID(0);
        invoke(TcpUtils.send(singleMonitorRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void sendLogoutRequest() {
        LogoutRequest logoutRequest=new LogoutRequest(CommandTypeConstant.LOGOUT_REQUEST);
        logoutRequest.setUserID(AppConstants.USER_ID);
        logoutRequest.setDeviceType(AppConstants.DEV_TYPE);
        logoutRequest.setRequestID(AppConstants.LOGOUT_REQ_ID);
        invoke(TcpUtils.send(logoutRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        //更新数据
        Subscription listSubscription = RxBus.getDefault().toObservable(AppConstants.PATIENT_LIST_DATA_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b==CommandTypeConstant.PATIENT_LIST_SUCCESS)
                        {
                            int patientID;
                            //说明更改了数据，新数据为全局变量
                            for(Patient patient:AppConstants.PATIENT_LIST_DATA)
                            {
                                patientID=patient.getPatientID();
                                if(hasPatient.containsKey(patientID))//说明已存在,且只有一个人
                                {
                                    patient.setName(mDatas.get(hasPatient.get(patientID)).getName());
                                    patient.setGender(mDatas.get(hasPatient.get(patientID)).getGender());
                                    patient.setHospitalNumber(mDatas.get(hasPatient.get(patientID)).getHospitalNumber());
                                    patient.setSelectStatus(mDatas.get(hasPatient.get(patientID)).getSelectStatus());
                                    mDatas.set(hasPatient.get(patientID),patient);
                                    mView.refreshView(patient,hasPatient.get(patientID));
                                    return;
                                }
                                else
                                {
                                    mDatas.add(patient);
                                    hasPatient.put(patientID,position++);
                                    System.out.println(patient.getPatientID());
                                }
                            }
                            mView.refreshView(mDatas);
                        }
                        else if(b==CommandTypeConstant.PATIENT_LIST_NONE_FAIL)
                        {
                            System.out.println("病人列表获取未知错误");
                        }
                        else {
                            System.out.println("未登录");
                        }
                    }
                });
        /*Subscription mutiPageSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_REQ_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b==true)
                            mView.setMutiPageState(AppConstants.STATE_SUCCESS);
                    }
                });
        Subscription singlePageSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_REQ_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b==true)
                            mView.setSinglePageState(AppConstants.STATE_SUCCESS);
                    }
                });
        ((LifeSubscription)mView).bindSubscription(mutiPageSubscription);
        ((LifeSubscription)mView).bindSubscription(singlePageSubscription);*/

        ((LifeSubscription)mView).bindSubscription(listSubscription);

        Subscription logoutSubscription = RxBus.getDefault().toObservable(AppConstants.LOGOUT_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b)
                            mView.logoutSuccess(b);
                    }
                });
        ((LifeSubscription)mView).bindSubscription(logoutSubscription);


        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                        {
                            sendVerify();
                        }
                        else
                        {
                            System.out.println("网络有问题，请过段时间再试");
                        }
                            //mView.reSendRequest();
                    }
                });

        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        Log.e("login","succees2");
                        if(s==true)
                        {
                            sentPatientListRequest();
                            if(lastMutiPatientID!=null)
                                sendMutiMonitorRequest(lastMutiPatientID);
                            if(lastSinglePatientID!=null)
                                mView.loadSinglePatient(lastSinglePatientID);
                        }
                    }
                });

    }
    public void sendVerify()
    {
        LoginRequest request=new LoginRequest(CommandTypeConstant.LOGIN_REQUEST);
        request.setUserID(AppConstants.USER_ID);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.LOGIN_REQ_ID);
        request.setLoginName(LOGIN_NAME);
        request.setLoginKey(LOGIN_KEY);
        invoke(TcpUtils.send(request), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    @Inject
    public PatientListPresenterImpl()
    {

    }

}
