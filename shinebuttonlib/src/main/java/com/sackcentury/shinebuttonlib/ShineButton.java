package com.sackcentury.shinebuttonlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private boolean isChecked = false;

    private int btn_color;
    private int btn_fill_color;

    Activity activity;
    ShineView shineView;
    ValueAnimator shakeAnimator;
    ShineView.ShineParams shineParams;

    OnCheckedChangeListener listener;

    private int bottomHeight;

    public ShineButton(Context context) {
        super(context);
    }

    public ShineButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initButton(context, attrs);
    }


    public ShineButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(context, attrs);
    }

    private void initButton(Context context, AttributeSet attrs) {
        if(context instanceof Activity){
            init((Activity) context);
        }
        shineParams = new ShineView.ShineParams();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton);
        btn_color = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY);
        btn_fill_color = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK);
        shineParams.allowRandomColor = a.getBoolean(R.styleable.ShineButton_allow_random_color, false);
        shineParams.animDuration = a.getInteger(R.styleable.ShineButton_shine_animation_duration, (int) shineParams.animDuration);
        shineParams.bigShineColor = a.getColor(R.styleable.ShineButton_big_shine_color, shineParams.bigShineColor);
        shineParams.clickAnimDuration = a.getInteger(R.styleable.ShineButton_click_animation_duration, (int) shineParams.clickAnimDuration);
        shineParams.enableFlashing = a.getBoolean(R.styleable.ShineButton_enable_flashing, false);
        shineParams.shineCount = a.getInteger(R.styleable.ShineButton_shine_count, shineParams.shineCount);
        shineParams.shineDistanceMultiple = a.getFloat(R.styleable.ShineButton_shine_distance_multiple, shineParams.shineDistanceMultiple);
        shineParams.shineTurnAngle = a.getFloat(R.styleable.ShineButton_shine_turn_angle, shineParams.shineTurnAngle);
        shineParams.smallShineColor = a.getColor(R.styleable.ShineButton_small_shine_color, shineParams.smallShineColor);
        shineParams.smallShineOffsetAngle = a.getFloat(R.styleable.ShineButton_small_shine_offset_angle, shineParams.smallShineOffsetAngle);

        a.recycle();
        setSrcColor(btn_color);
    }

    public int getBottomHeight() {
        return bottomHeight;
    }

    public int getColor() {
        return btn_fill_color;
    }

    public boolean isChecked() {
        return isChecked;
    }


    public void setBtn_color(int btn_color) {
        this.btn_color = btn_color;
    }

    public void setBtn_fill_color(int btn_fill_color) {
        this.btn_fill_color = btn_fill_color;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (checked) {
            setSrcColor(btn_fill_color);
            isChecked = true;
        } else {
            setSrcColor(btn_color);
            isChecked = false;
        }
        onListenerUpdate(checked);
    }

    private void onListenerUpdate(boolean checked) {
        if (listener != null) {
            listener.onCheckedChanged(this, checked);
        }
    }

    public void setCancel() {
        setSrcColor(btn_color);
        isChecked = false;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(new OnButtonClickListener(l));
    }

    public void setOnCheckStateChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
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
        bottomHeight = metrics.heightPixels - location[1];
    }

    public void showAnim() {
        final ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        if (shineView != null) {
            rootView.removeView(shineView);
        }
        shineView = new ShineView(activity, this, shineParams);
        rootView.addView(shineView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        doShareAnim();
    }

    private void doShareAnim() {
        shakeAnimator = ValueAnimator.ofFloat(0.4f, 1f, 0.9f, 1f);
        shakeAnimator.setInterpolator(new LinearInterpolator());
        shakeAnimator.setDuration(500);
        shakeAnimator.setStartDelay(180);
        invalidate();
        shakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setScaleX((float) valueAnimator.getAnimatedValue());
                setScaleY((float) valueAnimator.getAnimatedValue());
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
            if (!isChecked) {
                showAnim();
                isChecked = true;
            } else {
                setCancel();
            }
            onListenerUpdate(isChecked);
            listener.onClick(view);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean checked);
    }
}
