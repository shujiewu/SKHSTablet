package cn.sk.skhstablet.presenter;

/**
 * Created by wyb on 2017/4/25.
 */

public interface ILoginPresenter {
    interface View extends BaseView<String>{
    }
    interface Presenter{
        void fetchStateData();
    }
}
