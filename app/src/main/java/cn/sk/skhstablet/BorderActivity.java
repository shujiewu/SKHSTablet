package cn.sk.skhstablet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sk.skhstablet.component.IconItem;
import cn.sk.skhstablet.component.MenuItem;
import cn.sk.skhstablet.utlis.Utils;

/**
 * Created by ldkobe on 2017/4/17.
 */

public abstract class BorderActivity extends FragmentActivity {
    View contentView;
    LinearLayout containerTop;
    LinearLayout containerLeft;
    LinearLayout containerRight;

    Drawable icon_menu_top;
    Drawable icon_menu_top_short;
    Drawable icon_menu_bottom;
    Drawable icon_menu_bottom_short;
    Drawable icon_menu_center;

    ImageView icn_menu_top;
    ImageView icn_menu_center;
    ImageView icn_menu_bottom;

    final static long ANIMATIONDURATION  = 200;

    private boolean menuShowed = false;
    private boolean patientListShow = false;
    private ArrayList<MenuItem> topItems = new ArrayList<MenuItem>();
    private HashMap<Integer, MenuItem> items = new HashMap<Integer, MenuItem>();
    private ArrayList<IconItem> leftItems = new ArrayList<IconItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_border);
        contentView = findViewById(R.id.contentView);
        icon_menu_top = getResources().getDrawable(R.drawable.icn_menu_top);
        icon_menu_top_short = getResources().getDrawable(R.drawable.icn_menu_top_short);
        icon_menu_bottom = getResources().getDrawable(R.drawable.icn_menu_bottom);
        icon_menu_bottom_short = getResources().getDrawable(R.drawable.icn_menu_bottom_short);
        icon_menu_center = getResources().getDrawable(R.drawable.icn_menu_center);

        configureMenus();
        configureMenuButton();
    }

    private void configureMenus() {
        containerTop = (LinearLayout) findViewById(R.id.ly_container_top);
        containerTop.post(new Runnable() {

            @Override
            public void run() {
                float origin = ViewHelper.getX(containerTop);
                ViewHelper.setX(containerTop, origin);
            }
        });
        containerLeft = (LinearLayout) findViewById(R.id.ly_container_left);
        containerLeft.post(new Runnable() {

            @Override
            public void run() {
                float origin = ViewHelper.getY(containerLeft);
                ViewHelper.setY(containerLeft, origin-containerLeft.getHeight());
            }
        });

        containerRight = (LinearLayout) findViewById(R.id.container_patient);
        containerRight.post(new Runnable() {

            @Override
            public void run() {
                float origin = ViewHelper.getX(containerRight);
                ViewHelper.setX(containerRight, origin-containerRight.getWidth()-Utils.dpToPx(48, getResources()));
            }
        });
        patientListShow=false;
     /*   ViewHelper.setX(containerTop, -containerTop.getWidth()+ Utils.dpToPx(48, getResources()));
        float origin = ViewHelper.getX(containerTop);
        ObjectAnimator.ofFloat(containerTop, "x", 0)
                .setDuration(200).start();*/
    }

    private void configureMenuButton(){
        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(menuShowed)
                    hideMenu();
                else
                    showMenu();

            }
        });
        icn_menu_top = (ImageView) findViewById(R.id.icn_menu_top);
        icn_menu_center = (ImageView) findViewById(R.id.icn_menu_center);
        icn_menu_bottom = (ImageView) findViewById(R.id.icn_menu_bottom);
    }

    public void showMenu(){

        // 旋转左上角图标
        final float centerRotateAnimation = Utils.dpToPx(48, getResources()) / 2;
        RotateAnimation rotateAnimationCenter = new RotateAnimation(0,225,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationCenter.setInterpolator(new AccelerateInterpolator());
        rotateAnimationCenter.setDuration(ANIMATIONDURATION);
        rotateAnimationCenter.setFillAfter(true);
        rotateAnimationCenter.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                //if(!topItems.isEmpty())
                //    topItems.get(0).show();
                //if(!leftItems.isEmpty())
                 //   leftItems.get(0).show();
                for(MenuItem menuItem : leftItems)
                    menuItem.show();
            }
        });
        icn_menu_center.startAnimation(rotateAnimationCenter);

        RotateAnimation rotateAnimationTop = new RotateAnimation(0,120,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationTop.setInterpolator(new AccelerateInterpolator());
        rotateAnimationTop.setDuration(ANIMATIONDURATION/2);
        rotateAnimationTop.setFillAfter(true);
        rotateAnimationTop.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                RotateAnimation rotateAnimationTop = new RotateAnimation(120,245,centerRotateAnimation,centerRotateAnimation);
                rotateAnimationTop.setInterpolator(new AccelerateInterpolator());
                rotateAnimationTop.setDuration(ANIMATIONDURATION/2);
                rotateAnimationTop.setFillAfter(true);
                icn_menu_top.setImageDrawable(icon_menu_top_short);
                icn_menu_top.startAnimation(rotateAnimationTop);
            }
        });
        icn_menu_top.startAnimation(rotateAnimationTop);

        RotateAnimation rotateAnimationBottom = new RotateAnimation(0,100,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationBottom.setInterpolator(new AccelerateInterpolator());
        rotateAnimationBottom.setDuration(ANIMATIONDURATION/2);
        rotateAnimationBottom.setFillAfter(true);
        rotateAnimationBottom.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                RotateAnimation rotateAnimationBottom = new RotateAnimation(100,195,centerRotateAnimation,centerRotateAnimation);
                rotateAnimationBottom.setInterpolator(new AccelerateInterpolator());
                rotateAnimationBottom.setDuration(ANIMATIONDURATION/2);
                rotateAnimationBottom.setFillAfter(true);
                icn_menu_bottom.setImageDrawable(icon_menu_bottom_short);
                icn_menu_bottom.startAnimation(rotateAnimationBottom);
            }
        });
        icn_menu_bottom.startAnimation(rotateAnimationBottom);

        // show left menu
        ViewHelper.setY(containerLeft, -containerLeft.getHeight()+ Utils.dpToPx(48, getResources()));
        float origin = ViewHelper.getY(containerLeft);
        ObjectAnimator.ofFloat(containerLeft, "y", 0)
                .setDuration(ANIMATIONDURATION).start();


        menuShowed = true;
        if(patientListShow==true)
        {
            origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x",Utils.dpToPx(48, getResources()))
                    .setDuration(ANIMATIONDURATION).start();
        }
        //缩放内容界面
        float originalWidth = contentView.getWidth();
        float width = contentView.getWidth() - Utils.dpToPx(48, getResources());
        float scaleX = width / originalWidth;
        float originalHeight = contentView.getHeight();
        float height = contentView.getHeight(); //- Utils.dpToPx(56, getResources());
        float scaleY = height / originalHeight;
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, scaleX, 1, scaleY,contentView.getWidth(),contentView.getHeight());
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setDuration(ANIMATIONDURATION/2);
        scaleAnimation.setFillAfter(true);
        contentView.startAnimation(scaleAnimation);

    }

    public boolean getPatientShow(){return patientListShow;}
    public void setPatientShow(boolean show){ patientListShow=show;}
    public void hideMenu(){
        // Animate icon button
        final float centerRotateAnimation = Utils.dpToPx(48, getResources()) / 2;
        RotateAnimation rotateAnimationCenter = new RotateAnimation(225,0,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationCenter.setInterpolator(new AccelerateInterpolator());
        rotateAnimationCenter.setDuration(ANIMATIONDURATION);
        rotateAnimationCenter.setFillAfter(true);
        icn_menu_center.startAnimation(rotateAnimationCenter);

        RotateAnimation rotateAnimationTop = new RotateAnimation(245,120,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationTop.setInterpolator(new AccelerateInterpolator());
        rotateAnimationTop.setDuration(ANIMATIONDURATION/2);
        rotateAnimationTop.setFillAfter(true);
        rotateAnimationTop.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                RotateAnimation rotateAnimationTop = new RotateAnimation(120,0,centerRotateAnimation,centerRotateAnimation);
                rotateAnimationTop.setInterpolator(new AccelerateInterpolator());
                rotateAnimationTop.setDuration(ANIMATIONDURATION/2);
                rotateAnimationTop.setFillAfter(true);
                icn_menu_top.setImageDrawable(icon_menu_top);
                icn_menu_top.startAnimation(rotateAnimationTop);
            }
        });
        icn_menu_top.startAnimation(rotateAnimationTop);

        RotateAnimation rotateAnimationBottom = new RotateAnimation(195,100,centerRotateAnimation,centerRotateAnimation);
        rotateAnimationBottom.setInterpolator(new AccelerateInterpolator());
        rotateAnimationBottom.setDuration(ANIMATIONDURATION/2);
        rotateAnimationBottom.setFillAfter(true);
        rotateAnimationBottom.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {}

            @Override
            public void onAnimationRepeat(Animation arg0) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                RotateAnimation rotateAnimationBottom = new RotateAnimation(100,0,centerRotateAnimation,centerRotateAnimation);
                rotateAnimationBottom.setInterpolator(new AccelerateInterpolator());
                rotateAnimationBottom.setDuration(ANIMATIONDURATION/2);
                rotateAnimationBottom.setFillAfter(true);
                icn_menu_bottom.setImageDrawable(icon_menu_bottom);
                icn_menu_bottom.startAnimation(rotateAnimationBottom);
            }
        });
        icn_menu_bottom.startAnimation(rotateAnimationBottom);


        // hide left menu
        float origin = ViewHelper.getY(containerLeft);
        ObjectAnimator.ofFloat(containerLeft, "y", -containerLeft.getHeight())
                .setDuration(ANIMATIONDURATION).start();

        if(patientListShow==true)
        {
            //hidePatientList();
            origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x",0)
                    .setDuration(ANIMATIONDURATION).start();
        }

        // hide RIGHT menu
      //  DisplayMetrics  dm = new DisplayMetrics();
     //   getWindowManager().getDefaultDisplay().getMetrics(dm);
     //   int screenWidth = dm.widthPixels;


       float originalWidth = contentView.getWidth();
        float width = contentView.getWidth() - Utils.dpToPx(48, getResources());
        float scaleX = width / originalWidth;
        float originalHeight = contentView.getHeight();
        float height = contentView.getHeight(); //- Utils.dpToPx(56, getResources());
        float scaleY = height / originalHeight;
        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX,1,scaleY,1,contentView.getWidth(),contentView.getHeight());
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setDuration(ANIMATIONDURATION/2);
        scaleAnimation.setFillAfter(true);
        contentView.startAnimation(scaleAnimation);

     //   for(MenuItem menuItem : topItems)
     //       menuItem.hide();
       for(MenuItem menuItem : leftItems)
            menuItem.hide();


        menuShowed = false;
    }

    public boolean isMenuShowed(){
        return this.menuShowed;
    }
    public abstract void onItemClick(int id);
    public MenuItem getItemById(int id){
        return items.get(id);
    }

    /**
     * Add a MenuItem to Top menu container
     * @param item
     */
    public void addTopItem(MenuItem item){
        //	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //	params.gravity= Gravity.RIGHT;
        //	item.setLayoutParams(params);
        topItems.add(item);
        items.put(item.id, item);
        containerTop.addView(item);
        configureTopAnimations();
        //if(menuShowed)
            item.show();
    }

    /**
     * Remove a top MenuItem by id
     * @param id
     */
    public void removeTopItem(int id){
        MenuItem item = getItemById(id);
        items.remove(id);
        topItems.remove(item);
        containerTop.removeAllViews();
        for(MenuItem menuItem : topItems)
            containerTop.addView(menuItem);
        configureTopAnimations();

    }

    /**
     * Add a MenuItem to left menu container
     * @param item
     */
    public void addLeftItem(IconItem item){
        leftItems.add(item);
        items.put(item.id, item);
        containerLeft.addView(item);
        configureLeftAnimations();
        if(menuShowed)
            item.show();
    }

    /**
     * Remove a top MenuItem by id
     * @param id
     */
    public void removeLeftItem(int id){
        MenuItem item = getItemById(id);
        items.remove(id);
        leftItems.remove(item);
        containerLeft.removeAllViews();
        for(MenuItem menuItem : leftItems)
            containerLeft.addView(menuItem);
        configureLeftAnimations();
    }


    /**
     * Clear top menu
     */
    public void removeAllTopItems(){
        for(MenuItem menuItem : topItems)
            items.remove(menuItem.id);
        topItems.clear();
        topItems = new ArrayList<MenuItem>();
        containerTop.removeAllViews();
    }

    /**
     * Clear top menu
     */
    public void removeAllLeftItems(){
        for(MenuItem menuItem : leftItems)
            items.remove(menuItem.id);
        leftItems.clear();
        leftItems = new ArrayList<IconItem>();
        containerLeft.removeAllViews();
    }

    /**
     * Configure show animations of items in top container
     */
    private void configureTopAnimations(){
        for(int i = 0; i < topItems.size(); i ++){
            MenuItem menuItem = topItems.get(i);
            if(i+1 != topItems.size())
                menuItem.setNextItem(topItems.get(i+1));
        }
    }

    /**
     * Configure show animations of items in top container
     */
    private void configureLeftAnimations(){
        for(int i = 0; i < leftItems.size(); i ++){
            MenuItem menuItem = leftItems.get(i);
            menuItem.setNextItem(null);
            if(i+1 != leftItems.size())
                menuItem.setNextItem(leftItems.get(i+1));
        }
    }

    /**
     * Change color for the menu
     * @param color
     */
    public void setMenuColor(int color){
        containerLeft.setBackgroundColor(color);
        containerTop.setBackgroundColor(color);
        //	viewFade.setBackgroundColor(color);

    }

    /**
     * If you set true, hide the menu when you touch outside of the menu
     * @param bool
     */
    public void setHidedOnTouchOutside(boolean bool){
        //	viewFade.setOnClickListener((bool)?onTouchOutside : null);
    }

    View.OnClickListener onTouchOutside = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(menuShowed)
                hideMenu();
        }
    };

    /**
     * Change color of the menu icon
     * @param color
     */
    public void setMenuIconColor(int color){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icn_menu_top);
        Bitmap bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapMutable = Utils.changeColorIcon(bitmapMutable, color);
        icon_menu_top = new BitmapDrawable(getResources(),bitmapMutable);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icn_menu_top_short);
        bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapMutable = Utils.changeColorIcon(bitmapMutable, color);
        icon_menu_top_short = new BitmapDrawable(getResources(),bitmapMutable);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icn_menu_center);
        bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapMutable = Utils.changeColorIcon(bitmapMutable, color);
        icon_menu_center = new BitmapDrawable(getResources(),bitmapMutable);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icn_menu_bottom);
        bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapMutable = Utils.changeColorIcon(bitmapMutable, color);
        icon_menu_bottom = new BitmapDrawable(getResources(),bitmapMutable);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icn_menu_bottom_short);
        bitmapMutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        bitmapMutable = Utils.changeColorIcon(bitmapMutable, color);
        icon_menu_bottom_short = new BitmapDrawable(getResources(),bitmapMutable);

        icn_menu_top.setImageDrawable((menuShowed)?icon_menu_top_short : icon_menu_top);
        icn_menu_center.setImageDrawable(icon_menu_center);
        icn_menu_bottom.setImageDrawable((menuShowed)?icon_menu_bottom_short : icon_menu_bottom);
    }

    // Show icon menu
    public void showIconMenu(){
        findViewById(R.id.btn_menu).setVisibility(View.VISIBLE);
    }

    // Hide icon menu
    public void hideIconMenu(){
        findViewById(R.id.btn_menu).setVisibility(View.INVISIBLE);
    }

    public boolean hasMenuItem(int id)
    {
        if(items.containsKey(id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
