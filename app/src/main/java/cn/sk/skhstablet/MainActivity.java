package cn.sk.skhstablet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import cn.sk.skhstablet.adapter.PatientListAdapter;
import cn.sk.skhstablet.component.IconItem;
import cn.sk.skhstablet.component.MenuItem;
import cn.sk.skhstablet.component.TextItem;
import cn.sk.skhstablet.component.TracksItemDecorator;
import cn.sk.skhstablet.fragment.MutiMonitorFragment;
import cn.sk.skhstablet.fragment.SingleMonitorFragment;
import cn.sk.skhstablet.model.Patient;
import cn.sk.skhstablet.model.PatientList;
import cn.sk.skhstablet.utlis.Utils;

public class MainActivity extends BorderActivity {
    private RecyclerView mRecyclerView;
    final int COPY = 0;
    final int PASTE = 1;
    final int MONITORALL = 2;
    final int ORDER = 3;
    final int SHOWALL = 4;
    final int  CREATE = 5;
    final static int MUTIMOITOR=6;
    final static int SINGLEMONITOR=7;
    final int STARTMONITOR=8;
    public final int SAVEEdit=9;
    private List<Patient> mDatas;
    private PatientListAdapter patientListAdapter;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private SingleMonitorFragment singleMonitorFragment;
    private MutiMonitorFragment mutiMonitorFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatas= PatientList.PATIENTS;
        IconItem iconItem2 = new IconItem(this,SHOWALL , R.drawable.messenger);
        addTopItem(iconItem2);
        iconItem2.show();

        IconItem  iconItem = new IconItem(this,MUTIMOITOR , R.drawable.ic_content_copy_white_24dp);
        addLeftItem(iconItem);
        iconItem = new IconItem(this,SINGLEMONITOR , R.drawable.ic_content_paste_white_24dp);
        addLeftItem(iconItem);
        mRecyclerView = (RecyclerView) findViewById(R.id.left_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mRecyclerView.setAdapter(patientListAdapter = new PatientListAdapter(mDatas));

        TracksItemDecorator itemDecorator = new TracksItemDecorator(
                getResources().getDimensionPixelSize(R.dimen.decoration_size));
        mRecyclerView.addItemDecoration(itemDecorator);
        patientListAdapter.setOnItemLongClickListener(new PatientListAdapter.OnPatientItemLongClickListener(){
            @Override
            public void onItemLongClick(View view , String data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                //MainActivity mainActivity=(MainActivity) getActivity();
                view.setPressed(true);
                //Toast.makeText(view.getContext(),"long click "+data,Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "开始监控"+data, Snackbar.LENGTH_LONG)
                        .show();
                view.postDelayed(() -> {
                    view.setPressed(false);
                    hidePatientList();
                    showFragment(FRAGMENT_SINGLE);
                    //callback.onClick(holder.getAdapterPosition());
                }, 200);

            }
        });

        fm = getFragmentManager();
        if(savedInstanceState==null)
            showFragment(FRAGMENT_MUTI);
    }
    public static final String POSITION = "position";
    public static final int FRAGMENT_SINGLE=0;
    public static final int FRAGMENT_MUTI=1;
    private int position;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        boolean show=this.isMenuShowed();
        if(show==true)
            outState.putInt("menushow",1);
        else
            outState.putInt("menushow",0);
        boolean patientshow=this.getPatientShow();
        if(patientshow==true)
            outState.putInt("patientshow",1);
        else
            outState.putInt("patientshow",0);
        outState.putInt(POSITION, position);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        int show=savedInstanceState.getInt("menushow");
      //  if(show==1)
      //      findViewById(R.id.btn_menu).performClick();
        showFragment(savedInstanceState.getInt(POSITION));
        super.onRestoreInstanceState(savedInstanceState);
      //  int patientShow=savedInstanceState.getInt("patientshow");
      //  if(patientShow==1)
       //     showPatientList();
        // else
        //     findViewById(R.id.btn_menu).performClick();
    }

    public void showPatientList()
    {
        // show RIGHT menu
        //   DisplayMetrics  dm = new DisplayMetrics();
        //   getWindowManager().getDefaultDisplay().getMetrics(dm);
        //    int screenWidth = dm.widthPixels;
        if(isMenuShowed())
        {
            ViewHelper.setX(containerRight, -containerRight.getWidth()+ Utils.dpToPx(48, getResources()));
            float origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x", origin+ containerRight.getWidth())
                    .setDuration(ANIMATIONDURATION).start();
            Log.e("dd",String.valueOf(origin));
            setPatientShow(true);
        }
        else
        {
            ViewHelper.setX(containerRight, -containerRight.getWidth());
            float origin = ViewHelper.getX(containerRight);
            ObjectAnimator.ofFloat(containerRight, "x", origin+ containerRight.getWidth())
                    .setDuration(ANIMATIONDURATION).start();
            Log.e("dd",String.valueOf(origin));
            setPatientShow(true);
        }

    }
    public void hidePatientList()
    {
        float origin = ViewHelper.getX(containerRight);
        ObjectAnimator.ofFloat(containerRight, "x", -containerRight.getWidth())
                .setDuration(ANIMATIONDURATION).start();
        setPatientShow(false);
        new Handler().post(new Runnable() {
            public void run() {
                removeTopItem(STARTMONITOR);
                removeTopItem(MONITORALL);
            }
        });
    }
    public void hideFragment(FragmentTransaction ft){
        //如果不为空，就先隐藏起来
        if (singleMonitorFragment!=null){
            ft.hide(singleMonitorFragment);
        }
        if(mutiMonitorFragment!=null) {
            ft.hide(mutiMonitorFragment);
        }
    }
    public void showFragment(int index){

        ft=fm.beginTransaction();
        hideFragment(ft);

        //注意这里设置位置
        position = index;

        switch (index){

            case FRAGMENT_SINGLE:
                /**
                 * 如果Fragment为空，就新建一个实例
                 * 如果不为空，就将它从栈中显示出来
                 */
                if (singleMonitorFragment==null){
                    singleMonitorFragment=new SingleMonitorFragment();
                    Log.e("TestA", "view == 2");
                    ft.add(R.id.contentView,singleMonitorFragment);
                }else {
                    ft.show(singleMonitorFragment);
                }
                //MenuItem textItem2=getItemById(STARTMONITOR);
                //textItem2.hide();
                break;
            case FRAGMENT_MUTI:
                if (mutiMonitorFragment==null){
                    mutiMonitorFragment=new MutiMonitorFragment();
                    ft.add(R.id.contentView,mutiMonitorFragment);
                    Log.e("TestA", "view == 3");
                }else {
                    ft.show(mutiMonitorFragment);
                }
                if(hasMenuItem(SAVEEdit))
                    removeTopItem(SAVEEdit);
                //MenuItem textItem=getItemById(STARTMONITOR);
                //textItem.show();
                break;
        }

        ft.commit();
    }
    @Override
    public void onAttachFragment(Fragment fragment){

        //当前的界面的保存状态，只是从新让新的Fragment指向了原本未被销毁的fragment，它就是onAttach方法对应的Fragment对象
        if(singleMonitorFragment == null && fragment instanceof SingleMonitorFragment){
            singleMonitorFragment = (SingleMonitorFragment)fragment;
        }else if(mutiMonitorFragment == null && fragment instanceof MutiMonitorFragment){
            mutiMonitorFragment = (MutiMonitorFragment) fragment;
        }
    }
    @Override
    public void onItemClick(int id) {
        switch(id){
            case MONITORALL:
               //patientListAdapter.getmDatas();
                for(Patient patient :patientListAdapter.mDatas)
                {
                    patient.setIdcard("已选择");
                }
                patientListAdapter.notifyDataSetChanged();

                break;
            case SINGLEMONITOR:
                showFragment(FRAGMENT_SINGLE);
                break;
            case SHOWALL:
                if(getPatientShow())
                {
                    hidePatientList();

                   // removeTopItem(STARTMONITOR);
                }
                else
                {
                    showPatientList();
                    TextItem textItem = new TextItem(this, MONITORALL, "全部选择", Color.parseColor("#1E88E5"));
                    addTopItem(textItem);
                    textItem.show();
                    textItem = new TextItem(this, STARTMONITOR, "开始监控", Color.parseColor("#1E88E5"));
                    addTopItem(textItem);
                    textItem.show();
                }

                break;
            case MUTIMOITOR:
                showFragment(FRAGMENT_MUTI);

                break;
            case STARTMONITOR:
                hidePatientList();
                //new Handler().post(new Runnable() {
                //    public void run() {
                 //       removeTopItem(STARTMONITOR);
                //       removeTopItem(MONITORALL);
                //    }
               // });
                showFragment(FRAGMENT_MUTI);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    /*class HomeAdapter extends RecyclerView.Adapter<MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_view, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            MyViewHolder.bind(holder, mDatas.get(position));
            holder.itemView.setOnClickListener(view -> {
                view.setPressed(true);
                view.postDelayed(() -> {
                    view.setPressed(false);
                    //callback.onClick(holder.getAdapterPosition());
                }, 200);
            });
        }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }


    }*/
}
/*class MyViewHolder extends RecyclerView.ViewHolder
{

    TextView name;
    TextView rfid;
    TextView idcard;
    TextView gender;

    public MyViewHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        rfid = (TextView) view.findViewById(R.id.rfid);
        idcard = (TextView) view.findViewById(R.id.idcard);
        gender=(TextView) view.findViewById(R.id.gender);
    }

    public static void bind(MyViewHolder viewHolder, Patient patient) {
        viewHolder.name.setText(patient.getName());
        viewHolder.rfid.setText(patient.getRfid());
        viewHolder.idcard.setText(patient.getIdcard());
        viewHolder.gender.setText(patient.getGender());
    }
}*/
