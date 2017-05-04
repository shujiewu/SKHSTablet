package cn.sk.skhstablet.presenter;

/**
 * Created by ldkobe on 2017/4/12.
 */

public interface BaseView<T> {
    void refreshView(T mData);//获取数据成功调用该方法。
    void reSendRequest();
}
