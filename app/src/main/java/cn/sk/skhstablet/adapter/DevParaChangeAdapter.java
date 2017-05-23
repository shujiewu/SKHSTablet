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
import cn.sk.skhstablet.component.EditTextWithDel;
import cn.sk.skhstablet.component.QuantityView;
import cn.sk.skhstablet.model.PatientDetail;

/**
 * Created by ldkobe on 2017/4/21.
 */

public class DevParaChangeAdapter extends RecyclerView.Adapter<DevParaChangeAdapter.DevParaChangeHolder>{

    public List<String> sportDevName;
    public List<String> sportDevValue;
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
        holder.qvSportParaValue.getmTextViewQuantity().setTag(position);
    }

    @Override
    public int getItemCount() {
        return sportDevName.size();
    }

    public class DevParaChangeHolder extends RecyclerView.ViewHolder {
        private TextView tvSportParaName;
        private TextView tvSportParaValue;
        //private SeekBar sbSportParaValue;
        private QuantityView qvSportParaValue;
        public DevParaChangeHolder(View view) {
            super(view);
            tvSportParaName = (TextView) view.findViewById(R.id.changeParaSportName);
            //tvParaName2 = (TextView) view.findViewById(R.id.paraname2);
            tvSportParaValue = (TextView) view.findViewById(R.id.changeParaSportValue);
            //tvParaValue2=(TextView) view.findViewById(R.id.paravalue2);
            //sbSportParaValue=(SeekBar)view.findViewById(R.id.changeSeekBarValue);
            qvSportParaValue=(QuantityView)view.findViewById(R.id.changeQVValue);

        }
        public void bind(final DevParaChangeHolder viewHolder, String paraName, String paraValue) {
            viewHolder.tvSportParaName.setText(paraName);
            viewHolder.tvSportParaValue.setText(paraValue);
            //viewHolder.sbSportParaValue.setMax(50);
           // viewHolder.sbSportParaValue.setProgress(Integer.valueOf(paraValue));
            viewHolder.qvSportParaValue.setQuantity(Integer.valueOf(paraValue));
            viewHolder.qvSportParaValue.setMaxQuantity(50);

            if (!paraName.equals("坡度"))
            {
                //viewHolder.tvSportParaValue.setFocusableInTouchMode(false);
                viewHolder.qvSportParaValue.setVisibility(View.GONE);
            }

            /*sbSportParaValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

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
            });*/

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
                            mOnEditChangeListener.SaveEdit((int)qvSportParaValue.getmTextViewQuantity().getTag(),s.toString());
                        }
                        Log.e("textchange","1");
                        //viewHolder.sbSportParaValue.setProgress(Integer.valueOf(s.toString()));
                    }
                }
            };
            viewHolder.qvSportParaValue.getmTextViewQuantity().addTextChangedListener(textWatcher);
        }
    }

    public interface SaveEditListener{
        void SaveEdit(int position, String string);
    }
}
