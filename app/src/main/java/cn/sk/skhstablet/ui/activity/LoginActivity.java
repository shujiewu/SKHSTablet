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
        //启动连接
        invoke(TcpUtils.connect(AppConstants.url, AppConstants.port), new Callback<Boolean>() {
            @Override
            public void onResponse(Boolean data) {
                if(data)
                {
                    fetchData();//注册链路观察者
                    this.unsubscribe();//取消订阅
                }
            }
        });
    }

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
