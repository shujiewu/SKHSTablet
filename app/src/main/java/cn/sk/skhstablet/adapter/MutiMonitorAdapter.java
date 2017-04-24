package cn.sk.skhstablet.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.utlis.Utils;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class MutiMonitorAdapter extends RecyclerView.Adapter<MutiMonitorAdapter.MutiMonitorHolder>{
    List<PatientDetail> patientDetailList;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    Context context;
    public MutiMonitorAdapter(Context context, List<PatientDetail> datas)
    {
        this.context=context;
        patientDetailList=datas;
    }
    @Override
    public MutiMonitorHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_view, parent, false);
        //view.setOnClickListener(this);
        return new MutiMonitorHolder(context,view);
    }
    /*@Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,(String)v.getTag());
        }
    }*/
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }
    @Override
    public void onBindViewHolder(MutiMonitorHolder holder, int position) {
        holder.bind(holder, patientDetailList.get(position));
        holder.itemView.setTag(patientDetailList.get(position).getName());

        holder.itemView.setOnLongClickListener(view -> {
            view.setPressed(true);
            view.postDelayed(() -> {
                view.setPressed(false);
                //callback.onClick(holder.getAdapterPosition());
            }, 200);
            if (mOnItemLongClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemLongClickListener.onItemLongClick(view,(String)view.getTag());
            }
            //Toast.makeText(view.getContext(),"long click "+mDatas.get(position),Toast.LENGTH_SHORT).show();
            return true;
        });
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
        RecyclerView recyclerPhyParaView;

        RecyclerView recyclerSportParaView;
        Context context;
        public MutiMonitorHolder(Context context,View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.mname);
            id = (TextView) view.findViewById(R.id.mid);
            dev = (TextView) view.findViewById(R.id.mdev);
            percent=(TextView) view.findViewById(R.id.mpercent);
            recyclerPhyParaView=(RecyclerView)view.findViewById(R.id.ry_phy_para);
            recyclerSportParaView=(RecyclerView)view.findViewById(R.id.ry_sport_para);
            this.context=context;
        }

        public void bind(MutiMonitorHolder viewHolder, PatientDetail patient) {
            viewHolder.name.setText(patient.getName());
            viewHolder.id.setText(patient.getId());
            viewHolder.dev.setText(patient.getDev());
            viewHolder.percent.setText(patient.getPercent());

            recyclerPhyParaView.setLayoutManager(new GridLayoutManager(context,2));
            ViewGroup.LayoutParams layoutParams = recyclerPhyParaView.getLayoutParams();
            int lineNumber = patient.getPhyDevName().size()%2;
            //layoutParams.height =200;
            recyclerPhyParaView.setLayoutParams(layoutParams);
            //recyclerPhyParaView.setLayoutManager(new LinearLayoutManager(context));
            recyclerPhyParaView.setAdapter(new PatientParaAdapter(patient.getPhyDevName(),patient.getPhyDevValue()));

            recyclerSportParaView.setLayoutManager(new GridLayoutManager(context,2));
            recyclerSportParaView.setAdapter(new SportDevParaAdapter(patient.getSportDevName(),patient.getSportDevValue()));

            TracksItemDecorator itemDecorator = new TracksItemDecorator(
                    context.getResources().getDimensionPixelSize(R.dimen.decoration_size));
            recyclerSportParaView.addItemDecoration(itemDecorator);
            recyclerPhyParaView.addItemDecoration(itemDecorator);
        }
    }
    public static interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view , String data);
    }
}
