package cn.sk.skhstablet.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.model.Patient;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListHolder>
{
    public List<Patient> mDatas;
    public PatientListAdapter(List<Patient> datas)
    {
        mDatas=datas;
    }

    @Override
    public PatientListHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_view, parent, false);
        return new PatientListHolder(view);
    }

    public List<Patient> getmDatas() {
        return mDatas;
    }

    @Override
    public void onBindViewHolder(PatientListHolder holder, int position)
    {
        holder.bind(holder, mDatas.get(position));
        holder.itemView.setOnClickListener(view -> {
            //int position1 = holder.getLayoutPosition();
           // Log.e("position1",String.valueOf(position1));
           // Log.e("position",String.valueOf(position));
            if(mDatas.get(position).getIdcard().equals("未选择"))
            {
                Log.e("1","1");

                mDatas.get(position).setIdcard("已选择");
                holder.idcard.setText("已选择");
                holder.idcard.setTextColor(Color.parseColor("#FFAD5B"));
            }
            else if(mDatas.get(position).getIdcard().equals("已选择"))
            {
                Log.e("2","2");
                mDatas.get(position).setIdcard("未选择");
                holder.idcard.setText("未选择");
                holder.idcard.setTextColor(Color.parseColor("#3F51B5"));
            }

            view.setPressed(true);
            view.postDelayed(() -> {
                view.setPressed(false);
                //callback.onClick(holder.getAdapterPosition());
            }, 200);
        });
        holder.itemView.setOnLongClickListener(view -> {
            view.setPressed(true);
            view.postDelayed(() -> {
                view.setPressed(false);
                //callback.onClick(holder.getAdapterPosition());
            }, 200);
            Toast.makeText(view.getContext(),"long click "+mDatas.get(position),Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    public class PatientListHolder extends RecyclerView.ViewHolder
    {

        TextView name;
        TextView rfid;
        TextView idcard;
        TextView gender;

        public PatientListHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            rfid = (TextView) view.findViewById(R.id.rfid);
            idcard = (TextView) view.findViewById(R.id.idcard);
            gender=(TextView) view.findViewById(R.id.gender);
        }

        public void bind(PatientListHolder viewHolder, Patient patient) {
            viewHolder.name.setText(patient.getName());
            viewHolder.rfid.setText(patient.getRfid());
            viewHolder.idcard.setText(patient.getIdcard());
            viewHolder.gender.setText(patient.getGender());
        }
    }

}
