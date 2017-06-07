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

/**
 * Created by ldkobe on 2017/4/18.
 */

public class SingleMonitorFragment extends BaseFragment<SingleMonPresenterImpl> implements ISingleMonPresenter.View {
    private RecyclerView rySportParaView;
    private RecyclerView ryPhyParaView;
    private PatientDetail patientDetail;
    @Inject
    public DevParaChangeAdapter devParaChangeAdapter;
    @Inject
    public PatientParaAdapter patientParaAdapter;
    @Inject
    public  ExercisePlanAdapter exercisePlanAdapter;

    private MainActivity mainActivity;
    private ExpandableListView elvExPlan;
    TextView name;
    TextView dev;
    TextView percent;
    TextView tvHospitalNumber;
    TextView tvDevNumber;

    PaperButton btnStart;
    PaperButton btnStop;
    PaperButton btnChange;
    private String singleMonitorID;
    private HashMap<Integer,String> changeDevPara= new HashMap<>();

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
        mPresenter.registerFetchResponse();
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

    @Override
    protected void loadData() {
        mPresenter.sendPatientDetailRequest(AppConstants.lastSinglePatientID);
    }


    public void loadData(String ID) {
        mPresenter.sendPatientDetailRequest(ID);
    }

    @Override
    public void refreshView(PatientDetail mData) {

        if(patientDetail!=null&&patientDetail.getDevType()==mData.getDevType()&&patientDetail.getPatientID()==mData.getPatientID()&&patientDetail.getSportDevName()!=null)
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

            }//局部更新
        }
        else
        {
            patientDetail= mData;
            devParaChangeAdapter.sportDevName=patientDetail.getSportDevName();
            devParaChangeAdapter.sportDevValue=patientDetail.getSportDevValue();
            devParaChangeAdapter.setDevType(patientDetail.getDevType());
            devParaChangeAdapter.notifyDataSetChanged();
        }
        patientParaAdapter.phyDevName=patientDetail.getPhyDevName();
        patientParaAdapter.phyDevValue=patientDetail.getPhyDevValue();
        patientParaAdapter.notifyDataSetChanged();

        if (patientDetail.getDeviceNumber()==null||!patientDetail.getDeviceNumber().equals(tvDevNumber.getText().toString()))
            changeDevPara.clear();

        name.setText(patientDetail.getName());
        dev.setText(patientDetail.getDev());
        percent.setText(patientDetail.getPercent());
        tvDevNumber.setText(String.valueOf(patientDetail.getDeviceNumber()));
        tvHospitalNumber.setText(String.valueOf(patientDetail.getHospitalNumber()));

        //if(patientDetail.getSportDevName()!=null&&btnChange.getVisibility()==View.GONE)
        //{
            //btnChange.setVisibility(View.VISIBLE);
        //}
    }

    @Override
    public void reSendRequest() {

    }

    @Override
    public void refreshExercisePlan(List<String> armTypes,List<List<String>> arms) {
        exercisePlanAdapter.armTypes=armTypes;
        exercisePlanAdapter.arms=arms;
        exercisePlanAdapter.notifyDataSetChanged();
        for(int i = 0; i < exercisePlanAdapter.getGroupCount(); i++){
            elvExPlan.collapseGroup(i);
            elvExPlan.expandGroup(i);
        }
    }

    @Override
    public void setPageState(int state) {
        if(state==AppConstants.STATE_SUCCESS&&this.getState()!= AppConstants.STATE_SUCCESS)
        {
            this.setState(AppConstants.STATE_SUCCESS);
            mPresenter.fetchExercisePlan();

            if (patient.getDeviceNumber()==null||!patient.getDeviceNumber().equals(patientDetail.getDeviceNumber()))
                changeDevPara.clear();

            //Patient patient=((MainActivity) getActivity()).patientListAdapter.mDatas.get(singleMonitorID);
            patientDetail.setPatientID(patient.getPatientID());
            patientDetail.setDeviceNumber(patient.getDeviceNumber());
            patientDetail.setDev(patient.getDev());
            patientDetail.setHospitalNumber(patient.getHospitalNumber());
            patientDetail.setName(patient.getName());
            patientDetail.setDevType(patient.getDevType());
            //patientDetail.set
            //patientDetail.setPercent(patient.get);
            // patientDetail.set
            name.setText(patientDetail.getName());
            dev.setText(patientDetail.getDev());
            //percent.setText(patientDetail.getPercent());
            tvDevNumber.setText(patientDetail.getDeviceNumber());
            tvHospitalNumber.setText(String.valueOf(patientDetail.getHospitalNumber()));
        }
        else if(state==AppConstants.STATE_ERROR)
            this.setState(AppConstants.STATE_ERROR);
        else if(state==AppConstants.STATE_EMPTY)
        {
            this.setState(AppConstants.STATE_EMPTY);
            //还需要清空数据
        }

    }

    @Override
    public void setControlState(byte resultState,byte controlState) {
        System.out.println("参数返回成功");
        if(resultState== CommandTypeConstant.SPORT_DEV_CONTORL_SUCC)
        {
            System.out.println("设备控制成功");
        }
        if(controlState==CommandTypeConstant. SPORT_DEV_CONTORL_LESS_MIN)
        {
            System.out.println("低于最小值");
        }
    }

    @Override
    public void refreshPhyData(PatientPhyData patientPhyData) {
        if(patientPhyData!=null&&patientDetail.getDev()==null)
        {
            patientParaAdapter.phyDevName=patientPhyData.getPhyDevName();
            patientParaAdapter.phyDevValue=patientPhyData.getPhyDevValue();
            System.out.println("生理數據顯示");
            patientParaAdapter.notifyDataSetChanged();
        }//设备为空才更新数据
        //更新心电数据
        Map<Short,List<Short>> map = patientPhyData.getEcgs();
        if(map!=null)
        {
            /*Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();

            }*/
            Short i=1;
            List<Short> ecgData=map.get(i);
            //if(ecgData==null)
            //{
                System.out.println("weikong");
           // }
            //for(int i=0;i<255;i++)
            //    System.out.println(ecgData.get(i));
            pathView.setECG(ecgData);
        }


        //if(patientPhyData.)

    }

    public void refreshPatient(byte devType,String devName,String devNumber)
    {
        if (devNumber==null||!devNumber.equals(patientDetail.getDeviceNumber()))
            changeDevPara.clear();

        patientDetail.setDev(devName);
        patientDetail.setDeviceNumber(devNumber);
        patientDetail.setDevType(devType);
        //patientDetail.setPercent(percentValue);
        dev.setText(devName);
        tvDevNumber.setText(devNumber);
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

    @Override
    protected void initView(View view) {


        mainActivity = (MainActivity) getActivity();

        name = (TextView) view.findViewById(R.id.sname);
        tvHospitalNumber = (TextView) view.findViewById(R.id.shospitalNumber);
        tvDevNumber = (TextView) view.findViewById(R.id.sdevNumber);
        dev = (TextView) view.findViewById(R.id.sdev);
        percent = (TextView) view.findViewById(R.id.spercent);

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



        btnStart = (PaperButton) view.findViewById(R.id.bt_start);
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

        btnChange= (PaperButton) view.findViewById(R.id.bt_change);
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

        btnChange.setVisibility(View.GONE);

        pathView=(PathView) view.findViewById(R.id.ecgView);
        //pathView.setECG(null);
        /*byte [] content=new byte[255];
        for(int i=0;i<content.length;i++)
        {
            content[i]=127;
        }
        pathView.setContent(content);
        */
       // btnChange.setVisibility(View.GONE);
    }
    private PathView pathView;
    public PatientDetail getPatientDetail()
    {
        return patientDetail;
    }
}
