package cn.sk.skhstablet.tcp.utils;

import android.util.Log;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.handler.CUSUMHandler;
import cn.sk.skhstablet.handler.ProtocolDecoder;
import cn.sk.skhstablet.handler.ProtocolEncoder;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.presenter.BaseView;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.ControlState;
import cn.sk.skhstablet.protocol.DeviceId;
import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.down.DevNameResponse;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.protocol.down.LoginAckResponse;
import cn.sk.skhstablet.protocol.down.LoginOtherResponse;
import cn.sk.skhstablet.protocol.down.MonitorDevFormResponse;
import cn.sk.skhstablet.protocol.down.PatientListResponse;
import cn.sk.skhstablet.protocol.down.PushAckResponse;
import cn.sk.skhstablet.protocol.down.SportDevControlResponse;
import cn.sk.skhstablet.protocol.down.SportDevFormResponse;
import cn.sk.skhstablet.protocol.up.IdleHeartRequest;
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

import static cn.sk.skhstablet.app.AppConstants.CONTROL_REQ_ID;
import static cn.sk.skhstablet.app.AppConstants.DEV_NAME;
import static cn.sk.skhstablet.app.AppConstants.MON_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_DATA;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NAME_FORM;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_LIST_NUMBER_FORM;
import static cn.sk.skhstablet.app.AppConstants.canModify;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.netState;
import static cn.sk.skhstablet.app.AppConstants.singleMonitorID;
import static cn.sk.skhstablet.app.CommandTypeConstant.IDLE_HEART_REQUEST;
import static cn.sk.skhstablet.app.CommandTypeConstant.LOGIN_OTHER;
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
        if(observable==null)
            return;
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        lifecycle.bindSubscription(subscription);
    }
    public static  <T> Subscription invoke(Observable<T> observable, Callback<T> callback)
    {
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        return subscription;
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
    static Connection<AbstractProtocol,AbstractProtocol> mConnection;
    static boolean isFirst=true;
    public static Observable<Boolean> connect(final String url, final int port) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                TcpClient.newClient(url, port)
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("linebase",
                                new Func0<ChannelHandler>() {
                                    @Override
                                    public ChannelHandler call() {
                                        return   new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 32768,
                                                4, 2, 0, 0, true);
                                    }
                                })
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("checksum",
                                new Func0<ChannelHandler>() {
                                    @Override
                                    public ChannelHandler call() {
                                        return   new CUSUMHandler();
                                    }
                                })
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("decoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new ProtocolDecoder();
                            }
                        })
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("encoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new ProtocolEncoder();
                            }

                        })
                        .channelOption(ChannelOption.SO_KEEPALIVE, true)
                        //.channelOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000) //服务器掉线还要等五秒，自己掉线直接重连
                        //.readTimeOut(5, TimeUnit.SECONDS) //5秒未读到数据
                        .readTimeOut(6, TimeUnit.SECONDS) //15秒未读到数据
                        .createConnectionRequest()

                        .subscribe(new Observer<Connection<AbstractProtocol, AbstractProtocol>>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                                subscriber.unsubscribe();
                                System.out.println("连接完成");
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
                                subscriber.unsubscribe();
                                e.printStackTrace();
                                System.out.println("连接失败");
                             }

                            @Override
                            public void onNext(Connection<AbstractProtocol, AbstractProtocol> connection) {
                                mConnection = connection;
                                subscriber.onNext(true);
                                netState=AppConstants.STATE_CONN;
                                System.out.println("连接成功");
                                if(isFirst)
                                {
                                    System.out.println("首次连接成功");
                                    sendIdleHeart();
                                    isFirst=false;
                                }
                            }
                        });
            }
        });
    }
   /* public  static void re(String url,int port)
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
    }*/
    public static Observable<AbstractProtocol> receive() {
        if (mConnection != null) {
            return mConnection.getInput();
        }
        return null;
    }

    /*public static Observable<Void> send(String s) {
        Log.e("10.40","5");
        return mConnection.write(Observable.just(s));
    }*/

    public static Observable<Void> send(AbstractProtocol s) {
        //Log.e("10.40","5");
        if (mConnection != null) {
            return mConnection.write(Observable.just(s));
        }
        else return null;
    }

    private static int spacingTime =3;
    /**
     * 断开自动重新连接
     */
    public static void closeConnection()
    {

        if (mConnection != null)
        {
            mConnection.closeNow();
        }
    }
    private static int reconnectTime=0;
    public static void reconnect() {
        reconnectTime++;
        if(reconnectTime==10)
        {
            RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(false));
            reconnectTime=0;
            ToastUtils.showShortToast("网络不可达，未知错误");
            netState=AppConstants.STATE_DIS_CONN;
            return;
        }
        netState=AppConstants.STATE_IN_CONN;
        //延迟spacingTime秒后进行重连
        Observable.timer(spacingTime, TimeUnit.SECONDS).subscribe((new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (mConnection != null)
                {
                    mConnection.closeNow();
                }
                invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
                    @Override
                    public void onResponse(Boolean data) {
                        fetchData();
                        RxBus.getDefault().post(AppConstants.RE_SEND_REQUEST,new Boolean(true));
                        System.out.println("重新连接成功");
                        reconnectTime=0;
                    }
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("尝试重新连接");
                        this.unsubscribe();
                        reconnect();

                    }
                });
            }
        }));
    }
    public static Subscription idleScription;
    private static int heartTime =3;
    public static void sendIdleHeart()
    {
        idleScription=Observable.interval(heartTime, TimeUnit.SECONDS).subscribe((new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                IdleHeartRequest idleHeartRequest=new IdleHeartRequest(IDLE_HEART_REQUEST) ;
                idleHeartRequest.setUserID(AppConstants.USER_ID);
                idleHeartRequest.setDeviceType(AppConstants.DEV_TYPE);
                idleHeartRequest.setRequestID((byte)0x00);
                invoke(TcpUtils.send(idleHeartRequest),new Callback<Void>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        System.out.println("心跳发送完成");
                        this.unsubscribe();
                    }
                    @Override
                    public void onError(Throwable e) {
                        //reconnect();  //心跳失败重连
                        this.unsubscribe();
                    }
                });
            }
        }));
    }
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
    public static void logoutUnsubscribe()
    {
        idleScription.unsubscribe();
        if(idleScription.isUnsubscribed())
        {
            System.out.println("取消了心跳订阅");
        }
        else
        {
            System.out.println("未取消了心跳订阅");
        }
        fetchScription.unsubscribe();
        if(fetchScription.isUnsubscribed())
        {
            System.out.println("取消了读取数据订阅");
        }
        else
        {
            System.out.println("未取消了读取数据订阅");
        }
    }
    public static Subscription fetchScription;
    public static void fetchData()
    {
        fetchScription=invoke(TcpUtils.receive(), new Callback<AbstractProtocol>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                this.unsubscribe();
            }
            @Override
            public void onResponse(final AbstractProtocol data) {
                if(data==null)
                    return;
                byte state;
                byte reqID;
                byte devType;
                int userID;
                switch (data.getCommand())
                {
                    case CommandTypeConstant.EXERCISE_PHYSIOLOGICAL_DATA_RESPONSE: {
                        //dataType=(byte)((ExercisePhysiologicalDataResponse)data).getDeviceId().getDeviceType();
                        //ExercisePhysiologicalDataResponse response=(ExercisePhysiologicalDataResponse) data;
                        RxBus.getDefault().post(AppConstants.PHY_DATA,((ExercisePhysiologicalDataResponse) data).getPatientPhyData());
                        System.out.println("生理数据发送");
                        break;
                    }
                    case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_RESPONSE: {
                        ///dataType=(byte)((ExerciseEquipmentDataResponse)data).getDeviceId().getDeviceType();
                        ExerciseEquipmentDataResponse response=(ExerciseEquipmentDataResponse)data;
                        PatientDetail patientDetail=response.getPatientDetail();
                        if(String.valueOf(patientDetail.getPatientID()).equals(singleMonitorID))
                        {
                            RxBus.getDefault().post(AppConstants.SINGLE_DATA,patientDetail);
                        }
                        RxBus.getDefault().post(AppConstants.MUTI_DATA, patientDetail);
                        break;
                    }
                    case CommandTypeConstant.DOCTOR_ADVICE_RESPONSE:
                        break;
                    case CommandTypeConstant.PATIENT_LIST_UPDATE_RESPONSE:
                    case CommandTypeConstant.PATIENT_LIST_NEW_DATA_RESPONSE:
                        state=((PatientListResponse)data).getState();
                        /*while (!canModify)
                        {
                        }*/
                        AppConstants.PATIENT_LIST_DATA=((PatientListResponse)data).getPatientList();
                        int size=AppConstants.PATIENT_LIST_DATA.size();
                        for(int i=0;i<size;i++)
                        {
                            if(!PATIENT_LIST_NAME_FORM.containsKey(PATIENT_LIST_DATA.get(i).getPatientID()))
                            {
                                PATIENT_LIST_NAME_FORM.put(PATIENT_LIST_DATA.get(i).getPatientID(),PATIENT_LIST_DATA.get(i).getName());
                                PATIENT_LIST_NUMBER_FORM.put(PATIENT_LIST_DATA.get(i).getPatientID(),PATIENT_LIST_DATA.get(i).getHospitalNumber());
                            }
                        }
                        RxBus.getDefault().post(AppConstants.PATIENT_LIST_DATA_STATE,new Byte(state));//通知数据改变
                        break;
                    case CommandTypeConstant.PATIENT_LIST_DATA_RESPONSE:  //完成注册
                        userID=((PatientListResponse)data).getUserID();
                        devType=((PatientListResponse)data).getDeviceType();
                        reqID=((PatientListResponse)data).getRequestID();
                        state=((PatientListResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.PATIENT_LIST_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==CommandTypeConstant.PATIENT_LIST_SUCCESS)
                        {
                            /*while (!canModify)
                            {
                            }*/
                            AppConstants.PATIENT_LIST_DATA=((PatientListResponse)data).getPatientList();
                            int number=AppConstants.PATIENT_LIST_DATA.size();
                            for(int i=0;i<number;i++)
                            {
                                if(!PATIENT_LIST_NAME_FORM.containsKey(PATIENT_LIST_DATA.get(i).getPatientID()))
                                {
                                    PATIENT_LIST_NAME_FORM.put(PATIENT_LIST_DATA.get(i).getPatientID(),PATIENT_LIST_DATA.get(i).getName());
                                    PATIENT_LIST_NUMBER_FORM.put(PATIENT_LIST_DATA.get(i).getPatientID(),PATIENT_LIST_DATA.get(i).getHospitalNumber());
                                }
                            }
                        }
                        RxBus.getDefault().post(AppConstants.PATIENT_LIST_DATA_STATE,new Byte(state));//通知数据改变
                        AppConstants.PATIENT_LIST_REQ_ID++;
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
                        {
                            RxBus.getDefault().post(AppConstants.SINGLE_REQ_STATE,new Byte(state));
                        }
                        else
                            RxBus.getDefault().post(AppConstants.SINGLE_REQ_STATE,new Byte(state));
                        AppConstants.SINGLE_REQ_ID++;
                        break;
                    case CommandTypeConstant.MUTI_MONITOR_RESPONSE:
                        userID=((PushAckResponse)data).getUserID();
                        reqID=((PushAckResponse)data).getRequestID();
                        devType=((PushAckResponse)data).getDeviceType();
                        state=((PushAckResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||devType!=AppConstants.DEV_TYPE)
                        {
                            return;
                        }
                        if(reqID==AppConstants.MUTI_REQ_ID)
                        {
                            RxBus.getDefault().post(AppConstants.MUTI_REQ_STATE,new Byte(state));
                        }
                        break;
                    /*case CommandTypeConstant.PATIENT_LIST_RESPONSE:
                        userID=((PushAckResponse)data).getUserID();
                        reqID=((PushAckResponse)data).getRequestID();
                        devType=((PushAckResponse)data).getDeviceType();
                        state=((PushAckResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.PATIENT_LIST_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                        {
                            RxBus.getDefault().post(AppConstants.PATIENT_LIST_REQ_STATE,new Boolean(true)); //暂时感觉不到有用
                            AppConstants.PATIENT_LIST_REQ_ID++;
                        }
                        else
                            RxBus.getDefault().post(AppConstants.PATIENT_LIST_REQ_STATE,new Boolean(false));
                        break;*/

                    //修改密码，登录，注册的响应
                    case CommandTypeConstant.CHANGE_KEY_ACK_RESPONSE:
                        //userID=((LoginAckResponse)data).getUserID();
                        //if(userID!=AppConstants.USER_ID)
                        //    return;
                        state=((LoginAckResponse)data).getState();
                        //if(state==SUCCESS)
                        RxBus.getDefault().post(AppConstants.CHANGE_KEY_STATE,new Byte(state));
                        //else
                            //RxBus.getDefault().post(AppConstants.CHANGE_KEY_STATE,new Boolean(false));
                        break;
                    case CommandTypeConstant.LOGIN_ACK_RESPONSE:
                        //userID=((LoginAckResponse)data).getUserID();
                        //if(userID!=AppConstants.USER_ID)
                        //    return;
                        state=((LoginAckResponse)data).getState();
                        if(state==SUCCESS)
                        {

                            String userName=((LoginAckResponse)data).getUserName();
                            System.out.println(userName);
                            AppConstants.USER_ID=((LoginAckResponse)data).getUserID();
                            //sendIdleHeart();
                            RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(true));
                        }
                        else
                            RxBus.getDefault().post(AppConstants.LOGIN_STATE,new Boolean(false));
                        AppConstants.LOGIN_REQ_ID++;
                        break;
                    case  CommandTypeConstant.LOGIN_OTHER_RESPONSE:
                        state=((LoginOtherResponse)data).getState();
                        if(state==LOGIN_OTHER)
                            RxBus.getDefault().post(AppConstants.LOGIN_OTHER_STATE,new Boolean(true));
                    case CommandTypeConstant.LOGOUT_ACK_RESPONSE:
                        userID=((LoginAckResponse)data).getUserID();
                        reqID=((LoginAckResponse)data).getRequestID();
                        devType=((LoginAckResponse)data).getDeviceType();
                        state=((LoginAckResponse)data).getState();
                        System.out.println("退出1");
                       // if(userID!=AppConstants.USER_ID||reqID!=AppConstants.LOGOUT_REQ_ID||devType!=AppConstants.DEV_TYPE)
                      //      return;

                        if(state==SUCCESS)
                        {
                            System.out.println("退出7");
                            RxBus.getDefault().post(AppConstants.LOGOUT_STATE,new Boolean(true));
                        }
                        else
                            RxBus.getDefault().post(AppConstants.LOGOUT_STATE,new Boolean(false));
                        AppConstants.LOGOUT_REQ_ID++;
                       // System.out.println("退出3");
                        break;

                    //格式名称对照表响应
                    /*case CommandTypeConstant.DEV_NAEM_RESPONSE:
                        userID=((DevNameResponse)data).getUserID();
                        if(userID!=AppConstants.USER_ID)
                            return;
                        AppConstants.DEV_NAME=((DevNameResponse)data).getDevName();
                        break;*/
                    case CommandTypeConstant.SPORT_DEV_FORM_RESPONSE:
                        userID=((SportDevFormResponse)data).getUserID();
                        reqID=((SportDevFormResponse)data).getRequestID();
                        devType=((SportDevFormResponse)data).getDeviceType();
                        state=((SportDevFormResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.SPORT_FORM_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                            AppConstants.SPORT_DEV_FORM=((SportDevFormResponse)data).getDevData();
                        AppConstants.SPORT_FORM_REQ_ID++;
                        break;
                    case CommandTypeConstant.MONITOR_DEV_FORM_RESPONSE:
                        userID=((MonitorDevFormResponse)data).getUserID();
                        reqID=((MonitorDevFormResponse)data).getRequestID();
                        devType=((MonitorDevFormResponse)data).getDeviceType();
                        state=((MonitorDevFormResponse)data).getState();
                        if(userID!=AppConstants.USER_ID||reqID!=AppConstants.PHY_FORM_REQ_ID||devType!=AppConstants.DEV_TYPE)
                            return;
                        if(state==SUCCESS)
                        {
                            MON_DEV_FORM=((MonitorDevFormResponse)data).getDevData();
                            System.out.println("mon form");
                            byte tyep=(byte)0x0f;
                            List<MonitorDevForm> monitorDevForms=MON_DEV_FORM.get(tyep);
                            System.out.println("monisize"+MON_DEV_FORM.size());
                        }
                            //MON_DEV_FORM=((MonitorDevFormResponse)data).getDevData();
                        //AppConstants.PHY_FORM_REQ_ID++;
                        break;
                    case CommandTypeConstant.SPORT_DEV_CONTROL_RESPONSE:{
                        userID=((SportDevControlResponse)data).getUserID();
                        reqID=((SportDevControlResponse)data).getRequestID();
                        devType=((SportDevControlResponse)data).getDeviceType();

                        byte resultState=((SportDevControlResponse)data).getState();
                        byte controlState=((SportDevControlResponse)data).getControlResultCode();
                        if(userID!=AppConstants.USER_ID||devType!=AppConstants.DEV_TYPE||reqID!=AppConstants.CONTROL_REQ_ID)
                            return;
                        CONTROL_REQ_ID++;
                        RxBus.getDefault().post(AppConstants.CONTORL_REQ_STATE,new ControlState(resultState,controlState));
                        break;
                    }
                }
            }
        });
    }
}
