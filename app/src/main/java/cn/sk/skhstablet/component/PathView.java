package cn.sk.skhstablet.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class PathView extends CardiographView {
    public PathView(Context context) {
        this(context,null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    //byte [] content=new byte[255];
    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();
        mPath2=new Path();
        avgThread = new Thread(runnable);//定期更新平均值的线程启动
        System.out.println("开始状态"+avgThread.getState());

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
        /*drawPath(canvas);
        drawECG(canvas,content);
        scrollBy(5,0);*/
        // postInvalidateDelayed(100);
        if(change)
        {
            drawNewECG(canvas);
            change=false;
        }

    }

    public boolean change=false;
    Queue<Short> queue=new LinkedList<Short>();
    Queue<Short> queue2=new LinkedList<Short>();
    public double size;
    boolean isfirst=true;
    float tmp = 0;
    Thread avgThread;
    List<Short> content=new ArrayList<>();
    Random random = new Random();
    int max=250;
    int min=0;
    public boolean isNewData=false;
    List<Short> newData=new ArrayList<>();
    public void setContent(List<Short> content) {
        change=true;
        for(Short i:content)
        {
            queue.offer(i);
            queue2.poll();
        }
        /*if(queue.size()>=1000)//
        {
            for(int i=0;i<25;i++)
                queue.poll();
        }*/
        postInvalidate();
    }
    public void setContent2(List<Short> content) {
        change=true;

        for(Short i:content)
        {
            queue2.offer(i);
            queue.poll();
        }

        /*if(queue2.size()>1000)//
        {
            for(int i=0;i<250;i++)
                queue2.poll();
        }*/
        postInvalidate();
    }
    public void setECG(List<Short> content) {
        if(newDataSize==8)
            newDataSize=0;
        isNewData=true;
        this.newData=content;
        newDataSize++;
        if(newDataSize==5)
        {
            for(int i=0;i<250;i++)
                queue.poll();
        }
        if(newDataSize==1)
        {
            for(int i=0;i<250;i++)
                queue2.poll();
        }
        if(stop)//如果绘制是停止状态
        {
            stop=false;
            while(true)
            {
                if(avgThread.getState()==Thread.State.NEW)//说明是第一次绘制
                {
                    avgThread.start();
                    break;
                }
                System.out.println("现状状态"+avgThread.getState());
                if(avgThread.getState()==Thread.State.TERMINATED)//说明是暂停之后重新启动
                {
                    avgThread = new Thread(runnable);
                    avgThread.start();
                    break;
                }
            }
        }
        if(avgThread.getState()==Thread.State.TIMED_WAITING)//线程正在睡眠
        {
            avgThread.interrupt();
            System.out.println("打断");
        }
    }
    boolean stop=true;
    public void stop()
    {
        stop=true;
        isNewData=false;
        queue.clear();
        queue2.clear();
        content.clear();
        newDataSize=0;
        mPath.reset();
        mPath2.reset();
        if(avgThread.getState()==Thread.State.TIMED_WAITING)//线程正在睡眠
        {
            avgThread.interrupt();
            System.out.println("打断");
        }
    }
    public boolean getStop()
    {
        return stop;
    }
    public void drawNewECG(Canvas canvas)
    {
        if(isfirst)//第一次绘制需要计算每个点绘制的宽度
        {
            mPath.reset();
            mPath.moveTo(0,mHeight);
            mPath2.reset();
            mPath2.moveTo(0,mHeight);
            isfirst=false;
            //System.out.println("首次");
            size=mWidth/1000.0;
            tmp=0;
        }

        mPath.reset();
        mPath2.reset();
        if(newDataSize>4)
        {
            mPath2.moveTo(0,mHeight);
            tmp=0;
            for(Short i:queue2)
            {
                mPath2.lineTo(tmp, mHeight-3*i);
                tmp+=size;
            }
            if(queue.size()!=0)
            {
                tmp+=250*size;//旧数据与新数据相隔250个点
                mPath.moveTo(tmp,mHeight-3*queue.peek());
                for(Short i:queue)
                {
                    mPath.lineTo(tmp, mHeight-3*i);
                    tmp+=size;
                }
            }

        }
        else
        {
            mPath.moveTo(0,mHeight);
            tmp=0;
            for(Short i:queue)
            {
                mPath.lineTo(tmp, mHeight-3*i);
                tmp+=size;
            }
            if(queue2.size()!=0)
            {
                tmp+=250*size;//旧数据与新数据相隔250个点
                mPath2.moveTo(tmp,mHeight-3*queue2.peek());
                for(Short i:queue2)
                {
                    mPath2.lineTo(tmp, mHeight-3*i);
                    tmp+=size;
                }
            }
        }
        System.out.println("绘制"+queue.size());
        //System.out.println(queue.size());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawPath(mPath,mPaint);
        canvas.drawPath(mPath2,mPaint);
    }
    public int newDataSize=0;
    int num=0;
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            System.out.println("线程已经启动");
            while (!stop) {
                try {
                    Thread.sleep(100);
                    //System.out.println("添加2");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    /*avgThread = new Thread(runnable);
                    avgThread.start();*/
                    if(stop)
                        break;
                }
                //重要：在数据集中添加新的点集
                content.clear();//每100ms绘制25个点
                if(isNewData)
                {
                    if(newData!=null)
                    {
                        for(int i=0;i<25;i++)
                        {
                            content.add(newData.get(num*25+i));
                        }
                        num++;
                        if(newDataSize>4)//大于四个点，添加点到第二条曲线
                        {
                            setContent2(content);
                        }
                        else
                        {
                            setContent(content);
                        }
                        //System.out.println("p");
                        //System.out.println("添加");
                    }
                    /*else
                    {
                        if(newDataSize>4)
                        {
                            change=true;
                            for(int i=0;i<25;i++)
                                queue.poll();
                            postInvalidate();
                        }
                    }*/
                }
                if(num==10)//新数据添加并绘制完毕
                {
                    num=0;
                    newData=null;
                }


                //System.out.println("运行状态"+avgThread.getState());
            }
            postInvalidate();
            System.out.println("线程已经退出");
            System.out.println("退出状态"+avgThread.getState());
        }
    };


}
