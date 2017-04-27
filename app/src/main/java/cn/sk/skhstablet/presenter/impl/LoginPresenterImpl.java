package cn.sk.skhstablet.presenter.impl;

import android.util.Log;

import javax.inject.Inject;
import javax.security.auth.login.LoginException;

import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.ILoginPresenter;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import rx.functions.Action1;

/**
 * Created by wyb on 2017/4/25.
 */

public class LoginPresenterImpl extends BasePresenter<ILoginPresenter.View> implements ILoginPresenter.Presenter {
    @Override
    public void fetchStateData() {

        invoke(TcpUtils.connect("localhost", 60000), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                //mView.refreshView(data);
                fetchVerifyState();
                //fetchVerifyState();
                for(int i=0;i<10;i++)
                {

                    try {
                        Thread.sleep(10);
                        sendVerify(i);

                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //mView.refreshView("121");

            }
        });
    }
    public void sendVerify(int i)
    {
        invoke(TcpUtils.send("hello world!"+String.valueOf(i)), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                System.out.println("send success!");
            }
        });
    }
    /*public void fetchVerifyState()
    {
        invoke(TcpUtils.receive(), new Callback<String>() {
            @Override
            public void onResponse(String data) {
                Log.e("first",data);
                //if(data.equals("echo=> hello world!0"))
                    mView.refreshView(data);
            }
        });
    }*/
    @Inject
    public LoginPresenterImpl()
    {
    }
}
