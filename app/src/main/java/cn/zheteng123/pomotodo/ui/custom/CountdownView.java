package cn.zheteng123.pomotodo.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

import cn.zheteng123.pomotodo.R;

/**
 * <pre>
 *     author : learner1999
 *     e-mail : roadoflearning@live.com
 *     time   : 2017/04/05
 *     desc   : 自定义倒计时组件
 *     version: 1.0
 * </pre>
 */
public class CountdownView extends View {

    /**
     * 倒计时类型
     */
    public enum Type {
        WORK, RELAX
    }

    private Type mType = Type.WORK;

    private Paint mPaint;

    /**
     * 用来保存文本的长宽
     */
    private Rect mTextBound;

    /**
     * 倒计时总时间
     */
    private int mTotalTime = 25 * 60;

    /**
     * 倒计时剩余时间
     */
    private int mRestSeconds = mTotalTime;

    /**
     * 首次绘制标志
     */
    private boolean bFirst = true;

    /**
     * 开始倒计时标志
     */
    private boolean bStart = false;

    /**
     * 倒计时环背景颜色（剩余时间颜色）
     */
    private int mRingBackColor;

    /**
     * 倒计时环前景颜色（已过去时间颜色）
     */
    private int mRingForeColor;

    /**
     * 倒计时结束监听器
     */
    private OnTimeOverListener mOnTimeOverListener;

    private Handler mHandler = new Handler();

    /**
     * 每隔一秒执行过一次
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mRestSeconds--;
            invalidate();
            if (mRestSeconds == 0) {
                if (mOnTimeOverListener != null) {
                    mOnTimeOverListener.onTimeOver();
                }
                return;
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public CountdownView(Context context) {
        super(context);
        init();
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        parseAttribute(attrs);
    }

    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        parseAttribute(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CountdownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        parseAttribute(attrs);
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextBound = new Rect();
    }

    private void parseAttribute(@Nullable AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CountdownView);
        mRingBackColor = ta.getColor(R.styleable.CountdownView_ring_back_color, Color.rgb(206, 77, 64));
        mRingForeColor = ta.getColor(R.styleable.CountdownView_ring_fore_color, Color.WHITE);
        ta.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int ringWidth = 35;

        // 画出环形底色部分
        mPaint.setColor(mRingBackColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ringWidth);
        canvas.drawArc(ringWidth / 2, ringWidth / 2, getWidth() - ringWidth / 2, getHeight() - ringWidth / 2, 0, 360, false, mPaint);

        // 在环形底色上画出已过去的时间
        mPaint.setColor(mRingForeColor);
        int rotateAngle = 360 * (mTotalTime - mRestSeconds) / mTotalTime;
        canvas.drawArc(ringWidth / 2, ringWidth / 2, getWidth() - ringWidth / 2, getHeight() - ringWidth / 2, -90, rotateAngle, false, mPaint);

        // 在圆环中央显示倒计时
        mPaint.setTextSize(90);
        mPaint.setStyle(Paint.Style.FILL);
        String strTime = "";
        if (mRestSeconds == 0) {
            strTime = "完成";
        } else {
            int minute = mRestSeconds / 60;
            int second = mRestSeconds % 60;
            strTime = String.format(Locale.getDefault(), "%02d:%02d", minute, second);
        }
        mPaint.getTextBounds(strTime, 0, strTime.length(), mTextBound);
        canvas.drawText(strTime, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 + mTextBound.height() / 2, mPaint);

        if (bStart && bFirst) {
            mHandler.post(mRunnable);
            bFirst = false;
        }
    }

    public void setOnTimeOverListener(OnTimeOverListener onTimeOverListener) {
        mOnTimeOverListener = onTimeOverListener;
    }

    public void setType(Type type) {
        mType = type;
    }

    /**
     * 开始倒计时
     */
    public void startCountdown() {
        bStart = true;
        invalidate();
    }

    /**
     * 倒计时结束监听器
     */
    public interface OnTimeOverListener {
        void onTimeOver();
    }
}
