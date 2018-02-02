package cn.sk.skhstablet.tcp;

import rx.Subscription;

/**
 * Created by ldkobe on 2017/3/21.
 * 这是一个用来管理RxJava观察者的生命周期用到的接口，在activity或fragment中实现，绑定观察者和activity或fragment的生命周期相同
 * 如果不取消观察者的订阅，会有内存泄漏的问题！
 */

public interface LifeSubscription {
    void bindSubscription(Subscription subscription);
}

