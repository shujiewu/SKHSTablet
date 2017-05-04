package cn.sk.skhstablet.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.injector.component.fragment.DaggerSingleMonitorComponent;
import cn.sk.skhstablet.injector.module.fragment.SingleMonitorModule;
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
    TextView id;
    TextView dev;
    TextView percent;
    private String singleMonitorID;

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
            Log.e("single ID1",singleMonitorID);
        }
        Log.e("single ID1","1212");
    }
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
            public void SaveEdit(int position, String string) {
                //Toast.makeText(getActivity(),"修改了"+position+"位置,"+string, Toast.LENGTH_SHORT).show();
                if(!mainActivity.hasMenuItem(mainActivity.SAVEEdit)&&!mainActivity.isNewSingle)
                {
                    Log.e("testsava","1");
                    TextItem textItem = new TextItem(mainActivity, mainActivity.SAVEEdit, "保存修改", Color.parseColor("#1E88E5"));
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
        Log.e("single ID1","121");
        return view;
    }

    @Override
    protected void initInject() {
        DaggerSingleMonitorComponent.builder()
                .singleMonitorModule(new SingleMonitorModule())
                .build().injectSingleMonitor(this);
    }

    @Override
    protected void loadData() {

    }


    public void loadData(String ID) {
        if(singleSubscription==null)
        {
            registerFetchResponse();
        }
//        Log.e("single ID",ID);
        mPresenter.fetchExercisePlan();
        mPresenter.fetchPatientDetailData(ID);
    }

    @Override
    public void refreshView(PatientDetail mData) {


        patientDetail= mData;
        patientParaAdapter.phyDevName=patientDetail.getPhyDevName();
        patientParaAdapter.phyDevValue=patientDetail.getPhyDevValue();
        patientParaAdapter.notifyDataSetChanged();

        devParaChangeAdapter.sportDevName=patientDetail.getSportDevName();
        devParaChangeAdapter.sportDevValue=patientDetail.getSportDevValue();
        devParaChangeAdapter.notifyDataSetChanged();

        name.setText(patientDetail.getName());
        id.setText(patientDetail.getId());
        dev.setText(patientDetail.getDev());
        percent.setText(patientDetail.getPercent());

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

    private CompositeSubscription singleSubscription;
    public void registerFetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.SINGLE_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        refreshView(s);
                    }
                });
        if (this.singleSubscription == null) {
            singleSubscription = new CompositeSubscription();
        }
        singleSubscription.add(mSubscription);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (singleSubscription != null &&  singleSubscription.hasSubscriptions()) {
            this. singleSubscription.unsubscribe();
        }
    }
}
