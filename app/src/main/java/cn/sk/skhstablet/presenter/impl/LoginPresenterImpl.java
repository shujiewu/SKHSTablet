package cn.sk.skhstablet.presenter.impl;

import javax.inject.Inject;

import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ILoginPresenter;

/**
 * Created by wyb on 2017/4/25.
 */

public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.View> implements ILoginPresenter.Presenter {
    @Override
    public void fetchStateData() {
        mView.refreshView(true);
    }
    @Inject
    public LoginPresenterImpl()
    {

    }
}
