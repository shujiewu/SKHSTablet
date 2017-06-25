package cn.sk.skhstablet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.sk.skhstablet.R;

/**
 * Created by wyb on 2017/4/21.
 * 患者运动设备数据列表的适配器
 */

public class SportDevParaAdapter extends RecyclerView.Adapter<SportDevParaAdapter.SportDevParaHolder> {
    List<String> sportDevName;
    List<String> sportDevValue;

    public SportDevParaAdapter(List<String> sportDevName, List<String> sportDevValue) {
        this.sportDevName = sportDevName;
        this.sportDevValue = sportDevValue;
    }

    @Override
    public SportDevParaAdapter.SportDevParaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sport_para_view, parent, false);
        return new SportDevParaHolder(view);
    }

    @Override
    public void onBindViewHolder(SportDevParaAdapter.SportDevParaHolder holder, int position) {
        holder.bind(holder, sportDevName.get(position), sportDevValue.get(position));
    }

    @Override
    public int getItemCount() {
        return sportDevName.size();
    }

    public class SportDevParaHolder extends RecyclerView.ViewHolder {
        private TextView tvSportParaName1;
        //private TextView tvParaName2;
        private TextView tvSportParaValue1;

        //private TextView tvParaValue2;
        public SportDevParaHolder(View view) {
            super(view);
            tvSportParaName1 = (TextView) view.findViewById(R.id.paraSportName1);
            //tvParaName2 = (TextView) view.findViewById(R.id.paraname2);
            tvSportParaValue1 = (TextView) view.findViewById(R.id.paraSportValue1);
            //tvParaValue2=(TextView) view.findViewById(R.id.paravalue2);
        }

        public void bind(SportDevParaHolder viewHolder, String paraName, String paraValue) {
            viewHolder.tvSportParaName1.setText(paraName);
            viewHolder.tvSportParaValue1.setText(paraValue);
        }
    }
}
