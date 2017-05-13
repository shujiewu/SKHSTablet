package cn.sk.skhstablet.presenter;

/**
 * Created by wyb on 2017/4/25.
 */

public interface ILoginPresenter {
    interface View extends BaseView<Boolean>{
        void setLoginDisable();
    }
    interface Presenter{
        void fetchStateData(String userID,String key);
        void registerFetchResponse();
        void sendFormatRequest();
    }
}
