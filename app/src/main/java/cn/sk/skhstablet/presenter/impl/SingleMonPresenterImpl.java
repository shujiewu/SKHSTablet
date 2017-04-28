package cn.sk.skhstablet.presenter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ISingleMonPresenter;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class SingleMonPresenterImpl extends BasePresenter<ISingleMonPresenter.View> implements ISingleMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData(String ID) {
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
    public void sendRequest(String ID)
    {
        invoke(TcpUtils.send(ID), new Action1<Void>() {
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
