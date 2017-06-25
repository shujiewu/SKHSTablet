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
 * 全局患者监测界面的列表适配器
 */

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.PatientListHolder>
{
    //全局患者列表
    public List<Patient> mDatas;
    //长按动作的监听，长按之后进入单人监控
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
        //每个项的单击监听,已选择变为未选择，未选择变为已选择，正在监控变为未选择
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
        //每个项的长按事件
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
               if (mOnItemLongClickListener != null) {
                   //注意这里使用getTag方法获取数据
                   mOnItemLongClickListener.onItemLongClick(view, (Integer) view.getTag());
               }
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
    }

    //以下函数是搜索功能的辅助函数
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
        TextView tvHospitalNumber;
        TextView gender;
        TextView tvSelectStatus;
        TextView tvPhyState;
        TextView tvMonState;
        TextView tvSportState;
        TextView status;
        public PatientListHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            tvHospitalNumber = (TextView) view.findViewById(R.id.hospitalNumber);
            tvSelectStatus = (TextView) view.findViewById(R.id.selectState);
            tvPhyState=(TextView) view.findViewById(R.id.tvPhyState);
            tvSportState=(TextView) view.findViewById(R.id.tvSportState);
            tvMonState=(TextView) view.findViewById(R.id.tvMonState);
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
                    //根据选中状态来改变颜色
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

            //设置患者运动状态，也就是圆形的颜色以及设备的值和颜色
            if(patient.getSportState()!=CommandTypeConstant.SPORT_DEV_CONNECT_OFFLINE)   //运动状态不为未打卡
            {
                if(patient.getSportState()== CommandTypeConstant.SPORT_NOMAL)
                    viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_green));//正常运动
                else if(patient.getSportState()== CommandTypeConstant.SPORT_WARNING)
                {
                    viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_orange));//预警运动
                }
                else
                {
                    viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_white));  //未运动
                }

                //设置运动设备名称
                if(patient.getConnectState()== CommandTypeConstant.SPORT_CONN_ONLINE)//设备在线
                {
                    viewHolder.tvSportState.setTextColor(context.getResources().getColor(R.color.textgreen));
                    viewHolder.tvSportState.setText(patient.getDev());
                }
                else
                {
                    viewHolder.tvSportState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    viewHolder.tvSportState.setText(context.getResources().getString(R.string.patient_offline));//设备离线
                }
            }
            else
            {
                viewHolder.status.setBackground(context.getResources().getDrawable(R.drawable.status_grey));//未打卡
                viewHolder.tvSportState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));//设备无
                viewHolder.tvSportState.setText(context.getResources().getString(R.string.none));
            }

            //设置生理仪和监护设备的值和颜色
            if(patient.getPhyConnectState()== CommandTypeConstant.PHY_DEV_CONNECT_ONLINE)
            {
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(R.color.textgreen));
                viewHolder.tvPhyState.setText(context.getResources().getString(R.string.patient_online));
                if(patient.getMonConnectState()== CommandTypeConstant.MON_DEV_CONNECT_ONLINE)
                {
                    viewHolder.tvMonState.setTextColor(context.getResources().getColor(R.color.textgreen));
                    viewHolder.tvMonState.setText("正常");
                }
                else if(patient.getMonConnectState()== CommandTypeConstant.MON_DEV_CONNECT_OFFLINE)
                {
                    viewHolder.tvMonState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    viewHolder.tvMonState.setText("异常");
                }
                else
                {
                    viewHolder.tvMonState.setTextColor(context.getResources().getColor(android.R.color.white));
                    viewHolder.tvMonState.setText("准备采集");
                }
            }
            else if (patient.getPhyConnectState()== CommandTypeConstant.PHY_DEV_CONNECT_OFFLINE)
            {
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvPhyState.setText("离线");
                viewHolder.tvMonState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvMonState.setText("");
            }
            else if(patient.getPhyConnectState()== CommandTypeConstant.PHY_DEV_CONNECT_NONE)
            {
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvPhyState.setText("未连接");
                viewHolder.tvMonState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvMonState.setText("");
            }
            else
            {
                viewHolder.tvMonState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvMonState.setText(context.getResources().getString(R.string.none));
                viewHolder.tvPhyState.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                viewHolder.tvPhyState.setText("");
            }

            viewHolder.gender.setText(patient.getGender());
        }
    }
    public static interface OnPatientItemLongClickListener {
        void onItemLongClick(View view , int data);
    }
}
