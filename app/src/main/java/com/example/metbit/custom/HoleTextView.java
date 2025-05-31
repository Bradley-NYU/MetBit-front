package com.example.metbit.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

public class HoleTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Paint backgroundPaint;
    private Paint strokePaint;

    private boolean isNight;

    private int[] dayColors = {
            Color.parseColor("#FF589e8e"),  // 顶部青绿
            Color.parseColor("#AAFFFFFF"),  // 中间过渡
            Color.parseColor("#EEFFFFFF")   // 底部：93%透明度白
    };

    private int[] nightColors = {
            Color.parseColor("#FF000000"),  // 顶部黑色
            Color.parseColor("#AAFFFFFF"),  // 中间过渡
            Color.parseColor("#EEFFFFFF")   // 底部：93%透明度hei
    };
    private int h;


    public HoleTextView(Context context) {
        super(context);
        init();
    }

    public HoleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_HARDWARE, null); // 开启硬件加速
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);

        // 金色描边
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.parseColor("#c3b049")); // 你的要求 金色
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2f); // 边缘宽度，可以根据需要调整
        strokePaint.setTextSize(24f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("onsize change", "onDraw: ");
        // 在这里正确初始化渐变背景
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("change", "onDraw: " + isNight);
        float[] positions = {0f, 0.7f, 1f}; // 白色在底部30%区域加强

        int[] colors;
        if (isNight) {
            colors = nightColors;
        } else {
            colors = dayColors;
        }
        backgroundPaint.setShader(new LinearGradient(
                0, 0, 0, h,
                colors, positions,
                Shader.TileMode.CLAMP
        ));

        // 1. 先绘制渐变背景
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        // 2. 再镂空文字

        Paint paint = getPaint();
        String text = getText().toString();

        float x = (getWidth() - paint.measureText(text)) / 2f;
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float y = (getHeight() - fontMetrics.bottom - fontMetrics.top) * 0.4f;

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawText(text, x, y, paint);
        paint.setXfermode(null);

        strokePaint.setTypeface(paint.getTypeface());
        strokePaint.setTextSize(paint.getTextSize());
        canvas.drawText(text, x, y, strokePaint);
    }

    public boolean isNight() {
        return isNight;
    }

    public void setNight(boolean night) {
        isNight = night;
    }
}