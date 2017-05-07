package cn.sk.skhstablet.presenter.impl;

import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IPatientListPresenter;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {
    @Override
    public void fetchPatientListData() {
        List<Patient> mDatas= PatientList.PATIENTS;
        mView.refreshView(mDatas);
    }

    @Override
    public void sendMutiMonitorRequest() {
        invoke(TcpUtils.send("sendMutiMonitorRequest"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Override
    public void sendCancelSingleMonitorReq() {
        invoke(TcpUtils.send("CancelSingleMonitorReq"), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }

    @Inject
    public PatientListPresenterImpl()
    {

    }

}
