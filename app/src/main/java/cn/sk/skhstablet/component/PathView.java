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

        /*for(int i=0;i<content.length;i++)
        {
            content[i]=0x10;
        }*/
        avgThread = new Thread(runnable);//定期更新平均值的线程启动
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

    /*public void setContent(byte[] content) {
        this.content = content;
        invalidate();
    }*/

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
        }
        if(queue.size()>1250)//
        {
            for(int i=0;i<25;i++)
                queue.poll();
        }
        postInvalidate();
    }

    public void setECG(List<Short> content) {
        isNewData=true;
        this.newData=content;
        newDataSize++;
        if(stop)
        {
            stop=false;
            avgThread.start();
        }
        System.out.println("这里");
    }
    boolean stop=true;
    public void stop()
    {
        stop=true;
    }
    public void drawNewECG(Canvas canvas)
    {
        if(isfirst)
        {
            mPath.reset();
            mPath.moveTo(0,mHeight);
            isfirst=false;
            //System.out.println("首次");
            size=mWidth/1000.0;
            tmp=0;
        }
        mPath.reset();
        mPath.moveTo(0,mHeight);
        tmp=0;
        int number=0;
        for(Short i:queue)
        {
            mPath.lineTo(tmp, mHeight-3*i);
            tmp+=size;
            number++;
            if(number==1000)
                break;
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(2);
        canvas.drawPath(mPath,mPaint);
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
                    Thread.sleep(100);//没隔固定时间求平均数
                    content.clear();
                    //System.out.println("添加2");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    avgThread = new Thread(runnable);
                    avgThread.start();
                }
                //重要：在数据集中添加新的点集
                //mDataset.addSeries(series);
                //System.out.println("添加1");
                if(isNewData)
                {
                    if(newData!=null)
                    {
                        for(int i=0;i<25;i++)
                        {
                            //int value=(int) random.nextInt(max)%(max-min+1) + min;
                            short value=1;
                            content.add(newData.get(num*25+i));
                        }
                        num++;
                        setContent(content);

                        //System.out.println("p");
                        //System.out.println("添加");
                    }
                    else
                    {
                        /*try {
                            Thread.sleep(10);//没隔固定时间求平均数
                            content.clear();
                            //System.out.println("添加2");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            avgThread = new Thread(runnable);
                            avgThread.start();
                        }*/
                        if(newDataSize>4)
                        {
                            change=true;
                            for(int i=0;i<25;i++)
                                queue.poll();
                            postInvalidate();
                        }
                    }
                    /*else
                    {
                        for(int i=0;i<25;i++)
                        {
                            //int value=(int) random.nextInt(max)%(max-min+1) + min;
                            short value=0;
                            content.add(value);
                        }
                    }*/
                }
                if(num==10)
                {
                    num=0;
                    newData=null;
                }

                //视图更新，没有这一步，曲线不会呈现动态
                //如果在非UI主线程中，需要调用postInvalidate()，具体参考api
            }
        }
    };


}
