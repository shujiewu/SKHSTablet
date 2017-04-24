package cn.sk.skhstablet.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cn.sk.skhstablet.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.MutiMonitorAdapter;
import cn.sk.skhstablet.adapter.PatientListAdapter;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;

/**
 * Created by ldkobe on 2017/4/17.
 */

public class MutiMonitorFragment extends Fragment {
    private RecyclerView recyclerView;
    private MutiMonitorAdapter mutiMonitorAdapter;
    private List<PatientDetail> mDatas;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatas= PatientDetailList.PATIENTS;
        view=inflater.inflate(R.layout.fragment_muti_monitor,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ry_muti_monitor);
      //  recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
       // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2 ,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(mutiMonitorAdapter = new MutiMonitorAdapter(getActivity(),mDatas));

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
        return view;
    }
}
