package cn.sk.skhstablet.presenter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IPatientListPresenter;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {

    private HashMap<Integer,Integer> hasPatient=new HashMap<>();
    public List<Patient> mDatas=new ArrayList<>();
    private int position=0;
    @Override
    public void sentPatientListRequest() {
        mDatas= PatientList.PATIENTS;
        mView.refreshView(mDatas);

        //在这里写发送
    }

    @Override
    public void sendMutiMonitorRequest() {
        invoke(TcpUtils.send("sendMutiMonitorRequest"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void sendCancelSingleMonitorReq() {
        invoke(TcpUtils.send("CancelSingleMonitorReq"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void sendLogoutRequest() {
        invoke(TcpUtils.send("logoutrequest"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        //更新数据
        Subscription listSubscription = RxBus.getDefault().toObservable(AppConstants.PATIENT_LIST_DATA_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b!=true)
                            return;
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
                                mDatas.set(hasPatient.get(patientID),patient);
                                mView.refreshView(patient,hasPatient.get(patientID));
                                return;
                            }
                            else
                            {
                                mDatas.add(patient);
                                hasPatient.put(patientID,position++);
                            }
                        }
                        mView.refreshView(mDatas);
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

    }

    @Inject
    public PatientListPresenterImpl()
    {

    }

}
