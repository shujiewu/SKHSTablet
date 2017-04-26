package cn.sk.skhstablet.injector.component.fragment;

import javax.inject.Singleton;

import cn.sk.skhstablet.injector.module.activity.MainActivityModule;
import cn.sk.skhstablet.ui.activity.MainActivity;
import dagger.Component;

/**
 * Created by wyb on 2017/4/26.
 */
@Singleton
@Component(modules = { MainActivityModule.class})
public interface MainActivtityComponent {
    void injectMainActivity(MainActivity mainActivity);
}
