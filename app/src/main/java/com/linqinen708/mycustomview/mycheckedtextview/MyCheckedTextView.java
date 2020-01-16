package com.linqinen708.mycustomview.mycheckedtextview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

/**
 * Created by Ian on 2020/1/6.
 * 状态选择 https://blog.csdn.net/qq_31796651/article/details/75010332
 */
public class MyCheckedTextView extends AppCompatTextView implements Checkable {

    static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    static final int[] UNABLE_STATE_SET = new int[]{-android.R.attr.state_enabled};
    //    static final int[] MY_STATE_SET = new int[]{android.R.attr.state_checked /*, android.R.attr.state_pressed*/  /*, android.R.attr.state_enabled*/};
    //    static final int[] MY_STATE_SET = new int[]{android.R.attr.state_selected /*, android.R.attr.state_pressed*/  /*, android.R.attr.state_enabled*/};
    static final int[] EMPTY_STATE_SET = new int[]{};

    public static final int SHAPE_RECTANGLE = 1;
    public static final int SHAPE_OVAL = 2;


    @IntDef({SHAPE_RECTANGLE, SHAPE_OVAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    private ColorStateList mSolidColor;
    private ColorStateList mStrokeColor;
    private ColorStateList mCheckedSolidColor;
    private ColorStateList mCheckedStrokeColor;
    private ColorStateList mUnableSolidColor;
    private ColorStateList mUnableStrokeColor;
    private ColorStateList mPressedSolidColor;
    private ColorStateList mPressedStrokeColor;

    private ColorStateList mPressedTextColor;
    private ColorStateList mCheckedTextColor;
    private ColorStateList mUnableTextColor;

    private ColorStateList mRippleColor;

    private int mStrokeWidth;
    private int mRadius;
    private int mRadiusTopLeft;
    private int mRadiusTopRight;
    private int mRadiusBottomLeft;
    private int mRadiusBottomRight;
    private int mShape;

    private Drawable mUnableDrawable;
    private Drawable mPressedDrawable;
    private Drawable mNormalDrawable;
    private Drawable mCheckedDrawable;

    private GradientDrawable mUnableGradientDrawable;
    private GradientDrawable mPressedGradientDrawable;
    private GradientDrawable mNormalGradientDrawable;
    private GradientDrawable mCheckedGradientDrawable;
    private boolean mChecked;
    /**因为attrs中名称是mtv_checked，如果使用databinding，需要名称一致*/
    private boolean mtv_checked;
    private boolean mIsGradient;

    /**
     * 是否使用ripple 水波纹属性
     */
//    private boolean isRipple;

    private ColorStateList mGradientCenterColor;
    private int mGradientStartColor;
    private int mGradientEndColor;
    private int mGradientRadius;
    private int mGradientType;
    private int mGradientOrientation;

    private List<OnCheckChangeListener> mOnCheckChangeListeners;

    public interface OnCheckChangeListener {
        void onCheckChange(MyCheckedTextView myTextView, boolean isChecked);
    }

    public MyCheckedTextView(Context context) {
        super(context);
    }

    public MyCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
//        LogT.i("extraSpace:" + extraSpace);
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isEnabled()) {
            if (isChecked()) {
                LogT.i("被选中");
                mergeDrawableStates(drawableState, CHECKED_STATE_SET);
            }
        } else {
            LogT.i("无效的");
            mergeDrawableStates(drawableState, UNABLE_STATE_SET);
        }

        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        LogT.i("状态改变:" + showState(getDrawableState()));
    }

    private String showState(int[] drawableState) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int state : drawableState) {
            switch (state) {
                case 16842912:
                    stringBuilder.append("state_checked，");
                    break;
                case 16842910:
                    stringBuilder.append("state_enabled，");
                    break;
                case -16842910:
                    stringBuilder.append("state_unable，");
                    break;
                case 16842919:
                    stringBuilder.append("state_pressed，");
                    break;
                case 16842909:
                    stringBuilder.append("state_window_focused，");
                    break;
                case 16843547:
                    stringBuilder.append("state_accelerated，");
                    break;
                case 16843597:
                    stringBuilder.append("state_multiline，");
                    break;
                case 16842913:
                    stringBuilder.append("state_selected，");
                    break;
                default:
                    stringBuilder.append(state).append("，");
                    break;
            }
        }
        return stringBuilder.toString();
    }

    private void init(Context context, AttributeSet attrs) {
        if (null == context || null == attrs) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCheckedTextView);

        initCompoundDrawables(context, typedArray);
        initRadius(typedArray);

        mChecked = typedArray.getBoolean(R.styleable.MyCheckedTextView_mtv_checked, false);

        mSolidColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_solid_color);
        mCheckedSolidColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_checked_solid_color);
        mUnableSolidColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_unable_solid_color);
        mPressedSolidColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_pressed_solid_color);
        mStrokeColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_stroke_color);
        mCheckedStrokeColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_checked_stroke_color);
        mUnableStrokeColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_unable_stroke_color);
        mPressedStrokeColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_pressed_stroke_color);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_stroke_width, 0);
        mRippleColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_ripple);
        mShape = typedArray.getInt(R.styleable.MyCheckedTextView_mtv_shape, SHAPE_RECTANGLE);

        mPressedTextColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_pressed_text_color);
        mCheckedTextColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_checked_text_color);
        mUnableTextColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_unable_text_color);

        mIsGradient = typedArray.getBoolean(R.styleable.MyCheckedTextView_mtv_is_gradient, false);
        mGradientCenterColor = typedArray.getColorStateList(R.styleable.MyCheckedTextView_mtv_gradient_center_color);
        mGradientStartColor = typedArray.getColor(R.styleable.MyCheckedTextView_mtv_gradient_start_color, Color.TRANSPARENT);
        mGradientEndColor = typedArray.getColor(R.styleable.MyCheckedTextView_mtv_gradient_end_color, Color.TRANSPARENT);
        mGradientRadius = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_gradient_radius, 0);
        mGradientType = typedArray.getInteger(R.styleable.MyCheckedTextView_mtv_gradient_type, GradientDrawable.LINEAR_GRADIENT);
        mGradientOrientation = typedArray.getInteger(R.styleable.MyCheckedTextView_mtv_gradient_orientation, 0);

        mPressedDrawable = typedArray.getDrawable(R.styleable.MyCheckedTextView_mtv_pressed_drawable);
        mCheckedDrawable = typedArray.getDrawable(R.styleable.MyCheckedTextView_mtv_checked_drawable);
        mUnableDrawable = typedArray.getDrawable(R.styleable.MyCheckedTextView_mtv_unable_drawable);
        mNormalDrawable = typedArray.getDrawable(R.styleable.MyCheckedTextView_mtv_normal_drawable);


        mPressedGradientDrawable = buildGradientDrawable(mPressedSolidColor, mPressedStrokeColor);
        mCheckedGradientDrawable = buildGradientDrawable(mCheckedSolidColor, mCheckedStrokeColor);
        mUnableGradientDrawable = buildGradientDrawable(mUnableSolidColor, mUnableStrokeColor);
        mNormalGradientDrawable = buildNormalDrawable();


        typedArray.recycle();
        initTextColor();
        setBackground(getFinalBackgroundDrawable());
    }

    /**
     * https://blog.csdn.net/qq_31796651/article/details/75010332
     */
    private void initTextColor() {
//        LogT.i("getCurrentTextColor():" + getCurrentTextColor());
        int[] colors = new int[]{
                mPressedTextColor == null ? getCurrentTextColor() : mPressedTextColor.getDefaultColor(),
                mCheckedTextColor == null ? getCurrentTextColor() : mCheckedTextColor.getDefaultColor(),
                getCurrentTextColor(),
                mUnableTextColor == null ? getCurrentTextColor() : mUnableTextColor.getDefaultColor(),
        };
        /*自己研究发现，优先顺序很重要，如果满足条件index = 0，就不会再执行之后的颜色判断，
        所以要先判断press和check属性，否则会导致颜色无效*/
        int[][] states = new int[4][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_checked, android.R.attr.state_enabled};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{-android.R.attr.state_enabled};
        ColorStateList colorList = new ColorStateList(states, colors);
        setTextColor(colorList);
    }

    private void initRadius(TypedArray typedArray) {
        mRadius = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_radius, 0);
        mRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_radius_left_top, mRadius);
        mRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_radius_right_top, mRadius);
        mRadiusBottomLeft = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_radius_left_bottom, mRadius);
        mRadiusBottomRight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_radius_right_bottom, mRadius);
    }

    private void initCompoundDrawables(Context context, TypedArray typedArray) {
        final int drawableStartId = typedArray.getResourceId(R.styleable.MyCheckedTextView_mtv_drawable_start, -1);
        final int drawableEndId = typedArray.getResourceId(R.styleable.MyCheckedTextView_mtv_drawable_end, -1);
        final int drawableTopId = typedArray.getResourceId(R.styleable.MyCheckedTextView_mtv_drawable_top, -1);
        final int drawableBottomId = typedArray.getResourceId(R.styleable.MyCheckedTextView_mtv_drawable_bottom, -1);
        final int drawableStartWidth = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_start_width, -1);
        final int drawableStartHeight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_start_height, -1);
        final int drawableEndWidth = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_end_width, -1);
        final int drawableEndHeight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_end_height, -1);
        final int drawableTopWidth = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_top_width, -1);
        final int drawableTopHeight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_top_height, -1);
        final int drawableBottomWidth = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_bottom_width, -1);
        final int drawableBottomHeight = typedArray.getDimensionPixelSize(R.styleable.MyCheckedTextView_mtv_drawable_bottom_height, -1);

        Drawable drawableLeft = loadDrawable(context, drawableStartId);
        Drawable drawableTop = loadDrawable(context, drawableTopId);
        Drawable drawableRight = loadDrawable(context, drawableEndId);
        Drawable drawableBottom = loadDrawable(context, drawableBottomId);

        setDrawableBounds(drawableStartWidth, drawableStartHeight, drawableLeft);
        setDrawableBounds(drawableTopWidth, drawableTopHeight, drawableTop);
        setDrawableBounds(drawableEndWidth, drawableEndHeight, drawableRight);
        setDrawableBounds(drawableBottomWidth, drawableBottomHeight, drawableBottom);

        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    // 加载资源文件中的Drawable
    private Drawable loadDrawable(Context context, int drawableId) {
        Drawable drawable = null;
        if (-1 != drawableId) {
            drawable = AppCompatResources.getDrawable(context, drawableId);

            if (drawable != null) {
                fixVectorDrawableTinting(drawable);
            }
        }
        return drawable;
    }

    /**
     * 修复矢量图
     *
     * @param drawable 待修复的Drawable
     */
    private void fixVectorDrawableTinting(final Drawable drawable) {
        final int[] originalState = drawable.getState();
        if (originalState.length == 0) {
            drawable.setState(CHECKED_STATE_SET);
        } else {
            drawable.setState(EMPTY_STATE_SET);
        }
        drawable.setState(originalState);
    }

    /**
     * 设置Drawable绘制的大小
     *
     * @param drawableWidth  drawable宽度
     * @param drawableHeight drawable高度
     * @param drawable       待绘制的drawable
     */
    private void setDrawableBounds(int drawableWidth, int drawableHeight, Drawable drawable) {
        if (null != drawable && drawableWidth > -1 && drawableHeight > -1) {
            drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        } else if (null != drawable) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
    }

    private void setDrawableShape(GradientDrawable drawable) {
        if (mShape == SHAPE_OVAL) {
            drawable.setShape(GradientDrawable.OVAL);
        } else {
            drawable.setShape(GradientDrawable.RECTANGLE);
        }
    }

    private GradientDrawable buildGradientDrawable(ColorStateList solidColor, ColorStateList strokeColor) {
        GradientDrawable gradientDrawable = null;
        if (null != solidColor || null != strokeColor) {
            gradientDrawable = new GradientDrawable();
            setDrawableShape(gradientDrawable);
            //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
            gradientDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
            gradientDrawable.setColor(null != solidColor ? solidColor.getDefaultColor() :
                    null != mSolidColor ? mSolidColor.getDefaultColor() : Color.TRANSPARENT);

            if (mStrokeWidth > 0) {
                gradientDrawable.setStroke(mStrokeWidth, null != strokeColor ? strokeColor.getDefaultColor() :
                        null != mStrokeColor ? mStrokeColor.getDefaultColor() : Color.TRANSPARENT);
            }
        }
        return gradientDrawable;
    }

    private GradientDrawable buildNormalDrawable() {
        GradientDrawable normalDrawable;
        if (mIsGradient) {
            int[] colors;
            if (mGradientCenterColor != null) {
                colors = new int[]{mGradientStartColor, mGradientCenterColor.getDefaultColor(), mGradientEndColor};
            } else {
                colors = new int[]{mGradientStartColor, mGradientEndColor};
            }
            normalDrawable = new GradientDrawable(getGradientOrientation(), colors);
            normalDrawable.setGradientType(mGradientType);
            normalDrawable.setGradientRadius(mGradientRadius);
        } else {
            normalDrawable = new GradientDrawable();
            if (mSolidColor != null) {
                LogT.i("设置背景颜色:");
                normalDrawable.setColor(mSolidColor.getDefaultColor());
            } else {
                LogT.i("无背景颜色:");
                normalDrawable.setColor(Color.TRANSPARENT);
            }
        }
        //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
        normalDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        setDrawableShape(normalDrawable);

        if (mStrokeColor != null && mStrokeWidth > 0) {
            LogT.i("设置stroke:");
            normalDrawable.setStroke(mStrokeWidth, mStrokeColor.getDefaultColor());
        }
        return normalDrawable;
    }

    private GradientDrawable.Orientation getGradientOrientation() {
        GradientDrawable.Orientation orientation;
        switch (mGradientOrientation) {
            case 1:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case 2:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case 3:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case 4:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case 5:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case 6:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case 7:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            default:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
        }
        return orientation;
    }

    private Drawable getFinalBackgroundDrawable() {
        /*点击的水波纹，如果触发了水波纹效果，则mPressedDrawable 就不会起作用*/
        if (mRippleColor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*mask 是水波纹的范围的drawable，如果不设置，会以控件宽高中的较大值为直径绘制水波纹*/
            return new RippleDrawable(mRippleColor, mNormalGradientDrawable, null);
        } /*else if (null == mCheckedGradientDrawable && null == mPressedGradientDrawable && null == mUnableGradientDrawable) {
            LogT.i("没有任何状态的背景:");
            return mNormalGradientDrawable;
        }*/ else {
            LogT.i("状态StateListDrawable:");
            /*自己研究发现，优先顺序很重要，如果满足条件一，就不会再执行之后的状态判断，
             *所以要先判断press和check属性，否则会导致颜色无效
             *经测试，如果enable状态先判断，则press和check都不会触发
             */
            StateListDrawable backgroundDrawable = new StateListDrawable();
            backgroundDrawable.addState(new int[]{android.R.attr.state_pressed}, mPressedDrawable == null ? mPressedGradientDrawable : mPressedDrawable);
            backgroundDrawable.addState(new int[]{android.R.attr.state_checked}, mCheckedDrawable == null ? mCheckedGradientDrawable : mCheckedDrawable);
            backgroundDrawable.addState(new int[]{android.R.attr.state_enabled}, mNormalDrawable == null ? mNormalGradientDrawable : mNormalDrawable);
            backgroundDrawable.addState(new int[]{-android.R.attr.state_enabled}, mUnableDrawable == null ? mUnableGradientDrawable : mUnableDrawable);

            return backgroundDrawable;
        }
    }

    private void resetRadius() {
        if (null != mNormalGradientDrawable) {
            mNormalGradientDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        }
        if (null != mCheckedGradientDrawable) {
            mCheckedGradientDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        }
        if (null != mPressedGradientDrawable) {
            mPressedGradientDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        }
        if (null != mUnableGradientDrawable) {
            mUnableGradientDrawable.setCornerRadii(new float[]{mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight, mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft});
        }

    }

    @Override
    public void setEnabled(boolean enabled) {
//        LogT.i("isEnabled():" + isEnabled() + ",enabled:" + enabled);
        super.setEnabled(enabled);
        //父类已经有refreshDrawableState()方法了，所以不需要再重复一遍
//        refreshDrawableState();

    }

    public boolean isMtv_checked() {
        return isChecked();
    }

    public void setMtv_checked(boolean mtv_checked) {
        this.mtv_checked = mtv_checked;
        setChecked(mtv_checked);
    }

    @Override
    public void setChecked(boolean checked) {
        LogT.i("mChecked:" + mChecked + ", checked:" + checked);
        if (this.mChecked != checked) {
            this.mChecked = checked;
            refreshDrawableState();
        }

        if (null != mOnCheckChangeListeners && mOnCheckChangeListeners.size() > 0) {
            for (OnCheckChangeListener onCheckChangeListener : mOnCheckChangeListeners) {
                onCheckChangeListener.onCheckChange(this, checked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public boolean isGradient() {
        return mIsGradient;
    }

    public void setGradient(boolean gradient) {
        if (mIsGradient != gradient) {
            mIsGradient = gradient;
            mNormalGradientDrawable = buildNormalDrawable();
            setBackground(getFinalBackgroundDrawable());
        }
    }

    public void setRadius(int radius) {
        mRadius = radius;
        mRadiusBottomLeft = radius;
        mRadiusBottomRight = radius;
        mRadiusTopLeft = radius;
        mRadiusTopRight = radius;

        resetRadius();
        setBackground(getFinalBackgroundDrawable());
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadiusTopLeft(int radiusTopLeft) {
        mRadiusTopLeft = radiusTopLeft;
        resetRadius();
        setBackground(getFinalBackgroundDrawable());
    }

    public int getRadiusTopLeft() {
        return mRadiusTopLeft;
    }

    public void setRadiusTopRight(int radiusTopRight) {
        mRadiusTopRight = radiusTopRight;
        resetRadius();
        setBackground(getFinalBackgroundDrawable());
    }

    public int getRadiusTopRight() {
        return mRadiusTopRight;
    }

    public void setRadiusBottomLeft(int radiusBottomLeft) {
        mRadiusBottomLeft = radiusBottomLeft;
        resetRadius();
        setBackground(getFinalBackgroundDrawable());
    }

    public int getRadiusBottomLeft() {
        return mRadiusBottomLeft;
    }

    public void setRadiusBottomRight(int radiusBottomRight) {
        mRadiusBottomRight = radiusBottomRight;
        resetRadius();
        setBackground(getFinalBackgroundDrawable());
    }

    public int getRadiusBottomRight() {
        return mRadiusBottomRight;
    }

    public void setSolidColor(@ColorInt int solidColor) {
        mSolidColor = ColorStateList.valueOf(solidColor);
        mNormalGradientDrawable.setColor(solidColor);
        setBackground(getFinalBackgroundDrawable());
    }

    public void setSolidColorRes(@ColorRes int solidColorRes) {
        int solidColor = ContextCompat.getColor(getContext(), solidColorRes);
        setSolidColor(solidColor);
    }

    @ColorInt
    public int getSolidColor() {
        return null != mSolidColor ? mSolidColor.getDefaultColor() : Color.TRANSPARENT;
    }

    public void setCheckedSolidColor(@ColorInt int checkedSolidColor) {
        mCheckedSolidColor = ColorStateList.valueOf(checkedSolidColor);
        mCheckedGradientDrawable.setColor(checkedSolidColor);
        setBackground(getFinalBackgroundDrawable());
    }

    public void setCheckedSolidColorRes(@ColorRes int selectedSolidColorRes) {
        int selectedSolidColor = ContextCompat.getColor(getContext(), selectedSolidColorRes);
        setCheckedSolidColor(selectedSolidColor);
    }

    @ColorInt
    public int getCheckedSolidColor() {
        return mCheckedSolidColor != null ? mCheckedStrokeColor.getDefaultColor() : Color.TRANSPARENT;
    }

    public void setStrokeColor(@ColorInt int stokeColor) {
        mStrokeColor = ColorStateList.valueOf(stokeColor);
        mNormalGradientDrawable.setStroke(mStrokeWidth, stokeColor);
        setBackground(getFinalBackgroundDrawable());
    }

    @ColorInt
    public int getStrokeColor() {
        return mStrokeColor != null ? mStrokeColor.getDefaultColor() : Color.TRANSPARENT;
    }

    public void setCheckedStrokeColor(@ColorInt int checkedStrokeColor) {
        mCheckedStrokeColor = ColorStateList.valueOf(checkedStrokeColor);
        mCheckedGradientDrawable.setStroke(mStrokeWidth, checkedStrokeColor);
        setBackground(getFinalBackgroundDrawable());
    }

    public void setCheckedStrokeColorRes(@ColorRes int selectedStokeColorRes) {
        int selectedStokeColor = ContextCompat.getColor(getContext(), selectedStokeColorRes);
        setCheckedStrokeColor(selectedStokeColor);
    }

    @ColorInt
    public int getCheckedStrokeColor() {
        return mCheckedStrokeColor != null ? mCheckedStrokeColor.getDefaultColor() : Color.TRANSPARENT;
    }


    public void setStrokeWidth(int strokeWidth) {
        mStrokeWidth = strokeWidth;
        mNormalGradientDrawable.setStroke(strokeWidth, mStrokeColor != null ? mStrokeColor.getDefaultColor() : Color.TRANSPARENT);
        mCheckedGradientDrawable.setStroke(strokeWidth, mCheckedStrokeColor != null ? mCheckedStrokeColor.getDefaultColor() : Color.TRANSPARENT);
        mPressedGradientDrawable.setStroke(strokeWidth, mPressedStrokeColor != null ? mPressedStrokeColor.getDefaultColor() : Color.TRANSPARENT);
        mUnableGradientDrawable.setStroke(strokeWidth, mUnableStrokeColor != null ? mUnableStrokeColor.getDefaultColor() : Color.TRANSPARENT);

        setBackground(getFinalBackgroundDrawable());
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setRippleColor(@ColorInt int rippleColor) {
        mRippleColor = ColorStateList.valueOf(rippleColor);
        setBackground(getFinalBackgroundDrawable());
    }

    public void setRippleColorRes(@ColorRes int rippleColorRes) {
        int rippleColor = ContextCompat.getColor(getContext(), rippleColorRes);
        setRippleColor(rippleColor);
    }

    @ColorInt
    public int getRippleColor() {
        return mRippleColor != null ? mRippleColor.getDefaultColor() : Color.TRANSPARENT;
    }

    public void setShape(@Shape int shape) {
        mShape = shape;
        mNormalGradientDrawable.setShape(shape);
        setBackground(getFinalBackgroundDrawable());
    }

    @Shape
    public int getShape() {
        return mShape;
    }


//    public Drawable getMaskDrawable() {
//        return mMaskDrawable;
//    }
//
//    /**
//     * 自己设置自定义的drawable
//     * 这个maskDrawable 只是用来做ripple水波纹用的
//     */
//    public void setMaskDrawable(Drawable maskDrawable) {
//        mMaskDrawable = maskDrawable;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (getBackground() != null && getBackground() instanceof RippleDrawable) {
//                setBackground(getFinalBackgroundDrawable());
//            }
//        }
//    }


    public void addOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        if (mOnCheckChangeListeners == null) {
            mOnCheckChangeListeners = new ArrayList<>();
        }
        mOnCheckChangeListeners.add(onCheckChangeListener);
    }

    public void removeOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        if (mOnCheckChangeListeners != null && mOnCheckChangeListeners.size() > 0) {
            mOnCheckChangeListeners.remove(onCheckChangeListener);
        }
    }

    public void removeAllCheckChangeListeners() {
        if (mOnCheckChangeListeners != null) {
            mOnCheckChangeListeners.clear();
        }
    }
}
