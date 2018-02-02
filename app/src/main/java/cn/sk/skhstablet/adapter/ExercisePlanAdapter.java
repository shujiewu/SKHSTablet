package cn.sk.skhstablet.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldkobe on 2017/4/21.
 * 医嘱列表适配器
 */

public class ExercisePlanAdapter extends BaseExpandableListAdapter {
    //医嘱方案总量，也就是每个医嘱的内容
    public List<String> armTypes=new ArrayList<>();
    //每个医嘱的分段内容
    public List<List<String>> arms=new ArrayList<>();
    public int IDPositon=-1;
    public int SegPosition=-1;
    Context context;
    public ExercisePlanAdapter(List<String> armTypes,List<List<String>> arms)
    {
        this.armTypes=armTypes;
        this.arms=arms;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return armTypes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition<arms.size())
            return arms.get(groupPosition).size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(groupPosition<armTypes.size())
            return armTypes.get(groupPosition);
        else
            return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition<armTypes.size()&&childPosition<arms.get(groupPosition).size())
            return arms.get(groupPosition).get(childPosition);//[groupPosition][childPosition];
        else
            return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public boolean hasStableIds() {
        return true;
    }

    //设置医嘱总量的显示格式
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        context=parent.getContext();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        //ImageView logo = new ImageView(context);
        //logo.setImageResource(logos[groupPosition]);
       // logo.setPadding(36, 15, 0, 0);
        //ll.addView(logo);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMarginStart(100);
        TextView textView = getTextView(context);
        if(getGroup(groupPosition)!=null)
            textView.setText(getGroup(groupPosition).toString());
        textView.setPadding(0,15, 0, 15);
        textView.setTextSize(18);
        if(groupPosition==IDPositon&&IDPositon>=0)
            textView.setTextColor(Color.parseColor("#1E88E5"));
        ll.addView(textView,param);
        return ll;
    }

    //设置医嘱分段的显示格式
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        context=parent.getContext();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = getTextView(context);
        if(getChild(groupPosition, childPosition)!=null)
            textView.setText(getChild(groupPosition, childPosition).toString());
        textView.setTextSize(14);
        if(groupPosition==IDPositon&&childPosition==SegPosition&&IDPositon>=0&&SegPosition>=0)
            textView.setTextColor(Color.parseColor("#1E88E5"));
        ll.addView(textView,param);
        return ll;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }
    private TextView getTextView(Context context1) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(context1);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(50, 15, 0, 15);
        textView.setTextSize(20);
        return textView;
    }
}
