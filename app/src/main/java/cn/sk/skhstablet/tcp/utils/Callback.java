package cn.sk.skhstablet.tcp.utils;

import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.Stateful;
import cn.sk.skhstablet.presenter.BaseView;
import cn.sk.skhstablet.ui.base.BaseFragment;
import cn.sk.skhstablet.utlis.NetworkHelper;
import rx.Subscriber;

import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.logoutUnsubscribe;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.mConnection;

//原来在这个类统一处理命令发送失败的问题，后来改为在发送命令的地方重写了，所以这里意义不大了

public class Callback<T> extends Subscriber<T> {
    private Stateful target;

    public void setTarget(Stateful target) {
        this.target = target;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        onfail();
    }

    @Override
    public void onNext(T data) {
        onResponse(data);
    }

    public void onResponse(T data) {
        ((BaseView) target).refreshView(data);
    }
    public void onfail() {
        /*if (!NetworkHelper.isAvailableByPing()) {
            ToastUtils.showShortToast("连接服务器失败，请检查连接");
            System.out.println("连接服务器失败，请检查连接");
            if (mConnection != null)
            {
                logoutUnsubscribe();
                mConnection.closeNow();
            }
            RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(false));
            netState=AppConstants.STATE_DIS_CONN;
        }
        else
        {
            System.out.println("尝试重新连接");
            TcpUtils.reconnect();
        }
        this.unsubscribe();*/
    }
}
