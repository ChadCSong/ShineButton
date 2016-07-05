package com.sackcentury.shinebuttonlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

import java.util.Random;

/**
 * @author Chad
 * @title com.sackcentury.shinebuttonlib
 * @description
 * @modifier
 * @date
 * @since 16/7/5 下午3:57
 **/
public class ShineView extends View {
    private static final String TAG = "ShineView";
    ShineAnimator shineAnimator;
    ValueAnimator clickAnimator;
    ShineButton shineButton;
    private Paint paint;
    private Paint paint2;
    private Paint paintSmall;

    int colorCount = 10;
    int colorRandom[] = new int[colorCount];

    int shineCount = 7;

    float smallOffsetAngle = 20;
    float turnAngle = 20;
    RectF rectF = new RectF();
    RectF rectFsmall = new RectF();

    Random random = new Random();
    int centerAnimX;
    int centerAnimY;
    int btnWidth;
    int btnHeight;
    double thirdLength;
    float value;
    float clickValue = 0;
    boolean isRun = false;

    public ShineView(Context context) {
        super(context);
    }

    public ShineView(Context context, ShineButton shineButton) {
        super(context);
        this.shineAnimator = new ShineAnimator();
        this.shineButton = shineButton;

        colorRandom[0] = Color.parseColor("#E91E63");
        colorRandom[1] = Color.parseColor("#03A9F4");
        colorRandom[2] = Color.parseColor("#009688");
        colorRandom[3] = Color.parseColor("#4CAF50");
        colorRandom[4] = Color.parseColor("#795548");
        colorRandom[5] = Color.parseColor("#F44336");
        colorRandom[6] = Color.parseColor("#E91E63");
        colorRandom[7] = Color.parseColor("#03A9F4");
        colorRandom[8] = Color.parseColor("#009688");
        colorRandom[9] = Color.parseColor("#4CAF50");

        paint = new Paint();
        paint.setColor(shineButton.getColor());
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setStrokeWidth(20);
        paint2.setStrokeCap(Paint.Cap.ROUND);

        paintSmall = new Paint();
        paintSmall.setColor(Color.RED);
        paintSmall.setStrokeWidth(10);
        paintSmall.setStyle(Paint.Style.STROKE);
        paintSmall.setStrokeCap(Paint.Cap.ROUND);

        clickAnimator = ValueAnimator.ofFloat(0f, 1.1f);
        clickAnimator.setDuration(200);
        clickAnimator.setInterpolator(new EasingInterpolator(Ease.QUART_OUT));
        clickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                clickValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        clickAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                clickValue = 0;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

//        paint.set
    }

    public ShineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void showAnimation(ShineButton shineButton) {
        btnWidth = shineButton.getWidth();
        btnHeight = shineButton.getHeight();
        thirdLength = getThirdLength(btnHeight, btnWidth);
        int[] location = new int[2];
        shineButton.getLocationInWindow(location);
        centerAnimX = location[0] + btnWidth / 2;
        centerAnimY = getMeasuredHeight() - shineButton.getBootomHeight() + btnHeight / 2;
        shineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                value = (float) valueAnimator.getAnimatedValue();
                paint.setStrokeWidth((btnWidth / 2) * (1.5f - value));
                paintSmall.setStrokeWidth((btnWidth / 3) * (1.5f - value));
                rectF.set(centerAnimX - (btnWidth / 1.5f * value), centerAnimY - (btnHeight / 1.5f * value), centerAnimX + (btnWidth / 1.5f * value), centerAnimY + (btnHeight / 1.5f * value));
                rectFsmall.set(centerAnimX - (btnWidth / 1.7f * value), centerAnimY - (btnHeight / 1.7f * value), centerAnimX + (btnWidth / 1.7f * value), centerAnimY + (btnHeight / 1.7f * value));
                invalidate();
            }
        });
        shineAnimator.startAnim(this, centerAnimX, centerAnimY);
        clickAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < shineCount; i++) {
//            paint.setColor(colorRandom[Math.abs(colorCount / 2 - i) >= colorCount ? colorCount - 1 : Math.abs(colorCount / 2 - i)]);
            canvas.drawArc(rectF, 360f / shineCount * i + 1 + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paint));
        }

        for (int i = 0; i < shineCount; i++) {
//            paint.setColor(colorRandom[Math.abs(colorCount / 2 - i) >= colorCount ? colorCount - 1 : Math.abs(colorCount / 2 - i)]);
            canvas.drawArc(rectFsmall, 360f / shineCount * i + 1 - smallOffsetAngle + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paintSmall));
        }
//        canvas.drawPoint(centerAnimX + (btnWidth / 2 * value), centerAnimY + (btnHeight / 2 * value), paint);
//        canvas.drawPoint(centerAnimX - (btnWidth / 2 * value), centerAnimY - (btnHeight / 2 * value), paint);
//        canvas.drawPoint(centerAnimX + (btnWidth / 2 * value), centerAnimY - (btnHeight / 2 * value), paint);
//        canvas.drawPoint(centerAnimX - (btnWidth / 2 * value), centerAnimY + (btnHeight / 2 * value), paint);
//
//        canvas.drawPoint(centerAnimX, centerAnimY + (float) (thirdLength / 2 * value), paint);
//        canvas.drawPoint(centerAnimX, centerAnimY - (float) (thirdLength / 2 * value), paint);
//        canvas.drawPoint(centerAnimX + (float) (thirdLength / 2 * value), centerAnimY, paint);
//        canvas.drawPoint(centerAnimX - (float) (thirdLength / 2 * value), centerAnimY, paint);
//
//        //右下
//        canvas.drawPoint(centerAnimX + (btnWidth / 2 * value), centerAnimY + (btnHeight / 2 * value)-30, paintSmall);
//        canvas.drawPoint(centerAnimX - (btnWidth / 2 * value), centerAnimY - (btnHeight / 2 * value)+30, paintSmall);
//        canvas.drawPoint(centerAnimX + (btnWidth / 2 * value)-30, centerAnimY - (btnHeight / 2 * value), paintSmall);
//        canvas.drawPoint(centerAnimX - (btnWidth / 2 * value)+20, centerAnimY + (btnHeight / 2 * value), paintSmall);
//
//        canvas.drawPoint(centerAnimX +30, centerAnimY + (float) (thirdLength / 2 * value)-20, paintSmall);
//        canvas.drawPoint(centerAnimX -30, centerAnimY - (float) (thirdLength / 2 * value)+20, paintSmall);
//        canvas.drawPoint(centerAnimX + (float) (thirdLength / 2 * value)-20, centerAnimY - 30, paintSmall);
//        canvas.drawPoint(centerAnimX - (float) (thirdLength / 2 * value)+20, centerAnimY +30, paintSmall);

        paint.setStrokeWidth(btnWidth * (clickValue) * 1.2f);
        if (clickValue != 0) {
            paint2.setStrokeWidth(btnWidth * (clickValue) * 1.2f - 8);
        } else {
            paint2.setStrokeWidth(0);
        }
        canvas.drawPoint(centerAnimX, centerAnimY, paint);
        canvas.drawPoint(centerAnimX, centerAnimY, paint2);
        if (shineAnimator != null && !isRun) {
            isRun = true;
            showAnimation(shineButton);
        }
    }

    private Paint getConfigPaint(Paint paint) {
//        paint.setColor(colorRandom[0]);
        return paint;
    }

    private double getThirdLength(int btnHeight, int btnWidth) {
        int all = btnHeight * btnHeight + btnWidth * btnWidth;
        return Math.sqrt(all);
    }
}
