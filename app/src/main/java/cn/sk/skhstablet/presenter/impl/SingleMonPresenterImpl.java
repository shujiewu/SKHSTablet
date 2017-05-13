package cn.sk.skhstablet.presenter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ISingleMonPresenter;
import cn.sk.skhstablet.protocol.up.SingleMonitorRequest;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.base.BaseFragment;
import rx.Subscription;
import rx.functions.Action1;

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
        List<String> armTypes=new ArrayList<>(Arrays.asList("运动方案1：在跑步机上运动10分钟", "运动方案2：在椭圆机上运动200米", "运动方案3：在跑步机上运动5分钟", "运动方案4"));
        List<List<String>> arms=new ArrayList<>();
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动1分钟", "第二段：以1米每秒的速度运动1分钟")));
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动2分钟", "第二段：以1米每秒的速度运动2分钟")));
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动3分钟", "第二段：以1米每秒的速度运动3分钟")));
        arms.add(new ArrayList<>(Arrays.asList("第一段：以2米每秒的速度在跑步机上运动4分钟", "第二段：以1米每秒的速度运动4分钟")));
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


        Subscription singlePageSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_REQ_STATE,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean b) {
                        if(b==true)
                            mView.setPageState(AppConstants.STATE_SUCCESS);
                    }
                });
        ((LifeSubscription)mView).bindSubscription(singlePageSubscription);
        ((LifeSubscription)mView).bindSubscription(mSubscription);
    }

    public void sendRequest(String ID)
    {
        SingleMonitorRequest singleMonitorRequest=new SingleMonitorRequest(CommandTypeConstant.SINGLE_MONITOR_REQUEST);
        singleMonitorRequest.setUserID(AppConstants.USER_ID);
        singleMonitorRequest.setDeviceType(AppConstants.DEV_TYPE);
        singleMonitorRequest.setRequestID(AppConstants.SINGLE_REQ_ID);
        singleMonitorRequest.setPatientNumber((short) 1);
        singleMonitorRequest.setPatientID(Integer.parseInt(ID));
        invoke(TcpUtils.send(singleMonitorRequest), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    @Inject
    public SingleMonPresenterImpl()
    {

    }
}
