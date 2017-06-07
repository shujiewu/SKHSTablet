package cn.sk.skhstablet.utlis;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

import java.util.Comparator;

import cn.sk.skhstablet.protocol.MonitorDevForm;
import cn.sk.skhstablet.protocol.SportDevForm;

public class Utils {
	
	
	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources){
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}
	
	public static int getRelativeTop(View myView) {
//	    if (myView.getParent() == myView.getRootView())
	    if(myView.getId() == android.R.id.content)
	        return 0;
	    else
	        return myView.getTop() + getRelativeTop((View) myView.getParent());
	}
	
	public static int getRelativeLeft(View myView) {
//	    if (myView.getParent() == myView.getRootView())
		if(myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}
	
	public static Bitmap changeColorIcon(Bitmap bitmap, int color){
		int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		for (int i=0; i<pixels.length; i++)
		    if(pixels[i] == Color.rgb(254, 255, 255))
		    	pixels[i] = color;
		bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		return bitmap;
	}
	public static Comparator monitorDevComp = new Comparator<MonitorDevForm>() {
		@Override
		public int compare(MonitorDevForm o1, MonitorDevForm o2) {
			// TODO Auto-generated method stub
			if(o1.getOrder()>o2.getOrder())
				return 1;
				//注意！！返回值必须是一对相反数，否则无效。jdk1.7以后就是这样。
				//      else return 0; //无效
			else return -1;
		}
	};

	public static Comparator sportDevComp = new Comparator<SportDevForm>() {
		@Override
		public int compare(SportDevForm o1, SportDevForm o2) {
			// TODO Auto-generated method stub
			if(o1.getUpOrder()>o2.getUpOrder())
				return 1;
				//注意！！返回值必须是一对相反数，否则无效。jdk1.7以后就是这样。
				//      else return 0; //无效
			else return -1;
		}
	};

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}
}
