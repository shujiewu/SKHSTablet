package cn.sk.skhstablet.injector.module.fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import cn.sk.skhstablet.adapter.DevParaChangeAdapter;
import cn.sk.skhstablet.adapter.ExercisePlanAdapter;
import cn.sk.skhstablet.adapter.PatientParaAdapter;
import dagger.Module;
import dagger.Provides;

/**
 * Created by wyb on 2017/4/26.
 */
@Module
public class SingleMonitorModule {
    @Provides
    @Singleton
    public DevParaChangeAdapter provideDevAdapter() {
        return new DevParaChangeAdapter(new ArrayList<String>(),new ArrayList<String>());
    }
    @Provides
    @Singleton
    public PatientParaAdapter providePatientAdapter()
    {
        return new PatientParaAdapter(new ArrayList<String>(),new ArrayList<String>());
    }
    @Provides
    @Singleton
    public ExercisePlanAdapter providePlanAdapter()
    {
        return new ExercisePlanAdapter(new ArrayList<String>(),new ArrayList<List<String>>());
    }

}
