package cn.sk.skhstablet.presenter;

import java.util.List;

import cn.sk.skhstablet.model.Patient;

/**
 * Created by wyb on 2017/4/25.
 */

public interface IPatientListPresenter {
    interface View extends BaseView<List<Patient>>{
    }
    interface Presenter{
        void fetchPatientListData();
    }
}
