package cn.sk.skhstablet.component;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

//自定义列表项之间的距离
public class TracksItemDecorator extends RecyclerView.ItemDecoration {

    private int size;

    public TracksItemDecorator(int size) {
        this.size = size;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = size;
        outRect.top = 0;
        outRect.left = 0;
        outRect.right = 0;
    }
}
