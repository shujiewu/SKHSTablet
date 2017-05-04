package cn.sk.skhstablet.presenter;

import android.util.Log;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.Stateful;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.http.utils.HttpUtils;

import java.util.List;

import cn.sk.skhstablet.tcp.utils.TcpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import static cn.sk.skhstablet.model.PatientDetailList.phyValue;
import static cn.sk.skhstablet.model.PatientDetailList.sportName;
import static cn.sk.skhstablet.model.PatientDetailList.sportValue;

/**
 * Created by ldkobe on 2017/3/22.
 */

public class BasePresenter<T extends BaseView> {

    protected T mView;//指的是界面，也就是BaseFragment或者BaseActivity
    public void setView(T view)
    {
        this.mView=view;
    }
    public void setLifeSubscription(LifeSubscription mLifeSubscription) {
        this.mView = (T) mLifeSubscription;
    }

    protected <T> void invoke(Observable<T> observable, Callback<T> callback) {
        TcpUtils.invoke(mView, observable, callback);
    }
    protected <T> void invoke(Observable<T> observable, Action1<T> callback) {
        TcpUtils.invoke(mView, observable, callback);
    }
    /**
     * 给子类检查返回集合是否为空
     * 这样子做虽然耦合度高，但是接口都不是统一定的，我们没有什么更好的办法
     * @param list
     */
    public void checkState(List list) {
        if (list.size() == 0) {
            if (mView instanceof Stateful)
                ((Stateful) mView).setState(AppConstants.STATE_EMPTY);
            return;
        }
    }
}
