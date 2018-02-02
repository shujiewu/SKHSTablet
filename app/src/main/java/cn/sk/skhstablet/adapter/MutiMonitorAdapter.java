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
 * 多患者监控界面的适配器
 */

public class MutiMonitorAdapter extends RecyclerView.Adapter<MutiMonitorAdapter.MutiMonitorHolder>{
    //多患者列表
    public List<PatientDetail> patientDetailList;
    //长按动作的监听，长按之后进入单人监控
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;
    Context context;
    public MutiMonitorAdapter(List<PatientDetail> datas)
    {
        patientDetailList=datas;
    }
    public MutiMonitorAdapter(Context context, List<PatientDetail> datas)
    {
        this.context=context;
        patientDetailList=datas;
    }
    @Override
    public MutiMonitorHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        context=parent.getContext();
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_patient_view, parent, false);
        //view.setOnClickListener(this);
        return new MutiMonitorHolder(context,view);
    }
    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }
    @Override
    public void onBindViewHolder(MutiMonitorHolder holder, int position) {
        holder.bind(holder, patientDetailList.get(position));
        //设置tag为患者ID，下面会用到
        holder.itemView.setTag(patientDetailList.get(position).getPatientID());

        //这里的长按监听器与全局患者列表的监听器不同，不需要判断患者是否已经是多人监控状态，因为肯定是
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                view.setPressed(true);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setPressed(false);
                    }
                }, 200);

                    //callback.onClick(holder.getAdapterPosition());

                if (mOnItemLongClickListener != null) {
                    //注意这里使用getTag方法获取数据
                    mOnItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
                }
                //Toast.makeText(view.getContext(),"long click "+mDatas.get(position),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return patientDetailList.size();
    }
    public class MutiMonitorHolder extends RecyclerView.ViewHolder
    {
        //患者名字
        TextView name;
        //设备名字
        TextView dev;
        //医嘱完成百分比
        //TextView percent;
        //住院号
        TextView tvHospitalNumber;
        //设备号
        TextView tvDevNumber;
        //生理参数列表
        RecyclerView recyclerPhyParaView;
        //运动设备参数列表
        RecyclerView recyclerSportParaView;
        Context context;
        public MutiMonitorHolder(Context context,View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.mname);
            dev = (TextView) view.findViewById(R.id.mdev);
            //percent=(TextView) view.findViewById(R.id.mpercent);
            tvDevNumber = (TextView) view.findViewById(R.id.mdevNumber);
            tvHospitalNumber = (TextView) view.findViewById(R.id.mhospitalNumber);

            recyclerPhyParaView=(RecyclerView)view.findViewById(R.id.ry_phy_para);
            recyclerSportParaView=(RecyclerView)view.findViewById(R.id.ry_sport_para);
            this.context=context;
        }

        public void bind(MutiMonitorHolder viewHolder, PatientDetail patient) {
            viewHolder.name.setText(patient.getName());
            viewHolder.dev.setText(patient.getDev());
            /*if(patient.getPercent()!=null)
            {
                //如果百分比为空，显示为控制，否则在数值的基础上增加百分号
                if(patient.getPercent().isEmpty())
                    viewHolder.percent.setText("");
                else
                {
                    System.out.println("更新了"+patient.getName()+"进度");
                    viewHolder.percent.setText(patient.getPercent()+"%"+patient.getName());
                }
           }*/
            viewHolder.tvHospitalNumber.setText(patient.getHospitalNumber());
            viewHolder. tvDevNumber.setText(patient.getDeviceNumber());

            //网格形式，总共显示两行患者
            recyclerPhyParaView.setLayoutManager(new GridLayoutManager(context,2));
            //如果值为空那么显示出来是空的，但这里不能传入null，否则出错
            if(patient.getPhyDevName()!=null)
                recyclerPhyParaView.setAdapter(new PatientParaAdapter(patient.getPhyDevName(),patient.getPhyDevValue()));

            recyclerSportParaView.setLayoutManager(new GridLayoutManager(context,2));
            if(patient.getSportDevName()!=null)
                recyclerSportParaView.setAdapter(new SportDevParaAdapter(patient.getSportDevName(),patient.getSportDevValue()));
            //System.out.println("muti3");
            //TracksItemDecorator itemDecorator = new TracksItemDecorator(
             //context.getResources().getDimensionPixelSize(R.dimen.decoration_size));
            //recyclerSportParaView.addItemDecoration(itemDecorator);
           // recyclerPhyParaView.addItemDecoration(itemDecorator);

            // ViewGroup.LayoutParams layoutParams = recyclerPhyParaView.getLayoutParams();
            //int lineNumber = patient.getPhyDevName().size()%2;
            //layoutParams.height =200;
            //recyclerPhyParaView.setLayoutParams(layoutParams);
            //recyclerPhyParaView.setLayoutManager(new LinearLayoutManager(context));
        }
    }
    public static interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view ,Integer data);//第二个参数为患者ID
    }
}
