package cn.sk.skhstablet.injector.module.fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import cn.sk.skhstablet.adapter.MutiMonitorAdapter;
import cn.sk.skhstablet.model.PatientDetail;
import dagger.Module;
import dagger.Provides;

/**
 * Created by wyb on 2017/4/25.
 */
@Module
public class MutiMonitorModule {
    @Provides
    @Singleton
    public MutiMonitorAdapter provideAdapter() {
        return new MutiMonitorAdapter(new ArrayList<PatientDetail>());//new ArrayList()这样子也可以，不过这里我们为了给自己看就写了泛型。
    }
}
