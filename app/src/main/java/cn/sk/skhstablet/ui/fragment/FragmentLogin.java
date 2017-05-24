package cn.sk.skhstablet.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private Handler handler = new Handler(){};




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
        }
        initLogin();
        textListener();
    }

    private void textListener() {
        userphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = userphone.getText().toString();
                if(CheckUtils.isMobile(text)){
                    //抖动
                    rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));

                }

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

                    //rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));

            }
        });
    }
    private String UserID;
    private String Key;
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

                /*if (TextUtils.isEmpty(phone)){
                    rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    loginusericon.setAnimation(Tools.shakeAnimation(2));
                    showSnackar(v,"IYO提示：请输入手机号码");
                    return;
                }
                if(!CheckUtils.isMobile(phone)){
                    //抖动
                    rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    loginusericon.setAnimation(Tools.shakeAnimation(2));
                    showSnackar(v,"IYO提示：用户名不正确");


                    return;
                }
                if (TextUtils.isEmpty(passwords)){
                    rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    codeicon.setAnimation(Tools.shakeAnimation(2));
                    showSnackar(v,"IYO提示：请输入密码");

                    return;
                }*/
                login_progress.setVisibility(View.VISIBLE);

                /*Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade,
                        R.anim.my_alpha_action);*/
                mPresenter.registerFetchResponse();
                UserID=userID;
                Key=key;
                loadData();

                bt_login.setEnabled(false);
/*
                BmobUser.loginByAccount(getActivity(), phone, passwords, new LogInListener<MyUser>() {

                    @Override
                    public void done(MyUser user, BmobException e) {
                        // TODO Auto-generated method stub
                        if(user!=null){
                            rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
                            rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
                            showSnackar(view,"IYO提示：登陆成功");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    login_progress.setVisibility(View.GONE);
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.fade,
                                            R.anim.my_alpha_action);


                                }
                            },1500);

                        }else{
                            login_progress.setVisibility(View.GONE);
                            rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                            codeicon.setAnimation(Tools.shakeAnimation(2));
                            showSnackar(view,"IYO提示：登陆失败");
                        }
                    }
                });*/
            }
        });




    }

    @Override
    public void refreshView(Boolean mData) {
        if(mData==true)
        {
            LOGIN_NAME=UserID;
            LOGIN_KEY=Key;
            mPresenter.sendFormatRequest();
            login_progress.setVisibility(View.GONE);
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade,
                    R.anim.my_alpha_action);
            getActivity().finish();
        }
        else
        {
            login_progress.setVisibility(View.GONE);
            rela_pass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
            codeicon.setAnimation(Tools.shakeAnimation(2));
            rela_name.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
            loginusericon.setAnimation(Tools.shakeAnimation(2));
            bt_login.setEnabled(true);
        }
        //userphone.setText(mData);
    }

    @Override
    public void reSendRequest() {
        mPresenter.sendVerify(UserID,Key);
    }

    @Override
    protected void initInject() {
        DaggerLoginComponent.builder()
                .build().injectLogin(this);
    }

    @Override
    protected void loadData() {
        mPresenter.fetchStateData(UserID,Key);
    }
    @Override
    public void setLoginDisable() {
        bt_login.setEnabled(true);
        login_progress.setVisibility(View.INVISIBLE);
    }
}
	
