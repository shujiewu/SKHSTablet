package cn.sk.skhstablet.presenter;

import java.util.List;

import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by wyb on 2017/4/25.
 */

public interface IMutiMonPresenter {
    interface View extends BaseView<List<PatientDetail>>{
        void refreshView(PatientDetail mData,int position);
        void setPageState(int state);
        int getPageState();
    }
    interface Presenter{
        void fetchPatientDetailData();
        void registerFetchResponse();
    }
}
