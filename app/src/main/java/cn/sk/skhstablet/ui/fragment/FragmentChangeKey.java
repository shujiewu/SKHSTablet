package cn.sk.skhstablet.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.component.EditTextWithDel;
import cn.sk.skhstablet.component.PaperButton;
import cn.sk.skhstablet.injector.component.fragment.DaggerLoginComponent;
import cn.sk.skhstablet.presenter.IChangekeyPresenter;
import cn.sk.skhstablet.presenter.impl.ChangeKeyPresenterImpl;
import cn.sk.skhstablet.ui.base.BaseFragment;
import cn.sk.skhstablet.utlis.Utils;


@ContentView(R.layout.fragment_change_key)
public class FragmentChangeKey extends BaseFragment<ChangeKeyPresenterImpl> implements IChangekeyPresenter.View{
@ViewInject(R.id.next)
PaperButton nextBt;
    @ViewInject(R.id.userpassword)
    EditTextWithDel userpassword;
    @ViewInject(R.id.loginname)
    EditTextWithDel loginname;
    @ViewInject(R.id.newpassword)
    EditTextWithDel newpassword;
    @ViewInject(R.id.renewpassword)
    EditTextWithDel renewpassword;
    @ViewInject(R.id.re_loginname)
    RelativeLayout re_loginname;
    @ViewInject(R.id.re_oldpass)
    RelativeLayout re_oldpass;
    @ViewInject(R.id.re_newpass)
    RelativeLayout re_newpass;
    @ViewInject(R.id.re_renewpass)
    RelativeLayout re_renewpass;
    @ViewInject(R.id.usericon)
    ImageView loginnameIv;
    @ViewInject(R.id.rekeyicon)
    ImageView rekeyIV;
    @ViewInject(R.id.codeicon)
    ImageView passIv;
    @ViewInject(R.id.newkeyicon)
    ImageView newKeyIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     return x.view().inject(this,inflater,container);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);
            mPresenter.registerFetchResponse();
        }
        initView(view);
        TextListener();

    }

    private void TextListener() {
        //用户名改变背景变
        loginname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                re_loginname.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //旧密码改变背景
        userpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                re_oldpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //新密码改变背景
        newpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                re_newpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ///重新输入新密码改变背景
        renewpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                re_renewpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_black));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String loginName;
    private String oldKey;
    private String newKey;
    @Override
    protected void initView(View view)  {

        //确认修改密码的点击事件
        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                final String password = userpassword.getText().toString();
                final String name = loginname.getText().toString();
                final String newpass = newpassword.getText().toString();
                final String renewpass = renewpassword.getText().toString();

                if (TextUtils.isEmpty(name)){
                   // fg_regist.setBackgroundResource(R.color.colorAccent);
                    re_loginname.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    loginnameIv.setAnimation(Utils.shakeAnimation(2));
                    showSnackar(view,"请输入登录名");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    re_oldpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    passIv.setAnimation(Utils.shakeAnimation(2));
                   // fg_regist.setBackgroundResource(R.color.colorAccent);
                    showSnackar(view,"请输入旧密码");
                    return;

                }
                if (TextUtils.isEmpty(newpass)){
                    re_newpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    newKeyIv.setAnimation(Utils.shakeAnimation(2));
                   // fg_regist.setBackgroundResource(R.color.colorAccent);
                    showSnackar(view,"请输入新密码");
                    return;
                }
                if (TextUtils.isEmpty(renewpass)){
                   re_renewpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                   rekeyIV.setAnimation(Utils.shakeAnimation(2));
                    // fg_regist.setBackgroundResource(R.color.colorAccent);
                    showSnackar(view,"请再次输入新密码");
                    return;
                }
                if(!renewpass.equals(newpass)){
                    re_newpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    newKeyIv.setAnimation(Utils.shakeAnimation(2));
                    re_renewpass.setBackground(getResources().getDrawable(R.drawable.bg_border_color_cutmaincolor));
                    rekeyIV.setAnimation(Utils.shakeAnimation(2));
                    showSnackar(view,"两次输入的密码不一致");
                    return;
                }
                loginName=name;
                oldKey=FragmentLogin.Encrypt(password);
                newKey=FragmentLogin.Encrypt(newpass);
                loadData();
            }
        });
    }
    //根据返回的密码修改状态刷新界面
    @Override
    public void refreshView(Byte mData) {
        if(mData== CommandTypeConstant.SUCCESS)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("密码修改成功")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        else if(mData== CommandTypeConstant.OLD_KEY_FALSE)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("用户名或密码错误")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            //System.out.println("接收到修改密码的状态1");
                        }
                    })
                    .show();
        }
        else if(mData==CommandTypeConstant.NONE_FAIL)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("修改失败，未知错误")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();
        }
        else
        {
            showSnackar(getView(),"连接服务器失败");
        }
    }

    @Override
    public void reSendRequest() {

        //mPresenter.sendRequest(loginName,oldKey,newKey);
    }

    @Override
    protected void initInject() {
        DaggerLoginComponent.builder()
                .build().injectChangeKey(this);
    }


    @Override
    protected void loadData() {
        mPresenter.sendRequest(loginName,oldKey,newKey);
    }
}
	
