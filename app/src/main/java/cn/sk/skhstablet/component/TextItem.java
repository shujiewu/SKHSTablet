package cn.sk.skhstablet.component;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sk.skhstablet.ui.activity.BorderActivity;
import cn.sk.skhstablet.utlis.Utils;

//主界面上方的文字按钮，如“全部选择”，“开始监控”
public class TextItem extends MenuItem {
	
	TextView textView;
	String text;
	int textColor = Color.WHITE;

	public TextItem(BorderActivity borderMenuActivity, int id, String text) {
		super(borderMenuActivity, id);
		this.text = text;
		configure();
	}
	public TextItem(BorderActivity borderMenuActivity, int id, String text, int textColor) {
		super(borderMenuActivity, id);
		this.text = text;
		this.textColor = textColor;
		configure();
	}
	
	private void configure(){
		setMinimumHeight(Utils.dpToPx(48, getResources()));
		textView = new TextView(getContext());
		textView.setText(text);
		textView.setTextColor(textColor);
		textView.setTypeface(null, Typeface.BOLD);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		params.setMargins(Utils.dpToPx(5, getResources()), 0, Utils.dpToPx(5, getResources()), 0);
		textView.setLayoutParams(params);
		addView(textView);

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(radius >= getWidth()){
			x = -1;
			y = -1;
			radius = getWidth() / 4;
			borderMenuActivity.onItemClick(id);
		}
	}
	
	
	public void setText(String text){
		textView.setText(text);
	}

}
