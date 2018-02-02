package cn.sk.skhstablet.app;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.blankj.utilcode.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Properties;

import cn.sk.skhstablet.injector.component.AppComponent;
//import cn.sk.skhstablet.injector.component.DaggerAppComponent;
import cn.sk.skhstablet.injector.component.DaggerAppComponent;
import cn.sk.skhstablet.injector.module.AppModule;

/**
 * Created by ldkobe on 2017/5/7.
 */

public class App extends Application {

    private static App instance;
    public static AppComponent appComponent;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(this);//一个utils库的初始化 https://github.com/Blankj/AndroidUtilCode/blob/master/README-CN.md
        //读取配置文件
        String serviceUrl="";
        int port =0;
        Properties proper = null;
        if(checkPermissionAllGranted(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}))
        {
            proper = loadConfig(this.getApplicationContext(),"/sdcard/skhs.properties"); //ProperTies.getProperties(getApplicationContext());
        }
        if(proper == null)
        {
            serviceUrl = "192.168.2.180";
            port =  10000;
        }
        else
        {
            serviceUrl = proper.getProperty("serverUrl");
            port =  Integer.parseInt(proper.getProperty("port"));
        }
        //读取配置文件的url和port
        AppConstants.url=serviceUrl;
        AppConstants.port= port;

    }

    public static AppComponent getAppComponent(){
       if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(instance))
                    .build();
        }
        return appComponent;
    }
    //读取配置文件
    public Properties loadConfig(Context context, String file) {
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
    /**
    * 检查是否拥有指定的所有权限
    * * */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

}