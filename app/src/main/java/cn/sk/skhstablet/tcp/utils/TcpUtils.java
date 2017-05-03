package cn.sk.skhstablet.tcp.utils;

import android.util.Log;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;

import java.nio.ByteOrder;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.handler.CUSUMHandler;
import cn.sk.skhstablet.handler.ProtocolDecoder;
import cn.sk.skhstablet.presenter.BaseView;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.Stateful;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
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

/**
 * Created by wyb on 2017/4/27.
 */

public class TcpUtils {
    public static  <T> void invoke(BaseView lifecycle, Observable<T> observable, Action1<T> callback)
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
                        .createConnectionRequest()

                        .subscribe(new Observer<Connection<AbstractProtocol, String>>() {
                            @Override
                            public void onCompleted() {
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                subscriber.onError(e);
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
}
