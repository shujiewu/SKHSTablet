package cn.sk.skhstablet.injector.module;

import android.app.Activity;
import android.support.v4.app.Fragment;

import cn.sk.skhstablet.injector.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by quantan.liu on 2017/3/21.
 */

@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @FragmentScope
    public Activity provideActivity() {
        return fragment.getActivity();
    }

}
