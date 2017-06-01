package cn.sk.skhstablet.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

public class PathView extends CardiographView {
    public PathView(Context context) {
        this(context,null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    byte [] content=new byte[255];
    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();

        for(int i=0;i<content.length;i++)
        {
            content[i]=0x10;
        }
    }


    private void drawPath(Canvas canvas) {
        // 重置path
        mPath.reset();

        //用path模拟一个心电图样式
        mPath.moveTo(0,mHeight/2);
        int tmp = 0;
        for(int i = 0;i<10;i++) {
            mPath.lineTo(tmp+20, 100);
            mPath.lineTo(tmp+70, mHeight / 2 + 50);
            mPath.lineTo(tmp+80, mHeight / 2);

            mPath.lineTo(tmp+200, mHeight / 2);
            tmp = tmp+200;
        }
        //设置画笔style
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawPath(mPath,mPaint);

    }

    public void setContent(byte[] content) {
        this.content = content;
        invalidate();
    }

    public void drawECG(Canvas canvas, byte[] content)
    {
        mPath.moveTo(0,mHeight/2);
        int tmp = 0;
        for(int i = 0;i<content.length;i++) {
            mPath.lineTo(tmp+i, mHeight / 2+content[i]);
            tmp+=2;
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawPath(mPath,mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPath(canvas);
        drawECG(canvas,content);
        scrollBy(5,0);
       // postInvalidateDelayed(100);
    }

}
