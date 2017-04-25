package cn.sk.skhstablet.injector.component.fragment;

import javax.inject.Singleton;

import cn.sk.skhstablet.injector.module.fragment.MutiMonitorModule;
import cn.sk.skhstablet.ui.fragment.MutiMonitorFragment;
import dagger.Component;

/**
 * Created by wyb on 2017/4/25.
 */
@Singleton
@Component(modules = { MutiMonitorModule.class})
public interface MutiMonitorComponent {
    void injectMutiMonitor(MutiMonitorFragment mutiMonitorFragment);
}
