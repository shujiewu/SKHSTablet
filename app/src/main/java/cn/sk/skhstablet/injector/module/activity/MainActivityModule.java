package cn.sk.skhstablet.injector.module.activity;

import java.util.ArrayList;

import javax.inject.Singleton;

import cn.sk.skhstablet.adapter.PatientListAdapter;
import cn.sk.skhstablet.adapter.PatientParaAdapter;
import cn.sk.skhstablet.model.Patient;
import dagger.Module;
import dagger.Provides;

/**
 * Created by wyb on 2017/4/25.
 */
@Module
public class MainActivityModule {
    @Provides
    @Singleton
    public PatientListAdapter provideDevAdapter() {
        return new PatientListAdapter(new ArrayList<Patient>());
    }
}
