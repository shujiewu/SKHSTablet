package cn.sk.skhstablet.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.PatientListAdapter;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.component.IconItem;
import cn.sk.skhstablet.component.TextItem;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.injector.component.fragment.DaggerMainActivtityComponent;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.tcp.LifeSubscription;
//import cn.sk.skhstablet.injector.component.fragment.DaggerMainActivtityComponent;
import cn.sk.skhstablet.injector.module.activity.MainActivityModule;
import cn.sk.skhstablet.presenter.IPatientListPresenter;
import cn.sk.skhstablet.presenter.impl.SingleMonPresenterImpl;
import cn.sk.skhstablet.tcp.Stateful;
import cn.sk.skhstablet.ui.base.BaseFragment;
import cn.sk.skhstablet.ui.fragment.MutiMonitorFragment;
import cn.sk.skhstablet.ui.fragment.SingleMonitorFragment;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.utlis.Utils;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_MONITOR;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_TRUE;
import static cn.sk.skhstablet.app.AppConstants.SPORT_DEV_FORM;
import static cn.sk.skhstablet.app.AppConstants.STATE_EMPTY;
import static cn.sk.skhstablet.app.AppConstants.STATE_LOADING;
import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.isLoginOther;
import static cn.sk.skhstablet.app.AppConstants.lastMutiPatientID;
import static cn.sk.skhstablet.app.AppConstants.mutiDatas;
import static cn.sk.skhstablet.app.AppConstants.mutiPosition;
import static cn.sk.skhstablet.app.AppConstants.singleMonitorID;
import static cn.sk.skhstablet.app.CommandTypeConstant.SPORT_DEV_CONTORL_TO;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.closeConnection;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.fetchScription;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.idleScription;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.logoutUnsubscribe;
import static cn.sk.skhstablet.tcp.utils.TcpUtils.reconnect;

public class MainActivity extends BorderActivity implements IPatientListPresenter.View,LifeSubscription,Stateful {
    //全局病人列表
    private RecyclerView mRecyclerView;
    //全部选择按钮
    final int MONITORALL = 2;
    //显示全局病人列表按钮
    final int SHOWALL = 4;
    //多人监控界面切换按钮
    final static int MUTIMOITOR=6;
    //单人监控界面切换按钮
    final static int SINGLEMONITOR=7;
    //开始监控按钮
    final int STARTMONITOR=8;
    //保存修改按钮
    public final int SAVE_EDIT=9;
    //退出登录按钮
    final  int LOG_OUT=10;
    //取消单人监控按钮
    public  final int CANCEL_SINGLE_MON=12;
    @Inject
    public PatientListAdapter patientListAdapter;

    SearchView searchView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SingleMonitorFragment singleMonitorFragment;
    private MutiMonitorFragment mutiMonitorFragment;

    //新选择的单人监控患者ID
    public String newSingleMonitorID;
    public Boolean isNewSingle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);}

        //初始化顶部按钮
        IconItem iconItem2 = new IconItem(this,SHOWALL , R.drawable.messenger);
        addTopItem(iconItem2);
        iconItem2.show();
        //初始化左侧按钮
        IconItem  iconItem = new IconItem(this,MUTIMOITOR , R.drawable.ic_content_copy_white_24dp);
        addLeftItem(iconItem);
        iconItem = new IconItem(this,SINGLEMONITOR , R.drawable.ic_content_paste_white_24dp);
        addLeftItem(iconItem);
        iconItem = new IconItem(this,LOG_OUT, R.drawable.exit_app);
        addLeftItem(iconItem);

        mRecyclerView = (RecyclerView) findViewById(R.id.left_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(patientListAdapter);
        //全局患者监控列表的行间距设置
        TracksItemDecorator itemDecorator = new TracksItemDecorator(
                getResources().getDimensionPixelSize(R.dimen.decoration_size));
        mRecyclerView.addItemDecoration(itemDecorator);
        //长按全局患者监控列表的患者单击事件
        patientListAdapter.setOnItemLongClickListener(new PatientListAdapter.OnPatientItemLongClickListener(){
            @Override
            public void onItemLongClick(final View view , int data){
                view.setPressed(true);
                //data是病人ID
                newSingleMonitorID=String.valueOf(data);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setPressed(false);
                        hidePatientList();
                    }
                }, 200);
                showFragment(FRAGMENT_SINGLE);
            }
        });

        fm = getSupportFragmentManager();;
        if(savedInstanceState==null)
            showFragment(FRAGMENT_MUTI);

        //注册MainActivity的观察者
        mPresenter.registerFetchResponse();
        //发送解析格式请求
        mPresenter.sendFormatRequest();
        initSearchView();
        loadData();
    }
    //搜索框相关的函数
    private void initSearchView()
    {
        searchView=(SearchView)findViewById(R.id.searchView);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        //searchView.setQueryHint("搜索");
        TextView textView = (TextView)findViewById(id);
        textView.setTextColor(Color.WHITE);

        textView.setHintTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        id=searchView.getContext().getResources().getIdentifier("android:id/search_close_btn",null,null);
        ImageView closeButton = (ImageView)findViewById(id);
        closeButton.setImageDrawable(getDrawable(R.drawable.ic_close_white)); //设置为自己定义的Icon

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<Patient> filteredModelList = filter(mPresenter.mDatas, newText);

                //reset
                patientListAdapter.setFilter(filteredModelList);
                patientListAdapter.animateTo(filteredModelList);
                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });
    }
    private List<Patient> filter(List<Patient> peoples, String query) {

        final List<Patient> filteredModelList = new ArrayList<>();
        for (Patient people : peoples) {
            final String nameEn = people.getName();
            if (nameEn.contains(query)) {

                filteredModelList.add(people);
            }
        }
        return filteredModelList;
    }

    public static final String POSITION = "position";
    public static final int FRAGMENT_SINGLE=0;
    public static final int FRAGMENT_MUTI=1;
    private int position;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boolean show=this.isMenuShowed();
        if(show==true)
            outState.putInt("menushow",1);
        else
            outState.putInt("menushow",0);
        boolean patientshow=this.getPatientShow();
        if(patientshow==true)
            outState.putInt("patientshow",1);
        else
            outState.putInt("patientshow",0);
        outState.putString("singleid",newSingleMonitorID);
        outState.putInt(POSITION, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int show=savedInstanceState.getInt("menushow");
      //  if(show==1)
      //      findViewById(R.id.btn_menu).performClick();
        newSingleMonitorID=savedInstanceState.getString("singleid");
        showFragment(savedInstanceState.getInt(POSITION));

        super.onRestoreInstanceState(savedInstanceState);
      //  int patientShow=savedInstanceState.getInt("patientshow");
      //  if(patientShow==1)
       //     showPatientList();
        // else
        //     findViewById(R.id.btn_menu).performClick();
    }

    //弹出全局患者监控列表
    public void showPatientList()
    {
        // show RIGHT menu
        //   DisplayMetrics  dm = new DisplayMetrics();
        //   getWindowManager().getDefaultDisplay().getMetrics(dm);
        //    int screenWidth = dm.widthPixels;
        if(isMenuShowed())
        {
            ViewHelper.setX(containerRight, -containerRight.getWidth()+ Utils.dpToPx(48, getResources()));
            float origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x", origin+ containerRight.getWidth())
                    .setDuration(ANIMATIONDURATION).start();
            setPatientShow(true);
        }
        else
        {
            ViewHelper.setX(containerRight, -containerRight.getWidth());
            float origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x", origin+ containerRight.getWidth())
                    .setDuration(ANIMATIONDURATION).start();
            setPatientShow(true);
        }
    }
    //隐藏全局患者监控列表
    public void hidePatientList()
    {
        float origin = ViewHelper.getX(containerRight);
        ObjectAnimator.ofFloat(containerRight, "x", -containerRight.getWidth())
                .setDuration(ANIMATIONDURATION).start();
        setPatientShow(false);
        new Handler().post(new Runnable() {
            public void run() {
                removeTopItem(STARTMONITOR);
                removeTopItem(MONITORALL);
            }
        });
    }
    public void hideFragment(FragmentTransaction ft){
        //如果不为空，就先隐藏起来
        if (singleMonitorFragment!=null){
            ft.hide(singleMonitorFragment);
        }
        if(mutiMonitorFragment!=null) {
            ft.hide(mutiMonitorFragment);
        }
    }
    @Override
    public void loadSinglePatient(String singleID)
    {
        singleMonitorID=singleID;
        //正常点击肯定会设置patient
        singleMonitorFragment.setPatient(patientListAdapter.mDatas.get(mPresenter.hasPatient.get(Integer.parseInt(singleMonitorID))));
        singleMonitorFragment.loadData(singleMonitorID);
        //singleMonitorFragment.getChangeDevPara().clear();
        isNewSingle=true;
    }
    public void showFragment(int index){

        ft=fm.beginTransaction();
        hideFragment(ft);

        //注意这里设置位置
        position = index;

        switch (index){

            case FRAGMENT_SINGLE:
                /**
                 * 如果Fragment为空，就新建一个实例
                 * 如果不为空，就将它从栈中显示出来
                 */
                if(!hasMenuItem(CANCEL_SINGLE_MON))
                {
                    TextItem textItem = new TextItem(this, this.CANCEL_SINGLE_MON, "取消监控", Color.parseColor("#1E88E5"));
                    addRightTopItem(textItem);
                }

                if (singleMonitorFragment==null){
                    singleMonitorFragment=SingleMonitorFragment.newInstance(newSingleMonitorID);
                    ft.add(R.id.contentView,singleMonitorFragment);

                }else {
                    ft.show(singleMonitorFragment);
                }
                ft.commit();
                fm.executePendingTransactions();

                //新的监控
                if(newSingleMonitorID!=null&&!newSingleMonitorID.equals(singleMonitorID))
                {
                    singleMonitorFragment.setState(STATE_LOADING);
                    //if(singleMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)///shanchu
                    //   singleMonitorFragment.setState(AppConstants.STATE_SUCCESS);
                    /*singleMonitorID=newSingleMonitorID;
                    singleMonitorFragment.setPatient(patientListAdapter.mDatas.get(mPresenter.hasPatient.get(Integer.parseInt(singleMonitorID))));
                    singleMonitorFragment.loadData(singleMonitorID);
                    singleMonitorFragment.getChangeDevPara().clear();
                    isNewSingle=true;*/
                    System.out.println("更换了单人监控对象");
                    loadSinglePatient(newSingleMonitorID);
                }
                else
                {
                    if(singleMonitorFragment.getChangeDevPara().size()!=0&&!hasMenuItem(SAVE_EDIT))
                    {
                        TextItem textItem = new TextItem(this,SAVE_EDIT, "保存修改", Color.parseColor("#1E88E5"));
                        addRightTopItem(textItem);
                    }
                }
                break;
            case FRAGMENT_MUTI:
                new Handler().post(new Runnable() {
                    public void run() {
                        removeAllRightTopItems();
                    }
                });
                if (mutiMonitorFragment==null){
                    mutiMonitorFragment=new MutiMonitorFragment();
                    ft.add(R.id.contentView,mutiMonitorFragment);
                    System.out.println("registermuti3");
                }else {
                    ft.show(mutiMonitorFragment);
                }
                /*if(hasMenuItem(SAVEEdit))
                    removeRightTopItem(SAVEEdit);
                if(hasMenuItem(CANCEL_SINGLE_MON))
                    removeRightTopItem(CANCEL_SINGLE_MON);*/

                //MenuItem textItem=getItemById(STARTMONITOR);
                //textItem.show();
              //  removeRightTopItem(CLOSE_SINGLE);
                ft.commit();
                fm.executePendingTransactions();
                break;
        }

        //ft.commit();
    }
    @Override
    public void onAttachFragment(Fragment fragment){

        //当前的界面的保存状态，只是从新让新的Fragment指向了原本未被销毁的fragment，它就是onAttach方法对应的Fragment对象
        if(singleMonitorFragment == null && fragment instanceof SingleMonitorFragment){
            singleMonitorFragment = (SingleMonitorFragment)fragment;
            Log.e("attach","single");
        }else if(mutiMonitorFragment == null && fragment instanceof MutiMonitorFragment){
            mutiMonitorFragment = (MutiMonitorFragment) fragment;
        }
    }
    @Override
    public void onItemClick(int id) {
        switch(id){
            case MONITORALL:
               //patientListAdapter.getmDatas();
                for(Patient patient :patientListAdapter.mDatas)
                {
                    patient.setSelectStatus(PATIENT_SELECT_STATUS_TRUE);
                }
                patientListAdapter.notifyDataSetChanged();

                break;
            case SINGLEMONITOR:
                showFragment(FRAGMENT_SINGLE);
                break;
            case SHOWALL:
                if(getPatientShow())
                {
                    hidePatientList();
                }
                else
                {
                    showPatientList();
                    TextItem textItem = new TextItem(this, MONITORALL, "全部选择", Color.parseColor("#1E88E5"));
                    addTopItem(textItem);
                    textItem.show();
                    textItem = new TextItem(this, STARTMONITOR, "开始监控", Color.parseColor("#1E88E5"));
                    addTopItem(textItem);
                    textItem.show();
                }

                break;
            case MUTIMOITOR:
                showFragment(FRAGMENT_MUTI);

                break;
            case STARTMONITOR:
                patientID.clear();
                for(Patient patient :patientListAdapter.mDatas)
                {
                    if(patient.getSelectStatus().equals(PATIENT_SELECT_STATUS_TRUE)||patient.getSelectStatus().equals(PATIENT_SELECT_STATUS_MONITOR))
                    {
                        patient.setSelectStatus(PATIENT_SELECT_STATUS_MONITOR);
                        patientID.add(patient.getPatientID());
                    }
                }
                patientListAdapter.notifyDataSetChanged();
                hidePatientList();
                sendMonitorRequest();
                showFragment(FRAGMENT_MUTI);
                break;
            case LOG_OUT:
                /*Alerter.create(this)
                        .setTitle("病人预警")
                        .setText("赵六血压超过上限！")
                        .setBackgroundColor(R.color.yal_ms_colorPrimary)
                        .setDuration(10000)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!getPatientShow()) {
                                    showPatientList();
                                }
                                mRecyclerView.getLayoutManager().scrollToPosition(3);
                            }
                        })
                        .show();*/
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定退出？")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                mPresenter.sendLogoutRequest();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
                break;
            case SAVE_EDIT:
                String content="";
                String key="";
                String val="";
                Map map = singleMonitorFragment.getChangeDevPara();
                Iterator iter = map.entrySet().iterator();
                final List<Integer> positonList=new ArrayList<>();
                final List<Integer> changeValueList=new ArrayList<>();
                final List<Byte> parameterCodeList=new ArrayList<>();
                byte devType=singleMonitorFragment.getDeviceType();
                final String deviceID=singleMonitorFragment.getDeviceID();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Integer position=(Integer)entry.getKey();
                    key = SPORT_DEV_FORM.get(devType).get(position).getChineseName();
                    val = (String)entry.getValue();
                    positonList.add(position);
                    changeValueList.add(Integer.parseInt(val.substring(val.indexOf("为")+1)));
                    parameterCodeList.add(SPORT_DEV_FORM.get(devType).get(position).getParameterCode());
                    content=content+key+"   "+val+"\n";
                }

                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText(content)
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                for(int i=0;i<positonList.size();i++)
                                {
                                    mPresenter.sendControl(deviceID,parameterCodeList.get(i),SPORT_DEV_CONTORL_TO,(byte)changeValueList.get(i).intValue());
                                }
                                sDialog.dismissWithAnimation();
                                //sDialog.dismissWithAnimation();
                                /*sDialog.setTitleText("修改成功")
                                        .setConfirmText("确定")
                                        .setContentText("")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);*/
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
                break;
            case CANCEL_SINGLE_MON:
                newSingleMonitorID=null;
                singleMonitorID=null;
                singleMonitorFragment.setPageState(AppConstants.STATE_EMPTY);
                mPresenter.sendCancelSingleMonitorReq();
                showFragment(FRAGMENT_MUTI);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initInject() {
        DaggerMainActivtityComponent.builder()
                .mainActivityModule(new MainActivityModule())
                .build().injectMainActivity(this);
    }

    @Override
    public void refreshView(List<Patient> mData) {
        patientListAdapter.mDatas= mData;
        patientListAdapter.notifyDataSetChanged();
        //mDatas=mData;
        int postion=0;
        for(Patient patient:mData)
        {
            if(patient.getSportState()== CommandTypeConstant.SPORT_WARNING)
            {
                patientWarning(patient.getName(),postion);
            }
            postion++;
        }
    }

    @Override
    public void refreshView(Patient mData, int position)
    {
        patientListAdapter.mDatas.set(position,mData);
        patientListAdapter.notifyItemChanged(position);
        if(mData.getSportState()== CommandTypeConstant.SPORT_WARNING)
        {
            patientWarning(mData.getName(),position);
        }
        if(singleMonitorFragment!=null&&singleMonitorFragment.getPatientDetail()!=null&&singleMonitorID!=null&&mData.getPatientID()==Integer.parseInt(singleMonitorID))
        {
             singleMonitorFragment.refreshPatient(mData);
            //if(mData.getPhyConnectState())
        }//更新单人监控
        if(mutiMonitorFragment.getPatientDetailList()!=null&&hasMutiPatient.containsKey(mData.getPatientID()))
        {
             mutiMonitorFragment.refreshDevInfo(mData,hasMutiPatient.get(mData.getPatientID()));
        }//更新多人监控
    }

    public void patientWarning(String name, final int position)
    {
          Alerter.create(this)
          .setTitle("病人预警")
          .setText(name+"生理参数预警！")
          .setBackgroundColor(R.color.yal_ms_colorPrimary)
          .setDuration(5000)
          .setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if(!getPatientShow()) {
                      showPatientList();
                  }
                  mRecyclerView.getLayoutManager().scrollToPosition(position);
              }
          })
          .show();
    }

    @Override
    public void reSendRequest() {

    }
    List<Integer> patientID=new ArrayList<>();
    void loadData()
    {
        mPresenter.sentPatientListRequest();
    }
    void sendMonitorRequest()
    {
        mutiMonitorFragment.setState(STATE_LOADING);
        //patientID.add(0);
       //patientID.add(1,1);
        mPresenter.sendMutiMonitorRequest(patientID);

        //if(mutiMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)
        //    mutiMonitorFragment.setState(AppConstants.STATE_SUCCESS);
    }

    @Override
    public void setMutiPageState(int state) {
        if(patientID.size()==0)
        {
            mutiMonitorFragment.setState(STATE_EMPTY);
            return;
        }//如果正在监控为0则页面设置为空
        if(state==AppConstants.STATE_SUCCESS)//&&mutiMonitorFragment.getState()!= AppConstants.STATE_SUCCESS
        {
            mutiMonitorFragment.setState(AppConstants.STATE_SUCCESS);
            mutiDatas.clear();
            hasMutiPatient.clear();
            mutiPosition=0;
             for(Patient patient :patientListAdapter.mDatas)
             {
                 for(int number=0;number<lastMutiPatientID.size();number++)
                 {
                     if(patient.getPatientID()==lastMutiPatientID.get(number))
                     {
                         //patient.setSelectStatus(PATIENT_SELECT_STATUS_MONITOR);
                         PatientDetail patientDetail=new PatientDetail();
                         //patientDetail.setDevType(patient.getDevType());
                         patientDetail.setDev(patient.getDev());
                         patientDetail.setName(patient.getName());
                         patientDetail.setPatientID(patient.getPatientID());
                         patientDetail.setHospitalNumber(patient.getHospitalNumber());
                         patientDetail.setPercent(null);
                         patientDetail.setPhyDevValue(new ArrayList<String>());
                         patientDetail.setPhyDevName(new ArrayList<String>());
                         patientDetail.setSportDevName(new ArrayList<String>());
                         patientDetail.setSportDevValue(new ArrayList<String>());
                         RxBus.getDefault().post(AppConstants.MUTI_DATA, patientDetail);
                         //patientID.add(patient.getPatientID());
                     }
                 }

             }
           // }
            return;
            //List<PatientDetail> patientDetails=new
        }
        if(state==AppConstants.STATE_ERROR)
            mutiMonitorFragment.setState(AppConstants.STATE_ERROR);
    }

    @Override
    public void setSinglePageState(int state) {
        if(state==AppConstants.STATE_SUCCESS)
            singleMonitorFragment.setState(AppConstants.STATE_SUCCESS);
    }

    @Override
    public void logoutSuccess(boolean b) {
        //退出

        if(b)
        {
            System.out.println("退出");
            logoutUnsubscribe();
            closeConnection();
            finish();
        }
        else
            System.out.println("未知错误");
    }

    @Override
    public void loginOther(boolean b) {
        if(b)
        {
            isLoginOther=true;
            logoutUnsubscribe();//先关闭所有
            closeConnection();
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("你的账户已在其他设备登录")
                    .setConfirmText("重新登录")
                    .setCancelText("退出")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            reconnect();
                            isLoginOther=false;
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            finish();
                        }
                    })
                    .show();
        }
    }

    private CompositeSubscription mCompositeSubscription;
    //用于添加rx的监听的在onDestroy中记得关闭不然会内存泄漏。
    @Override
    public void bindSubscription(Subscription subscription) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(subscription);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void setState(int state) {

    }
}
