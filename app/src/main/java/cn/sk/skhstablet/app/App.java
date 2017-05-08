package cn.sk.skhstablet.app;

import android.app.Application;

import com.blankj.utilcode.utils.Utils;

import cn.sk.skhstablet.injector.component.AppComponent;
//import cn.sk.skhstablet.injector.component.DaggerAppComponent;
import cn.sk.skhstablet.injector.component.DaggerAppComponent;
import cn.sk.skhstablet.injector.module.AppModule;

/**
 * Created by ldkobe on 2017/5/7.
 */

public class App extends Application {

    //现在只完成了dagger2和Retrofit配合完成网络请求
    private static App instance;
    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(this);//一个utils库的初始化 https://github.com/Blankj/AndroidUtilCode/blob/master/README-CN.md
    }

    public static AppComponent getAppComponent(){
       if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(instance))
                    .build();
        }
        return appComponent;
    }

}