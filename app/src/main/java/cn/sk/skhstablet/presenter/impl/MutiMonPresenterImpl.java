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
import cn.sk.skhstablet.model.PatientPhyData;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.protocol.SportDevForm;
import cn.sk.skhstablet.protocol.up.MutiMonitorRequest;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
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
import static cn.sk.skhstablet.app.AppConstants.mutiDatas;
import static cn.sk.skhstablet.app.AppConstants.mutiPosition;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.setConnDisable;
import static cn.sk.skhstablet.utlis.Utils.secToTime;

/**
 * Created by wyb on 2017/4/25.
 * 多人监控的Presenter实现，对应于MutiMonitorFragment
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    //发送多人监控请求
    @Override
    public void fetchPatientDetailData() {
        sendRequest();
    }

    //注册观察者
    @Override
    public void registerFetchResponse() {
        //注册多人监控运动数据返回的观察者
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        //如果多人监控界面是成功界面，则判断界面是否包含此患者，如果包含该患者，则直接局部更新患者数据，否则重新刷新整个页面

                        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
                        {
                            int patientID=s.getPatientID();
                            if(hasMutiPatient.containsKey(patientID))
                            {

                                int pos=hasMutiPatient.get(patientID);
                                System.out.println("患者"+patientID+"准备在多人界面更新运动数据"+pos);
                                s.setPhyDevValue(mutiDatas.get(pos).getPhyDevValue());
                                s.setPhyDevName(mutiDatas.get(pos).getPhyDevName());
                                mutiDatas.set(pos,s);
                                mView.refreshView(s,pos);
                            }
                            else
                            {
                                mutiDatas.add(s);
                                hasMutiPatient.put(patientID,mutiPosition++);
                                //System.out.println(hasMutiPatient);
                               // System.out.println("mtu"+mutiDatas.get(0).getName());
                                mView.refreshView(mutiDatas);

                            }
                        }//是成功界面才更新，否则抛弃
                    }
                });
        //注册多人监控生理数据返回的观察者
        Subscription mSubscription1 = RxBus.getDefault().toObservable(AppConstants.MUTI_PHY_DATA,PatientPhyData.class)
                .subscribe(new Action1<PatientPhyData>() {
                    @Override
                    public void call(PatientPhyData s) {
                        //如果多人监控界面是成功界面，则判断界面是否包含此患者，如果包含该患者，则直接局部更新患者数据，否则重新刷新整个页面
                        if(mView.getPageState()==AppConstants.STATE_SUCCESS)
                        {
                            int patientID=s.getPatientID();
                            if(hasMutiPatient.containsKey(patientID))
                            {
                                int position=hasMutiPatient.get(patientID);
                                System.out.println("患者"+patientID+"准备在多人界面更新生理数据"+position);
                                //更新生理数据

                                mutiDatas.get(position).setPhyDevName(s.getPhyDevName());
                                mutiDatas.get(position).setPhyDevValue(s.getPhyDevValue());
                                mView.refreshView(mutiDatas.get(position),position);
                            }
                        }//是成功界面才更新，否则抛弃
                    }
                });
        /*Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                            mView.reSendRequest();
                    }
                });*/


        /*Subscription mutiPageSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_REQ_STATE,Byte.class)
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte b) {
                        if(b== CommandTypeConstant.SUCCESS)
                        {
                            //mView.setPageState(AppConstants.STATE_SUCCESS);
                            mutiDatas.clear();
                            hasMutiPatient.clear();
                            mutiPosition=0;
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
                });*/
        //System.out.println("registermuti");
        //((LifeSubscription)mView).bindSubscription(mutiPageSubscription);

        ((LifeSubscription)mView).bindSubscription(mSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscription1);
        //((LifeSubscription)mView).bindSubscription(mSubscriptionRequest);

    }

    //这个函数主要是用于用户在加载失败界面空白处重新点击刷新之后才发送命令，用户在主界面上方点击“开始监控”按钮发送命令是在PatientListPresenterImpl中实现的，因为“开始监控”按钮属于Mainactivity而不是具体MutiFragment
    public void sendRequest()
    {
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
            @Override
            public void onError(Throwable e) {
                System.out.println("重新多人监控发送失败");
                mView.setPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
        });

        SingleMonitorRequest mutiMonitorRequestPhy=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        mutiMonitorRequestPhy.setUserID(AppConstants.USER_ID);
        mutiMonitorRequestPhy.setDeviceType(AppConstants.DEV_TYPE);
        mutiMonitorRequestPhy.setRequestID(AppConstants.MUTI_REQ_ID);
        mutiMonitorRequest.setPatientNumber((short) lastMutiPatientID.size());
        mutiMonitorRequest.setPatientID(lastMutiPatientID);
        invoke(TcpUtils.send(mutiMonitorRequestPhy),new Callback<Void>() {
            @Override
            public void onError(Throwable e) {
                System.out.println("重新多人监控生理数据请求发送失败");
                mView.setPageState(AppConstants.STATE_ERROR);
                this.unsubscribe();
                setConnDisable();
            }
            @Override
            public void onCompleted() {
                super.onCompleted();
                System.out.println("重新多人监控生理数据请求发送完成");
                this.unsubscribe();
            }
        });
    }
    //List<PatientDetail> mData=new ArrayList<>();
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

    /*void testMutiData1()
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
    }*/
}
