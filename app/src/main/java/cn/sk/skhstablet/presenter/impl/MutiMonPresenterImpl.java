package cn.sk.skhstablet.presenter.impl;

import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;

/**
 * Created by wyb on 2017/4/25.
 */

public class MutiMonPresenterImpl extends BasePresenter<IMutiMonPresenter.View> implements IMutiMonPresenter.Presenter {
    @Override
    public void fetchPatientDetailData() {
        List<PatientDetail> mData =PatientDetailList.PATIENTS;
        mView.refreshView(mData);
    }
    @Inject
    public MutiMonPresenterImpl()
    {

    }
}
