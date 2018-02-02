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
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePhysiologicalDataResponse;
import cn.sk.skhstablet.protocol.down.ExercisePlanResponse;
import cn.sk.skhstablet.protocol.down.LoginAckResponse;
import cn.sk.skhstablet.protocol.down.LoginOtherResponse;
import cn.sk.skhstablet.protocol.down.MonitorDevFormResponse;
import cn.sk.skhstablet.protocol.down.PatientListResponse;
import cn.sk.skhstablet.protocol.down.PushAckResponse;
import cn.sk.skhstablet.protocol.down.SignOutResponse;
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
import static cn.sk.skhstablet.app.CommandTypeConstant.SIGN_OUT_RESPONSE;
import static cn.sk.skhstablet.app.CommandTypeConstant.SUCCESS;
import static cn.sk.skhstablet.model.PatientDetailList.phyValue;
import static cn.sk.skhstablet.model.PatientDetailList.sportName;
import static cn.sk.skhstablet.model.PatientDetailList.sportValue;

/**
 * Created by wyb on 2017/4/27.
 * 这个类负责网络连接，消息分发，大部分是静态方法
 */

public class TcpUtils {
    //invoke函数用于设置观察者和被观察者所运行的线程，并绑定其生命周期
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
        Subscription subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
        lifecycle.bindSubscription(subscription);

    }
    static Connection<AbstractProtocol,AbstractProtocol> mConnection;
    static boolean isFirst=true;//判断是否是首次登陆
    //网络连接静态类
    public static Observable<Boolean> connect(final String url, final int port) {
        netState=AppConstants.STATE_IN_CONN;
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
                                }) //增加半包解码器
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("checksum",
                                new Func0<ChannelHandler>() {
                                    @Override
                                    public ChannelHandler call() {
                                        return   new CUSUMHandler();
                                    }
                                }) //增加校验
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("decoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new ProtocolDecoder();
                            }
                        })  //解码器
                        .<AbstractProtocol,AbstractProtocol>addChannelHandlerLast("encoder",
                        new Func0<ChannelHandler>() {
                            @Override
                            public ChannelHandler call() {
                                return new ProtocolEncoder();
                            }

                        })  //编码器
                        .channelOption(ChannelOption.SO_KEEPALIVE, true)
                        //.channelOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000) //服务器掉线还要等五秒，自己掉线直接重连
                        //.readTimeOut(5, TimeUnit.SECONDS) //5秒未读到数据
                        .readTimeOut(6, TimeUnit.SECONDS) //通过心跳来实现的，6秒未读到数据则读取超时失败
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
                                netState=AppConstants.STATE_CONN;  //网络状态设置为已连接
                                System.out.println("连接成功");
                                //if(isFirst)
                                //{
                                    //System.out.println("连接成功");
                                    sendIdleHeart();//连接成功开始发送心跳
                                //    isFirst=false;
                               // }
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
    //对服务器响应做观察
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
    //对命令发送情况做观察
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

    //关闭链路
    public static void closeConnection()
    {
        netState=AppConstants.STATE_DIS_CONN;
        if (mConnection != null)
        {
            mConnection.closeNow();
        }
    }
    //安全地设置网络为不连通
    public static void setConnDisable()
    {
        netState=AppConstants.STATE_DIS_CONN;
        logoutUnsubscribe();
        closeConnection();
        /*if(netState==AppConstants.STATE_DIS_CONN)
             reconnect();*/
    }
    private static int reconnectTime=0;
    //重新连接
    public static void reconnect() {
        reconnectTime++;
        if(reconnectTime==3)
        {
            //重连三次失败，通知其他组件不要重新发送命令
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
                    logoutUnsubscribe();
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
        //每间隔heartTime事件发送一次
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
                        this.unsubscribe(); //发送成功后取消订阅
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

    //安全地关闭连接需要取消几个静态方法中的观察者订阅
    public static void logoutUnsubscribe()
    {
        if(idleScription!=null)
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
        }
        if(fetchScription!=null)
        {
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
    }
    public static Subscription fetchScription;
    //读取服务器响应，这个响应已经经过了decoder解码器，这里只需要做数据分发
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
                        //System.out.println("生理数据响应0xB1 处于分发状态");
                        ExercisePhysiologicalDataResponse response=(ExercisePhysiologicalDataResponse) data;
                        //如果多人监控界面包含这个患者
                        if(hasMutiPatient.containsKey(response.getUserID()))
                        {
                            //System.out.println("患者"+response.getUserID()+"生理数据响应0xB1 向多人监控界面转发");
                            RxBus.getDefault().post(AppConstants.MUTI_PHY_DATA,((ExercisePhysiologicalDataResponse) data).getPatientPhyData());
                        }
                        if(String.valueOf(response.getUserID()).equals(singleMonitorID))
                        {
                            //System.out.println("患者"+response.getUserID()+"生理数据响应0xB1 向单人监控界面转发");
                            RxBus.getDefault().post(AppConstants.PHY_DATA,((ExercisePhysiologicalDataResponse) data).getPatientPhyData());
                        }
                        //post即为数据发送，数据接收的地方为PresenterImpl中注册的观察者
                        break;
                    }
                    case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_RESPONSE: {
                        ///dataType=(byte)((ExerciseEquipmentDataResponse)data).getDeviceId().getDeviceType();
                        //System.out.println("运动设备数据响应0xC3 处于分发状态");
                        ExerciseEquipmentDataResponse response=(ExerciseEquipmentDataResponse)data;
                        PatientDetail patientDetail=response.getPatientDetail();
                        if(String.valueOf(patientDetail.getPatientID()).equals(singleMonitorID))  //如果该数据被单人监控所订阅，需要发送到单人监控界面
                        {
                            //System.out.println("患者"+patientDetail.getPatientID()+"运动数据响应0xC3 向单人监控界面转发");
                            RxBus.getDefault().post(AppConstants.SINGLE_DATA,patientDetail);
                        }
                        if(hasMutiPatient.containsKey(patientDetail.getPatientID()))
                        {
                            //System.out.println("患者"+patientDetail.getPatientID()+"运动数据响应0xC3 向多人监控界面转发");
                            RxBus.getDefault().post(AppConstants.MUTI_DATA, patientDetail);
                        }
                        break;
                    }
                    case CommandTypeConstant.EXERCISE_PLAN_RESPONSE:  //医嘱响应还未测试
                    {
                        ExercisePlanResponse response=(ExercisePlanResponse)data;
                        if(response.getPatientID()==Integer.valueOf(singleMonitorID))
                        {
                            System.out.println("医嘱响应转发");
                            RxBus.getDefault().post(AppConstants.EXERCISE_PLAN_STATE,response);
                        }
                        break;
                    }
                    case CommandTypeConstant.PATIENT_LIST_UPDATE_RESPONSE:  //全局患者状态更新和新到患者响应的处理方法一样
                    case CommandTypeConstant.PATIENT_LIST_NEW_DATA_RESPONSE:
                        state=((PatientListResponse)data).getState();
                        /*while (!canModify)
                        {
                        }*/
                        AppConstants.PATIENT_LIST_DATA=((PatientListResponse)data).getPatientList();  //这里直接将该数据赋给一个全局变量
                        int size=AppConstants.PATIENT_LIST_DATA.size();
                        for(int i=0;i<size;i++)
                        {
                            //如果全局患者的姓名hash中不存在该患者，说明是新签到的患者，这里需要添加进来用于对运动设备数据解码时直接根据患者id找到该患者姓名和住院号
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
                    case CommandTypeConstant.SIGN_OUT_RESPONSE:
                        List<Integer> signOutPatient=((SignOutResponse)data).getPatientNumber();
                        if(signOutPatient.size()!=0)
                        {
                            AppConstants.SIGN_OUT_PATIENT_LIST=signOutPatient;
                            RxBus.getDefault().post(AppConstants.PATIENT_LIST_DATA_STATE,SIGN_OUT_RESPONSE);//通知数据改变
                        }
                        break;
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
                        //if(reqID==AppConstants.MUTI_REQ_ID)
                        //{
                            RxBus.getDefault().post(AppConstants.MUTI_REQ_STATE,new Byte(state));
                        //}
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
                        break;
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

                    //运动设备和监护设备协议解析格式响应
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
                            System.out.println();
                            System.out.println("mon form");
                            byte tyep=(byte)0x0f;
                            List<MonitorDevForm> monitorDevForms=MON_DEV_FORM.get(tyep);
                            System.out.println("monisize"+MON_DEV_FORM.size());
                        }
                            //MON_DEV_FORM=((MonitorDevFormResponse)data).getDevData();
                        //AppConstants.PHY_FORM_REQ_ID++;
                        break;
                    //控制命令响应
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
