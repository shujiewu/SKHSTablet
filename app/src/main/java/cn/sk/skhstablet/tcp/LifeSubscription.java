package cn.sk.skhstablet.tcp;

import rx.Subscription;

/**
 * Created by ldkobe on 2017/3/21.
 */

public interface LifeSubscription {
    void bindSubscription(Subscription subscription);
}

