package cn.sk.skhstablet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.model.Patient;

import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_FALSE;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_MONITOR;
import static cn.sk.skhstablet.app.AppConstants.PATIENT_SELECT_STATUS_TRUE;

/**
 * Created by ldkobe on 2017/4/18.
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListHolder>
{
    public List<Patient> mDatas;
    private OnPatientItemLongClickListener mOnItemLongClickListener = null;
    public PatientListAdapter(List<Patient> datas)
    {
        mDatas=datas;
    }
    public Context context;
    @Override
    public PatientListHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_view, parent, false);
        context=parent.getContext();
        return new PatientListHolder(view);
    }

    public List<Patient> getmDatas() {
        return mDatas;
    }

    @Override
    public void onBindViewHolder(final PatientListHolder holder, final int position)
    {
        holder.bind(holder, mDatas.get(position));
        holder.itemView.setTag( mDatas.get(position).getPatientID());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(mDatas.get(position).getSelectStatus().equals(PATIENT_SELECT_STATUS_FALSE))
                {
                    mDatas.get(position).setSelectStatus(PATIENT_SELECT_STATUS_TRUE);
                    holder.tvSelectStatus.setText(PATIENT_SELECT_STATUS_TRUE);
                }
                else if(mDatas.get(position).getSelectStatus().equals(PATIENT_SELECT_STATUS_TRUE))
                {
                    mDatas.get(position).setSelectStatus(PATIENT_SELECT_STATUS_FALSE);
                    holder.tvSelectStatus.setText(PATIENT_SELECT_STATUS_FALSE);
                }
                else if(mDatas.get(position).getSelectStatus().equals(PATIENT_SELECT_STATUS_MONITOR))
                {
                    mDatas.get(position).setSelectStatus(PATIENT_SELECT_STATUS_FALSE);
                    holder.tvSelectStatus.setText(PATIENT_SELECT_STATUS_FALSE);
                }

                view.setPressed(true);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setPressed(false);
                    }
                }, 200);
            }
        });
            //int position1 = holder.getLayoutPosition();
           // Log.e("position1",String.valueOf(position1));
           // Log.e("position",String.valueOf(position));

       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(final View view) {
               view.setPressed(true);
               view.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       view.setPressed(false);
                   }
               },200);

                   //callback.onClick(holder.getAdapterPosition());
               //Log.e("longclick", "text1");
               if (mOnItemLongClickListener != null) {
                   //注意这里使用getTag方法获取数据
                   mOnItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
                   //Log.e("longclick", "text");
               }
               //Toast.makeText(view.getContext(),"long click "+mDatas.get(position),Toast.LENGTH_SHORT).show();
               return true;
           }
       });
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }
    public void setOnItemLongClickListener(OnPatientItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
        Log.e("longclick","text2");
    }
    public void setFilter(List<Patient> peoples) {
        mDatas = new ArrayList<>();
        mDatas.addAll(peoples);
        notifyDataSetChanged();
    }
    public void animateTo(List<Patient> peoples) {
        applyAndAnimateRemovals(peoples);
        applyAndAnimateAdditions(peoples);
        applyAndAnimateMovedItems(peoples);
    }
    private void applyAndAnimateRemovals(List<Patient> peoples) {
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            final Patient people = mDatas.get(i);
            if (!peoples.contains(people)) {
                removeItem(i);
            }
        }
    }
    private void applyAndAnimateAdditions(List<Patient> peoples) {
        for (int i = 0, count = peoples.size(); i < count; i++) {
            final Patient people = mDatas.get(i);
            if (!mDatas.contains(people)) {
                addItem(i, people);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Patient> peoples) {
        for (int toPosition = peoples.size() - 1; toPosition >= 0; toPosition--) {
            final Patient people = peoples.get(toPosition);
            final int fromPosition = mDatas.indexOf(people);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }


    public Patient removeItem(int position) {
        final Patient people = mDatas.remove(position);
        notifyItemRemoved(position);
        return people;
    }


    public void addItem(int position, Patient people) {
        mDatas.add(position, people);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Patient people = mDatas.remove(fromPosition);
        mDatas.add(toPosition, people);
        notifyItemMoved(fromPosition, toPosition);
    }

    public class PatientListHolder extends RecyclerView.ViewHolder
    {

        TextView name;
        //TextView rfid;
        //TextView idcard;
        TextView tvHospitalNumber;
        TextView gender;
        TextView tvSelectStatus;
        TextView tvPhyState;
        TextView tvSportState;
        TextView status;
        public PatientListHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            tvHospitalNumber = (TextView) view.findViewById(R.id.hospitalNumber);
            tvSelectStatus = (TextView) view.findViewById(R.id.selectState);
            tvPhyState=(TextView) view.findViewById(R.id.tvPhyState);
            tvSportState=(TextView) view.findViewById(R.id.tvSportState);
            gender=(TextView) view.findViewById(R.id.gender);
            status=(TextView) view.findViewById(R.id.status);
            TextWatcher textWatcher = new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().equals(PATIENT_SELECT_STATUS_FALSE))
                        tvSelectStatus.setTextColor(Color.parseColor("#1E88E5"));
                    else if(s.toString().equals(PATIENT_SELECT_STATUS_TRUE))
                        tvSelectStatus.setTextColor(Color.parseColor("#FFAD5B"));
                    else
                        tvSelectStatus.setTextColor(Color.parseColor("#29c741"));
                }
            };
            tvSelectStatus.addTextChangedListener(textWatcher);
        }

        public void bind(PatientListHolder viewHolder, Patient patient) {
            viewHolder.name.setText(patient.getName());
            viewHolder.tvSelectStatus.setText(patient.getSelectStatus());
            viewHolder.tvHospitalNumber.setText(patient.getHospitalNumber());
            if(patient.getSportState()== CommandTypeConstant.SPORT_NOMAL)
                viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_green));
            else if(patient.getSportState()== CommandTypeConstant.SPORT_WARNING)
                viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_orange));
            else
                viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_grey));

            if(patient.getMonConnectState()== CommandTypeConstant.PHY_DEV_CONNECT_ONLINE)
            {
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(R.color.textgreen));
                viewHolder.tvPhyState.setText(context.getResources().getString(R.string.patient_online));
            }
            else
            {
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvPhyState.setText(context.getResources().getString(R.string.patient_offline));
            }

            if(patient.getConnectState()== CommandTypeConstant.SPORT_DEV_CONNECT_ONLINE)
            {
                viewHolder.tvSportState.setTextColor(context.getResources().getColor(R.color.textgreen));
                viewHolder.tvSportState.setText(patient.getDev());
            }
            else
            {
                viewHolder.tvSportState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvSportState.setText(context.getResources().getString(R.string.patient_offline));
            }

            viewHolder.gender.setText(patient.getGender());
        }
    }
    public static interface OnPatientItemLongClickListener {
        void onItemLongClick(View view , int data);
    }
}
