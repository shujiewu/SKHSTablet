package cn.sk.skhstablet.tcp;

/**
 * Created by wyb on 2017/4/27.
 * 这个接口用于设置页面状态，主要是单人监控和多人监控界面，因为它们有四种可能的状态，详见LoadingPage
 */

public interface Stateful {
    void setState(int state);
}
