package cn.sk.skhstablet.injector.component.fragment;

import javax.inject.Singleton;

import cn.sk.skhstablet.injector.module.fragment.SingleMonitorModule;
import cn.sk.skhstablet.ui.fragment.SingleMonitorFragment;
import dagger.Component;

/**
 * Created by wyb on 2017/4/26.
 */
@Singleton
@Component(modules = { SingleMonitorModule.class})
public interface SingleMonitorComponent {
    void injectSingleMonitor(SingleMonitorFragment singleMonitorFragment);
}
