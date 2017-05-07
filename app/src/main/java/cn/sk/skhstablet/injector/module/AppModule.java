package cn.sk.skhstablet.injector.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import cn.sk.skhstablet.app.App;
/**
 * Created by quantan.liu on 2017/3/21.
 */
@Module
public class AppModule {

    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplicationContext() {
        return application;
    }
}
