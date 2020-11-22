package com.dss.wanandroid.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.dss.wanandroid.R;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_OUTSIDE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * 首页圆形按钮菜单，画个圆
 */
public class CircleView extends View {

    private int color;
    private Paint paint;


    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取xml文件中对应的参数列表
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        //给color赋值为xml里面设置的color
        color = array.getColor(R.styleable.CircleView_color, Color.GREEN);

        //画笔
        paint = new Paint();
        //画笔设颜色
        paint.setColor(color);

        //设置阴影，半径、x偏移、y偏移、颜色
        paint.setShadowLayer(10f,3f,6f,Color.parseColor("#CCCCCC"));

        array.recycle();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //画圆，参数为圆心坐标，半径和画笔
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2*0.8f, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //设置点击态（颜色加深）
        switch (event.getAction()){
            case ACTION_DOWN:
                paint.setColor(darkerColor(color));
                invalidate();
                break;
            case ACTION_CANCEL:
            case ACTION_UP:
                paint.setColor(color);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 加深颜色
     * @param color 原来的颜色
     * @return 更深的颜色
     */
    private int darkerColor(int color){
        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);
        hsv[2] = hsv[2]*0.8f;
        return Color.HSVToColor(hsv);
    }
}
