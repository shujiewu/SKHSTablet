package cn.sk.skhstablet.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.transition.Visibility;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sk.skhstablet.app.AppConstants;
//import cn.sk.skhstablet.injector.component.fragment.DaggerSingleMonitorComponent;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.component.PaperButton;
import cn.sk.skhstablet.component.PathView;
import cn.sk.skhstablet.injector.component.fragment.DaggerSingleMonitorComponent;
import cn.sk.skhstablet.injector.module.fragment.SingleMonitorModule;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientPhyData;
import cn.sk.skhstablet.presenter.ISingleMonPresenter;
import cn.sk.skhstablet.presenter.impl.SingleMonPresenterImpl;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.ui.activity.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.DevParaChangeAdapter;
import cn.sk.skhstablet.adapter.ExercisePlanAdapter;
import cn.sk.skhstablet.adapter.PatientParaAdapter;
import cn.sk.skhstablet.component.TextItem;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.ui.base.BaseFragment;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static cn.sk.skhstablet.app.AppConstants.mutiDatas;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class SingleMonitorFragment extends BaseFragment<SingleMonPresenterImpl> implements ISingleMonPresenter.View {
    private RecyclerView rySportParaView;//运动参数列表
    private RecyclerView ryPhyParaView;   //生理参数列表
    private PatientDetail patientDetail;  //病人详细信息
    @Inject
    public DevParaChangeAdapter devParaChangeAdapter;  //设备参数的适配器
    @Inject
    public PatientParaAdapter patientParaAdapter;//病人生理参数的适配器
    @Inject
    public  ExercisePlanAdapter exercisePlanAdapter;//医嘱适配器

    private MainActivity mainActivity;
    private ExpandableListView elvExPlan; //医嘱列表
    //病人基本信息控件
    TextView name;
    TextView dev;
    TextView percent;
    TextView tvHospitalNumber;
    TextView tvDevNumber;
    TextView tvConstraintContent;
    PaperButton btnStart;
    PaperButton btnStop;
    //PaperButton btnChange;
    private String singleMonitorID; //单人监测的患者ID
    private HashMap<Integer,String> changeDevPara= new HashMap<>();//改变了的设备参数   位置映射到值

    public HashMap<Integer, String> getChangeDevPara() {
        return changeDevPara;
    }

    public static SingleMonitorFragment newInstance(String singleMonitorID) {
        SingleMonitorFragment newFragment = new SingleMonitorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("singleMonitorID", singleMonitorID);
        newFragment.setArguments(bundle);
        return newFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            singleMonitorID = args.getString("singleMonitorID");
            //Log.e("single ID1",singleMonitorID);
        }
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);
            mPresenter.registerFetchResponse();
        }
        //Log.e("single ID1","1212");
    }
    //View view;

    /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_single_monitor, container,false);
        //View subLayout1 = view.findViewById(R.id.sPatientDetail);
        initInject();
        if (mPresenter!=null){
            mPresenter.setView(this);}

        mainActivity=(MainActivity) getActivity();
        name = (TextView) view.findViewById(R.id.sname);
        id = (TextView) view.findViewById(R.id.sid);
        dev = (TextView) view.findViewById(R.id.sdev);
        percent=(TextView) view.findViewById(R.id.spercent);


        rySportParaView = (RecyclerView) view.findViewById(R.id.sry_sport_para);
        rySportParaView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rySportParaView.setAdapter(devParaChangeAdapter);
        TracksItemDecorator itemDecorator = new TracksItemDecorator(10);
        rySportParaView.addItemDecoration(itemDecorator);
        devParaChangeAdapter.setOnEditChangeListener(new DevParaChangeAdapter.SaveEditListener() {
            @Override
            public void SaveEdit(int position, String devValue) {
                //Log.e("testsava","1");
                //Toast.makeText(getActivity(),"修改了"+position+"位置,"+string, Toast.LENGTH_SHORT).show();
                String devName=devParaChangeAdapter.sportDevName.get(position);
                if(devValue.equals(devParaChangeAdapter.sportDevValue.get(position))&&changeDevPara.containsKey(devName))
                    changeDevPara.remove(devName);
                else
                {
                    changeDevPara.put(devName,devValue);
                }
                if(!mainActivity.hasMenuItem(mainActivity.SAVE_EDIT))
                {
                    TextItem textItem = new TextItem(mainActivity, mainActivity.SAVE_EDIT, "保存修改", Color.parseColor("#1E88E5"));
                    mainActivity.addRightTopItem(textItem);
                }
            }
        }
        );



        ryPhyParaView=(RecyclerView)view.findViewById(R.id.sry_phy_para);
        itemDecorator = new TracksItemDecorator(10);
        ryPhyParaView.addItemDecoration(itemDecorator);
        ryPhyParaView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        ryPhyParaView.setAdapter(patientParaAdapter);

        elvExPlan = (ExpandableListView) view.findViewById(R.id.elv_exercise_plan);
        //exercisePlanAdapter = new ExercisePlanAdapter();
        elvExPlan.setAdapter(exercisePlanAdapter);
       // for(int i = 0; i < exercisePlanAdapter.getGroupCount(); i++){
       //     elvExPlan.expandGroup(i);
       // }

      //  IconItem iconItem = new IconItem(mainActivity, mainActivity.CLOSE_SINGLE , R.drawable.close_pushcha);
      //  mainActivity.addRightTopItem(iconItem);
      //  TextItem textItem = new TextItem(mainActivity, mainActivity.CLOSE_SINGLE, "取消监控", Color.parseColor("#1E88E5"));
      //  mainActivity.addRightTopItem(textItem);

        //loadData(singleMonitorID);
        mPresenter.registerFetchResponse();
        return view;
    }*/

    @Override
    protected void initInject() {
        DaggerSingleMonitorComponent.builder()
                .singleMonitorModule(new SingleMonitorModule())
                .build().injectSingleMonitor(this);
        if (mPresenter!=null){
            mPresenter.setView(this);}
    }

    //发送单人监控命令，此代码在加载失败界面出现后点击空白处重新加载时起作用
    @Override
    protected void loadData() {
        mPresenter.sendPatientDetailRequest(AppConstants.lastSinglePatientID);
    }

    //发送单人监控命令
    public void loadData(String ID) {
        mPresenter.sendPatientDetailRequest(ID);
    }

    //接收到运动设备数据之后更新界面
    @Override
    public void refreshView(PatientDetail mData) {

        //如果当前存在单人监控患者且运动设备不变，且有运动设备参数才进行局部参数更新
        if(patientDetail!=null&&patientDetail.getDevType()==mData.getDevType()&&patientDetail.getPatientID()==mData.getPatientID()&&patientDetail.getSportDevName()!=null&&patientDetail.getSportDevName().size()!=0)
        {
            devParaChangeAdapter.sportDevName=patientDetail.getSportDevName();
            devParaChangeAdapter.sportDevValue=patientDetail.getSportDevValue();
            devParaChangeAdapter.setDevType(patientDetail.getDevType());
            //devParaChangeAdapter.notifyDataSetChanged();
            int size=patientDetail.getSportDevName().size();
            for(int i=0;i<size;i++)
            {
                 if(patientDetail.getSportDevName().get(i).equals(mData.getSportDevName().get(i))&&!patientDetail.getSportDevValue().get(i).equals(mData.getSportDevValue().get(i)))
                 {
                     patientDetail.getSportDevValue().set(i,mData.getSportDevValue().get(i));
                     devParaChangeAdapter.sportDevValue.set(i,mData.getSportDevValue().get(i));
                     devParaChangeAdapter.notifyItemChanged(i);
                 }

            }//局部更新设备参数
            System.out.println("局部更新了单人监控的设备数据");
            patientDetail= mData;//前面先更新了运动数据，然后全部更新
        }
        else
        {
            patientDetail= mData;
            devParaChangeAdapter.sportDevName=patientDetail.getSportDevName();
            devParaChangeAdapter.sportDevValue=patientDetail.getSportDevValue();
            devParaChangeAdapter.setDevType(patientDetail.getDevType());
            devParaChangeAdapter.notifyDataSetChanged();
            System.out.println("更新了单人监控的设备数据");
        }//否则全部更新


        /*//生理参数直接更新
        patientParaAdapter.phyDevName=patientDetail.getPhyDevName();
        patientParaAdapter.phyDevValue=patientDetail.getPhyDevValue();
        patientParaAdapter.notifyDataSetChanged();
        */
        //设备改变
        if (patientDetail.getDeviceNumber()==null||!patientDetail.getDeviceNumber().equals(tvDevNumber.getText().toString()))
            changeDevPara.clear();

        //更新患者基本信息
        name.setText(patientDetail.getName());
        dev.setText(patientDetail.getDev());
        if(patientDetail.getPercent()!=null)
        {
            if(patientDetail.getPercent().isEmpty())
                percent.setText(patientDetail.getPercent());
            else
            {
                percent.setText(patientDetail.getPercent()+"%");
                System.out.println("通过运动数据更新单人界面进度");
            }

        }
        tvDevNumber.setText(String.valueOf(patientDetail.getDeviceNumber()));
        tvHospitalNumber.setText(String.valueOf(patientDetail.getHospitalNumber()));

        //if(patientDetail.getSportDevName()!=null&&btnChange.getVisibility()==View.GONE)
        //{
            //btnChange.setVisibility(View.VISIBLE);
        //}
        boolean changePlan=false;
        if(patientDetail.getExercisePlanId()!=nowPlanID)
        {
            nowPlanID=patientDetail.getExercisePlanId();
            changePlan=true;
        }
        if(patientDetail.getExercisePlanSectionNumber()!=nowPlanSegment){
            nowPlanSegment=patientDetail.getExercisePlanSectionNumber();
            changePlan=true;
        }
        if(changePlan&&planIDList!=null)
        {
            int IDPos=planIDList.indexOf(nowPlanID);
            exercisePlanAdapter.IDPositon=IDPos;
            exercisePlanAdapter.SegPosition=nowPlanSegment-1;
            exercisePlanAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void reSendRequest() {

    }

    //接收到医嘱数据后更新界面
    int nowPlanID;
    int nowPlanSegment;
    List<Integer> planIDList;
    @Override
    public void refreshExercisePlan(List<String> armTypes,List<List<String>> arms,String constrait,List<Integer> planID) {
        tvConstraintContent.setText(constrait);
        planIDList=planID;
        exercisePlanAdapter.armTypes=armTypes;
        exercisePlanAdapter.arms=arms;
        exercisePlanAdapter.notifyDataSetChanged();
        for(int i = 0; i < exercisePlanAdapter.getGroupCount(); i++){
            elvExPlan.collapseGroup(i);
            elvExPlan.expandGroup(i);
        }
    }

    //设置页面状态
    @Override
    public void setPageState(int state) {
        //如果是成功界面，说明第一次成功打开此界面或者更换了单人监控的患者
        if(state==AppConstants.STATE_SUCCESS&&this.getState()!= AppConstants.STATE_SUCCESS)
        {
           // System.out.println("shezhiyemianzhuangt");


           // System.out.println(exercisePlanAdapter.getGroupCount()+"duan");
           if(exercisePlanAdapter!=null&&elvExPlan!=null)
            {
                //exercisePlanAdapter.arms=new ArrayList<>();
                //exercisePlanAdapter.armTypes=new ArrayList<>();
               // exercisePlanAdapter.notifyDataSetChanged();
                exercisePlanAdapter=new ExercisePlanAdapter(new ArrayList<String>(), new ArrayList<List<String>>());
                tvConstraintContent.setText("");
                elvExPlan.setAdapter(exercisePlanAdapter);
           //     System.out.println("shezhiyemianzhuangt3");
            }
            this.setState(AppConstants.STATE_SUCCESS);
            if(pathView!=null&&!pathView.getStop())
                pathView.stop();
            //mPresenter.fetchExercisePlan();

            if (patient.getDeviceNumber()==null||!patient.getDeviceNumber().equals(patientDetail.getDeviceNumber()))
                changeDevPara.clear();

            //Patient patient=((MainActivity) getActivity()).patientListAdapter.mDatas.get(singleMonitorID);
            patientDetail.setPatientID(patient.getPatientID());
            patientDetail.setDeviceNumber(patient.getDeviceNumber());
            patientDetail.setDev(patient.getDev());
            patientDetail.setHospitalNumber(patient.getHospitalNumber());
            patientDetail.setName(patient.getName());
            patientDetail.setDevType(patient.getDevType());
            patientDetail.setPercent("");
            patientDetail.setSportDevName(new ArrayList<String>());
            patientDetail.setSportDevValue(new ArrayList<String>());
            patientDetail.setPhyDevName(new ArrayList<String>());
            patientDetail.setPhyDevValue(new ArrayList<String>());
            patientParaAdapter.phyDevName=new ArrayList<String>();
            patientParaAdapter.phyDevValue=new ArrayList<String>();
            devParaChangeAdapter.sportDevName=new ArrayList<String>();
            devParaChangeAdapter.sportDevValue=new ArrayList<String>();
            devParaChangeAdapter.notifyDataSetChanged();
            patientParaAdapter.notifyDataSetChanged();

            //patientDetail.set
            //patientDetail.setPercent(patient.get);
            // patientDetail.set
            name.setText(patientDetail.getName());
            dev.setText(patientDetail.getDev());
            if(patientDetail.getPercent()!=null)
            {
                if(patientDetail.getPercent().isEmpty())
                    percent.setText(patientDetail.getPercent());
                else
                {
                    percent.setText(patientDetail.getPercent()+"%");
                    System.out.println("通过设置页面状态更新单人界面进度");
                }
            }
            tvDevNumber.setText(patientDetail.getDeviceNumber());
            tvHospitalNumber.setText(String.valueOf(patientDetail.getHospitalNumber()));
            //exercisePlanAdapter.arms=new ArrayList<>();
            //exercisePlanAdapter.armTypes=new ArrayList<>();
            //exercisePlanAdapter.notifyDataSetChanged();
           // mPresenter.fetchExercisePlan(String.valueOf(patientDetail.getPatientID()));
            nowPlanSegment=-1;
            nowPlanID=-1;
            planIDList=new ArrayList<>();
            System.out.println("发送"+patientDetail.getName());
            PatientDetail patientNewDetail=new PatientDetail();
            patientNewDetail.setPatientID(patient.getPatientID());
            patientNewDetail.setDeviceNumber(patient.getDeviceNumber());
            patientNewDetail.setDev(patient.getDev());
            patientNewDetail.setHospitalNumber(patient.getHospitalNumber());
            patientNewDetail.setName(patient.getName());
            patientNewDetail.setDevType(patient.getDevType());
            patientNewDetail.setPercent("");
            patientNewDetail.setSportDevName(new ArrayList<String>());
            patientNewDetail.setSportDevValue(new ArrayList<String>());
            patientNewDetail.setPhyDevName(new ArrayList<String>());
            patientNewDetail.setPhyDevValue(new ArrayList<String>());
            RxBus.getDefault().post(AppConstants.MUTI_DATA, patientNewDetail);
            loadData(String.valueOf(patientDetail.getPatientID()));

            //mutiDatas.add(patientDetail);

            System.out.println("shezhiwancheng");
        }
        else if(state== AppConstants.STATE_LOADING)
        {
            if(pathView!=null&&!pathView.getStop())
                pathView.stop();
            if(exercisePlanAdapter!=null)
            {
                exercisePlanAdapter.arms=new ArrayList<>();
                exercisePlanAdapter.armTypes=new ArrayList<>();
                exercisePlanAdapter.notifyDataSetChanged();
            }
            this.setState(AppConstants.STATE_LOADING);
        }
        else if(state==AppConstants.STATE_ERROR)
        {
            if(pathView!=null&&!pathView.getStop())
                pathView.stop();
            this.setState(AppConstants.STATE_ERROR);
        }

        else if(state==AppConstants.STATE_EMPTY)
        {
            this.setState(AppConstants.STATE_EMPTY);
            //还需要清空数据
            changeDevPara.clear();
            patientDetail=new PatientDetail();
            nowPlanSegment=-1;
            nowPlanID=-1;
            planIDList=new ArrayList<>();
            if(pathView!=null&&pathView.getStop())
                pathView.stop();
        }

    }


    //根据参数控制返回结果显示不同提示
    @Override
    public void setControlState(byte resultState,byte controlState) {
        System.out.println("参数返回成功");
        if(resultState== CommandTypeConstant.SPORT_DEV_CONTORL_SUCC)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("参数修改成功")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

        }
        if(controlState==CommandTypeConstant. SPORT_DEV_CONTORL_LESS_MIN)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("参数修改低于最小值")
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
        else if(controlState==CommandTypeConstant. SPORT_DEV_CONTORL_GREATER_MAX)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("参数修改超过最大值")
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
        else if(controlState==CommandTypeConstant.SPORT_DEV_CONTORL_FREE)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("参数修改失败！设备正处于空闲态")
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
        else if(controlState==CommandTypeConstant.SPORT_DEV_CONTORL_READY)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("参数修改失败！设备正处于就绪态")
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
        else if(controlState==CommandTypeConstant.SPORT_DEV_CONTORL_RUN)
        {
            new SweetAlertDialog(this.getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("参数修改失败！设备正处于运行态")
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
        else
        {
            return;
        }
    }

    public void setChangeDevPara(HashMap<Integer, String> changeDevPara) {
        this.changeDevPara = changeDevPara;
    }

    //接收到生理数据后更新界面
    @Override
    public void refreshPhyData(PatientPhyData patientPhyData) {
        //if(patientPhyData!=null&&patientDetail.getDev()==null)
        //{
            patientParaAdapter.phyDevName=patientPhyData.getPhyDevName();
            patientParaAdapter.phyDevValue=patientPhyData.getPhyDevValue();
            //System.out.println("生理數據顯示");
            patientParaAdapter.notifyDataSetChanged();
       // }//设备为空才更新数据
        //更新心电数据
        Map<Short,List<Short>> map = patientPhyData.getEcgs();
        if(map!=null)
        {
            /*Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

            }*/
            Short i=1;//这里暂时只绘制了一条
            List<Short> ecgData=map.get(i);
            pathView.setECG(ecgData);
        }


        //if(patientPhyData.)

    }

    //根据全局患者监测的病人状态更新界面
    public void refreshPatient(Patient mData)
    {
        byte devType=mData.getDevType();
        String devName=mData.getDev();
        String devNumber=mData.getDeviceNumber();
        //如果当前患者设备为空或者设备改变，需要清空设备数据
        if (devNumber==null||!devNumber.equals(patientDetail.getDeviceNumber()))
        {
            System.out.println("更新了单人监控的设备");
            changeDevPara.clear();
            patientDetail.setSportDevName(new ArrayList<String>());//.clear();
            patientDetail.setSportDevValue(new ArrayList<String>());//.clear();
            patientDetail.setPercent("");
            devParaChangeAdapter.sportDevName=patientDetail.getSportDevName();
            devParaChangeAdapter.sportDevValue=patientDetail.getSportDevValue();

            devParaChangeAdapter.notifyDataSetChanged();

        }
        //生理仪不在线，则清空患者生理数据
        if(!(mData.getPhyConnectState()==CommandTypeConstant.PHY_DEV_CONNECT_ONLINE))//&&mData.getMonConnectState()==CommandTypeConstant.MON_DEV_CONNECT_ONLINE))
        {
            patientDetail.setPhyDevName(new ArrayList<String>());//.clear();
            patientDetail.setPhyDevValue(new ArrayList<String>());//.clear();
            patientParaAdapter.phyDevName=patientDetail.getPhyDevName();
            patientParaAdapter.phyDevValue=patientDetail.getPhyDevValue();
            patientParaAdapter.notifyDataSetChanged();
            pathView.stop();
        }
        patientDetail.setDev(devName);
        patientDetail.setDeviceNumber(devNumber);
        patientDetail.setDevType(devType);
        patientDetail.setName(mData.getName());
        patientDetail.setHospitalNumber(mData.getHospitalNumber());

        name.setText(mData.getName());
        tvHospitalNumber.setText(mData.getHospitalNumber());
        dev.setText(devName);
        tvDevNumber.setText(devNumber);

        if(patientDetail.getPercent()!=null)
        {
            if(patientDetail.getPercent().isEmpty())
                percent.setText(patientDetail.getPercent());
            else
            {
                percent.setText(patientDetail.getPercent()+"%");
                System.out.println("根据病人状态更新患者的单人监控进度");
            }

        }

        //percent.setText(percentValue);
    }
    /*public void registerFetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        refreshView(s);
                    }
                });
        bindSubscription(mSubscription);
    }*/

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_monitor;
    }
    Patient patient;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getDeviceID()
    {
        return patientDetail.getDeviceNumber();
    }
    public byte getDeviceType()
    {
        return patientDetail.getDevType();
    }

    //初始化界面
    @Override
    protected void initView(View view) {


        mainActivity = (MainActivity) getActivity();

        name = (TextView) view.findViewById(R.id.sname);
        tvHospitalNumber = (TextView) view.findViewById(R.id.shospitalNumber);
        tvDevNumber = (TextView) view.findViewById(R.id.sdevNumber);
        dev = (TextView) view.findViewById(R.id.sdev);
        percent = (TextView) view.findViewById(R.id.spercent);
        tvConstraintContent=(TextView) view.findViewById(R.id.tvConstraintContent);
        rySportParaView = (RecyclerView) view.findViewById(R.id.sry_sport_para);
        rySportParaView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rySportParaView.setAdapter(devParaChangeAdapter);
        TracksItemDecorator itemDecorator = new TracksItemDecorator(10);
        rySportParaView.addItemDecoration(itemDecorator);
        //参数修改适配器的参数修改监听器
        devParaChangeAdapter.setOnEditChangeListener(new DevParaChangeAdapter.SaveEditListener() {
                                                         @Override
                                                         public void SaveEdit(int position, String devValue) {
              //Log.e("testsava","1");
              //Toast.makeText(getActivity(),"修改了"+position+"位置,"+string, Toast.LENGTH_SHORT).show();

              //String devName = devParaChangeAdapter.sportDevName.get(position);
              String devOriginValue = devParaChangeAdapter.sportDevValue.get(position);
              /*if (devValue.equals(devParaChangeAdapter.sportDevValue.get(position)) && changeDevPara.containsKey(position))//如果未改变
                  changeDevPara.remove(position);
              else {
                  changeDevPara.put(position, devOriginValue+"修改为"+devValue);
              }*/
               changeDevPara.put(position, devOriginValue+"修改为"+devValue);
              if (!mainActivity.hasMenuItem(mainActivity.SAVE_EDIT)) {
                  TextItem textItem = new TextItem(mainActivity, mainActivity.SAVE_EDIT, "保存修改", Color.parseColor("#1E88E5"));
                  mainActivity.addRightTopItem(textItem);
              }
          }
                                                     }
        );


        ryPhyParaView = (RecyclerView) view.findViewById(R.id.sry_phy_para);
        itemDecorator = new TracksItemDecorator(10);
        ryPhyParaView.addItemDecoration(itemDecorator);
        ryPhyParaView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        patientParaAdapter.setPageState(AppConstants.SINGLE_DATA);
        ryPhyParaView.setAdapter(patientParaAdapter);

        elvExPlan = (ExpandableListView) view.findViewById(R.id.elv_exercise_plan);
        elvExPlan.setAdapter(exercisePlanAdapter);

        patientDetail=new PatientDetail();



      /*  btnStart = (PaperButton) view.findViewById(R.id.bt_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(patientDetail!=null)
                    mPresenter.sendControlStartStop(patientDetail.getPatientID(),patientDetail.getDeviceNumber(),CommandTypeConstant.SPORT_DEV_CONTORL_STATR);
            }
        });

        btnStop = (PaperButton) view.findViewById(R.id.bt_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(patientDetail!=null)
                    mPresenter.sendControlStartStop(patientDetail.getPatientID(),patientDetail.getDeviceNumber(),CommandTypeConstant.SPORT_DEV_CONTORL_STOP);
            }
        });
*/
        /*btnChange= (PaperButton) view.findViewById(R.id.bt_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content="";
                String key="";
                String val="";
                Map map = changeDevPara;
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    key = (String)entry.getKey();
                    val = (String)entry.getValue();
                    content=content+key+"   "+val+"\n";
                }

                new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText(content)
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
            }
        });

        btnChange.setVisibility(View.GONE);*/

        pathView=(PathView) view.findViewById(R.id.ecgView);
    }
    private PathView pathView;//心电图
    public PatientDetail getPatientDetail()
    {
        return patientDetail;
    }
}
