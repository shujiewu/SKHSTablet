package cn.sk.skhstablet.presenter;

import java.util.List;

import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by wyb on 2017/4/25.
 */

public interface IMutiMonPresenter {
    interface View extends BaseView<List<PatientDetail>>{
    }
    interface Presenter{
        void fetchPatientDetailData();
    }
}