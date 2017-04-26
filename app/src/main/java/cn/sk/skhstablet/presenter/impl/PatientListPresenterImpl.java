package cn.sk.skhstablet.presenter.impl;

import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IPatientListPresenter;

/**
 * Created by wyb on 2017/4/25.
 */

public class PatientListPresenterImpl extends BasePresenter<IPatientListPresenter.View> implements IPatientListPresenter.Presenter {
    @Override
    public void fetchPatientListData() {
        List<Patient> mDatas= PatientList.PATIENTS;
        mView.refreshView(mDatas);
    }
    @Inject
    public PatientListPresenterImpl()
    {

    }

}
