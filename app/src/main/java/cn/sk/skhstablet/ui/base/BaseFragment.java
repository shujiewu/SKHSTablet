package cn.sk.skhstablet.ui.base;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import cn.sk.skhstablet.component.LoadingPage;
import cn.sk.skhstablet.presenter.BasePresenter;
import cn.sk.skhstablet.presenter.BaseView;
import cn.sk.skhstablet.tcp.LifeSubscription;
import cn.sk.skhstablet.tcp.Stateful;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by wyb on 2017/4/25.
 */

public abstract class BaseFragment <P extends BasePresenter> extends Fragment implements Stateful,LifeSubscription {
    @Inject
    protected P mPresenter;

    private boolean mIsVisible = false;     // fragment是否显示了

    private boolean isPrepared = false;

    private boolean isFirst = true; //只加载一次界面

    public LoadingPage mLoadingPage;

    protected View contentView;
    //private Unbinder bind;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if (mLoadingPage == null) {
            mLoadingPage = new LoadingPage(getContext()) {
                @Override
                protected void initView() {
                    if (isFirst) {
                        BaseFragment.this.contentView = this.contentView;
                        //bind = ButterKnife.bind(BaseFragment.this, contentView);
                        //BaseFragment.this.initView();
                        isFirst = false;
                    }
                }

                @Override
                protected void loadData() {
                    BaseFragment.this.loadData();
                }

                /*@Override
                protected int getLayoutId() {
                    return BaseFragment.this.getLayoutId();
                }
            };
        }
        isPrepared = true;
        loadBaseData();
        return mLoadingPage;*/
        //initInject();
        return container;
    }
    public void loadBaseData() {
        if (!mIsVisible || !isPrepared || !isFirst) {
            return;
        }
        loadData();
    }
    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {//fragment可见
            mIsVisible = true;
            onVisible();
        } else {//fragment不可见
            mIsVisible = false;
            onInvisible();
        }
    }
    protected void onInvisible() {
    }
    /**
     * 显示时加载数据,需要这样的使用
     * 注意声明 isPrepared，先初始化
     * 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected void onVisible() {
       /* if (isFirst) {
            initInject();
            if (mPresenter!=null){
                mPresenter.setView(this);}
        }
        loadBaseData();//根据获取的数据来调用showView()切换界面*/
    }

    /**
     * 2
     * 网络请求成功在去加载布局
     *
     * @return
     */
    //protected abstract int getLayoutId();
    /**
     * 3
     * 子类关于View的操作(如setAdapter)都必须在这里面，会因为页面状态不为成功，而binding还没创建就引用而导致空指针。
     * loadData()和initView只执行一次，如果有一些请求需要二次的不要放到loadData()里面。
     */
    //protected abstract void initView();

    public void showSnackar(View view ,String string){
        Snackbar.make(view, string, Snackbar.LENGTH_LONG)
                .show();
    }
    protected abstract void initInject();
    protected abstract void loadData();

    @Override
    public void setState(int state) {
    }



    private CompositeSubscription mCompositeSubscription;
    //用于添加rx的监听的在onDestroy中记得关闭不然会内存泄漏。
    @Override
    public void bindSubscription(Subscription subscription) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(subscription);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
