package cn.sk.skhstablet.ui.base;

/**
 * Created by wyb on 2017/4/25.
 */



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ToastUtils;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.component.LoadingPage;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.receiver.NetworkStateReceiver;

@SuppressLint({ "ResourceAsColor", "NewApi" })
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity{

    @Inject
    protected T mPresenter;
    private Context mContext = this;
    private NetworkStateReceiver networkStateReceiver;
    private long preTime;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //initUI();
        //initInject();
        networkStateReceiver = new NetworkStateReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
        }
    }

    // 双击返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - this.preTime > 2000) {

                finish();
            } else {
                finish();
                //System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            this.preTime = System.currentTimeMillis();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    Toast mToast;

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }
    /**
     * 开启activity
     */
    public static void launchActivity(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    protected abstract void initInject();

/*
    @Override
    public void setState(int state) {
        mLoadingPage.state = state;
        mLoadingPage.showPage();
    }
*/
    /**
     * dagger2注入
     */
//    protected abstract void initInject();

    //    一，判断权限、
    //    二、有权限，执行 / 没权限，请求权限
    //    三、请求后的权限回调（这里注意CODE,要匹配）
    //    四、业务逻辑（有权限则直接从第二步跳到这步，没权限则在第三步的回调中调用）
    //    五、在需要进行授权的Activity中 extends 这个封装了权限请求的BaseActvity
    //第一步，先判断是否有权限
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
    //第二步
    protected void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
        ToastUtils.showLongToast("如果拒绝授权,会导致应用无法正常使用");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.CODE_READ_FILE:
                //例子：请求相机的回调
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showLongToast("现在您拥有了权限");
                    // 这里写你需要的业务逻辑
                    //doYourNeedDo();
                } else {
                    ToastUtils.showShortToast("您拒绝授权,会导致应用无法正常使用，可以在系统设置中重新开启权限");
                }
                break;
            case AppConstants.CODE_WRITE_FILE:
                //另一个权限的回调
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showLongToast("现在您拥有了权限");
                    // 这里写你需要的业务逻辑
                    //doYourNeedDo();
                } else {
                    ToastUtils.showShortToast("您拒绝授权,会导致应用无法正常使用，可以在系统设置中重新开启权限");
                }
                break;
        }
    }
}

