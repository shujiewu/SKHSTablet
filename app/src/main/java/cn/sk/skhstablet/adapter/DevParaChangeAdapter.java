package cn.sk.skhstablet.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import cn.sk.skhstablet.R;
import cn.sk.skhstablet.component.EditTextWithDel;
import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by ldkobe on 2017/4/21.
 */

public class DevParaChangeAdapter extends RecyclerView.Adapter<DevParaChangeAdapter.DevParaChangeHolder>{

    List<String> sportDevName;
    List<String> sportDevValue;
    private SaveEditListener mOnEditChangeListener = null;

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
        holder.bind(holder, sportDevName.get(position), sportDevValue.get(position));
        holder.etSportParaValue.setTag(position);
    }

    @Override
    public int getItemCount() {
        return sportDevName.size();
    }

    public class DevParaChangeHolder extends RecyclerView.ViewHolder {
        private TextView tvSportParaName;
        private EditText etSportParaValue;
        private SeekBar sbSportParaValue;
        public DevParaChangeHolder(View view) {
            super(view);
            tvSportParaName = (TextView) view.findViewById(R.id.changeParaSportName);
            //tvParaName2 = (TextView) view.findViewById(R.id.paraname2);
            etSportParaValue = (EditText) view.findViewById(R.id.changeParaSportValue);
            //tvParaValue2=(TextView) view.findViewById(R.id.paravalue2);
            sbSportParaValue=(SeekBar)view.findViewById(R.id.changeSeekBarValue);

        }
        public void bind(DevParaChangeHolder viewHolder, String paraName, String paraValue) {
            viewHolder.tvSportParaName.setText(paraName);
            viewHolder.etSportParaValue.setText(paraValue);
            viewHolder.sbSportParaValue.setMax(50);
            viewHolder.sbSportParaValue.setProgress(Integer.valueOf(paraValue));

            if (!paraName.equals("坡度"))
            {
                viewHolder.etSportParaValue.setFocusableInTouchMode(false);
                viewHolder.sbSportParaValue.setVisibility(View.GONE);
            }

            sbSportParaValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    viewHolder.etSportParaValue.setText(String.valueOf(i));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

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
                        if (mOnEditChangeListener != null) {
                            //注意这里使用getTag方法获取数据
                            mOnEditChangeListener.SaveEdit((int)etSportParaValue.getTag(),s.toString());
                        }
                        viewHolder.sbSportParaValue.setProgress(Integer.valueOf(s.toString()));
                    }
                    else
                    {
                        viewHolder.sbSportParaValue.setProgress(0);
                    }
                }
            };
            viewHolder.etSportParaValue.addTextChangedListener(textWatcher);
        }
    }

    public interface SaveEditListener{
        void SaveEdit(int position, String string);
    }
}