package cn.sk.skhstablet.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
//mport cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
//import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.app.AppConstants;
import cn.sk.skhstablet.injector.component.fragment.DaggerMutiMonitorComponent;
import cn.sk.skhstablet.injector.module.fragment.MutiMonitorModule;
import cn.sk.skhstablet.presenter.IMutiMonPresenter;
import cn.sk.skhstablet.presenter.impl.MutiMonPresenterImpl;
import cn.sk.skhstablet.rx.RxBus;
import cn.sk.skhstablet.ui.activity.MainActivity;
import cn.sk.skhstablet.R;
import cn.sk.skhstablet.adapter.MutiMonitorAdapter;
import cn.sk.skhstablet.model.PatientDetail;
import cn.sk.skhstablet.model.PatientDetailList;
import cn.sk.skhstablet.ui.base.BaseFragment;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ldkobe on 2017/4/17.
 */

public class MutiMonitorFragment extends BaseFragment<MutiMonPresenterImpl> implements IMutiMonPresenter.View {
    private RecyclerView recyclerView;

    @Inject
    MutiMonitorAdapter mutiMonitorAdapter;
    private List<PatientDetail> mDatas=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mDatas= PatientDetailList.PATIENTS;
        view=inflater.inflate(R.layout.fragment_muti_monitor,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ry_muti_monitor);
      //  recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),1));
       // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2 ,LinearLayoutManager.HORIZONTAL,false));
        /*{
            @Override
            public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
                int measuredWidth = recyclerView.getMeasuredWidth();
                int measuredHeight = recyclerView.getMeasuredHeight();
                Log.e("menseh", String.valueOf(measuredHeight));
                Log.e("mensw", String.valueOf(measuredWidth ));
                int myMeasureHeight = 0;
                int count = state.getItemCount();
                Log.e("mensw", String.valueOf(count));
           /*     for (int i = 0; i < count; i++) {
                    View view = recycler.getViewForPosition(i);
                    if (view != null) {
                        /*if (myMeasureHeight < measuredHeight && i % 2 == 0) {
                            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                    getPaddingLeft() + getPaddingRight(), p.width);
                            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                    getPaddingTop() + getPaddingBottom(), p.height);
                            view.measure(childWidthSpec, childHeightSpec);
                            myMeasureHeight += view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                        }
                        RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                                getPaddingLeft() + getPaddingRight(), p.width);
                        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                                getPaddingTop() + getPaddingBottom(), p.height);
                        view.measure(childWidthSpec, childHeightSpec);
                        myMeasureHeight += view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                        recycler.recycleView(view);
                    }
                }
                  //DisplayMetrics  dm = new DisplayMetrics();
                  //getWindowManager().getDefaultDisplay().getMetrics(dm);
                   int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
                Log.e("mese",String.valueOf(screenHeight));
//                    Log.i("Height", "" + Math.min(measuredHeight, myMeasureHeight));
                //myMeasureHeight=
                setMeasuredDimension(measuredWidth,screenHeight);
            }

        });*/
        initInject();
        recyclerView.setAdapter(mutiMonitorAdapter);

        mutiMonitorAdapter.setOnItemLongClickListener(new MutiMonitorAdapter.OnRecyclerViewItemLongClickListener(){
            @Override
            public void onItemLongClick(View view , String data){
                //Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
                MainActivity mainActivity=(MainActivity) getActivity();
                //   view.setPressed(true);
                //    view.postDelayed(() -> {
                view.setPressed(false);

                //callback.onClick(holder.getAdapterPosition());
                //    }, 200);
                mainActivity.showFragment(mainActivity.FRAGMENT_SINGLE);
            }
        });
        loadData();


        return view;
    }

    @Override
    protected void initInject() {

     DaggerMutiMonitorComponent.builder()
                .mutiMonitorModule(new MutiMonitorModule())
                .build().injectMutiMonitor(this);
        mPresenter.setView(this);
    }

    @Override
    protected void loadData() {
        if(mutiSubscription==null)
        {
            registerFetchResponse();
        }
        mPresenter.fetchPatientDetailData();
    }

    @Override
    public void refreshView(List<PatientDetail> mData) {
        mutiMonitorAdapter.patientDetailList=mDatas;
        mutiMonitorAdapter.notifyDataSetChanged();
    }
    private CompositeSubscription mutiSubscription;
    public void registerFetchResponse()
    {
        Subscription mSubscription = RxBus.getDefault().toObservable(AppConstants.MUTI_DATA,PatientDetail.class)
                .subscribe(new Action1<PatientDetail>() {
                    @Override
                    public void call(PatientDetail s) {
                        mDatas.add(s);
                        refreshView(mDatas);
                    }
                });
        if (this.mutiSubscription == null) {
            mutiSubscription = new CompositeSubscription();
        }
        mutiSubscription.add(mSubscription);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (mutiSubscription != null &&  mutiSubscription.hasSubscriptions()) {
            this. mutiSubscription.unsubscribe();
        }
    }
}
