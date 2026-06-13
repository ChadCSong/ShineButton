package com.sackcentury.shinebuttonlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;

import com.sackcentury.shinebuttonlib.listener.SimpleAnimatorListener;

/**
 * ShineButton is a customizable animated button with a "shine" effect.
 * It inherits from {@link PorterShapeImageView} to support custom shapes via PNG masks.
 *
 * @author Chad
 * @since 16/7/5
 **/
public class ShineButton extends PorterShapeImageView {
    private static final String TAG = "ShineButton";

    /**
     * Whether the button is currently in the "checked" (filled) state.
     */
    private boolean isChecked = false;

    /**
     * The color of the button in its normal (unchecked) state.
     */
    private int btnColor;

    /**
     * The color of the button when it is checked.
     */
    private int btnFillColor;

    private static final int DEFAULT_WIDTH = 50;
    private static final int DEFAULT_HEIGHT = 50;

    private DisplayMetrics metrics = new DisplayMetrics();

    /**
     * The host activity, used to add the {@link ShineView} to the root layout.
     */
    private Activity activity;

    /**
     * The view responsible for rendering the shine particles.
     */
    private ShineView shineView;

    /**
     * Animator for the button's scale/shake effect when clicked.
     */
    private ValueAnimator shakeAnimator;

    /**
     * Parameters controlling the shine animation's appearance.
     */
    private ShineView.ShineParams shineParams = new ShineView.ShineParams();

    /**
     * Listener for state changes.
     */
    private OnCheckedChangeListener listener;

    private int bottomHeight;
    private int realBottomHeight;

    /**
     * Reference to a parent dialog, if any, to fix positioning issues.
     */
    Dialog mFixDialog;

    public ShineButton(Context context) {
        super(context);
        if (context instanceof Activity) {
            init((Activity) context);
        }
    }

    public ShineButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initButton(context, attrs);
    }

    public ShineButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton(context, attrs);
    }

    /**
     * Initializes the button attributes from XML.
     */
    private void initButton(Context context, AttributeSet attrs) {
        if (context instanceof Activity) {
            init((Activity) context);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton);
        btnColor = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY);
        btnFillColor = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK);

        // Map XML attributes to ShineParams
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
        shineParams.shineSize = a.getDimensionPixelSize(R.styleable.ShineButton_shine_size, shineParams.shineSize);
        a.recycle();

        setSrcColor(btnColor);
    }

    /**
     * Sets a reference to the host dialog to ensure the shine animation is placed correctly.
     *
     * @param fixDialog The parent dialog.
     */
    public void setFixDialog(Dialog fixDialog) {
        mFixDialog = fixDialog;
    }

    public int getBottomHeight(boolean real) {
        return real ? realBottomHeight : bottomHeight;
    }

    /**
     * Returns the fill color used in the checked state.
     */
    public int getColor() {
        return btnFillColor;
    }

    /**
     * Whether the button is currently checked.
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Sets the base color of the button.
     */
    public void setBtnColor(int btnColor) {
        this.btnColor = btnColor;
        setSrcColor(this.btnColor);
    }

    /**
     * Sets the fill color used when the button is checked.
     */
    public void setBtnFillColor(int btnFillColor) {
        this.btnFillColor = btnFillColor;
    }

    /**
     * Updates the checked state with an optional animation.
     */
    public void setChecked(boolean checked, boolean anim) {
        setChecked(checked, anim, true);
    }

    /**
     * Internal setChecked implementation.
     *
     * @param checked  The new state.
     * @param anim     Whether to show the shine/shake animation.
     * @param callBack Whether to notify the listener.
     */
    private void setChecked(boolean checked, boolean anim, boolean callBack) {
        isChecked = checked;
        if (checked) {
            setSrcColor(btnFillColor);
            if (anim) showAnim();
        } else {
            setSrcColor(btnColor);
            if (anim) setCancel();
        }
        if (callBack) {
            onListenerUpdate(checked);
        }
    }

    /**
     * Sets the checked state without animation or callback.
     */
    public void setChecked(boolean checked) {
        setChecked(checked, false, false);
    }

    private void onListenerUpdate(boolean checked) {
        if (listener != null) {
            listener.onCheckedChanged(this, checked);
        }
    }

    /**
     * Cancels the animation and reverts to the unchecked state.
     */
    public void setCancel() {
        setSrcColor(btnColor);
        if (shakeAnimator != null) {
            shakeAnimator.end();
            shakeAnimator.cancel();
        }
    }

    // --- Configuration setters for ShineParams ---

    public void setAllowRandomColor(boolean allowRandomColor) {
        shineParams.allowRandomColor = allowRandomColor;
    }

    public void setAnimDuration(int durationMs) {
        shineParams.animDuration = durationMs;
    }

    public void setBigShineColor(int color) {
        shineParams.bigShineColor = color;
    }

    public void setClickAnimDuration(int durationMs) {
        shineParams.clickAnimDuration = durationMs;
    }

    public void enableFlashing(boolean enable) {
        shineParams.enableFlashing = enable;
    }

    public void setShineCount(int count) {
        shineParams.shineCount = count;
    }

    public void setShineDistanceMultiple(float multiple) {
        shineParams.shineDistanceMultiple = multiple;
    }

    public void setShineTurnAngle(float angle) {
        shineParams.shineTurnAngle = angle;
    }

    public void setSmallShineColor(int color) {
        shineParams.smallShineColor = color;
    }

    public void setSmallShineOffAngle(float angle) {
        shineParams.smallShineOffsetAngle = angle;
    }

    public void setShineSize(int size) {
        shineParams.shineSize = size;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l instanceof OnButtonClickListener) {
            super.setOnClickListener(l);
        } else {
            if (onButtonClickListener != null) {
                onButtonClickListener.setListener(l);
            }
        }
    }

    public void setOnCheckStateChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    OnButtonClickListener onButtonClickListener;

    /**
     * Initializes the button. Must be called after the button is created programmatically or from XML.
     *
     * @param activity The host activity.
     */
    public void init(Activity activity) {
        this.activity = activity;
        onButtonClickListener = new OnButtonClickListener();
        setOnClickListener(onButtonClickListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calPixels();
    }

    /**
     * Starts the shine and shake animations.
     */
    public void showAnim() {
        if (activity == null && getContext() instanceof Activity) {
            activity = (Activity) getContext();
        }
        if (activity != null) {
            shineView = new ShineView(activity, this, shineParams);
            ViewGroup rootView;
            // Determine where to add the ShineView (Dialog root or Activity root)
            if (mFixDialog != null && mFixDialog.getWindow() != null) {
                rootView = (ViewGroup) mFixDialog.getWindow().getDecorView();
                View innerView = rootView.findViewById(android.R.id.content);
                if (innerView != null) {
                    rootView.addView(shineView, new ViewGroup.LayoutParams(innerView.getWidth(), innerView.getHeight()));
                } else {
                    rootView.addView(shineView, new ViewGroup.LayoutParams(rootView.getWidth(), rootView.getHeight()));
                }
            } else {
                rootView = (ViewGroup) activity.getWindow().getDecorView();
                rootView.addView(shineView, new ViewGroup.LayoutParams(rootView.getWidth(), rootView.getHeight()));
            }
            doShareAnim();
        } else {
            Log.e(TAG, "ShineButton must be initialized with an Activity context to show animation.");
        }
    }

    /**
     * Removes a view (typically ShineView) from the activity's content view.
     */
    public void removeView(View view) {
        if (activity != null) {
            final ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            rootView.removeView(view);
        } else {
            Log.e(TAG, "Please init.");
        }
    }

    /**
     * Sets the shape of the button using a raw resource ID (PNG mask).
     */
    public void setShapeResource(int raw) {
        setShape(androidx.core.content.res.ResourcesCompat.getDrawable(getResources(), raw, null));
    }

    /**
     * Executes the button's scale/shake animation.
     */
    private void doShareAnim() {
        shakeAnimator = ValueAnimator.ofFloat(0.4f, 1f, 0.9f, 1f);
        shakeAnimator.setInterpolator(new LinearInterpolator());
        shakeAnimator.setDuration(500);
        shakeAnimator.setStartDelay(180);
        invalidate();
        shakeAnimator.addUpdateListener(valueAnimator -> {
            setScaleX((float) valueAnimator.getAnimatedValue());
            setScaleY((float) valueAnimator.getAnimatedValue());
        });
        shakeAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                setSrcColor(btnFillColor);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setSrcColor(isChecked ? btnFillColor : btnColor);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                setSrcColor(btnColor);
            }

        });
        shakeAnimator.start();
    }

    /**
     * Calculates position and visibility metrics to assist with positioning the shine animation.
     */
    private void calPixels() {
        if (activity != null && metrics != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int[] location = new int[2];
            getLocationInWindow(location);
            Rect visibleFrame = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);
            realBottomHeight = visibleFrame.height() - location[1];
            bottomHeight = metrics.heightPixels - location[1];
        }
    }

    /**
     * Internal click listener to handle the toggle logic and animations.
     */
    public class OnButtonClickListener implements OnClickListener {
        private OnClickListener listener;

        public void setListener(OnClickListener listener) {
            this.listener = listener;
        }

        public OnButtonClickListener() {}

        @Override
        public void onClick(View view) {
            if (!isChecked) {
                isChecked = true;
                showAnim();
            } else {
                isChecked = false;
                setCancel();
            }
            onListenerUpdate(isChecked);
            if (listener != null) {
                listener.onClick(view);
            }
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View view, boolean checked);
    }
}
