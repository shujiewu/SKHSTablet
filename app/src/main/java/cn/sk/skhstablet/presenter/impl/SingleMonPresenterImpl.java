package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.model.PatientPhyData;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ISingleMonPresenter;
import cn.sk.skhstablet.protocol.ControlState;
import cn.sk.skhstablet.protocol.SportDevForm;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
import cn.sk.skhstablet.protocol.up.SportDevControlRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.base.BaseFragment;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;

import static cn.sk.skhstablet.app.AppConstants.CONTROL_REQ_ID;
import static cn.sk.skhstablet.app.AppConstants.DEV_NAME;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NAME_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NUMBER_FORM;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.isCancelSingle;
import static cn.sk.skhstablet.app.AppConstants.lastSinglePatientID;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;

/**
 * Created by wyb on 2017/4/25.
 */

public class SingleMonPresenterImpl extends BasePresenter<ISingleMonPresenter.View> implements ISingleMonPresenter.Presenter {
    @Override
    public void sendPatientDetailRequest(String ID) {
       // PatientDetail patientDetail= PatientDetailList.PATIENTS.get(0);
       // mView.refreshView(patientDetail);
        sendRequest(ID);
    }

    @Override
    public void fetchExercisePlan() {
        List<String> armTypes=new ArrayList<>(Arrays.asList("运动方案1：在跑步机上运动10分钟", "运动方案2：在椭圆机上运动200米", "运动方案3：在跑步机上运动5分钟"));
        List<List<String>> arms=new ArrayList<>();
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动1分钟", "第二段：以1米每秒的速度运动1分钟")));
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在椭圆机上运动2分钟", "第二段：以1米每秒的速度运动2分钟")));
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动3分钟", "第二段：以1米每秒的速度运动3分钟")));
        //arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动4分钟", "第二段：以1米每秒的速度运动4分钟")));
        mView.refreshExercisePlan(armTypes,arms);
    }

    @Override
    public void registerFetchResponse() {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        mView.refreshView(s);

                    }
                });

        Subscription mPhySubscription = RxBus.getDefault().toObservable(AppConstants.PHY_DATA,PatientPhyData.class)
                .subscribe(new Action1<PatientPhyData>() {
                    @Override
                    public void call(PatientPhyData s) {
                        System.out.println("生理數據接收");
                        mView.refreshPhyData(s);

                    }
                });
        Subscription singlePageSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_REQ_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b== CommandTypeConstant.SUCCESS)
                        {
                            if(isCancelSingle)
                            {
                                mView.setPageState(AppConstants.STATE_EMPTY);
                                isCancelSingle=false;
                            }
                            else
                                mView.setPageState(AppConstants.STATE_SUCCESS);
                        }

                        else if(b==CommandTypeConstant.NONE_FAIL)
                        {
                            System.out.println("单人获取未知错误");
                        }
                        else {
                            System.out.println("未登录");
                        }
                        //testSinglePatient();
                    }
                });

        Subscription controlSubscription = RxBus.getDefault().toObservable(AppConstants.CONTORL_REQ_STATE,ControlState.class)
                .subscribe(new Action1<ControlState>() {
                    @Override
                    public void call(ControlState b) {
                        mView.setControlState(b.getResultState(),b.getControlState());
                    }
                });
        ((LifeSubscription)mView).bindSubscription(singlePageSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        ((LifeSubscription)mView).bindSubscription(controlSubscription);

        ((LifeSubscription)mView).bindSubscription(mPhySubscription);
    }

    @Override
    public void sendControlStartStop(int patientID, String deviceID,byte type) {
        if(deviceID!=null)
        {
            SportDevControlRequest sportDevControlRequest=new SportDevControlRequest(CommandTypeConstant.SPORT_DEV_CONTROL_REQUEST);
            sportDevControlRequest.setUserID(AppConstants.USER_ID);
            sportDevControlRequest.setDeviceType(AppConstants.DEV_TYPE);
            sportDevControlRequest.setRequestID(CONTROL_REQ_ID);
            sportDevControlRequest.setDeviceID(deviceID);
            sportDevControlRequest.setParameterCode(CommandTypeConstant.SPORT_DEV_START_STOP);
            sportDevControlRequest.setParaType(type);
            invoke(TcpUtils.send(sportDevControlRequest), new Callback<Void>() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    System.out.println("启停控制发送完成");
                    this.unsubscribe();
                }
            });
        }
    }

    public void sendRequest(String ID)
    {
        SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        singleMonitorRequest.setUserID(AppConstants.USER_ID);
        singleMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        singleMonitorRequest.setRequestID(AppConstants.SINGLE_REQ_ID);
        singleMonitorRequest.setPatientNumber((short) 1);
        singleMonitorRequest.setPatientID(Integer.parseInt(ID));
        lastSinglePatientID=ID;
        invoke(TcpUtils.send(singleMonitorRequest), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendsingle","error");
                mView.setPageState(AppConstants.STATE_ERROR);
                if(netState==AppConstants.STATE_DIS_CONN)
                    reconnect();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("重新发送单人监控生理仪发送完成");
                this.unsubscribe();
            }
        });


        List<Integer> patientIDList=new ArrayList<>();
        patientIDList.add(Integer.parseInt(ID));
        MutiMonitorRequest mutiMonitorRequest=new MutiMonitorRequest(CommandTypeConstant.MUTI_MONITOR_REQUEST);
        mutiMonitorRequest.setUserID(AppConstants.USER_ID);
        mutiMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequest.setRequestID(AppConstants.SINGLE_SPORT_REQ_ID);
        mutiMonitorRequest.setPatientNumber((short)1);
        mutiMonitorRequest.setPatientID(patientIDList);//zhiyouyige
        invoke(TcpUtils.send(mutiMonitorRequest), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendsingle","error");
                mView.setPageState(AppConstants.STATE_ERROR);
                if(netState==AppConstants.STATE_DIS_CONN)
                    reconnect();

            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("重新发送单人监控康复设备发送完成");
                this.unsubscribe();
            }
        });
        //mView.setPageState(AppConstants.STATE_LOADING);
    }
    @Inject
    public SingleMonPresenterImpl()
    {

    }

    void testSinglePatient()
    {
        Patient patient= PatientList.PATIENTS.get(2);
        PatientDetail patientDetail=new PatientDetail();
        patientDetail.setPatientID(patient.getPatientID());
        patientDetail.setName(PATIENT_LIST_NAME_FORM.get(patientDetail.getPatientID()));
        patientDetail.setHospitalNumber(PATIENT_LIST_NUMBER_FORM.get(patientDetail.getPatientID()));
        patientDetail.setDev(DEV_NAME.get(patient.getDevType()));
        patientDetail.setDevType(patient.getDevType());
        patientDetail.setDeviceNumber(patient.getDeviceNumber());
        patientDetail.setPercent("90");
        List<String> sportDevName=new ArrayList<String>();  //指的是参数名称，而不是设备名称
        List<String> phyDevName=new ArrayList<String>(Arrays.asList("心率","收缩压","舒张压","血氧"));
        List<String> phyDevValue=new ArrayList<String>(Arrays.asList("99","99","132","94"));
        List<String> sportDevValue=new ArrayList<String>();

        List<SportDevForm> sportDevForms=SPORT_DEV_FORM.get(patient.getDevType());
        String [] val=new String[]{"780","119","1208","51","3"};
        for(int i=0;i<sportDevForms.size();i++) {
            sportDevName.add(sportDevForms.get(i).getChineseName());
            sportDevValue.add(val[i]);///byte
        }
        patientDetail.setPhyDevName(phyDevName);
        patientDetail.setPhyDevValue(phyDevValue);
        patientDetail.setSportDevName(sportDevName);
        patientDetail.setSportDevValue(sportDevValue);
       // if(mView.getState()==AppConstants.STATE_SUCCESS)
        //{
        mView.refreshView(patientDetail);
       // }//成功界面才更新，否则抛弃
    }
}
