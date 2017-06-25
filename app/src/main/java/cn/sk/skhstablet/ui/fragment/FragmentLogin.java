package cn.sk.skhstablet.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.sk.skhstablet.app.AppConstants;
//import cn.sk.skhstablet.injector.component.fragment.DaggerLoginComponent;
import cn.sk.skhstablet.injector.component.fragment.DaggerLoginComponent;
import cn.sk.skhstablet.presenter.ILoginPresenter;
import cn.sk.skhstablet.presenter.impl.LoginPresenterImpl;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.ui.activity.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.component.EditTextWithDel;
import cn.sk.skhstablet.component.PaperButton;
import cn.sk.skhstablet.ui.base.BaseFragment;
import cn.sk.skhstablet.utlis.CheckUtils;
import cn.sk.skhstablet.utlis.Tools;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static cn.sk.skhstablet.app.AppConstants.LOGIN_KEY;
import static cn.sk.skhstablet.app.AppConstants.LOGIN_NAME;

//import cn.cqu.sk.HomeActivity;
//import cn.cqu.sk.UserNameActivity;

/*import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
*/
@ContentView(R.layout.fragment_login)
public class FragmentLogin extends BaseFragment<LoginPresenterImpl> implements ILoginPresenter.View  {
    @ViewInject(R.id.userph)
    EditTextWithDel userphone;
    @ViewInject(R.id.userpass)
    EditTextWithDel userpass;
    @ViewInject(R.id.bt_login)
    Button bt_login;
    @ViewInject(R.id.login_progress)
    ProgressBar login_progress;
    @ViewInject(R.id.tv_forgetcode)
    TextView tv_forgetcode;
    @ViewInject(R.id.loginusericon)
    ImageView loginusericon;
    @ViewInject(R.id.codeicon)
    ImageView codeicon;
    @ViewInject(R.id.rela_name)
    RelativeLayout rela_name;
    @ViewInject(R.id.rela_pass)
    RelativeLayout rela_pass;
    //private Handler handler = new Handler(){};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this,inflater,container);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(View view)  {

    }
   @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);
            mPresenter.registerFetchResponse();
        }
        initLogin();
        textListener();
    }
    //增加文字改变监听器
    private void textListener() {
        userphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        userpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private String UserID;
    private String Key;
    //初始化登录
    private void initLogin() {
        SharedPreferences userinfo = getActivity().getSharedPreferences("userinfo", 0);
        userphone.setText(userinfo.getString("username",null));
        userpass.setText(userinfo.getString("password",null));

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final   String userID = userphone.getText().toString();
                final String key = userpass.getText().toString();
                final  View view= v;

                //判断输入为空
                if (TextUtils.isEmpty(userID)){
                    rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    loginusericon.setAnimation(Tools.shakeAnimation(2));
                    showSnackar(v,"请输入登录名");
                    return;
                }
                //判断输入为空
                if (TextUtils.isEmpty(key)){
                    rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    codeicon.setAnimation(Tools.shakeAnimation(2));
                    showSnackar(v,"请输入密码");
                    return;
                }
                //开始登录
                login_progress.setVisibility(View.VISIBLE);

                UserID=userID;
                Key=key;
                bt_login.setEnabled(false);
                loadData();
            }
        });
    }
    @Override
    protected void loadData() {
        mPresenter.sendVerify(UserID,Key);
    }
    //根据返回的登录状态刷新界面
    @Override
    public void refreshView(Boolean mData) {
        //登录成功
        if(mData)
        {
            LOGIN_NAME=UserID;
            LOGIN_KEY=Key;
            login_progress.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade,
                    R.anim.my_alpha_action);
            getActivity().finish();
        }
        //登录失败
        else
        {
            //提示用户名或密码错误
            login_progress.setVisibility(View.GONE);
            rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
            codeicon.setAnimation(Tools.shakeAnimation(2));
            rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
            loginusericon.setAnimation(Tools.shakeAnimation(2));
            bt_login.setEnabled(true);
        }
    }

    //重连
    @Override
    public void reSendRequest() {
        //mPresenter.sendVerify(UserID,Key);
    }//重新发送请求

    @Override
    protected void initInject() {
        DaggerLoginComponent.builder()
                .build().injectLogin(this);
    }

    //提示登录失败后将按钮和进度条还原
    @Override
    public void setLoginDisable() {
        showSnackar(getView(),"连接服务器失败");
        login_progress.setVisibility(View.GONE);
        bt_login.setEnabled(true);
    }
}
	
