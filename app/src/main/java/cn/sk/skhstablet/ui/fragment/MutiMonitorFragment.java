package cn.sk.skhstablet.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
//mport cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.app.AppConstants;
//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.component.LoadingPage;
import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.injector.module.fragment.MutiMonitorModule;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.presenter.impl.MutiMonPresenterImpl;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.ui.activity.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.MutiMonitorAdapter;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.ui.base.BaseFragment;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static cn.sk.skhstablet.app.AppConstants.hasMutiPatient;
import static cn.sk.skhstablet.app.AppConstants.mutiDatas;
import static cn.sk.skhstablet.app.CommandTypeConstant.PHY_CONN_ONLINE;
import static cn.sk.skhstablet.app.CommandTypeConstant.PHY_DEV_CONNECT_ONLINE;

/**
 * Created by ldkobe on 2017/4/17.
 */

public class MutiMonitorFragment extends BaseFragment<MutiMonPresenterImpl> implements IMutiMonPresenter.View {
    private RecyclerView recyclerView;   //多人监控界面是一个大的列表

    @Inject
    MutiMonitorAdapter mutiMonitorAdapter; //多人监控界面的适配器

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);
            mPresenter.registerFetchResponse();
        }
        //mPresenter.registerFetchResponse();
    }
    View view;
    /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.basefragment_state_loading,container,false);
        return view;
       view=inflater.inflate(R.layout.fragment_muti_monitor,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ry_muti_monitor);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2 ,LinearLayoutManager.HORIZONTAL,false));
        initInject();
        recyclerView.setAdapter(mutiMonitorAdapter);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mutiMonitorAdapter.setOnItemLongClickListener(new MutiMonitorAdapter.OnRecyclerViewItemLongClickListener(){
            @Override
            public void onItemLongClick(View view , String data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                MainActivity mainActivity=(MainActivity) getActivity();
                view.setPressed(false);
                mainActivity.newSingleMonitorID=data;
                mainActivity.showFragment(mainActivity.FRAGMENT_SINGLE);
            }
        });
        mPresenter.registerFetchResponse();
        loadData();
        return view;
    }*/
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return container;
        if (mLoadingPage == null) {
            mLoadingPage = new LoadingPage(getContext(),container,inflater) {
                @Override
                protected void initView() {
                    MutiMonitorFragment.this.contentView = this.contentView;
                        //bind = ButterKnife.bind(BaseFragment.this, contentView);
                    MutiMonitorFragment.this.initView(this.contentView);
                }

                @Override
                protected void loadData() {
                    MutiMonitorFragment.this.loadData();
                }

                @Override
                protected int getLayoutId() {
                    return MutiMonitorFragment.this.getLayoutId();
                }
            };
        }
        loadBaseData();
        this.setState(AppConstants.STATE_SUCCESS);
        return mLoadingPage;
    }*/
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_muti_monitor;
    }

    @Override
    protected void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.ry_muti_monitor);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2 ,LinearLayoutManager.HORIZONTAL,false));

        recyclerView.setAdapter(mutiMonitorAdapter);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);//此代码使得更新界面不会闪烁

        //长按患者进入单人监控的监听器
        mutiMonitorAdapter.setOnItemLongClickListener(new MutiMonitorAdapter.OnRecyclerViewItemLongClickListener(){
            @Override
            public void onItemLongClick(View view ,Integer data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                MainActivity mainActivity=(MainActivity) getActivity();
                view.setPressed(false);
                mainActivity.newSingleMonitorID=String.valueOf(data);
                mainActivity.showFragment(mainActivity.FRAGMENT_SINGLE);
            }
        });
        System.out.println("registermuti");
    }


    @Override
    protected void initInject() {

     DaggerMutiMonitorComponent.builder()
                .mutiMonitorModule(new MutiMonitorModule())
                .build().injectMutiMonitor(this);
        mPresenter.setView(this);
    }

    //发送多人监控命令,此代码在加载失败界面出现后点击空白处重新加载时起作用
    @Override
    protected void loadData() {
        mPresenter.fetchPatientDetailData();
    }

    //直接更新所有患者
    @Override
    public void refreshView(List<PatientDetail> mData) {
        mutiMonitorAdapter.patientDetailList=mData;

        for(PatientDetail patientDetail :mData)
        {
            System.out.println("直接更新"+patientDetail.getName()+"位置为"+hasMutiPatient.get(patientDetail.getPatientID())+"进度为"+patientDetail.getPercent());
        }
        mutiMonitorAdapter.notifyDataSetChanged();
    }
    //局部更新单个患者
    @Override
    public void refreshView(PatientDetail mData,int position) {
        mutiMonitorAdapter.patientDetailList.set(position,mData);
       // for(PatientDetail patientDetail :mData)
       // {
       // System.out.println("jubu更新"+mData.getName()+"位置为"+position+"进度为"+mData.getPercent());
        // }

        for(PatientDetail patientDetail :mutiMonitorAdapter.patientDetailList)
        {
            System.out.println("111"+patientDetail.getName()+"进度"+patientDetail.getPercent());
        }
        mutiMonitorAdapter.notifyItemChanged(position);
    }

    //根据全局患者监控病人状态改变更新
    public void refreshDevInfo(Patient mData, int position) {
        PatientDetail patientDetail=mutiMonitorAdapter.patientDetailList.get(position);
        if(mData.getDeviceNumber()==null||!mData.getDeviceNumber().equals(patientDetail.getDeviceNumber()))
        {
            System.out.println("更新了设备");

            patientDetail.setName(mData.getName());
            patientDetail.setHospitalNumber(mData.getHospitalNumber());

            patientDetail.setSportDevName(new ArrayList<String>());
            patientDetail.setSportDevValue(new ArrayList<String>());
            patientDetail.setPercent("");
            patientDetail.setDeviceNumber(mData.getDeviceNumber());
            patientDetail.setDev(mData.getDev());
            patientDetail.setDevType(mData.getDevType());
            //patientDetail.setPhyDevName(new ArrayList<String>());//.clear();
            //patientDetail.setPhyDevValue(new ArrayList<String>());//.clear();
        }
        //根据生理仪连接状态清空生理数据列表
        if(mData.getPhyConnectState()!=PHY_DEV_CONNECT_ONLINE)
        {
            patientDetail.setPhyDevName(new ArrayList<String>());//.clear();
            patientDetail.setPhyDevValue(new ArrayList<String>());//.clear();
        }
       /* if(!(mData.getPhyConnectState()== CommandTypeConstant.PHY_DEV_CONNECT_ONLINE&&mData.getMonConnectState()==CommandTypeConstant.MON_DEV_CONNECT_ONLINE))
        {
            patientDetail.setPhyDevName(new ArrayList<String>());//.clear();
            patientDetail.setPhyDevValue(new ArrayList<String>());//.clear();
        }*/
      //  System.out.println("kankanzhelizhixinglel");
        mutiDatas.set(position,patientDetail);
        mutiMonitorAdapter.patientDetailList.set(position,patientDetail);
        mutiMonitorAdapter.notifyItemChanged(position);
    }

    //设置页面状态
    @Override
    public void setPageState(int state) {
        System.out.println("设置多人监控界面");
        if(state==AppConstants.STATE_SUCCESS&&this.getState()!= AppConstants.STATE_SUCCESS)
        {
            this.setState(AppConstants.STATE_SUCCESS);
            return;
        }
        if(state==AppConstants.STATE_ERROR)
        {
            System.out.println("设置多人监控界面失败");
            this.setState(AppConstants.STATE_ERROR);
        }

        //else if(state==AppConstants.)

    }

    @Override
    public int getPageState() {
        return this.getState();
    }

    @Override
    public void reSendRequest() {
        loadData();
    }

    //private CompositeSubscription mutiSubscription;

    /*private HashMap<String,Integer> hasPatient=new HashMap<>();
    private int position=0;


    public void registerFetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        String id=s.getId();
                        if(hasPatient.containsKey(id))
                        {
                            mDatas.set(hasPatient.get(id),s);
                            refreshView(s,hasPatient.get(id));
                        }
                        else
                        {
                            mDatas.add(s);
                            hasPatient.put(id,position++);
                            refreshView(mDatas);
                        }

                    }
                });

        Subscription mSubscriptionRequest = RxBus.getDefault().toObservable(AppConstants.RE_SEND_REQUEST,Boolean.class)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean s) {
                        if(s==true)
                            reSendRequest();
                    }
                });
        bindSubscription(mSubscription);
        bindSubscription(mSubscriptionRequest);
    }*/
    public List<PatientDetail> getPatientDetailList()
    {
        return mutiMonitorAdapter.patientDetailList;
    }
}
