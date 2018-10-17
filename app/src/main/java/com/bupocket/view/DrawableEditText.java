package com.bupocket.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class DrawableEditText extends AppCompatEditText {
    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public DrawableEditText(Context context) {
        super(context);
    }

    public DrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface OnDrawableClickListener {
        void onDrawableClick();
    }

    private OnDrawableClickListener OnDrawableClickListener;

    public void setOnDrawableClickListener(OnDrawableClickListener OnDrawableClickListener) {
        this.OnDrawableClickListener = OnDrawableClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
            if (drawableRight != null) {
                //本次点击事件的x轴坐标，如果>当前控件宽度-控件右间距-drawable实际展示大小
                if (event.getX() >= (getWidth() - getPaddingRight() - drawableRight.getIntrinsicWidth())) {
                    //设置点击EditText右侧图标EditText失去焦点，
                    // 防止点击EditText右侧图标EditText获得焦点，软键盘弹出
                    setFocusableInTouchMode(false);
                    setFocusable(false);
                    if (OnDrawableClickListener != null) {
                        OnDrawableClickListener.onDrawableClick();
                    }
                } else {
                    setFocusableInTouchMode(true);
                    setFocusable(true);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
