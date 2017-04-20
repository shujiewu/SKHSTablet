package cn.sk.skhstablet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class MutiMonitorAdapter extends RecyclerView.Adapter<MutiMonitorAdapter.MutiMonitorHolder> implements View.OnClickListener{
    List<PatientDetail> patientDetailList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public MutiMonitorAdapter(List<PatientDetail> datas)
    {
        patientDetailList=datas;
    }
    @Override
    public MutiMonitorHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_view, parent, false);
        view.setOnClickListener(this);
        return new MutiMonitorHolder(view);
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String)v.getTag());
        }
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    @Override
    public void onBindViewHolder(MutiMonitorHolder holder, int position) {
        holder.bind(holder, patientDetailList.get(position));
        holder.itemView.setTag(patientDetailList.get(position).getName());
    }
    @Override
    public int getItemCount()
    {
        return patientDetailList.size();
    }
    public class MutiMonitorHolder extends RecyclerView.ViewHolder
    {

        TextView name;
        TextView id;
        TextView dev;
        TextView percent;

        public MutiMonitorHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.mname);
            id = (TextView) view.findViewById(R.id.mid);
            dev = (TextView) view.findViewById(R.id.mdev);
            percent=(TextView) view.findViewById(R.id.mpercent);
        }

        public void bind(MutiMonitorHolder viewHolder, PatientDetail patient) {
            viewHolder.name.setText(patient.getName());
            viewHolder.id.setText(patient.getId());
            viewHolder.dev.setText(patient.getDev());
            viewHolder.percent.setText(patient.getPercent());
        }
    }
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
}
