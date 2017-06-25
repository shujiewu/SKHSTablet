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
import cn.sk.skhstablet.protocol.up.MonitorDevFormRequest;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.protocol.up.PatientListRequest;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
import cn.sk.skhstablet.protocol.up.SportDevControlRequest;
import cn.sk.skhstablet.protocol.up.SportDevFormRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.fragment.SingleMonitorFragment;
import rx.Subscription;
import rx.functions.Action1;

import static cn.sk.skhstablet.app.AppConstants.CONTROL_REQ_ID;
import static cn.sk.skhstablet.app.AppConstants.LOGIN_KEY;
import static cn.sk.skhstablet.app.AppConstants.LOGIN_NAME;
import static cn.sk.skhstablet.app.AppConstants.MON_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_DATA;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NAME_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NUMBER_FORM;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.canModify;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.isCancelSingle;
import static cn.sk.skhstablet.app.AppConstants.isLogout;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.app.AppConstants.lastSinglePatientID;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

/**
 * Created by wyb on 2017/4/25.
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {

    public HashMap<Integer,Integer> hasPatient=new HashMap<>();
    public List<Patient> mDatas=new ArrayList<>();
    private int position=0;
    //发送全局病人列表请求
    @Override
    public void sentPatientListRequest() {
        PatientListRequest patientListRequest=new PatientListRequest(CommandTypeConstant.PATIENT_LIST_REQUEST);
        patientListRequest.setUserID(AppConstants.USER_ID);
        patientListRequest.setDeviceType(AppConstants.DEV_TYPE);
        patientListRequest.setRequestID(AppConstants.PATIENT_LIST_REQ_ID);
        invoke(TcpUtils.send(patientListRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("病人列表发送完成");
                this.unsubscribe();
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("病人列表发送失败");
                this.unsubscribe();
                setConnDisable();
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
        invoke(TcpUtils.send(mutiMonitorRequest),new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                System.out.println("多人监控发送失败");
                mView.setMutiPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("多人监控发送完成");
                this.unsubscribe();
            }
        });
    }

    @Override
    public void sendCancelSingleMonitorReq() {
        SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        singleMonitorRequest.setUserID(AppConstants.USER_ID);
        singleMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        singleMonitorRequest.setRequestID(AppConstants.SINGLE_REQ_ID);
        singleMonitorRequest.setPatientNumber((short) 0);
        //singleMonitorRequest.setPatientID(0);
        isCancelSingle=true;
        invoke(TcpUtils.send(singleMonitorRequest), new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendcancel","error");
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("取消单人监控发送完成");
                this.unsubscribe();
            }
        });
    }

    @Override
    public void sendLogoutRequest() {
        LogoutRequest logoutRequest=new LogoutRequest(CommandTypeConstant.LOGOUT_REQUEST);
        logoutRequest.setUserID(AppConstants.USER_ID);
        logoutRequest.setDeviceType(AppConstants.DEV_TYPE);
        logoutRequest.setRequestID(AppConstants.LOGOUT_REQ_ID);
        isLogout=true;
        invoke(TcpUtils.send(logoutRequest),new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                Log.e("sendlogout","error");
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("注销发送完成");
                this.unsubscribe();
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
                            canModify=false;
                            for(Patient patient: PATIENT_LIST_DATA)
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
                            canModify=true;
                            mView.refreshView(mDatas);
                            //testPatientList();
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
        ((LifeSubscription)mView).bindSubscription(listSubscription);

        Subscription logoutSubscription = RxBus.getDefault().toObservable(AppConstants.LOGOUT_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b)
                        {
                            mView.logoutSuccess(true);
                        }
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
        ((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        Log.e("login","succees2");
                        if(s==true)
                        {
                            if(isLogout)
                            {
                                sendLogoutRequest();
                                return;
                            }
                            if(SPORT_DEV_FORM==null||MON_DEV_FORM==null)
                            {
                                sendFormatRequest();
                                return;
                            }
                            sentPatientListRequest();
                            /*if(lastMutiPatientID!=null)
                                sendMutiMonitorRequest(lastMutiPatientID);*/
                            if(isCancelSingle)
                            {
                                sendCancelSingleMonitorReq();
                                return;
                            }
                            /*if(lastSinglePatientID!=null)
                                mView.loadSinglePatient(lastSinglePatientID);*/
                        }
                    }
                });
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        Subscription mutiPageSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_REQ_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b== CommandTypeConstant.SUCCESS)
                        {
                            mView.setMutiPageState(AppConstants.STATE_SUCCESS);
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
        ((LifeSubscription)mView).bindSubscription(mutiPageSubscription);


        Subscription loginOtherSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_OTHER_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b)
                        {
                            mView.loginOther(true);
                        }
                    }
                });
        ((LifeSubscription)mView).bindSubscription(loginOtherSubscription);
    }
    public void sendVerify()
    {
        LoginRequest request=new LoginRequest(CommandTypeConstant.LOGIN_REQUEST);
        request.setUserID(AppConstants.USER_ID);
        request.setDeviceType(AppConstants.DEV_TYPE);
        request.setRequestID(AppConstants.LOGIN_REQ_ID);
        request.setLoginName(LOGIN_NAME);
        request.setLoginKey(LOGIN_KEY);
        invoke(TcpUtils.send(request), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("重连验证发送完成");
                this.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("重连验证发送失败");
                this.unsubscribe();
                setConnDisable();
            }
        });
    }

    @Override
    public void sendControl(String deviceID,byte parameterCode,byte paraType,byte paraControlValue) {
        if(deviceID!=null)
        {
            SportDevControlRequest sportDevControlRequest=new SportDevControlRequest(CommandTypeConstant.SPORT_DEV_CONTROL_REQUEST);
            sportDevControlRequest.setUserID(AppConstants.USER_ID);
            sportDevControlRequest.setDeviceType(AppConstants.DEV_TYPE);
            sportDevControlRequest.setRequestID(CONTROL_REQ_ID);
            sportDevControlRequest.setDeviceID(deviceID);
            sportDevControlRequest.setParameterCode(parameterCode);
            sportDevControlRequest.setParaType(paraType);
            sportDevControlRequest.setParaControlValue(paraControlValue);
            invoke(TcpUtils.send(sportDevControlRequest), new Callback<Void>() {
                @Override
                public void onCompleted() {
                    super.onCompleted();
                    System.out.println("参数修改发送完成");
                    this.unsubscribe();
                }
                @Override
                public void onError(Throwable e) {
                    System.out.println("参数修改发送失败");
                    this.unsubscribe();
                    setConnDisable();
                }
            });
        }
    }

    @Override
    public void sendFormatRequest()
    {
        SportDevFormRequest sportDevFormRequest=new SportDevFormRequest(CommandTypeConstant.SPORT_DEV_FORM_REQUEST);
        sportDevFormRequest.setUserID(AppConstants.USER_ID);
        //System.out.println("AppConstants.USER_ID"+AppConstants.USER_ID);
        sportDevFormRequest.setDeviceType(AppConstants.DEV_TYPE);
        sportDevFormRequest.setRequestID(AppConstants.SPORT_FORM_REQ_ID);
        MonitorDevFormRequest monitorDevFormRequest=new MonitorDevFormRequest(CommandTypeConstant.MONITOR_DEV_FORM_REQUEST);
        monitorDevFormRequest.setUserID(AppConstants.USER_ID);
        monitorDevFormRequest.setDeviceType(AppConstants.DEV_TYPE);
        monitorDevFormRequest.setRequestID(AppConstants.PHY_FORM_REQ_ID);
        invoke(TcpUtils.send(sportDevFormRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("运动设备格式发送完成");
                this.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                System.out.println("运动设备格式发送失败");
                this.unsubscribe();
                setConnDisable();
            }
        });
        invoke(TcpUtils.send(monitorDevFormRequest), new Callback<Void>() {
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("监护格式发送完成");
                this.unsubscribe();
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                System.out.println("监护格式发送失败");
                this.unsubscribe();
                setConnDisable();
            }
        });
    }
    @Inject
    public PatientListPresenterImpl()
    {

    }
    void testPatientList()
    {
        int size=PatientList.PATIENTS.size();
        for(int i=0;i<size;i++)
        {
            if(!PATIENT_LIST_NAME_FORM.containsKey(PatientList.PATIENTS.get(i).getPatientID()))
            {
                PATIENT_LIST_NAME_FORM.put(PatientList.PATIENTS.get(i).getPatientID(),PatientList.PATIENTS.get(i).getName());
                PATIENT_LIST_NUMBER_FORM.put(PatientList.PATIENTS.get(i).getPatientID(),PatientList.PATIENTS.get(i).getHospitalNumber());
            }
        }
        int patientID;
        for(Patient patient:PatientList.PATIENTS)
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

}
