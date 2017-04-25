package cn.sk.skhstablet.ui.base;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import cn.sk.skhstablet.presenter.BasePresenter;

/**
 * Created by wyb on 2017/4/25.
 */

public abstract class BaseFragment <P extends BasePresenter> extends Fragment {
    @Inject
    protected P mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initInject();
        return container;
    }
    public void showSnackar(View view ,String string){
        Snackbar.make(view, string, Snackbar.LENGTH_LONG)
                .show();
    }
    protected abstract void initInject();
    protected abstract void loadData();
}
