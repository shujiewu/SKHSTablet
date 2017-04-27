package cn.sk.skhstablet.ui.activity;

/**
 * Created by wyb on 2017/4/25.
 */
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.TabViewPagerAdapter;
import cn.sk.skhstablet.ui.fragment.FragmentChangeKey;
import cn.sk.skhstablet.ui.fragment.FragmentLogin;
import cn.sk.skhstablet.ui.base.BaseActivity;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.reactivex.netty.channel.Connection;
import io.reactivex.netty.protocol.tcp.server.ConnectionHandler;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setupViewPager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                rxNettyServerTest();
            }
        }).start();
    }
    public void rxNettyServerTest() {
        TcpServer<String, String> server;
        server = TcpServer.newServer(60000)
                .<String, String>addChannelHandlerLast("string-decoder",
                    new Func0<ChannelHandler>() {
                        @Override
                        public ChannelHandler call() {
                            return new StringDecoder();
                        }
                    })
                .<String, String>addChannelHandlerLast("string-encoder",
                        new Func0<ChannelHandler>() {
                        @Override
                        public ChannelHandler call() {
                            return new StringEncoder();
                         }
                    })
                .start(new ConnectionHandler<String, String>() {
                    @Override
                    public Observable<Void> handle(Connection<String, String> newConnection) {
                        return newConnection.writeStringAndFlushOnEach(
                            newConnection.getInput().map(
                                    new Func1<String, String>() {
                                    @Override
                                    public String call(String s) {
                                        System.out.println("receive:" + s);
                                        return "echo=> " + s;
                                    }}
                            )
                        );
                     }
                });
        server.awaitShutdown();
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
