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
import java.util.List;


import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.PatientListAdapter;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.component.IconItem;
import cn.sk.skhstablet.component.TextItem;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.injector.component.fragment.DaggerMainActivtityComponent;
import cn.sk.skhstablet.tcp.LifeSubscription;
//import cn.sk.skhstablet.injector.component.fragment.DaggerMainActivtityComponent;
import cn.sk.skhstablet.injector.module.activity.MainActivityModule;
import cn.sk.skhstablet.presenter.IPatientListPresenter;
import cn.sk.skhstablet.presenter.impl.SingleMonPresenterImpl;
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
import static cn.sk.skhstablet.app.AppConstants.STATE_LOADING;

public class MainActivity extends BorderActivity implements IPatientListPresenter.View,LifeSubscription {
    private RecyclerView mRecyclerView;
    final int COPY = 0;
    final int PASTE = 1;
    final int MONITORALL = 2;
    final int ORDER = 3;
    final int SHOWALL = 4;
    final int  CREATE = 5;
    final static int MUTIMOITOR=6;
    final static int SINGLEMONITOR=7;
    final int STARTMONITOR=8;
    public final int SAVE_EDIT=9;
    final  int LOG_OUT=10;
    public final int CLOSE_SINGLE=11;
    //private List<Patient> mDatas;
    public  final int CANCEL_SINGLE_MON=12;
    @Inject
    public PatientListAdapter patientListAdapter;

    SearchView searchView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SingleMonitorFragment singleMonitorFragment;
    private MutiMonitorFragment mutiMonitorFragment;
    private String singleMonitorID;
    public String newSingleMonitorID;
    public Boolean isNewSingle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mDatas=new ArrayList<>();
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);}

        //mDatas= PatientList.PATIENTS;
        IconItem iconItem2 = new IconItem(this,SHOWALL , R.drawable.messenger);
        addTopItem(iconItem2);
        iconItem2.show();

        IconItem  iconItem = new IconItem(this,MUTIMOITOR , R.drawable.ic_content_copy_white_24dp);
        addLeftItem(iconItem);
        iconItem = new IconItem(this,SINGLEMONITOR , R.drawable.ic_content_paste_white_24dp);
        addLeftItem(iconItem);

        iconItem = new IconItem(this,LOG_OUT, R.drawable.ic_create_white_24dp);
        addLeftItem(iconItem);

        mRecyclerView = (RecyclerView) findViewById(R.id.left_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mRecyclerView.setAdapter(patientListAdapter);

        TracksItemDecorator itemDecorator = new TracksItemDecorator(
                getResources().getDimensionPixelSize(R.dimen.decoration_size));
        mRecyclerView.addItemDecoration(itemDecorator);
        patientListAdapter.setOnItemLongClickListener(new PatientListAdapter.OnPatientItemLongClickListener(){
            @Override
            public void onItemLongClick(final View view , String data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                //MainActivity mainActivity=(MainActivity) getActivity();
                view.setPressed(true);
                //Toast.makeText(view.getContext(),"long click "+data,Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "开始监控"+data, Snackbar.LENGTH_LONG)
                        .show();
      /*         view.postDelayed(() -> {
                    view.setPressed(false);
                    hidePatientList();
                    showFragment(FRAGMENT_SINGLE);
                    //callback.onClick(holder.getAdapterPosition());
                }, 200);*/
                newSingleMonitorID=data;
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

        initSearchView();
        loadData();

    }
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
            //final String desEn = people.getDescription().toLowerCase();
            //final String name = people.getName();
            //final String des = people.getDescription();
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
            Log.e("dd",String.valueOf(origin));
            setPatientShow(true);
        }
        else
        {
            ViewHelper.setX(containerRight, -containerRight.getWidth());
            float origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x", origin+ containerRight.getWidth())
                    .setDuration(ANIMATIONDURATION).start();
            Log.e("dd",String.valueOf(origin));
            setPatientShow(true);
        }

    }
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
                    Log.e("TestA", "view == 2");

                    ft.add(R.id.contentView,singleMonitorFragment);

                }else {
                    ft.show(singleMonitorFragment);
                }
                ft.commit();
                fm.executePendingTransactions();


                if(newSingleMonitorID!=null&&!newSingleMonitorID.equals(singleMonitorID))
                {
                    //singleMonitorFragment.setState(STATE_LOADING);
                    if(singleMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)
                        singleMonitorFragment.setState(AppConstants.STATE_SUCCESS);
                    singleMonitorID=newSingleMonitorID;
                    singleMonitorFragment.loadData(singleMonitorID);
                    singleMonitorFragment.getChangeDevPara().clear();
                    isNewSingle=true;
                }
                else
                {
                    if(singleMonitorFragment.getChangeDevPara().size()!=0&&!hasMenuItem(SAVE_EDIT))
                    {
                        TextItem textItem = new TextItem(this,SAVE_EDIT, "保存修改", Color.parseColor("#1E88E5"));
                        addRightTopItem(textItem);
                    }
                }
                /*if(hasMenuItem(SAVEEdit)) {
                    new Handler().post(new Runnable() {
                        public void run() {
                            removeRightTopItem(SAVEEdit);
                        }
                    });
                }*/
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
                    Log.e("TestA", "view == 3");
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

                   // removeTopItem(STARTMONITOR);
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
                for(Patient patient :patientListAdapter.mDatas)
                {
                    if(patient.getSelectStatus().equals(PATIENT_SELECT_STATUS_TRUE)||patient.getSelectStatus().equals(PATIENT_SELECT_STATUS_MONITOR))
                    {
                        patient.setSelectStatus(PATIENT_SELECT_STATUS_MONITOR);
                    }
                }
                patientListAdapter.notifyDataSetChanged();
                hidePatientList();
                sendMonitorRequest();
                //new Handler().post(new Runnable() {
                //    public void run() {
                 //       removeTopItem(STARTMONITOR);
                //       removeTopItem(MONITORALL);
                //    }
               // });
                showFragment(FRAGMENT_MUTI);
                break;
            case LOG_OUT:
                Alerter.create(this)
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
                        .show();
                break;
            case SAVE_EDIT:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("确定修改参数？")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                //sDialog.dismissWithAnimation();
                                sDialog
                                        .setTitleText("修改成功")
                                        .setConfirmText("确定")
                                        .setContentText("")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
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
            case CANCEL_SINGLE_MON:
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
        //mDatas=mData;
        patientListAdapter.mDatas= mData;
        patientListAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshView(Patient mData, int position)
    {
        patientListAdapter.mDatas.set(position,mData);
        patientListAdapter.notifyItemChanged(position);
    }



    @Override
    public void reSendRequest() {

    }

    void loadData()
    {
        mPresenter.fetchPatientListData();
    }
    void sendMonitorRequest()
    {
        mutiMonitorFragment.setState(STATE_LOADING);
        mPresenter.sendMutiMonitorRequest();

        if(mutiMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)
            mutiMonitorFragment.setState(AppConstants.STATE_SUCCESS);
    }

    @Override
    public void setMutiPageState(int state) {
        if(state==AppConstants.STATE_SUCCESS&&mutiMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)
            mutiMonitorFragment.setState(AppConstants.STATE_SUCCESS);
    }

    @Override
    public void setSinglePageState(int state) {
        if(state==AppConstants.STATE_SUCCESS&&singleMonitorFragment.getState()!= AppConstants.STATE_SUCCESS)
            singleMonitorFragment.setState(AppConstants.STATE_SUCCESS);
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

}
