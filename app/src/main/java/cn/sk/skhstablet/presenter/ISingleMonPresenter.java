package cn.sk.skhstablet.presenter;

import java.util.List;

import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by wyb on 2017/4/25.
 */

public interface ISingleMonPresenter {
    interface View extends BaseView<PatientDetail>{
        void refreshExercisePlan(List<String> armTypes,List<List<String>> arms);
        void setPageState(int state);
        void setControlState(byte resultState,byte controlState);
    }
    interface Presenter{
        void sendPatientDetailRequest(String ID);
        void fetchExercisePlan();
        void registerFetchResponse();
        void sendControlStartStop(int patientID,String deviceID,byte type);
    }
}
