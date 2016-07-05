package com.sackcentury.shinebuttonlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;

/**
 * @author Chad
 * @title com.sackcentury.shinebuttonlib
 * @description
 * @modifier
 * @date
 * @since 16/7/5 下午2:27
 **/
public class ShineButton extends PorterShapeImageView {
    private int rawResourceId;

    public int getColor() {
        return btn_fill_color;
    }

    private int btn_color;
    private int btn_fill_color;
    private int btn_assist_color;
    Activity activity;
    private Paint paint;
    ShineView shineView;
    ValueAnimator shakeAnimator;
    public int getBootomHeight() {
        return bootomHeight;
    }

    private int bootomHeight;
    private int bottomWidth;
    boolean animationLock = false;

    public ShineButton(Context context) {
        super(context);
    }

    public ShineButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton);
        rawResourceId = a.getResourceId(R.styleable.ShineButton_raw_resource, 0);
        btn_color = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY);
        btn_fill_color = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK);
        btn_assist_color = a.getColor(R.styleable.ShineButton_btn_assist_color, Color.RED);
        a.recycle();
        setSrcColor(btn_color);
        paint = new Paint();
    }

    public ShineButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton);
        rawResourceId = a.getResourceId(R.styleable.ShineButton_raw_resource, 0);
        btn_color = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY);
        btn_fill_color = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK);
        btn_assist_color = a.getColor(R.styleable.ShineButton_btn_assist_color, Color.RED);
        a.recycle();
        setSrcColor(btn_color);
        paint = new Paint();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(new OnButtonClickListener(l));
    }


    public void init(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int[] location = new int[2];
        getLocationInWindow(location);
        bootomHeight = metrics.heightPixels - location[1];
    }

    public void showAnim() {
        final ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        if(shineView!=null){
            rootView.removeView(shineView);
        }
        shineView = new ShineView(activity, this);
        rootView.addView(shineView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        doShareAnim();
    }

    private void doShareAnim() {
        shakeAnimator = ValueAnimator.ofFloat(0.4f,1f,0.9f,1f);
        shakeAnimator.setInterpolator(new LinearInterpolator());
        shakeAnimator.setDuration(500);
        shakeAnimator.setStartDelay(180);
        invalidate();
        shakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleX((float)valueAnimator.getAnimatedValue());
                setScaleY((float)valueAnimator.getAnimatedValue());
            }
        });
        shakeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setSrcColor(btn_fill_color);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        shakeAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public class OnButtonClickListener implements OnClickListener {
        OnClickListener listener;

        public OnButtonClickListener(OnClickListener l) {
            listener = l;
        }

        @Override
        public void onClick(View view) {
            showAnim();
            listener.onClick(view);
        }
    }
}
