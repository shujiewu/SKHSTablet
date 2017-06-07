package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.protocol.SportDevForm;
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

import static cn.sk.skhstablet.app.AppConstants.DEV_NAME;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NAME_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NUMBER_FORM;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.utlis.Utils.secToTime;

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {
        //sendRequest();
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
                                System.out.println("收到多人数据");
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
                            //mView.setPageState(AppConstants.STATE_SUCCESS);
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

                       //testMutiData1();
                        //testMutiData2();
                        //testMutiData3();
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
        invoke(TcpUtils.send(mutiMonitorRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("重新发送多人监控发送完成");
                this.unsubscribe();
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

    void testMutiData1()
    {
        Patient patient=PatientList.PATIENTS.get(0);
        PatientDetail patientDetail=new PatientDetail();
        patientDetail.setPatientID(patient.getPatientID());
        patientDetail.setName(PATIENT_LIST_NAME_FORM.get(patientDetail.getPatientID()));
        patientDetail.setHospitalNumber(PATIENT_LIST_NUMBER_FORM.get(patientDetail.getPatientID()));
        patientDetail.setDev(DEV_NAME.get(patient.getDevType()));
        patientDetail.setDevType(patient.getDevType());
        patientDetail.setDeviceNumber(patient.getDeviceNumber());
        patientDetail.setPercent("10");
        List<String> sportDevName=new ArrayList<String>();  //指的是参数名称，而不是设备名称
        List<String> phyDevName=new ArrayList<String>(Arrays.asList("心率","收缩压","舒张压"));
        List<String> phyDevValue=new ArrayList<String>(Arrays.asList("90","87","120"));
        List<String> sportDevValue=new ArrayList<String>();

        List<SportDevForm> sportDevForms=SPORT_DEV_FORM.get(patient.getDevType());
        int [] val=new int[]{5565,33,100,52,3,1000,10};
        for(int i=0;i<sportDevForms.size();i++) {
            if(sportDevForms.get(i).getDisplayUnit().isEmpty())
                sportDevName.add(sportDevForms.get(i).getChineseName());
            else
            {
                if(sportDevForms.get(i).getDisplayUnit().equals("s"))
                    sportDevName.add(sportDevForms.get(i).getChineseName());
                else
                    sportDevName.add(sportDevForms.get(i).getChineseName()+"("+sportDevForms.get(i).getDisplayUnit()+")");
            }

            if(sportDevForms.get(i).getRate()!=1.0)
                sportDevValue.add(String.valueOf(val[i]*sportDevForms.get(i).getRate()));///byte
            else
            {
                if(sportDevForms.get(i).getDisplayUnit().equals("s"))
                    sportDevValue.add(secToTime(val[i]));///byte
                else
                    sportDevValue.add(String.valueOf(val[i]));///byte
            }

        }
        patientDetail.setPhyDevName(phyDevName);
        patientDetail.setPhyDevValue(phyDevValue);
        patientDetail.setSportDevName(sportDevName);
        patientDetail.setSportDevValue(sportDevValue);
        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
        {
            int patientID=patientDetail.getPatientID();
            if(hasMutiPatient.containsKey(patientID))
            {
                mDatas.set(hasMutiPatient.get(patientID),patientDetail);
                mView.refreshView(patientDetail,hasMutiPatient.get(patientID));
            }
            else
            {
                mDatas.add(patientDetail);
                hasMutiPatient.put(patientID,position++);
                mView.refreshView(mDatas);
            }
        }//成功界面才更新，否则抛弃
    }

    void testMutiData2()
    {
        Patient patient=PatientList.PATIENTS.get(1);
        PatientDetail patientDetail=new PatientDetail();
        patientDetail.setPatientID(patient.getPatientID());
        patientDetail.setName(PATIENT_LIST_NAME_FORM.get(patientDetail.getPatientID()));
        patientDetail.setHospitalNumber(PATIENT_LIST_NUMBER_FORM.get(patientDetail.getPatientID()));
        patientDetail.setDev(DEV_NAME.get(patient.getDevType()));
        patientDetail.setDevType(patient.getDevType());
        patientDetail.setDeviceNumber(patient.getDeviceNumber());
        patientDetail.setPercent("17");
        List<String> sportDevName=new ArrayList<String>();  //指的是参数名称，而不是设备名称
        List<String> phyDevName=new ArrayList<String>(Arrays.asList("心率","收缩压","舒张压","血氧"));
        List<String> phyDevValue=new ArrayList<String>(Arrays.asList("97","80","127","97"));
        List<String> sportDevValue=new ArrayList<String>();

        List<SportDevForm> sportDevForms=SPORT_DEV_FORM.get(patient.getDevType());
        String [] val=new String[]{"35","23","148","59","3","1200","8"};
        for(int i=0;i<sportDevForms.size();i++) {
            sportDevName.add(sportDevForms.get(i).getChineseName());
            sportDevValue.add(val[i]);///byte
        }
        patientDetail.setPhyDevName(phyDevName);
        patientDetail.setPhyDevValue(phyDevValue);
        patientDetail.setSportDevName(sportDevName);
        patientDetail.setSportDevValue(sportDevValue);
        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
        {
            int patientID=patientDetail.getPatientID();
            if(hasMutiPatient.containsKey(patientID))
            {
                mDatas.set(hasMutiPatient.get(patientID),patientDetail);
                mView.refreshView(patientDetail,hasMutiPatient.get(patientID));
            }
            else
            {
                mDatas.add(patientDetail);
                hasMutiPatient.put(patientID,position++);
                mView.refreshView(mDatas);
            }
        }//成功界面才更新，否则抛弃
    }

    void testMutiData3()
    {
        Patient patient=PatientList.PATIENTS.get(2);
        PatientDetail patientDetail=new PatientDetail();
        patientDetail.setPatientID(patient.getPatientID());
        patientDetail.setName(PATIENT_LIST_NAME_FORM.get(patientDetail.getPatientID()));
        patientDetail.setHospitalNumber(PATIENT_LIST_NUMBER_FORM.get(patientDetail.getPatientID()));
        patientDetail.setDev(DEV_NAME.get(patient.getDevType()));
        patientDetail.setDevType(patient.getDevType());
        patientDetail.setDeviceNumber(patient.getDeviceNumber());
        patientDetail.setPercent("27");
        List<String> sportDevName=new ArrayList<String>();  //指的是参数名称，而不是设备名称
        List<String> phyDevName=new ArrayList<String>(Arrays.asList("心率","收缩压","舒张压","血氧"));
        List<String> phyDevValue=new ArrayList<String>(Arrays.asList("110","90","127","96"));
        List<String> sportDevValue=new ArrayList<String>();

        List<SportDevForm> sportDevForms=SPORT_DEV_FORM.get(patient.getDevType());
        String [] val=new String[]{"25","29","188","49","2"};
        for(int i=0;i<sportDevForms.size();i++) {
            sportDevName.add(sportDevForms.get(i).getChineseName());
            sportDevValue.add(val[i]);///byte
        }
        patientDetail.setPhyDevName(phyDevName);
        patientDetail.setPhyDevValue(phyDevValue);
        patientDetail.setSportDevName(sportDevName);
        patientDetail.setSportDevValue(sportDevValue);
        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
        {
            int patientID=patientDetail.getPatientID();
            if(hasMutiPatient.containsKey(patientID))
            {
                mDatas.set(hasMutiPatient.get(patientID),patientDetail);
                mView.refreshView(patientDetail,hasMutiPatient.get(patientID));
            }
            else
            {
                mDatas.add(patientDetail);
                hasMutiPatient.put(patientID,position++);
                mView.refreshView(mDatas);
            }
        }//成功界面才更新，否则抛弃
    }
}
