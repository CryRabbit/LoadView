package com.cryrabbit.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by CryRabbit on 2017/11/24.
 */

public class LoadView extends View{
    public LoadView(Context context) {
        super(context);
    }

    public LoadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        Log.i("ysm:",w+":"+h);
    }

    Paint paint=new Paint();
    private Path path;
    private State state=State.add;
    int mViewWidth,mViewHeight;
    int pathstart=0;
    int start=0;
    int end=20;

    int proess;

    int colors[]={Color.RED,Color.GREEN,Color.YELLOW,Color.BLUE};
    int color=0;

    private ValueAnimator valueAnimator1,valueAnimator2;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private ValueAnimator.AnimatorListener animatorListener;
    private Handler mHandler;

    public void init(){
        initPaint();
        initpath();
        initListener();
        initHandler();
        initAnimator();
        valueAnimator1.start();
    }
    public void initPaint(){
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(colors[color]);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setAntiAlias(true);
    }

    public void initpath(){
        RectF rectF=new RectF(-50,-50,50,50);
        measure=new PathMeasure();
        path=new Path();
        path.addArc(rectF,pathstart,360);
    }

    public void initListener(){
        mUpdateListener=new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                proess=(int)valueAnimator.getAnimatedValue();
                invalidate();
            }
        };
        animatorListener=new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHandler.sendEmptyMessageDelayed(0,100);
            }
        };
    }

    public void initHandler(){
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                color++;
                color=color%4;
                paint.setColor(colors[color]);
                switch (state){
                    case add:
                        state=state.less;
                        valueAnimator2.start();
                        break;
                    case less:
                        state=state.add;
                        pathstart+=280;
                        pathstart%=360;
                        start=0;
                        Log.i("ysm:",pathstart+"");
                        RectF rectF=new RectF(-50,-50,50,50);
                        path=new Path();
                        measure=new PathMeasure();
                        path.addArc(rectF,pathstart,359.9f);
                        measure.setPath(path,false);
                        float pos[] = new float[2];
                        float tan[] = new float[2];
                        measure.getPosTan(0,pos,tan);
                        Log.i("ysm:pos",pos[0]+":"+pos[1]);
                        Log.i("ysm:tan",tan[0]+":"+tan[1]);
                        valueAnimator1.start();
                        break;
                }
            }
        };
    }

    public void initAnimator(){
        valueAnimator1=ValueAnimator.ofInt(20,300).setDuration(600);
        valueAnimator2=ValueAnimator.ofInt(0,280).setDuration(600);
        valueAnimator1.addUpdateListener(mUpdateListener);
        valueAnimator2.addUpdateListener(mUpdateListener);
        valueAnimator1.addListener(animatorListener);
        valueAnimator2.addListener(animatorListener);
    }


    public enum State{
        add,
        less
    }
    private PathMeasure measure;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mViewWidth/2,mViewHeight/2);
        Path dst=new Path();
        switch (state){
            case add:
                measure.setPath(path,false);
                float startl=start*measure.getLength()/360;
                end=proess;
                float end1=end*measure.getLength()/360;
                measure.getSegment(startl,end1,dst,true);
                canvas.drawPath(dst,paint);
                break;
            case less:
                start=proess;
                measure.setPath(path,false);
                float start2=start*measure.getLength()/360;
                float end2=end*measure.getLength()/360;
                measure.getSegment(start2,end2,dst,true);
                canvas.drawPath(dst,paint);
                break;
        }
    }
}
