package cn.sk.skhstablet.injector.component;

import javax.inject.Singleton;

import cn.sk.skhstablet.app.App;
import cn.sk.skhstablet.injector.module.AppModule;
import dagger.Component;

/**
 * Created by ldkobe on 2017/5/7.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    App getContext();  // 提供App的Context
}
