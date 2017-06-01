package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.model.PatientPhyData;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ISingleMonPresenter;
import cn.sk.skhstablet.protocol.ControlState;
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
                            mView.setPageState(AppConstants.STATE_SUCCESS);
                        else if(b==CommandTypeConstant.NONE_FAIL)
                        {
                            System.out.println("单人获取未知错误");
                        }
                        else {
                            System.out.println("未登录");
                        }
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
        SportDevControlRequest sportDevControlRequest=new SportDevControlRequest(CommandTypeConstant.SPORT_DEV_CONTROL_REQUEST);
        sportDevControlRequest.setUserID(AppConstants.USER_ID);
        sportDevControlRequest.setDeviceType(AppConstants.DEV_TYPE);
        sportDevControlRequest.setRequestID(CONTROL_REQ_ID);
        sportDevControlRequest.setDeviceID(deviceID);
        sportDevControlRequest.setParameterCode(CommandTypeConstant.SPORT_DEV_START_STOP);
        sportDevControlRequest.setParaType(type);
        invoke(TcpUtils.send(sportDevControlRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
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
        });
        //mView.setPageState(AppConstants.STATE_LOADING);
    }
    @Inject
    public SingleMonPresenterImpl()
    {

    }
}
