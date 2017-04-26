package cn.sk.skhstablet.injector.component.fragment;

import javax.inject.Singleton;

import cn.sk.skhstablet.ui.fragment.FragmentChangeKey;
import cn.sk.skhstablet.ui.fragment.FragmentLogin;
import dagger.Component;

/**
 * Created by wyb on 2017/4/26.
 */
@Singleton
@Component()
public interface LoginComponent {
    void injectLogin(FragmentLogin fragmentLogin);
    void injectChangeKey(FragmentChangeKey fragmentChangeKey);
}
