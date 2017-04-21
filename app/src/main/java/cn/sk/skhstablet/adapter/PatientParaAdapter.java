package cn.sk.skhstablet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by wyb on 2017/4/21.
 */

public class PatientParaAdapter extends RecyclerView.Adapter<PatientParaAdapter.PatientParaHolder> {
    List<String> phyDevName;
    List<String> phyDevValue;
    public PatientParaAdapter(List<String> phyDevName,List<String> phyDevValue)
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
            //tvParaValue2=(TextView) view.findViewById(R.id.paravalue2);
        }
        public void bind(PatientParaHolder viewHolder, String paraName,String paraValue) {
           viewHolder.tvParaName1.setText(paraName);
            viewHolder.tvParaValue1.setText(paraValue);
        }
    }
}
