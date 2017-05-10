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
import cn.sk.skhstablet.protocol.down.DevNameResponse;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.protocol.down.LoginAckResponse;
import cn.sk.skhstablet.protocol.down.MonitorDevFormResponse;
import cn.sk.skhstablet.protocol.down.PatientListResponse;
import cn.sk.skhstablet.protocol.down.PushAckResponse;
import cn.sk.skhstablet.protocol.down.SportDevFormResponse;
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

import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_DATA;
import static cn.sk.skhstablet.app.CommandTypeConstant.MUTI_MONITOR_RESPONSE;
import static cn.sk.skhstablet.app.CommandTypeConstant.SUCCESS;
import static cn.sk.skhstablet.model.PatientDetailList.phyValue;
import static cn.sk.skhstablet.model.PatientDetailList.sportName;
import static cn.sk.skhstablet.model.PatientDetailList.sportValue;

/**
 * Created by wyb on 2017/4/27.
 */

public class TcpUtils {
    public static  <T> void invoke(LifeSubscription lifecycle,Observable<T> observable, Action1<T> callback)
    {
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        lifecycle.bindSubscription(subscription);
    }
    public static  <T> void invoke(Observable<T> observable, Callback<T> callback)
    {
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }
    public static <T> void invoke(LifeSubscription lifecycle, Observable<T> observable, Callback<T> callback) {
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
        /*if (!NetworkUtils.isConnected()) {
            ToastUtils.showShortToast("网络连接已断开");
            if (target != null) {
                //target.setState(AppConstants.STATE_ERROR);
            }
            return;
        }*/

        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        lifecycle.bindSubscription(subscription);

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
                        .channelOption(ChannelOption.SO_KEEPALIVE, true)
                        .channelOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000) //服务器掉线还要等五秒，自己掉线直接重连
                        //.readTimeOut(5, TimeUnit.SECONDS) //5秒未读到数据
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
    public  static void re(String url,int port)
    {
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
                .readTimeOut(spacingTime, TimeUnit.SECONDS)
                .createConnectionRequest()

                .subscribe(new Observer<Connection<AbstractProtocol, String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //reconnect();
                    }

                    @Override
                    public void onNext(Connection<AbstractProtocol, String> connection) {
                        RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(true));
                        mConnection = connection;
                        reconnectTime=0;
                        Log.e("20.40","1");
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
    private static int reconnectTime=0;
    public static void reconnect() {
        reconnectTime++;
        if(reconnectTime==20)
        {
            RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(false));
            reconnectTime=0;
            return;
        }

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
                //re(AppConstants.url, AppConstants.port);
                invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
                    @Override
                    public void onResponse(Boolean data) {
                        fetchData();
                        RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(true));
                        //lifecycle.reSendRequest();
                        reconnectTime=0;
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
                byte state;
                byte reqID;
                byte devType;
                int userID;
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
                    case CommandTypeConstant.DOCTOR_ADVICE_RESPONSE:
                        break;


                    case CommandTypeConstant.PATIENT_LIST_UPDATE_RESPONSE:
                    case CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE:  //完成注册
                        userID=((PatientListResponse)data).getUserID();
                        devType=((PatientListResponse)data).getDeviceType();
                        if(userID!=AppConstants.USER_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        AppConstants.PATIENT_LIST_DATA=((PatientListResponse)data).getPatientList();
                        RxBus.getDefault().post(AppConstants.PATIENT_LIST_DATA_STATE,new Boolean(true));//通知数据改变
                        break;
                        /*userID=((PatientListResponse)data).getUserID();
                        devType=((PatientListResponse)data).getDeviceType();
                        if(userID!=AppConstants.USER_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        AppConstants.PATIENT_LIST_DATA=((PatientListResponse)data).getPatientList();
                        RxBus.getDefault().post(AppConstants.PATIENT_LIST_DATA_STATE,new Boolean(true));//通知数据改变
                        break;*/

                    //多人监控，单人监控和病人列表请求的响应
                    case CommandTypeConstant.SINGLE_MONITOR_RESPONSE:
                        userID=((PushAckResponse)data).getUserID();
                        reqID=((PushAckResponse)data).getRequestID();
                        devType=((PushAckResponse)data).getDeviceType();
                        state=((PushAckResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.SINGLE_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.SINGLE_REQ_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.SINGLE_REQ_STATE,new Boolean(false));
                        break;
                    case CommandTypeConstant.MUTI_MONITOR_RESPONSE:
                        userID=((PushAckResponse)data).getUserID();
                        reqID=((PushAckResponse)data).getRequestID();
                        devType=((PushAckResponse)data).getDeviceType();
                        state=((PushAckResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.MUTI_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.MUTI_REQ_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.MUTI_REQ_STATE,new Boolean(false));
                        break;
                    case CommandTypeConstant.PATIENT_LIST_RESPONSE:
                        userID=((PushAckResponse)data).getUserID();
                        reqID=((PushAckResponse)data).getRequestID();
                        devType=((PushAckResponse)data).getDeviceType();
                        state=((PushAckResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.PATIENT_LIST_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.PATIENT_LIST_REQ_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.PATIENT_LIST_REQ_STATE,new Boolean(false));
                        break;

                    //修改密码，登录，注册的响应
                    case CommandTypeConstant.CHANGE_KEY_ACK_RESPONSE:
                        userID=((LoginAckResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        state=((LoginAckResponse)data).getState();
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.CHANGE_KEY_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.CHANGE_KEY_STATE,new Boolean(false));
                        break;
                    case CommandTypeConstant.LOGIN_ACK_RESPONSE:
                        userID=((LoginAckResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        state=((LoginAckResponse)data).getState();
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(false));
                        break;
                    case CommandTypeConstant.LOGOUT_ACK_RESPONSE:
                        userID=((LoginAckResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        state=((LoginAckResponse)data).getState();
                        if(state==SUCCESS)
                            RxBus.getDefault().post(AppConstants.LOGOUT_STATE,new Boolean(true));
                        else
                            RxBus.getDefault().post(AppConstants.LOGOUT_STATE,new Boolean(false));
                        break;

                    //格式名称对照表响应
                    case CommandTypeConstant.DEV_NAEM_RESPONSE:
                        userID=((DevNameResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        AppConstants.DEV_NAME=((DevNameResponse)data).getDevName();
                        break;
                    case CommandTypeConstant.SPORT_DEV_FORM_RESPONSE:
                        userID=((DevNameResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        AppConstants.SPORT_DEV_FORM=((SportDevFormResponse)data).getDevData();
                        break;
                    case CommandTypeConstant.MONITOR_DEV_FORM_RESPONSE:
                        userID=((DevNameResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        AppConstants.MON_DEV_FORM=((MonitorDevFormResponse)data).getDevData();
                        break;
                }

                switch (dataType)
                {
                    case CommandTypeConstant.LOGIN_SUCCESS:
                        RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                        break;
                    case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_REQUEST:
                        ExercisePhysiologicalDataResponse exercisePhysiologicalDataResponse=(ExercisePhysiologicalDataResponse)data;
                        PatientDetail patientDetail1=new PatientDetail("张er1", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue);
                        patientDetail1.setId(String.valueOf(data.getDeviceId().getDeviceNumber()));
                        //String id=String.valueOf(data.getDeviceId().getDeviceNumber());

                        RxBus.getDefault().post(AppConstants.SINGLE_DATA,patientDetail1);
                        break;
                    case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_REQUEST:
                        ExerciseEquipmentDataResponse exercisedata=(ExerciseEquipmentDataResponse)data;
                        PatientDetail patientDetail=new PatientDetail("张er1", "1", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue);
                        patientDetail.setId(String.valueOf(exercisedata.getPatientId()));
                        patientDetail.setPercent(String.valueOf(exercisedata.getExercisePlanCompletionRate()));
                        //for(int i=0;i<8;i++)
                        //{
                         //   patientDetail.setId(String.valueOf(i));
                            RxBus.getDefault().post(AppConstants.MUTI_DATA, patientDetail);

                       // }
                        // RxBus.getDefault().post(AppConstants.MUTI_DATA, new PatientDetail("张er2", "2", "跑步机","10%   第一段",PatientDetailList.phyName,phyValue,sportName,sportValue));
                        break;
                }
            }
        });
    }
}
