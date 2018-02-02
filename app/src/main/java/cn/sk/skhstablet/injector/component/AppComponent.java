package cn.sk.skhstablet.injector.component;

import javax.inject.Singleton;

import cn.sk.skhstablet.app.App;
import cn.sk.skhstablet.injector.module.AppModule;
import dagger.Component;

/**
 * Created by ldkobe on 2017/5/7.
 * injector包中依赖注入的代码可以不用理解，因为开始为了学习dagger框架而采用的，主要用于presenter和adaper的初始化
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    App getContext();  // 提供App的Context
}
