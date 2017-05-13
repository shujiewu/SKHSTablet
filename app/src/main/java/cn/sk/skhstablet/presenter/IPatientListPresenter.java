package cn.sk.skhstablet.presenter;

import java.util.List;

import cn.sk.skhstablet.model.Patient;

/**
 * Created by wyb on 2017/4/25.
 */

public interface IPatientListPresenter {
    interface View extends BaseView<List<Patient>>{
        void refreshView(Patient mData,int position);
        void setMutiPageState(int state);
        void setSinglePageState(int state);
        void logoutSuccess(boolean b);
    }
    interface Presenter{
        void sentPatientListRequest();
        void sendMutiMonitorRequest(List<Integer> patientID);
        void sendCancelSingleMonitorReq();
        void sendLogoutRequest();
        void registerFetchResponse();
    }
}
