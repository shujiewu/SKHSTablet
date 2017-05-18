package cn.sk.skhstablet.presenter;

/**
 * Created by wyb on 2017/4/25.
 */

public interface IChangekeyPresenter {
    interface View extends BaseView<Byte>{
    }
    interface Presenter{
        void sendRequest(String loginName,String oldKey,String newKey);
        void registerFetchResponse();
    }
}
