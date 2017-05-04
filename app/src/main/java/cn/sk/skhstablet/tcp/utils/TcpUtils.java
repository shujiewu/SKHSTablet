package cn.sk.skhstablet.tcp.utils;

import android.util.Log;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;

import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.handler.CUSUMHandler;
import cn.sk.skhstablet.handler.ProtocolDecoder;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BaseView;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.Stateful;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.client.TcpClient;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import static cn.sk.skhstablet.model.PatientDetailList.phyValue;
import static cn.sk.skhstablet.model.PatientDetailList.sportName;
import static cn.sk.skhstablet.model.PatientDetailList.sportValue;

/**
 * Created by wyb on 2017/4/27.
 */

public class TcpUtils {
    public static  <T> void invoke(BaseView lifecycle,Observable<T> observable, Action1<T> callback)
    {
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }
    public static  <T> void invoke(Observable<T> observable, Callback<T> callback)
    {
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }
    public static <T> void invoke(BaseView lifecycle, Observable<T> observable, Callback<T> callback) {
        Stateful target = null;
        target=(Stateful)lifecycle;
        callback.setTarget(target);
        /*if (lifecycle instanceof Stateful) {
            target = (Stateful) lifecycle;
            callback.setTarget(target);
        }*/
        /**
         * 先判断网络连接状态和网络是否可用，放在回调那里好呢，还是放这里每次请求都去判断下网络是否可用好呢？
         * 如果放在请求前面太耗时了，如果放回掉提示的速度慢，要10秒钟请求超时后才提示。
         * 最后采取的方法是判断网络是否连接放在外面，网络是否可用放在回掉。
         */
/*        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShortToast("网络连接已断开");
            if (target != null) {
                target.setState(AppConstants.STATE_ERROR);
            }
            return;
        }
*/
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
       // lifecycle.bindSubscription(subscription);

    }
    static Connection<AbstractProtocol,String> mConnection;

    public static Observable<Boolean> connect(final String url, final int port) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                TcpClient.newClient(url, port)
                        .<String,AbstractProtocol>addChannelHandlerLast("linebase",
                                new Func0<ChannelHandler>() {
                                    @Override
                                    public ChannelHandler call() {
                                        return   new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 32768,
                                                4, 2, 0, 0, true);
                                    }
                                })
                        .<String,AbstractProtocol>addChannelHandlerLast("checksum",
                                new Func0<ChannelHandler>() {
                                    @Override
                                    public ChannelHandler call() {
                                        return   new CUSUMHandler();
                                    }
                                })
                        .<String,AbstractProtocol>addChannelHandlerLast("decoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new ProtocolDecoder();
                            }
                        })
                        .<String,AbstractProtocol>addChannelHandlerLast("encoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new StringEncoder();
                            }

                        })
                        //.readTimeOut(spacingTime, TimeUnit.SECONDS)
                        .createConnectionRequest()

                        .subscribe(new Observer<Connection<AbstractProtocol, String>>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();Log.e("r","rc2");
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                                //reconnect();
                                Log.e("r","rc");
                             }

                            @Override
                            public void onNext(Connection<AbstractProtocol, String> connection) {
                                mConnection = connection;
                                subscriber.onNext(true);
                                Log.e("10.40","1");
                            }
                        });
            }
        });
    }
    public static Observable<AbstractProtocol> receive() {
        if (mConnection != null) {
            return mConnection.getInput();
        }
        return null;
    }

    public static Observable<Void> send(String s) {
        Log.e("10.40","5");
        return mConnection.write(Observable.just(s));
    }


    private static int spacingTime =2;
    /**
     * 断开自动重新连接
     */
    public static void reconnect(final BaseView lifecycle) {

        //延迟spacingTime秒后进行重连
        Observable.timer(spacingTime, TimeUnit.SECONDS).subscribe((new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (mConnection != null)
                {
                    mConnection.closeNow();
                    Log.e("error", "close");
                }
                Log.e("error", "reconnect");
                invoke(lifecycle,TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
                    @Override
                    public void onResponse(Boolean data) {
                        fetchData();
                    }
                });
            }
        }));
    }

    public static void fetchData()
    {
        Log.e("error", "refetch");
        invoke(TcpUtils.receive(), new Callback<AbstractProtocol>() {
            @Override
            public void onResponse(final AbstractProtocol data) {
                // ByteBuf buf = (ByteBuf)data;
                // byte[] req = new byte[buf.readableBytes()];
                //  buf.readBytes(req);
//                int i=((ExerciseEquipmentDataResponse)data).getPatientId();
//                Log.e("login",String.valueOf(i));
                Log.e("10.40","2");
                if(data==null)
                    return;
                byte dataType=0;
                switch (data.getCommand())
                {
                    case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_REQUEST: {
                        dataType=(byte)((ExercisePhysiologicalDataResponse)data).getDeviceId().getDeviceType();
                        break;
                    }
                    case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_REQUEST: {
                        dataType=(byte)((ExerciseEquipmentDataResponse)data).getDeviceId().getDeviceType();
                        break;
                    }
                }

                switch (dataType)
                {
                    case CommandTypeConstant.LOGIN_SUCCESS:
                        RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                        break;
                    case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_REQUEST:
                        RxBus.getDefault().post(AppConstants.SINGLE_DATA, new PatientDetail("张er1", "1", "跑步机","10%   第一段", PatientDetailList.phyName,phyValue,sportName,sportValue));
                        break;
                    case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_REQUEST:
                        ExerciseEquipmentDataResponse exercisedata=(ExerciseEquipmentDataResponse)data;
                        PatientDetail patientDetail=new PatientDetail("张er1", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue);
                        patientDetail.setId(String.valueOf(exercisedata.getPatientId()));
                        patientDetail.setPercent(String.valueOf(exercisedata.getExercisePlanCompletionRate()));
                        RxBus.getDefault().post(AppConstants.MUTI_DATA, patientDetail);
                        RxBus.getDefault().post(AppConstants.MUTI_DATA, new PatientDetail("张er2", "2", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                        break;
                }



                //RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                /*if(data.equals("echo=> hello world!"))
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
                }*/
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
