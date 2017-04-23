package cn.sk.skhstablet.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ldkobe on 2017/4/21.
 */

public class ExercisePlanAdapter implements ExpandableListAdapter {
    private String[] armTypes = new String[]{
            "运动方案1：在跑步机上运动10分钟", "运动方案2：在椭圆机上运动200米", "运动方案3：在跑步机上运动5分钟", "运动方案4"
    };
    private String[][] arms = new String[][]{
            {"第一段：以2米每秒的速度在跑步机上运动1分钟", "第二段：以1米每秒的速度运动1分钟"},
            {"第一段：以2米每秒的速度在跑步机上运动2分钟", "第二段：以1米每秒的速度运动2分钟"},
            {"第一段：以2米每秒的速度在跑步机上运动3分钟", "第二段：以1米每秒的速度运动3分钟"},
            {"第一段：以2米每秒的速度在跑步机上运动4分钟", "第二段：以1米每秒的速度运动4分钟"},
    };
    Context context;
    public ExercisePlanAdapter(Context context)
    {
        this.context=context;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getGroupCount() {
        return armTypes.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return arms[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return armTypes[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return arms[groupPosition][childPosition];
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        //ImageView logo = new ImageView(context);
        //logo.setImageResource(logos[groupPosition]);
       // logo.setPadding(36, 15, 0, 0);
        //ll.addView(logo);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMarginStart(100);
        TextView textView = getTextView();
        textView.setText(getGroup(groupPosition).toString());
        textView.setPadding(0,15, 0, 15);
        textView.setTextSize(12);
        ll.addView(textView,param);
        return ll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.
                LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = getTextView();
        textView.setText(getChild(groupPosition, childPosition).toString());
        textView.setTextSize(10);
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
    private TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(context);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textView.setPadding(50, 15, 0, 15);
        textView.setTextSize(20);
        return textView;
    }
}
