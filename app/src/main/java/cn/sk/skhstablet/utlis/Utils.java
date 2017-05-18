package cn.sk.skhstablet.utlis;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

import java.util.Comparator;

import cn.sk.skhstablet.protocol.MonitorDevForm;

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
}
