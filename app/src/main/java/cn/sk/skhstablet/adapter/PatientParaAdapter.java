package cn.sk.skhstablet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by wyb on 2017/4/21.
 * 患者生理参数列表的适配器
 */

public class PatientParaAdapter extends RecyclerView.Adapter<PatientParaAdapter.PatientParaHolder> {
    public List<String> phyDevName; //生理参数名称
    public List<String> phyDevValue;//生理参数值

    //根据这个值来判断是多人监控页面采用此适配器还是单人监控页面采用,单人监控页面的字体要大一些
    private int pageState= AppConstants.MUTI_DATA;

    public void setPageState(int pageState) {
        this.pageState = pageState;
    }

    public PatientParaAdapter(List<String> phyDevName, List<String> phyDevValue)
    {
        this.phyDevName=phyDevName;
        this.phyDevValue=phyDevValue;
    }
    @Override
    public PatientParaAdapter.PatientParaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_para_view, parent, false);
        return new PatientParaHolder(view);
    }

    @Override
    public void onBindViewHolder(PatientParaAdapter.PatientParaHolder holder, int position) {
        holder.bind(holder, phyDevName.get(position),phyDevValue.get(position));
    }

    @Override
    public int getItemCount() {
        return phyDevName.size();
    }

    public class PatientParaHolder extends RecyclerView.ViewHolder {
        private TextView tvParaName1;
        //private TextView tvParaName2;
        private TextView tvParaValue1;
        //private TextView tvParaValue2;

        public PatientParaHolder(View view)
        {
            super(view);
            tvParaName1 = (TextView) view.findViewById(R.id.paraname1);
            //tvParaName2 = (TextView) view.findViewById(R.id.paraname2);
            tvParaValue1 = (TextView) view.findViewById(R.id.paravalue1);
            if(pageState==AppConstants.SINGLE_DATA)
            {
                tvParaName1.setTextSize(18);
                tvParaValue1.setTextSize(22);
            }
            //System.out.println("muti5");
            //tvParaValue2=(TextView) view.findViewById(R.id.paravalue2);
        }
        public void bind(PatientParaHolder viewHolder, String paraName,String paraValue) {
           viewHolder.tvParaName1.setText(paraName);
            viewHolder.tvParaValue1.setText(paraValue);
        }
    }
}
