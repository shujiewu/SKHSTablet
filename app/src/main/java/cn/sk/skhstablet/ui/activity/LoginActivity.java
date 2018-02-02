package cn.sk.skhstablet.ui.activity;

/**
 * Created by wyb on 2017/4/25.
 */
import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Properties;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.TabViewPagerAdapter;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.tcp.utils.Callback;
import cn.sk.skhstablet.tcp.utils.TcpUtils;
import cn.sk.skhstablet.ui.fragment.FragmentChangeKey;
import cn.sk.skhstablet.ui.fragment.FragmentLogin;
import cn.sk.skhstablet.ui.base.BaseActivity;

import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchData;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.invoke;

//登录activity
public class LoginActivity extends BaseActivity {
    //View viewFade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //有权限，则写你的业务逻辑
            System.out.println("----------------有权限---------");
            initData();

        }else {
            //没权限，进行权限请求
            requestPermission(AppConstants.CODE_CREATE_FILE, Manifest.permission.READ_EXTERNAL_STORAGE);
            //requestPermission(AppConstants.CODE_WRITE_FILE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            System.out.println("----------------没有权限---------");
        }




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
    private void initData(){
        //检查是否有sd卡以及sd卡的权限
        String filePath = "/sdcard";
        final String fileName = "/skhs.properties";
    }
    //读取配置文件
    public Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }
    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    private String read(String fileName)
    {
        try{
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            {
                System.out.println("SD卡可用");
                File sdCardDir = Environment.getExternalStorageDirectory();
                FileInputStream fis = new FileInputStream(sdCardDir.getCanonicalPath()+fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder  sb = new StringBuilder("");
                String lines = null;
                while((lines = br.readLine()) != null)
                {
                    sb.append(lines);
                }
                br.close();
                return sb.toString();

            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static boolean makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                if(file.mkdir())
                    return true;
                else
                    return false;
            }
            else
                return true;
        } catch (Exception e) {
            Log.i("error:", e+"");
            return false;
        }
    }



}
