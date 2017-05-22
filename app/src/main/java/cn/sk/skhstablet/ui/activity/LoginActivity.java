package cn.sk.skhstablet.ui.activity;

/**
 * Created by wyb on 2017/4/25.
 */
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;
import com.nineoldandroids.view.ViewHelper;

import java.nio.ByteOrder;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.TabViewPagerAdapter;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.handler.ProtocolDecoder;
import cn.sk.skhstablet.handler.ProtocolEncoder;
import cn.sk.skhstablet.protocol.AbstractProtocol;
import cn.sk.skhstablet.protocol.DeviceId;
import cn.sk.skhstablet.protocol.Version;
import cn.sk.skhstablet.protocol.down.ExerciseEquipmentDataResponse;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.fragment.FragmentChangeKey;
import cn.sk.skhstablet.ui.fragment.FragmentLogin;
import cn.sk.skhstablet.ui.base.BaseActivity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.server.ConnectionHandler;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.invoke;

public class LoginActivity extends BaseActivity {
    //View viewFade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setupViewPager();
        //viewFade = findViewById(R.id.view_fade);
        //ViewHelper.setAlpha(viewFade, 0);
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                rxNettyServerTest();
            }
        }).start();*/

        invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                //Log.e("10.40","4");
                fetchData();

                //fetchVerifyState();
            }
        });
    }
     /*public void rxNettyServerTest() {
        TcpServer<String,AbstractProtocol> server;
        server = TcpServer.newServer(60000)

                .<String,AbstractProtocol>addChannelHandlerLast("string-decoder",
                    new Func0<ChannelHandler>() {
                        @Override
                        public ChannelHandler call() {
                            return new StringDecoder();
                        }
                    })
                .<String,AbstractProtocol>addChannelHandlerLast("string-encoder",
                        new Func0<ChannelHandler>() {
                        @Override
                        public ChannelHandler call() {
                            return new MyEncoder();
                         }
                    })
                .start(new ConnectionHandler<String, AbstractProtocol>() {
                    @Override
                    public Observable<Void> handle(Connection<String, AbstractProtocol> newConnection) {
                        return newConnection.writeAndFlushOnEach(
                            newConnection.getInput().map(
                                    new Func1<String, AbstractProtocol>() {
                                    @Override
                                    public AbstractProtocol call(String s) {
                                        System.out.println("receive:" + s);
                                        return test();
                                    }}
                            )
                        );

                     }
                });
        server.awaitShutdown();
    }
   public static class MyEncoder extends MessageToByteEncoder<AbstractProtocol> {

        @Override
        protected void encode(ChannelHandlerContext ctx, AbstractProtocol request, ByteBuf out) throws Exception {
            out.writeBytes(request.getStart());
            int startIndex = out.writerIndex();
            out.writeShortLE(1);
            encodeVersion(request.getVersion(), out);
            out.writeByte(request.getCommand());
            encodeDeviceId(request.getDeviceId(), out);
            switch (request.getCommand()) {
                case CommandTypeConstant.EXERCISE_EQUIPMENT_DATA_REQUEST: {
                    encodeExerciseEquipmentDataRequest((ExerciseEquipmentDataResponse) request, out);
                    break;
                }
            }
            out.writeShortLE(0);
            out.setShortLE(startIndex, out.writerIndex() - startIndex - 2);
            accumulation(out);
        }

        private void accumulation(ByteBuf out) {
            long start = System.currentTimeMillis();
            int sumIndex = out.writerIndex() - 2;
            int sum = 0;
            for (int i = 4; i < sumIndex; i++) {
                sum += out.getUnsignedByte(i);
            }
            out.setShortLE(sumIndex, sum);
            long end = System.currentTimeMillis();
            // System.out.println(end - start);
        }
        private void encodeExerciseEquipmentDataRequest(ExerciseEquipmentDataResponse request, ByteBuf out) {
            out.writeLongLE(request.getRFID());
            out.writeIntLE(request.getPatientId());
            out.writeIntLE((int) request.getDataPacketNumber());
            out.writeShortLE(request.getPhysiologicalLength());
            out.writeBytes(request.getPhysiologicalData());
            out.writeByte(request.getExercisePlanCompletionRate());
            out.writeShortLE(request.getPerformedExecutionAmount());
            out.writeIntLE(request.getExercisePlanId());
            out.writeByte(request.getExercisePlanSectionNumber());
            out.writeBytes(request.getEquipmentData());
        }

        private void encodeDeviceId(DeviceId deviceId, ByteBuf out) {
            out.writeByte(deviceId.deviceType);
            out.writeByte(deviceId.deviceModel);
            out.writeLongLE(deviceId.deviceNumber);
        }

        private void encodeVersion(Version version, ByteBuf out) {
            out.writeByte(version.majorVersionNumber);
            out.writeByte(version.secondVersionNumber);
            out.writeShortLE(version.reviseVersionNumber);
        }
    }
    ExerciseEquipmentDataResponse test()
    {
        DeviceId deviceId = new DeviceId();
        deviceId.deviceType = 0x02;
        deviceId.deviceModel = 0x00;
        ExerciseEquipmentDataResponse request = new ExerciseEquipmentDataResponse();
        deviceId.deviceNumber = 10000L;
        Version version = new Version();
        version.majorVersionNumber = 1;
        version.secondVersionNumber = 2;
        version.reviseVersionNumber = 3;
        request.setDeviceId(deviceId);
        request.setVersion(version);
        request.setPatientId(1);
        request.setDataPacketNumber(8888L);
        request.setRFID(111111L);
        request.setPhysiologicalLength(6);
        for (int i = 0; i < request.getPhysiologicalLength(); i++) {
            request.getPhysiologicalData()[i] = 0x11;
        }
        request.setExercisePlanCompletionRate((short) 8);
        request.setPerformedExecutionAmount(8);
        request.setExercisePlanId(1);
        request.setExercisePlanSectionNumber((short) 2);
        byte[] equipmentData = new byte[6];
        request.setEquipmentData(equipmentData);

        Log.e("10.40","3");
        return request;
    }*/

    @Override
    protected void initInject() {

    }

    //设置tab下的viewpager
    private void setupViewPager() {

        final ViewPager login_viewpager = (ViewPager) findViewById(R.id.login_viewpager);
        setupViewPager(login_viewpager);
        TabLayout login_tabs = (TabLayout) findViewById(R.id.login_tabs);
        login_tabs.setupWithViewPager(login_viewpager);
        login_tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                final 	int f=tab.getPosition();
                login_viewpager.setCurrentItem(f);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentLogin(), "登录");
        adapter.addFrag(new FragmentChangeKey(), "修改密码");
        viewPager.setAdapter(adapter);
    }


}
