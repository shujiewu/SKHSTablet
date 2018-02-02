package cn.sk.skhstablet.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.app.CommandTypeConstant;
import cn.sk.skhstablet.component.EditTextWithDel;
import cn.sk.skhstablet.component.FloatQuantityView;
import cn.sk.skhstablet.component.QuantityView;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.SportDevForm;

/**
 * Created by ldkobe on 2017/4/21.
 * 改变运动设备参数的列表适配器
 */

public class DevParaChangeAdapter extends RecyclerView.Adapter<DevParaChangeAdapter.DevParaChangeHolder>{

    public List<String> sportDevName;//列表显示的运动设备名称
    public List<String> sportDevValue;//列表显示的运动设备值
    private SaveEditListener mOnEditChangeListener = null; //参数改变的监听器
    public byte devType;//设备类型

    public byte getDevType() {
        return devType;
    }

    public void setDevType(byte devType) {
        this.devType = devType;
    }

    public DevParaChangeAdapter(List<String> sportDevName, List<String> sportDevValue)
    {
        this.sportDevName = sportDevName;
        this.sportDevValue = sportDevValue;
    }
    public void setOnEditChangeListener(SaveEditListener listener) {
        this.mOnEditChangeListener = listener;
    }
    @Override
    public DevParaChangeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport_para_change_view, parent, false);
        return new DevParaChangeHolder(view);
    }

    @Override
    public void onBindViewHolder(DevParaChangeHolder holder, int position) {
        holder.bind(holder, sportDevName.get(position), sportDevValue.get(position),position);
        holder.qvSportParaValue.getmTextViewQuantity().setTag(position);
        holder.qvSportParaFloatValue.getmTextViewQuantity().setTag(position);
    }

    @Override
    public int getItemCount() {
        return sportDevName.size();
    }

    public class DevParaChangeHolder extends RecyclerView.ViewHolder {
        private TextView tvSportParaName;//运动设备名称
        private TextView tvSportParaValue;//运动设备值
        //private SeekBar sbSportParaValue;
        private QuantityView qvSportParaValue;//整数参数修改框
        private FloatQuantityView qvSportParaFloatValue;//浮点数参数修改框
        public DevParaChangeHolder(View view) {
            super(view);
            tvSportParaName = (TextView) view.findViewById(R.id.changeParaSportName);
            tvSportParaValue = (TextView) view.findViewById(R.id.changeParaSportValue);
            qvSportParaValue=(QuantityView)view.findViewById(R.id.changeQVValue);
            qvSportParaFloatValue=(FloatQuantityView)view.findViewById(R.id.changeFloatQVValue);
        }
        public void bind(final DevParaChangeHolder viewHolder, String paraName, String paraValue,int position) {
            viewHolder.tvSportParaName.setText(paraName);
            viewHolder.tvSportParaValue.setText(paraValue);

            //获取该参数的解析格式
            final SportDevForm sportDevForm= AppConstants.SPORT_DEV_FORM.get(devType).get(position);
            //如果参数可以调节
            if(sportDevForm.getCanControl())
            {
                //倍率不为1,说明是浮点数修改
                if(sportDevForm.getRate()!=1.0)
                {
                    //设置显示值
                    viewHolder.qvSportParaFloatValue.setQuantity(Float.parseFloat(paraValue));
                    //设置最大最小值和可见
                    viewHolder.qvSportParaFloatValue.setMaxQuantity((float)((double)sportDevForm.getControlMaxValue()));
                    viewHolder.qvSportParaFloatValue.setMinQuantity((float)((double)sportDevForm.getControlMinValue()));
                    viewHolder.qvSportParaFloatValue.setVisibility(View.VISIBLE);
                    viewHolder.qvSportParaValue.setVisibility(View.GONE);
                    //增加值改变的观察者
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
                            //参数改变之后回调SaveEditListener接口
                            if(!s.toString().isEmpty())
                            {
                                float value;
                                if (mOnEditChangeListener != null&&!s.toString().equals(viewHolder.tvSportParaValue.getText().toString())) {
                                    //注意这里使用getTag方法获取数据
                                    try {
                                        value=Float.parseFloat(s.toString());
                                        //intValue=(int)(value*sportDevForm.getRate());
                                    } catch (Exception e) {
                                        return;
                                    }
                                    mOnEditChangeListener.SaveEdit((int)qvSportParaFloatValue.getmTextViewQuantity().getTag(),String.valueOf(value));
                                }
                                //viewHolder.sbSportParaValue.setProgress(Integer.valueOf(s.toString()));
                            }
                        }
                    };
                    viewHolder.qvSportParaFloatValue.getmTextViewQuantity().addTextChangedListener(textWatcher);
                }
                else
                {
                    viewHolder.qvSportParaValue.setQuantity(Integer.valueOf(paraValue));
                    viewHolder.qvSportParaValue.setMaxQuantity((int)(double)sportDevForm.getControlMaxValue());
                    viewHolder.qvSportParaValue.setMinQuantity((int)(double)sportDevForm.getControlMinValue());
                    viewHolder.qvSportParaValue.setVisibility(View.VISIBLE);
                    viewHolder.qvSportParaFloatValue.setVisibility(View.GONE);
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
                            if(!s.toString().isEmpty())
                            {
                                if (mOnEditChangeListener != null&&!s.toString().equals(viewHolder.tvSportParaValue.getText().toString())) {
                                    //注意这里使用getTag方法获取数据
                                    mOnEditChangeListener.SaveEdit((int)qvSportParaValue.getmTextViewQuantity().getTag(),s.toString());
                                }
                            }
                        }
                    };
                    viewHolder.qvSportParaValue.getmTextViewQuantity().addTextChangedListener(textWatcher);
                }
            }
            else
            {
                viewHolder.qvSportParaValue.setVisibility(View.GONE);
                viewHolder.qvSportParaFloatValue.setVisibility(View.GONE);
            }
        }
    }

    public interface SaveEditListener{
        void SaveEdit(int position, String string);//参数为修改参数的位置，以及修改后的值
    }
}
