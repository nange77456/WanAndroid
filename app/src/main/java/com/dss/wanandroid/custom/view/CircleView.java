package com.dss.wanandroid.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dss.wanandroid.R;

/**
 * 首页圆形按钮菜单，画个圆
 */
public class CircleView extends View {
    private int color;


    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取xml文件中对应的参数列表
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        //给color赋值为xml里面设置的color
        color = array.getColor(R.styleable.CircleView_color, Color.GREEN);

        array.recycle();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画笔
        Paint paint = new Paint();
        //画笔设颜色
        paint.setColor(color);
        //画圆，参数为圆心坐标，半径和画笔
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2,paint);

    }
}
