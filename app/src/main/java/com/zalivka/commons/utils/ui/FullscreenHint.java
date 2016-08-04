package com.zalivka.commons.utils.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zalivka.commons.R;
import com.zalivka.commons.utils.EnvUtils;
import com.zalivka.commons.utils.Fonts;
import com.zalivka.commons.utils.ScrProps;

public class FullscreenHint extends RelativeLayout {

    private static final float RADIUS = ScrProps.scale(EnvUtils.isTablet() ? 60 : 40);

    public interface OnDismissListener {
        void onDismiss();
    }

    public OnDismissListener mOnDismiss;

    private TextHint mTextHint;

    public Button mDoNotShow;

    private ValueAnimator mValAnimator;

    private DimmedHint mDimmedHint = new DimmedHint();

    private static Paint sPaint = new Paint();
    private static Path sPath = new Path();
    private static Path sOutsidePath = new Path();

    private int mHlCenter[];

    private long mShowTimestamp;

    public static final String FROM_HINT = "passed_from_hint";

    @Nullable
    private View mAnchorButton;

    public FullscreenHint(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        if (isInEditMode())
            return;

        mTextHint = new TextHint(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);

        mTextHint.setTypeface(Fonts.getTypeface(Fonts.LIGHT));
        mTextHint.setTextSize(25);
        mTextHint.setTextColor(Color.WHITE);
        addView(mTextHint, lp);

        mDoNotShow = new Button(getContext());
        mDoNotShow.setBackgroundResource(R.drawable.blue_btn_color);
        mDoNotShow.setTextColor(Color.WHITE);
        LayoutParams lp2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = ScrProps.scale(10);
        lp2.setMargins(margin, margin, margin, margin);
        addView(mDoNotShow, lp2);

        mValAnimator = ObjectAnimator.ofFloat(mDimmedHint, "progress",
                0f, 1f).setDuration(400);
        mValAnimator.setInterpolator(new DecelerateInterpolator());
        mValAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mDimmedHint.progress = (Float)valueAnimator.getAnimatedValue();
                mDimmedHint.alpha = (int) (200 * mDimmedHint.progress);
                mDimmedHint.x = (int) (mTextHint.getX() + (mHlCenter[0] - mTextHint.getX()) * mDimmedHint.progress);

                float radius = RADIUS; // + (1 - mDimmedHint.progress) * 500;

                sPath.reset();
                sPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                sPath.addCircle(mDimmedHint.x, mHlCenter[1], radius-2, Path.Direction.CCW);

                sOutsidePath.reset();
                float scrWidth = ScrProps.screenWidth + 100;
                sOutsidePath.addRect(0f, 0f, scrWidth, mHlCenter[1] - radius, Path.Direction.CW);
                sOutsidePath.addRect(0f, 0f, mDimmedHint.x - radius, ScrProps.screenHeight, Path.Direction.CW);
                sOutsidePath.addRect(0f, mHlCenter[1] + radius, scrWidth, ScrProps.screenHeight, Path.Direction.CW);
                sOutsidePath.addRect(mDimmedHint.x + radius, mHlCenter[1] - radius, scrWidth, ScrProps.screenHeight, Path.Direction.CW);

//                Rect rect = new Rect((int)(mDimmedHint.x - 2*RADIUS), (int)(mHlCenter[1] - 2*RADIUS), (int)(mDimmedHint.x + 2*RADIUS), (int)(mHlCenter[1] + 2*RADIUS));
//                invalidate(rect);
                invalidate();
            }
        });

        sPaint.setAntiAlias(true);
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setContent(int textRes, int topDrawable) {
        setContent(getContext().getString(textRes), topDrawable);
    }

    public void setContent(String string, int topDrawable) {
        mTextHint.setText(Html.fromHtml(string));

        if (topDrawable != 0) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), topDrawable);
            BitmapDrawable dr = new BitmapDrawable(getResources(), bm);
            dr.setBounds(0, 0, bm.getWidth(), bm.getHeight());
            mTextHint.setCompoundDrawables(null, dr, null, null);
            mTextHint.setCompoundDrawablePadding(15);
        }
    }

    public void show(int[] coord) {
        mShowTimestamp = System.currentTimeMillis();
        mHlCenter = coord;
        setVisibility(VISIBLE);
        mValAnimator.start();
    }

    public void show(View hintButton) {
        mAnchorButton = hintButton;
        int loc[] = new int[2];
        int size = hintButton.getHeight() / 2;
        hintButton.getLocationOnScreen(loc);
        loc[0] += size;
        loc[1] += size;
        show(loc);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private class DimmedHint {
        float progress;
        int x;
        int y;
        int alpha;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode() || sPath == null)
            return;
        sPaint.setColor(Color.BLACK);
        sPaint.setAlpha(mDimmedHint.alpha);
        canvas.drawPath(sPath, sPaint);
        canvas.drawPath(sOutsidePath, sPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        RectF rect = new RectF(mHlCenter[0] - RADIUS, mHlCenter[1] - RADIUS, mHlCenter[0] + RADIUS, mHlCenter[1] + RADIUS);
        if (rect.contains(event.getX(), event.getY())) {
            hide();
            if (mAnchorButton != null) {
                mAnchorButton.setTag(FROM_HINT);
                mAnchorButton.onTouchEvent(event);
                return true;
            } else
                return true;
        } else {
            return true;
        }


//        else if (event.getAction() == MotionEvent.ACTION_UP && System.currentTimeMillis() - mShowTimestamp > 1000) {
//            if (mOnDismiss != null)
//                mOnDismiss.onDismiss();
//            hide();
//            return true;
//        }
    }

    private static class TextHint extends TextView {

        public TextHint(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }
    }
}
