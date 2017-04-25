package cn.sk.skhstablet.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;


//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
//mport cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.injector.module.fragment.MutiMonitorModule;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.presenter.impl.MutiMonPresenterImpl;
import cn.sk.skhstablet.ui.activity.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.MutiMonitorAdapter;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.ui.base.BaseFragment;

/**
 * Created by ldkobe on 2017/4/17.
 */

public class MutiMonitorFragment extends BaseFragment<MutiMonPresenterImpl> implements IMutiMonPresenter.View {
    private RecyclerView recyclerView;

    @Inject
    MutiMonitorAdapter mutiMonitorAdapter;
    private List<PatientDetail> mDatas;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mDatas= PatientDetailList.PATIENTS;
        view=inflater.inflate(R.layout.fragment_muti_monitor,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ry_muti_monitor);
      //  recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
       // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2 ,LinearLayoutManager.HORIZONTAL,false));

        recyclerView.setAdapter(mutiMonitorAdapter);

        mutiMonitorAdapter.setOnItemLongClickListener(new MutiMonitorAdapter.OnRecyclerViewItemLongClickListener(){
            @Override
            public void onItemLongClick(View view , String data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                MainActivity mainActivity=(MainActivity) getActivity();
             //   view.setPressed(true);
            //    view.postDelayed(() -> {
                    view.setPressed(false);

                    //callback.onClick(holder.getAdapterPosition());
            //    }, 200);
                mainActivity.showFragment(mainActivity.FRAGMENT_SINGLE);
            }
        });
        loadData();
        return view;
    }

    @Override
    protected void initInject() {

     DaggerMutiMonitorComponent.builder()
                .mutiMonitorModule(new MutiMonitorModule())
                .build().injectMutiMonitor(this);
        mPresenter.setView(this);
    }

    @Override
    protected void loadData() {
        mPresenter.fetchPatientDetailData();
    }

    @Override
    public void refreshView(List<PatientDetail> mData) {
        mDatas=mData;
        mutiMonitorAdapter.patientDetailList=mDatas;
        mutiMonitorAdapter.notifyDataSetChanged();
    }
}
