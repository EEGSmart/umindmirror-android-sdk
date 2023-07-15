package com.eegsmart.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by Liusong on 2017/7/12.
 */

public class LineChartView extends View {


    private float yMax = 100;
    private float yMin = -100;
    private int maxPoint = 256 * 10;
    private float yscale = 1.0f;
    private float X_coord = 1.0f;
    private float viewWidth;
    private float viewHeight;

    //频繁的删除插入数据使用linkList效率会高
    private LinkedList<Float> listData = new LinkedList<>();

    // 数据线
    private Paint linePaint = new Paint();
    private int dataLineWid = 2;
    private int dataColor = Color.parseColor("#ff0000");
    // 数据阴影
    public boolean showShadow = false;
    private Paint shadowPaint = new Paint();
    // 背景线
    private int viewBgColor = Color.parseColor("#00000000");
    private Paint bgPaint = new Paint();
    private int bgLineWid = 1;
    private int bgLineColor = Color.parseColor("#b2d1f9");

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LineChartView setYMin(float yMin) {
        this.yMin = yMin;
        yscale = viewHeight / (yMax - yMin);
        return this;
    }

    public LineChartView setYMax(float yMax) {
        this.yMax = yMax;
        yscale = viewHeight / (yMax - yMin);
        return this;
    }

    public LineChartView setXMax(int maxPoint) {
        this.maxPoint = maxPoint;
        while(listData.size() > maxPoint){
            listData.removeFirst();
        }
        X_coord = viewWidth / (maxPoint - 1);
        return this;
    }

    public LineChartView setLineColor(int color){
        this.bgLineColor = color;
        bgPaint.setColor(bgLineColor);
        return this;
    }

    public LineChartView setDateColor(int color){
        this.dataColor = color;
        linePaint.setColor(dataColor);
        return this;
    }

    public synchronized void addData(float[] data) {
        for (int i = 0; i < data.length; i++) {
            listData.add(data[i]);
            if (listData.size() > maxPoint) {
                listData.removeFirst();
            }
        }
        postInvalidate();
    }

    public synchronized void clearData() {
        listData.clear();
        addData(new float[maxPoint]);
    }

    private void init(Context context, AttributeSet attrs) {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStrokeWidth(bgLineWid);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(bgLineColor);
        bgPaint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(dataLineWid);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(dataColor);

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        listData = new LinkedList<>();
        for (int i = 0; i < maxPoint; i++) {
            listData.add(0f);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = getWidth();
        viewHeight = getHeight();
        yscale = viewHeight / (yMax - yMin);
        X_coord = viewWidth / (maxPoint - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(viewBgColor);
        drawLines(canvas);
        drawWave(canvas);
    }

    private synchronized void drawWave(Canvas canvas) {
        if(listData.isEmpty())
            return;

        Path linePath = new Path();
        Path shadowPath = new Path();
        for (int i = 0; i < listData.size(); i++) {
            if(i == 0){
                shadowPath.moveTo(i * X_coord, getYL(listData.get(i)));
                linePath.moveTo(i * X_coord, getYL(listData.get(i)));
            }else {
                shadowPath.lineTo(i * X_coord, getYL(listData.get(i)));
                linePath.lineTo(i * X_coord, getYL(listData.get(i)));
            }
        }
        shadowPath.lineTo(viewWidth, viewHeight);
        shadowPath.lineTo(0, viewHeight);
        shadowPath.lineTo(0, getYL(listData.get(0)));
        shadowPath.close();

        if(showShadow){
            shadowPaint.setShader(new LinearGradient(0, 0, 0, viewHeight,
                    dataColor, Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawPath(shadowPath, shadowPaint);
        }
        canvas.drawPath(linePath, linePaint);
    }

    private void drawLines(Canvas canvas) {
        int viewYStart = 2;
        canvas.drawLine(0, viewYStart, viewWidth, viewYStart, bgPaint);
        canvas.drawLine(0, (viewHeight - viewYStart) / 4.0f, viewWidth, (viewHeight - viewYStart) / 4.0f, bgPaint);
        canvas.drawLine(0, (viewHeight - viewYStart) / 4.0f * 2, viewWidth, (viewHeight - viewYStart) / 4.0f * 2, bgPaint);
        canvas.drawLine(0, (viewHeight - viewYStart) / 4.0f * 3, viewWidth, (viewHeight - viewYStart) / 4.0f * 3, bgPaint);
        canvas.drawLine(0, viewHeight - 2, viewWidth, viewHeight, bgPaint);
    }

    private float getYL(float dig) {
        return viewHeight / 2 - (dig - (yMin + yMax) / 2) * yscale;
    }
}
