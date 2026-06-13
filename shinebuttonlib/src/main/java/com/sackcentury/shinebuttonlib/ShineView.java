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
import com.sackcentury.shinebuttonlib.listener.SimpleAnimatorListener;

import java.util.Random;

/**
 * ShineView is a transparent view that overlays the activity/dialog to draw the shine animation particles.
 * It uses two animators: one for the expanding shine particles and one for the button's "click" splash effect.
 *
 * @author Chad
 * @since 16/7/5
 **/
public class ShineView extends View {
    private static final String TAG = "ShineView";

    /**
     * Delay between frame refreshes in milliseconds. Increased to 25ms to save CPU.
     */
    private static long FRAME_REFRESH_DELAY = 25;

    private ShineAnimator shineAnimator;
    private ValueAnimator clickAnimator;

    private ShineButton shineButton;
    private Paint paint;
    private Paint paint2;
    private Paint paintSmall;

    private int colorCount = 10;
    private static int[] colorRandom = new int[10];

    // Animation configuration properties
    private int shineCount;
    private float smallOffsetAngle;
    private float turnAngle;
    private long animDuration;
    private long clickAnimDuration;
    private float shineDistanceMultiple;
    private int smallShineColor = colorRandom[0];
    private int bigShineColor = colorRandom[1];
    private int shineSize = 0;
    private boolean allowRandomColor = false;
    private boolean enableFlashing = false;

    private RectF rectF = new RectF();
    private RectF rectFSmall = new RectF();

    private Random random = new Random();
    private int centerAnimX;
    private int centerAnimY;
    private int btnWidth;
    private int btnHeight;

    private double thirdLength;
    private float value;
    private float clickValue = 0;
    private boolean isRun = false;
    private float distanceOffset = 0.2f;

    public ShineView(Context context) {
        super(context);
    }

    /**
     * Creates a ShineView and initializes the particle and splash animators.
     *
     * @param context     The context.
     * @param shineButton The target button.
     * @param shineParams Configuration for the animation.
     */
    public ShineView(Context context, final ShineButton shineButton, ShineParams shineParams) {
        super(context);
        initShineParams(shineParams, shineButton);
        this.shineAnimator = new ShineAnimator(animDuration, shineDistanceMultiple, clickAnimDuration);
        ValueAnimator.setFrameDelay(FRAME_REFRESH_DELAY);
        this.shineButton = shineButton;

        // Main shine particles paint
        paint = new Paint();
        paint.setColor(bigShineColor);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        // Click splash center paint
        paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint2.setStrokeWidth(20);
        paint2.setStrokeCap(Paint.Cap.ROUND);

        // Secondary small shine particles paint
        paintSmall = new Paint();
        paintSmall.setColor(smallShineColor);
        paintSmall.setStrokeWidth(10);
        paintSmall.setStyle(Paint.Style.STROKE);
        paintSmall.setStrokeCap(Paint.Cap.ROUND);

        // Click splash animation (expanding circle)
        clickAnimator = ValueAnimator.ofFloat(0f, 1.1f);
        ValueAnimator.setFrameDelay(FRAME_REFRESH_DELAY);
        clickAnimator.setDuration(clickAnimDuration);
        clickAnimator.setInterpolator(new EasingInterpolator(Ease.QUART_OUT));
        clickAnimator.addUpdateListener(valueAnimator -> {
            clickValue = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        clickAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                clickValue = 0;
                invalidate();
            }
        });

        // Shine particle animation callback
        shineAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // Remove the view from the root once animation is done
                shineButton.removeView(ShineView.this);
            }
        });
    }

    public ShineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Calculates positions and starts the animations.
     *
     * @param shineButton The button to animate around.
     */
    public void showAnimation(ShineButton shineButton) {
        btnWidth = shineButton.getWidth();
        btnHeight = shineButton.getHeight();
        thirdLength = getThirdLength(btnHeight, btnWidth);

        int[] location = new int[2];
        shineButton.getLocationInWindow(location);
        centerAnimX = location[0] + shineButton.getWidth() / 2;
        centerAnimY = location[1] + shineButton.getHeight() / 2;

        // If in a dialog, adjust coordinate offsets
        if (shineButton.mFixDialog != null && shineButton.mFixDialog.getWindow() != null) {
            View decor = shineButton.mFixDialog.getWindow().getDecorView();
            centerAnimX = centerAnimX - decor.getPaddingLeft();
            centerAnimY = centerAnimY - decor.getPaddingTop();
        }

        shineAnimator.addUpdateListener(valueAnimator -> {
            value = (float) valueAnimator.getAnimatedValue();
            // Dynamically adjust particle stroke width as they expand
            if (shineSize != 0 && shineSize > 0) {
                paint.setStrokeWidth((shineSize) * (shineDistanceMultiple - value));
                paintSmall.setStrokeWidth(((float) shineSize / 3 * 2) * (shineDistanceMultiple - value));
            } else {
                paint.setStrokeWidth((btnWidth / 2.0f) * (shineDistanceMultiple - value));
                paintSmall.setStrokeWidth((btnWidth / 3.0f) * (shineDistanceMultiple - value));
            }

            // Calculate expanding rects for the arcs (particles)
            float xOffset = btnWidth / (3 - shineDistanceMultiple) * value;
            float yOffset = btnHeight / (3 - shineDistanceMultiple) * value;
            rectF.set(centerAnimX - xOffset, centerAnimY - yOffset, centerAnimX + xOffset, centerAnimY + yOffset);

            float xOffsetSmall = btnWidth / ((3 - shineDistanceMultiple) + distanceOffset) * value;
            float yOffsetSmall = btnHeight / ((3 - shineDistanceMultiple) + distanceOffset) * value;
            rectFSmall.set(centerAnimX - xOffsetSmall, centerAnimY - yOffsetSmall, centerAnimX + xOffsetSmall, centerAnimY + yOffsetSmall);

            invalidate();
        });
        shineAnimator.startAnim();
        clickAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw primary shine particles
        for (int i = 0; i < shineCount; i++) {
            if (allowRandomColor) {
                paint.setColor(colorRandom[Math.abs(colorCount / 2 - i) >= colorCount ? colorCount - 1 : Math.abs(colorCount / 2 - i)]);
            }
            canvas.drawArc(rectF, 360f / shineCount * i + 1 + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paint));
        }

        // Draw secondary (small) shine particles
        for (int i = 0; i < shineCount; i++) {
            if (allowRandomColor) {
                paint.setColor(colorRandom[Math.abs(colorCount / 2 - i) >= colorCount ? colorCount - 1 : Math.abs(colorCount / 2 - i)]);
            }
            canvas.drawArc(rectFSmall, 360f / shineCount * i + 1 - smallOffsetAngle + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paintSmall));
        }

        // Draw click splash point
        paint.setStrokeWidth(btnWidth * (clickValue) * (shineDistanceMultiple - distanceOffset));
        if (clickValue != 0) {
            paint2.setStrokeWidth(btnWidth * (clickValue) * (shineDistanceMultiple - distanceOffset) - 8);
        } else {
            paint2.setStrokeWidth(0);
        }
        canvas.drawPoint(centerAnimX, centerAnimY, paint);
        canvas.drawPoint(centerAnimX, centerAnimY, paint2);

        // Trigger animation on first draw if not already running
        if (shineAnimator != null && !isRun) {
            isRun = true;
            showAnimation(shineButton);
        }
    }

    private Paint getConfigPaint(Paint paint) {
        if (enableFlashing) {
            paint.setColor(colorRandom[random.nextInt(colorCount - 1)]);
        }
        return paint;
    }

    private double getThirdLength(int btnHeight, int btnWidth) {
        return Math.sqrt(btnHeight * btnHeight + btnWidth * btnWidth);
    }

    /**
     * Container for shine animation parameters.
     */
    public static class ShineParams {
        static {
            colorRandom[0] = Color.parseColor("#FFFF99");
            colorRandom[1] = Color.parseColor("#FFCCCC");
            colorRandom[2] = Color.parseColor("#996699");
            colorRandom[3] = Color.parseColor("#FF6666");
            colorRandom[4] = Color.parseColor("#FFFF66");
            colorRandom[5] = Color.parseColor("#F44336");
            colorRandom[6] = Color.parseColor("#666666");
            colorRandom[7] = Color.parseColor("#CCCC00");
            colorRandom[8] = Color.parseColor("#666666");
            colorRandom[9] = Color.parseColor("#999933");
        }

        public boolean allowRandomColor = false;
        public long animDuration = 1500;
        public int bigShineColor = 0;
        public long clickAnimDuration = 200;
        public boolean enableFlashing = false;
        public int shineCount = 7;
        public float shineTurnAngle = 20;
        public float shineDistanceMultiple = 1.5f;
        public float smallShineOffsetAngle = 20;
        public int smallShineColor = 0;
        public int shineSize = 0;
    }

    /**
     * Initializes internal properties from the provided ShineParams.
     */
    private void initShineParams(ShineParams shineParams, ShineButton shineButton) {
        shineCount = shineParams.shineCount;
        turnAngle = shineParams.shineTurnAngle;
        smallOffsetAngle = shineParams.smallShineOffsetAngle;
        enableFlashing = shineParams.enableFlashing;
        allowRandomColor = shineParams.allowRandomColor;
        shineDistanceMultiple = shineParams.shineDistanceMultiple;
        animDuration = shineParams.animDuration;
        clickAnimDuration = shineParams.clickAnimDuration;
        smallShineColor = shineParams.smallShineColor;
        bigShineColor = shineParams.bigShineColor;
        shineSize = shineParams.shineSize;

        if (smallShineColor == 0) {
            smallShineColor = colorRandom[6];
        }

        if (bigShineColor == 0) {
            bigShineColor = shineButton.getColor();
        }
    }
}
