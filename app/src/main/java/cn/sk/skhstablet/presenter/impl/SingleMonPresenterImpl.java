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
import cn.sk.skhstablet.protocol.ExercisePlan;
import cn.sk.skhstablet.protocol.SportDevForm;
import cn.sk.skhstablet.protocol.down.ExercisePlanResponse;
import cn.sk.skhstablet.protocol.up.ExercisePlanRequest;
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
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.app.AppConstants.lastSinglePatientID;
import static cn.sk.skhstablet.app.AppConstants.mutiPosition;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.app.AppConstants.singleMonitorID;
import static cn.sk.skhstablet.app.CommandTypeConstant.SUCCESS;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

/**
 * Created by wyb on 2017/4/25.
 * 单人监控的Presenter，对应于SingleMonitroFragment
 */

public class SingleMonPresenterImpl extends BasePresenter<ISingleMonPresenter.View> implements ISingleMonPresenter.Presenter {
    //发送单人监控请求
    @Override
    public void sendPatientDetailRequest(String ID) {
       // PatientDetail patientDetail= PatientDetailList.PATIENTS.get(0);
       // mView.refreshView(patientDetail);
        sendRequest(ID);
    }

    //以前测试医嘱用的函数
    @Override
    public void fetchExercisePlan(String ID) {
        ExercisePlanRequest exercisePlanRequest=new ExercisePlanRequest(CommandTypeConstant.EXERCISE_PLAN_REQUEST);
        exercisePlanRequest.setUserID(AppConstants.USER_ID);
        exercisePlanRequest.setDeviceType(AppConstants.DEV_TYPE);
        exercisePlanRequest.setRequestID(AppConstants.SINGLE_REQ_ID);
        exercisePlanRequest.setPatientID(Integer.parseInt(ID));
        invoke(TcpUtils.send(exercisePlanRequest), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendsingle","error");
                mView.setPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("发送单人监控医嘱完成");
                this.unsubscribe();
            }
        });
    }

    @Override
    public void registerFetchResponse() {
        //对单人监控所需的设备数据数据返回的观察者
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        System.out.println("运动数据准备在单人监控界面显示");
                        mView.refreshView(s);

                    }
                });

        //对单人监控所需的生理数据返回的观察者
        Subscription mPhySubscription = RxBus.getDefault().toObservable(AppConstants.PHY_DATA,PatientPhyData.class)
                .subscribe(new Action1<PatientPhyData>() {
                    @Override
                    public void call(PatientPhyData s) {
                        System.out.println("生理数据准备在单人监控界面显示");
                        mView.refreshPhyData(s);

                    }
                });
        //对单人监控请求响应的观察者
        Subscription singlePageSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_REQ_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b== SUCCESS)
                        {
                            //如果是取消单人监控，即发送的患者数量为1
                            /*if(isCancelSingle)
                            {
                                mView.setPageState(AppConstants.STATE_EMPTY);
                                isCancelSingle=false;
                            }
                            else*/
                           // System.out.print("准备设置单人监控界面");
                           // mView.setPageState(AppConstants.STATE_SUCCESS);
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
        //对控制请求响应的观察者
        Subscription controlSubscription = RxBus.getDefault().toObservable(AppConstants.CONTORL_REQ_STATE,ControlState.class)
                .subscribe(new Action1<ControlState>() {
                    @Override
                    public void call(ControlState b) {
                        mView.setControlState(b.getResultState(),b.getControlState());
                    }
                });

        //对医嘱响应的观察者，还没测试使用，只是写在这里
        Subscription planSubscription = RxBus.getDefault().toObservable(AppConstants.EXERCISE_PLAN_STATE,ExercisePlanResponse.class)
                .subscribe(new Action1<ExercisePlanResponse>() {
                    @Override
                    public void call(ExercisePlanResponse exercisePlanResponse) {
                        System.out.print("医嘱收到1");
                       // mView.setPageState(AppConstants.STATE_SUCCESS);
                        if(exercisePlanResponse.getState()==SUCCESS)
                        {
                            List<String> armTypes=new ArrayList<>();
                            List<List<String>> arms=new ArrayList<>();
                            List<Integer> planID=new ArrayList<Integer>();
                            List<ExercisePlan> exercisePlanList=exercisePlanResponse.getExercisePlanList();
                            if(exercisePlanList!=null)
                            {
                                for(ExercisePlan exercisePlan:exercisePlanList)
                                {
                                    armTypes.add(exercisePlan.getContent());
                                    arms.add(exercisePlan.getSegment());
                                    planID.add(exercisePlan.getExercisePlanID());
                                }
                            }
                            mView.refreshExercisePlan(armTypes,arms,exercisePlanResponse.getConstraintContent(),planID);
                        }
                    }
                });
        ((LifeSubscription)mView).bindSubscription(singlePageSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        ((LifeSubscription)mView).bindSubscription(controlSubscription);
        ((LifeSubscription)mView).bindSubscription(planSubscription);
        ((LifeSubscription)mView).bindSubscription(mPhySubscription);
    }

    //启停控制，可能将来要删除
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

                @Override
                public void onError(Throwable e) {
                    System.out.println("启停控制发送失败");
                    this.unsubscribe();
                    setConnDisable();
                }
            });
        }
    }
    //单人监控请求
    public void sendRequest(String ID)
    {
        int id=Integer.parseInt(ID);
        //先查看多人监控是否有该病人，如果有，则不需要发送请求，如果没有，说明从左侧长按选择的，才需要发送请求
        //发送医嘱请求

        fetchExercisePlan(ID);
        if(lastMutiPatientID.contains(ID))
        {
            lastSinglePatientID=ID;
            mView.setPageState(AppConstants.STATE_SUCCESS);
            System.out.println("多人界面包含单人");
            System.out.println(hasMutiPatient);
            return;
        }
        //发送一个生理数据请求
        SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        singleMonitorRequest.setUserID(AppConstants.USER_ID);
        singleMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        singleMonitorRequest.setRequestID(AppConstants.SINGLE_REQ_ID);


        //List<Integer> patientID=lastMutiPatientID;
        lastMutiPatientID.add(id);
        singleMonitorRequest.setPatientID(lastMutiPatientID);
        singleMonitorRequest.setPatientNumber((short) lastMutiPatientID.size());
        lastSinglePatientID=ID;
        //hasMutiPatient.put(id,mutiPosition++);

        invoke(TcpUtils.send(singleMonitorRequest), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendsingle","error");
                mView.setPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("发送单人监控生理仪发送完成");
                this.unsubscribe();
            }
        });



        //List<Integer> patientIDList=new ArrayList<>();
        //lastMutiPatientID.add(Integer.parseInt(ID));
        //patientIDList.add(Integer.parseInt(ID));
        //还要根据患者是否已经处于多人监控状态而发送运动数据请求

        //if(!hasMutiPatient.containsKey(id))//如果多人监控界面不含有该单人监控患者，则需要发送该请求
        //{
            //lastMutiPatientID.add(id);
            MutiMonitorRequest mutiMonitorRequest=new MutiMonitorRequest(CommandTypeConstant.MUTI_MONITOR_REQUEST);
            mutiMonitorRequest.setUserID(AppConstants.USER_ID);
            mutiMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
            mutiMonitorRequest.setRequestID(AppConstants.SINGLE_SPORT_REQ_ID);
            mutiMonitorRequest.setPatientNumber((short)lastMutiPatientID.size());
            mutiMonitorRequest.setPatientID(lastMutiPatientID);//zhiyouyige
            invoke(TcpUtils.send(mutiMonitorRequest), new Callback<Void>() {
                @Override
                public void onError(Throwable e) {
                    Log.e("sendsingle","error");
                    mView.setPageState(AppConstants.STATE_ERROR);
                    this.unsubscribe();
                    setConnDisable();
                }
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    System.out.println("发送单人监控康复设备发送完成");
                    this.unsubscribe();
                }
            });
        //}
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
