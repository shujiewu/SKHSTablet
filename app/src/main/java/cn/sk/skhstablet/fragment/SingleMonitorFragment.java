package cn.sk.skhstablet.fragment;

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
import android.widget.Toast;

import cn.sk.skhstablet.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.DevParaChangeAdapter;
import cn.sk.skhstablet.adapter.ExercisePlanAdapter;
import cn.sk.skhstablet.adapter.PatientParaAdapter;
import cn.sk.skhstablet.adapter.SportDevParaAdapter;
import cn.sk.skhstablet.component.IconItem;
import cn.sk.skhstablet.component.TextItem;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class SingleMonitorFragment extends Fragment {
    private RecyclerView rySportParaView;
    private RecyclerView ryPhyParaView;
    private PatientDetail patientDetail;
    private DevParaChangeAdapter devParaChangeAdapter;
    private MainActivity mainActivity;
    private ExpandableListView elvExPlaen;
    TextView name;
    TextView id;
    TextView dev;
    TextView percent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        patientDetail= PatientDetailList.PATIENTS.get(0);
        view = inflater.inflate(R.layout.fragment_single_monitor, container,false);
        //View subLayout1 = view.findViewById(R.id.sPatientDetail);
        mainActivity=(MainActivity) getActivity();
        name = (TextView) view.findViewById(R.id.sname);
        id = (TextView) view.findViewById(R.id.sid);
        dev = (TextView) view.findViewById(R.id.sdev);
        percent=(TextView) view.findViewById(R.id.spercent);

        name.setText(patientDetail.getName());
        id.setText(patientDetail.getId());
        dev.setText(patientDetail.getDev());
        percent.setText(patientDetail.getPercent());

        rySportParaView = (RecyclerView) view.findViewById(R.id.sry_sport_para);
        ryPhyParaView=(RecyclerView)view.findViewById(R.id.sry_phy_para);
        rySportParaView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rySportParaView.setAdapter(devParaChangeAdapter=new DevParaChangeAdapter(patientDetail.getSportDevName(),patientDetail.getSportDevValue()));
        TracksItemDecorator itemDecorator = new TracksItemDecorator(1);
        rySportParaView.addItemDecoration(itemDecorator);

        itemDecorator = new TracksItemDecorator(10);
        ryPhyParaView.addItemDecoration(itemDecorator);
        devParaChangeAdapter.setOnEditChangeListener(new DevParaChangeAdapter.SaveEditListener() {
            @Override
            public void SaveEdit(int position, String string) {
                //Toast.makeText(getActivity(),"修改了"+position+"位置,"+string, Toast.LENGTH_SHORT).show();
                if(!mainActivity.hasMenuItem(mainActivity.SAVEEdit))
                {
                    Log.e("testsava","1");
                    TextItem textItem = new TextItem(mainActivity, mainActivity.SAVEEdit, "保存修改", Color.parseColor("#1E88E5"));
                    mainActivity.addRightTopItem(textItem);
                }
            }
        }
        );


        ryPhyParaView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        ryPhyParaView.setAdapter(new PatientParaAdapter(patientDetail.getPhyDevName(),patientDetail.getPhyDevValue()));

        elvExPlaen = (ExpandableListView) view.findViewById(R.id.elv_exercise_plan);

        ExercisePlanAdapter expandableListAdapter = new ExercisePlanAdapter(getActivity());
        elvExPlaen.setAdapter(expandableListAdapter);
        for(int i = 0; i < expandableListAdapter.getGroupCount(); i++){
            elvExPlaen.expandGroup(i);
        }

      //  IconItem iconItem = new IconItem(mainActivity, mainActivity.CLOSE_SINGLE , R.drawable.close_pushcha);
      //  mainActivity.addRightTopItem(iconItem);
      //  TextItem textItem = new TextItem(mainActivity, mainActivity.CLOSE_SINGLE, "取消监控", Color.parseColor("#1E88E5"));
      //  mainActivity.addRightTopItem(textItem);
        return view;
    }
}
