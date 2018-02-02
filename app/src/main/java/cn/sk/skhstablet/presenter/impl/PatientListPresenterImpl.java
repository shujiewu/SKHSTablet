package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import static cn.sk.skhstablet.app.AppConstants.SIGN_OUT_PATIENT_LIST;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.canModify;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.isCancelSingle;
import static cn.sk.skhstablet.app.AppConstants.isLogout;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.app.AppConstants.lastSinglePatientID;
import static cn.sk.skhstablet.app.AppConstants.mutiDatas;
import static cn.sk.skhstablet.app.AppConstants.mutiPosition;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.app.AppConstants.singleMonitorID;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;

/**
 * Created by wyb on 2017/4/25.
 * 全局患者监控的presenter实现，对应于mainactivity，这个类的名字起的不好，应该是MainActivityPresenterImpl
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {

    //患者id和所处列表位置的映射
    public HashMap<Integer,Integer> hasPatient=new HashMap<>();
    //患者数据
    public List<Patient> mDatas=new ArrayList<>();
    //当前最后一位患者所处的位置
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

    //这样做耦合度有点高
    //发送多人监控的命令，即用户点击“开始监控”按钮之后
    @Override
    public void sendMutiMonitorRequest(List<Integer> patientID) {
        MutiMonitorRequest mutiMonitorRequest=new MutiMonitorRequest(CommandTypeConstant.MUTI_MONITOR_REQUEST);
        mutiMonitorRequest.setUserID(AppConstants.USER_ID);
        mutiMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequest.setRequestID(AppConstants.MUTI_REQ_ID);
        mutiMonitorRequest.setPatientNumber((short) patientID.size());
        mutiMonitorRequest.setPatientID(patientID);
        lastMutiPatientID=patientID;  //将多人监控的患者id列表赋给一个全局变量，为了可以重新发送此请求
        invoke(TcpUtils.send(mutiMonitorRequest),new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                System.out.println("多人监控运动设备请求发送失败");
                mView.setMutiPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("多人监控运动设备请求发送完成");
                this.unsubscribe();
            }
        });

        SingleMonitorRequest mutiMonitorRequestPhy=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        mutiMonitorRequestPhy.setUserID(AppConstants.USER_ID);
        mutiMonitorRequestPhy.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequestPhy.setRequestID(AppConstants.MUTI_REQ_ID);
        mutiMonitorRequestPhy.setPatientNumber((short) patientID.size());
        mutiMonitorRequestPhy.setPatientID(patientID);
        invoke(TcpUtils.send(mutiMonitorRequestPhy),new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                System.out.println("多人监控生理数据请求发送失败");
                mView.setMutiPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("多人监控生理数据请求发送完成");
                this.unsubscribe();
            }
        });
    }

    //发送取消单人监控命令，因为“取消监控”的按钮也在MainActivity中，这里只写了取消了对生理数据的订阅，其实该患者的运动设备数据并未取消订阅，这里可能会有问题
    @Override
    public void sendCancelSingleMonitorReq() {
        /*SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
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
        });*/
    }

    //发送注销命令请求
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
        //全局患者数据响应的观察者
        Subscription listSubscription = RxBus.getDefault().toObservable(AppConstants.PATIENT_LIST_DATA_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b==CommandTypeConstant.PATIENT_LIST_SUCCESS)
                        {
                            int patientID;
                            //这里有一个问题，如果患者更新数据快的话， PATIENT_LIST_DATA数据可能会被新到来的数据异步修改，这里本来打算采用canModify作为信号量,但是在TcpUtils中遇到了死循环的问题，暂时还没解决
                            //可以在这里将PATIENT_LIST_DATA赋值给一个新的局部变量,还没有测试这里
                            canModify=false;
                            List<Patient> PatientListData=PATIENT_LIST_DATA;
                            for(Patient patient: PatientListData)
                            {
                                patientID=patient.getPatientID();
                                if(hasPatient.containsKey(patientID))//说明已存在该患者,且只有一个人，这里是患者状态更新
                                {
                                    patient.setName(mDatas.get(hasPatient.get(patientID)).getName());
                                    patient.setGender(mDatas.get(hasPatient.get(patientID)).getGender());
                                    patient.setHospitalNumber(mDatas.get(hasPatient.get(patientID)).getHospitalNumber());
                                    patient.setSelectStatus(mDatas.get(hasPatient.get(patientID)).getSelectStatus());
                                    mDatas.set(hasPatient.get(patientID),patient);
                                    mView.refreshView(patient,hasPatient.get(patientID));//根据位置更新患者
                                    return;
                                }
                                else
                                {
                                    mDatas.add(patient);          //说明该患者不存在，增加该患者到全局监控列表
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
                        else if(b==CommandTypeConstant.SIGN_OUT_RESPONSE)
                        {
                            if(SIGN_OUT_PATIENT_LIST.size()>0)
                            {
                                boolean changeMuti=false;
                                boolean changeSingle=false;
                                for(Integer id:AppConstants.SIGN_OUT_PATIENT_LIST)
                                {
                                    int changeposition=hasPatient.get(id);
                                    mDatas.remove(changeposition);
                                    //修改其他病人的位置
                                    Iterator iter = hasPatient.entrySet().iterator();
                                    while (iter.hasNext()) {
                                        Map.Entry entry = (Map.Entry) iter.next();
                                        Integer patientPosition = (Integer) entry.getValue();
                                        Integer patientID=(Integer)entry.getKey();
                                        if(patientPosition>changeposition)
                                            hasPatient.put(patientID,--patientPosition);
                                    }
                                    hasPatient.remove(id);
                                    position--;
                                    if(hasMutiPatient.containsKey(id))
                                    {
                                        int mutiposition=hasMutiPatient.get(id);
                                        //修改其他病人的位置
                                        mutiDatas.remove(mutiposition);
                                        Iterator mutiiter = hasMutiPatient.entrySet().iterator();
                                        while (mutiiter.hasNext()) {
                                            Map.Entry entry = (Map.Entry) mutiiter.next();
                                            Integer patientPosition = (Integer) entry.getValue();
                                            Integer patientID=(Integer)entry.getKey();
                                            if(patientPosition>mutiposition)
                                                hasMutiPatient.put(patientID,--patientPosition);
                                        }
                                        hasMutiPatient.remove(id);
                                        mutiPosition--;
                                        changeMuti=true;
                                    }
                                    if(singleMonitorID!=null&&singleMonitorID.equals(String.valueOf(id)))
                                    {
                                        singleMonitorID=null;
                                        changeSingle=true;
                                    }

                                }
                                mView.refreshView(mDatas);
                                if(changeMuti)
                                    mView.refreshMutiView();
                                if(changeSingle)
                                    mView.refreshSinlgeView();
                                SIGN_OUT_PATIENT_LIST.clear();
                            }
                        }
                        else {
                            System.out.println("未登录");
                        }
                    }
                });
        ((LifeSubscription)mView).bindSubscription(listSubscription);

        //退出状态响应的观察者
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

        //重连成功后发送重连命令的观察者
        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s)
                        {
                            sendVerify();//重连成功后发送用户验证
                        }
                        else
                        {
                            System.out.println("网络有问题，请过段时间再试");
                        }
                            //mView.reSendRequest();
                    }
                });
        ((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);
        //对重连成功，发送用户验证得到响应的观察者
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.LOGIN_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        Log.e("login","succees2");
                        if(s)
                        {
                            //说明是发送退出命令失败
                            if(isLogout)
                            {
                                sendLogoutRequest();
                                return;
                            }
                            //说明是取消单人监控命令发送失败
                            if(isCancelSingle)
                            {
                                sendCancelSingleMonitorReq();
                                return;
                            }
                            //如果以下这两个参数为null,说明发送参数格式解析请求时出错
                            if(SPORT_DEV_FORM==null||MON_DEV_FORM==null)
                            {
                                sendFormatRequest();
                                //return;
                            }
                            //发送最新的全局患者列表请求
                            sentPatientListRequest();
                            //原来写的是也重新发送单人监控和多人监控请求，但是后来改为由用户点击空白处重新发送请求
                            /*if(lastMutiPatientID!=null)
                                sendMutiMonitorRequest(lastMutiPatientID);*/

                            /*if(lastSinglePatientID!=null)
                                mView.loadSinglePatient(lastSinglePatientID);*/
                        }
                    }
                });
        ((LifeSubscription)mView).bindSubscription(mSubscription);
        //多人监控响应的观察者
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

        //在其他设备登录的响应的观察者
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
    //这个函数用于主界面中，因为掉线重新连接到服务器后重新发送用户验证
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

    //发送控制命令
    @Override
    public void sendControl(String deviceID,byte parameterCode,byte paraType,short paraControlValue) {
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

    //发送参数解析格式请求
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
