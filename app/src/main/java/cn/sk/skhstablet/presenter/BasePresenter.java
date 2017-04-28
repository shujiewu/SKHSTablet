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
    public void fetchVerifyState()
    {
        invoke(TcpUtils.receive(), new Callback<String>() {
            @Override
            public void onResponse(final String data) {
                Log.e("first",data);
                if(data.equals("echo=> hello world!0"))
                     mView.refreshView(data);
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
                    RxBus.getDefault().post(1, new PatientDetail("张er", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                    RxBus.getDefault().post(1, new PatientDetail("张er2", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                    /*onNextAction = new Action1<PatientDetail>() {
                        // onNext()
                        @Override
                        public void call(PatientDetail s) {
                            PatientDetailList.PATIENTS.get(0).setName(data);
                        }
                    };
                    observable = Observable.create(new Observable.OnSubscribe<PatientDetail>() {
                        @Override
                        public void call(Subscriber<? super PatientDetail> subscriber) {
                            subscriber.onNext(new PatientDetail("张er", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                            //subscriber.onNext("Hi");
                            //subscriber.onNext("Aloha");
                            //subscriber.onCompleted();
                        }
                    });*/
                    //PatientDetailList.PATIENTS.get(0).setName(data);
                    //PatientDetailList.PATIENTS.add(new PatientDetail("张er", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                   // }catch (InterruptedException e) {
                    //    e.printStackTrace();
                   // }

                    //mView.refreshView(mData);
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
