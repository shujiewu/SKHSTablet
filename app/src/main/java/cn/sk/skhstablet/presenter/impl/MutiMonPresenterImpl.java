package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {

        sendRequest();
        fetchResponse();

    }
    public void sendRequest()
    {
        invoke(TcpUtils.send("hello worldx!"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    List<PatientDetail> mData=new ArrayList<>();
    public void fetchResponse()
    {
        invoke(Observable.from(PatientDetailList.PATIENTS), new Callback<PatientDetail>() {
            @Override
            public void onResponse(PatientDetail data) {

                Log.e("second",data.toString());
                //if(data.equals("echo=> hello worldx!"))
                //{
                   // List<PatientDetail> mData =PatientDetailList.PATIENTS;
                   // mData.get(0).setName(data);
                   // mView.refreshView(data);
                //}
                mData.add(data);
                mView.refreshView(mData);
            }
        });
    }
    @Inject
    public MutiMonPresenterImpl()
    {

    }
}
