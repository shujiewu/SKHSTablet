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
//用于心电绘制，这个是原来250个字节用的，现在更改为500个字节，整个界面显示1000个值，也就是4秒的数据
public class PathView extends CardiographView {
    public PathView(Context context) {
        this(context,null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();  //用于绘制线条1
        mPath2=new Path();   //用于绘制线条2
        avgThread = new Thread(runnable); //开启线程用于绘制
        System.out.println("开始状态"+avgThread.getState());

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
    Queue<Short> queue=new LinkedList<Short>();  //线条1用到的数据队列
    Queue<Short> queue2=new LinkedList<Short>(); //线条2用到的数据队列
    public double size;
    boolean isfirst=true;
    float tmp = 0;
    Thread avgThread;
    List<Short> content=new ArrayList<>();
    public boolean isNewData=false;
    List<Short> newData=new ArrayList<>();
    //设置第一条线条的内容
    public void setContent(List<Short> content) {
        change=true;
        for(Short i:content)
        {
            queue.offer(i);
            queue2.poll(); //第一条增加几个点 第二条减少几个点
        }
        /*if(queue.size()>=1000)//
        {
            for(int i=0;i<25;i++)
                queue.poll();
        }*/
        postInvalidate();//
    }
    //设置第二条线条的内容
    public void setContent2(List<Short> content) {
        change=true;

        for(Short i:content)
        {
            queue2.offer(i);
            queue.poll();
        }
        postInvalidate();
    }
    //用于设置新的数据
    public void setECG(List<Short> content) {
        if(newDataSize==8)//如果超过了8个点，说明两条线都绘制完了  需要重新开始
            newDataSize=0;
        isNewData=true;
        this.newData=content;
        newDataSize++;
        if(newDataSize==5) //如果绘制第五个点，需要删除第一条线的250个点的数据
        {
            for(int i=0;i<250;i++)
                queue.poll();
        }
        if(newDataSize==1)//如果绘制第一个点，需要删除第而条线的250个点的数据
        {
            for(int i=0;i<250;i++)
                queue2.poll();
        }
        if(stop)//如果绘制时是停止状态
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
    //停止绘制，清空数据
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
        if(avgThread.getState()==Thread.State.TIMED_WAITING)//如果线程正在睡眠，快速唤醒并停止
        {
            avgThread.interrupt();
            System.out.println("打断");
        }
    }
    public boolean getStop()
    {
        return stop;
    }
    //绘制新数据
    public void drawNewECG(Canvas canvas)
    {
        if(isfirst)//第一次绘制需要计算每个点绘制的宽度
        {
            mPath.reset();
            mPath.moveTo(0,mHeight/2);
            mPath2.reset();
            mPath2.moveTo(0,mHeight/2);
            isfirst=false;
            //System.out.println("首次");
            size=mWidth/1000.0;
            tmp=0;
        }

        mPath.reset();
        mPath2.reset();
        //如果数据大于4个点，绘制第二条线
        if(newDataSize>4)
        {
            mPath2.moveTo(0,mHeight/2);//mHeight/2是设置的纵坐标基准线
            tmp=0;
            for(Short i:queue2)  //绘制所有第二条线数据队列中的数据
            {
                mPath2.lineTo(tmp, mHeight/2-i);
                tmp+=size;
            }
            if(queue.size()!=0)  //如果第一条线还有数据，则需要绘制第一条线
            {
                tmp+=250*size;//旧数据与新数据相隔250个点
                mPath.moveTo(tmp,mHeight/2-queue.peek());//第一条线的初始x坐标与第二条线相差250个点的数据
                for(Short i:queue)
                {
                    mPath.lineTo(tmp, mHeight/2-i);
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
                mPath.lineTo(tmp, mHeight/2-i);
                tmp+=size;
            }
            if(queue2.size()!=0)
            {
                tmp+=250*size;//旧数据与新数据相隔250个点
                mPath2.moveTo(tmp,mHeight/2-queue2.peek());
                for(Short i:queue2)
                {
                    mPath2.lineTo(tmp,mHeight/2-i);
                    tmp+=size;
                }
            }
        }
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
                    if(stop) //如果线程被唤醒且为停止命令，则跳出此循环
                        break;
                }
                //重要：在数据集中添加新的点集
                content.clear();//每100ms绘制25个点
                if(isNewData)  //如果是新数据
                {
                    if(newData!=null)
                    {
                        for(int i=0;i<25;i++)
                        {
                            content.add(newData.get(num*25+i));  //添加25个点
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
                }
                if(num==10)//新数据添加并绘制完毕，10次循环就可以绘制完1秒的数据
                {
                    num=0;
                    newData=null;
                }


                //System.out.println("运行状态"+avgThread.getState());
            }
            postInvalidate();//退出之后需要重新绘制一次 刷新界面为空
            System.out.println("线程已经退出");
            System.out.println("退出状态"+avgThread.getState());
        }
    };


}
