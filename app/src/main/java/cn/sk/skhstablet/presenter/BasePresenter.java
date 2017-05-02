package cn.sk.skhstablet.presenter;

import android.util.Log;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.Stateful;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.http.utils.HttpUtils;

import java.util.List;

import cn.sk.skhstablet.tcp.utils.TcpUtils;
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
    public Action1<PatientDetail> onNextAction;
    public Observable<PatientDetail> onNextAction1;
    public Observable observable;
    public void fetchData()
    {
        invoke(TcpUtils.receive(), new Callback<String>() {
            @Override
            public void onResponse(final String data) {

                if(data.equals("echo=> hello world!"))
                {
                    RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                    Log.e("login","succees");
                }
                else if(data.equals("echo=> hello worldx!"))
                {
                  //  List<PatientDetail> mData = PatientDetailList.PATIENTS;
                  //  mData.get(0).setName(data);
                    //try {
                        //Thread.sleep(5000);
                        //data.setName(String.valueOf(i));
                    //    PatientDetailList.PATIENTS.get(0).setName(data);
                    //    PatientDetailList.PATIENTS.get(1).setName(data);
                    Log.e("third",data);
                    RxBus.getDefault().post(AppConstants.MUTI_DATA, new PatientDetail("张er1", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                    RxBus.getDefault().post(AppConstants.MUTI_DATA, new PatientDetail("张er2", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                }
                else if(data.equals("echo=> 张三"))
                {
                    Log.e("third",data);
                    RxBus.getDefault().post(AppConstants.SINGLE_DATA, new PatientDetail("张er", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                }
                else if(data.equals("echo=> 李四"))
                {
                    Log.e("third",data);
                    RxBus.getDefault().post(AppConstants.SINGLE_DATA, new PatientDetail("李四", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                }
                else if(data.equals("echo=> 张er1"))
                {
                    Log.e("third",data);
                    RxBus.getDefault().post(AppConstants.SINGLE_DATA, new PatientDetail("张er1", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                }
                else if(data.equals("echo=> 张er2"))
                {
                    Log.e("third",data);
                    RxBus.getDefault().post(AppConstants.SINGLE_DATA, new PatientDetail("张er2", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                }
            }
        });
        /*invoke(TcpUtils.receive(), new Action1<String>() {
            @Override
            public void call(String aVoid) {
                System.out.println("send success!");
            }
        });*/
    }
}
