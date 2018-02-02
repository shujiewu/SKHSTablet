package cn.sk.skhstablet.component;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.sk.skhstablet.R;

/**
 * Created by ldkobe on 2017/5/3.
 */
//浮点数的增加和减少控件，如果后期添加了参数控制的调节精度，需要修改增加和减少的onclick函数，如果需要修改此控件的样式，也要在此进行修改
public class FloatQuantityView extends LinearLayout implements View.OnClickListener {

    private Drawable quantityBackground, addButtonBackground, removeButtonBackground;

    private String addButtonText, removeButtonText;

    //private int quantity, maxQuantity, minQuantity;
    private float fquantity, fmaxQuantity, fminQuantity;

    private int quantityPadding;

    private int quantityTextColor, addButtonTextColor, removeButtonTextColor;

    private Button mButtonAdd, mButtonRemove;
    private TextView mTextViewQuantity;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(float newQuantity, boolean programmatically);

        void onLimitReached();
    }

    private OnQuantityChangeListener onQuantityChangeListener;

    public FloatQuantityView(Context context) {
        super(context);
        init(null, 0);
    }

    public FloatQuantityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FloatQuantityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FloatQuantityView, defStyle, 0);

        addButtonText = getResources().getString(R.string.qv_add);
        if (a.hasValue(R.styleable.FloatQuantityView_fqv_addButtonText)) {
            addButtonText = a.getString(R.styleable.FloatQuantityView_fqv_addButtonText);
        }
        addButtonBackground = ContextCompat.getDrawable(getContext(), R.drawable.qv_btn_selector);
        if (a.hasValue(R.styleable.FloatQuantityView_fqv_addButtonBackground)) {
            addButtonBackground = a.getDrawable(R.styleable.FloatQuantityView_fqv_addButtonBackground);
        }
        addButtonTextColor = a.getColor(R.styleable.FloatQuantityView_fqv_addButtonTextColor, Color.WHITE);

        removeButtonText = getResources().getString(R.string.qv_remove);
        if (a.hasValue(R.styleable.FloatQuantityView_fqv_removeButtonText)) {
            removeButtonText = a.getString(R.styleable.FloatQuantityView_fqv_removeButtonText);
        }
        removeButtonBackground = ContextCompat.getDrawable(getContext(), R.drawable.qv_btn_selector);
        if (a.hasValue(R.styleable.FloatQuantityView_fqv_removeButtonBackground)) {
            removeButtonBackground = a.getDrawable(R.styleable.FloatQuantityView_fqv_removeButtonBackground);
        }
        removeButtonTextColor = a.getColor(R.styleable.FloatQuantityView_fqv_removeButtonTextColor, Color.WHITE);

        fquantity = a.getFloat(R.styleable.FloatQuantityView_fqv_quantity, 0);
        fmaxQuantity = a.getFloat(R.styleable.FloatQuantityView_fqv_maxQuantity, Integer.MAX_VALUE);
        fminQuantity = a.getFloat(R.styleable.FloatQuantityView_fqv_minQuantity, 0);


        quantityPadding = (int) a.getDimension(R.styleable.FloatQuantityView_fqv_quantityPadding, pxFromDp(24));
        quantityTextColor = a.getColor(R.styleable.FloatQuantityView_fqv_quantityTextColor, getResources().getColor(R.color.maincolor));
        quantityBackground = ContextCompat.getDrawable(getContext(), R.drawable.qv_bg_selector);
        if (a.hasValue(R.styleable.FloatQuantityView_fqv_quantityBackground)) {
            quantityBackground = a.getDrawable(R.styleable.FloatQuantityView_fqv_quantityBackground);
        }

        a.recycle();
        int dp6 = pxFromDp(6);

        mButtonAdd = new Button(getContext());
        mButtonAdd.setGravity(Gravity.CENTER);
        mButtonAdd.setPadding(dp6, dp6, dp6, dp6);
        mButtonAdd.setMinimumHeight(0);
        mButtonAdd.setMinimumWidth(0);
        mButtonAdd.setMinHeight(0);
        mButtonAdd.setMinWidth(0);
        setAddButtonBackground(addButtonBackground);
        setAddButtonText(addButtonText);
        setAddButtonTextColor(addButtonTextColor);

        mButtonRemove = new Button(getContext());
        mButtonRemove.setGravity(Gravity.CENTER);
        mButtonRemove.setPadding(dp6, dp6, dp6, dp6);
        mButtonRemove.setMinimumHeight(0);
        mButtonRemove.setMinimumWidth(0);
        mButtonRemove.setMinHeight(0);
        mButtonRemove.setMinWidth(0);
        setRemoveButtonBackground(removeButtonBackground);
        setRemoveButtonText(removeButtonText);
        setRemoveButtonTextColor(removeButtonTextColor);

        mTextViewQuantity = new TextView(getContext());
        mTextViewQuantity.setGravity(Gravity.CENTER);
        mTextViewQuantity.setTextSize(18);
        setQuantityTextColor(quantityTextColor);
        setQuantity(fquantity);
        setQuantityBackground(quantityBackground);
        setQuantityPadding(quantityPadding);

        setOrientation(HORIZONTAL);

        addView(mButtonRemove, new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT,1));
        addView(mTextViewQuantity,new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT,(float)1.5));
        addView(mButtonAdd, new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT,1));

        mButtonAdd.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
        mTextViewQuantity.setOnClickListener(this);
    }

    public TextView getmTextViewQuantity()
    {
        return mTextViewQuantity;
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonAdd) {
            if (fquantity + 0.1 > fmaxQuantity) {
                if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached();
            } else {
                fquantity += 0.1;

                BigDecimal bd = new BigDecimal(fquantity);
                bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
                mTextViewQuantity.setText(bd.toString());

                if (onQuantityChangeListener != null)
                    onQuantityChangeListener.onQuantityChanged(fquantity, false);
            }
        } else if (v == mButtonRemove) {
            if (fquantity - 0.1 < fminQuantity) {
                if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached();
            } else {
                fquantity -= 0.1;
                BigDecimal bd = new BigDecimal(fquantity);
                bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
                mTextViewQuantity.setText(bd.toString());

                if (onQuantityChangeListener != null)
                    onQuantityChangeListener.onQuantityChanged(fquantity, false);
            }
        } else if (v == mTextViewQuantity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("更改值");

            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.qv_dialog_changequantity, null, false);
            final EditText et = (EditText) inflate.findViewById(R.id.qv_et_change_qty);
            et.setText(String.valueOf(fquantity));

            builder.setView(inflate);
            builder.setPositiveButton("确定更改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newQuantity = et.getText().toString();
                    if (isNumber(newQuantity)) {
                        float intNewQuantity = Float.parseFloat(newQuantity);
                        setQuantity(intNewQuantity);
                    }
                }
            }).setNegativeButton("取消", null);
            builder.show();
        }
    }


    public OnQuantityChangeListener getOnQuantityChangeListener() {
        return onQuantityChangeListener;
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener onQuantityChangeListener) {
        this.onQuantityChangeListener = onQuantityChangeListener;
    }

    public Drawable getQuantityBackground() {
        return quantityBackground;
    }

    public void setQuantityBackground(Drawable quantityBackground) {
        this.quantityBackground = quantityBackground;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mTextViewQuantity.setBackground(quantityBackground);
        } else {
            mTextViewQuantity.setBackgroundDrawable(quantityBackground);
        }
    }

    public Drawable getAddButtonBackground() {
        return addButtonBackground;
    }

    public void setAddButtonBackground(Drawable addButtonBackground) {
        this.addButtonBackground = addButtonBackground;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButtonAdd.setBackground(addButtonBackground);
        } else {
            mButtonAdd.setBackgroundDrawable(addButtonBackground);
        }
    }

    public Drawable getRemoveButtonBackground() {
        return removeButtonBackground;
    }

    public void setRemoveButtonBackground(Drawable removeButtonBackground) {
        this.removeButtonBackground = removeButtonBackground;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mButtonRemove.setBackground(removeButtonBackground);
        } else {
            mButtonRemove.setBackgroundDrawable(removeButtonBackground);
        }
    }

    public String getAddButtonText() {
        return addButtonText;
    }

    public void setAddButtonText(String addButtonText) {
        this.addButtonText = addButtonText;
        mButtonAdd.setText(addButtonText);
    }

    public String getRemoveButtonText() {
        return removeButtonText;
    }

    public void setRemoveButtonText(String removeButtonText) {
        this.removeButtonText = removeButtonText;
        mButtonRemove.setText(removeButtonText);
    }

    public float getQuantity() {
        return fquantity;
    }

    public void setQuantity(float fquantity) {
        boolean limitReached = false;

        if (fquantity > fmaxQuantity) {
            fquantity = fmaxQuantity;
            limitReached = true;
            if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached();
        }
        if (fquantity < fminQuantity) {
            fquantity = fminQuantity;
            limitReached = true;
            if (onQuantityChangeListener != null) onQuantityChangeListener.onLimitReached();
        }


        if (!limitReached && onQuantityChangeListener != null)
            onQuantityChangeListener.onQuantityChanged(fquantity, true);

        this.fquantity = fquantity;
        mTextViewQuantity.setText(String.valueOf(this.fquantity));
    }

    public float getMaxQuantity() {
        return fmaxQuantity;
    }

    public void setMaxQuantity(float maxQuantity) {
        this.fmaxQuantity = maxQuantity;
    }

    public float getMinQuantity() {
        return fminQuantity;
    }

    public void setMinQuantity(float minQuantity) {
        this.fminQuantity = minQuantity;
    }

    public int getQuantityPadding() {
        return quantityPadding;
    }

    public void setQuantityPadding(int quantityPadding) {
        this.quantityPadding = quantityPadding;
        mTextViewQuantity.setPadding(quantityPadding, 0, quantityPadding, 0);
    }

    public int getQuantityTextColor() {
        return quantityTextColor;
    }

    public void setQuantityTextColor(int quantityTextColor) {
        this.quantityTextColor = quantityTextColor;
        mTextViewQuantity.setTextColor(quantityTextColor);
    }

    public void setQuantityTextColorRes(int quantityTextColorRes) {
        this.quantityTextColor = ContextCompat.getColor(getContext(), quantityTextColorRes);
        mTextViewQuantity.setTextColor(quantityTextColor);
    }

    public int getAddButtonTextColor() {
        return addButtonTextColor;
    }

    public void setAddButtonTextColor(int addButtonTextColor) {
        this.addButtonTextColor = addButtonTextColor;
        mButtonAdd.setTextColor(addButtonTextColor);
    }

    public void setAddButtonTextColorRes(int addButtonTextColorRes) {
        this.addButtonTextColor = ContextCompat.getColor(getContext(), addButtonTextColorRes);
        mButtonAdd.setTextColor(addButtonTextColor);
    }

    public int getRemoveButtonTextColor() {
        return removeButtonTextColor;
    }

    public void setRemoveButtonTextColor(int removeButtonTextColor) {
        this.removeButtonTextColor = removeButtonTextColor;
        mButtonRemove.setTextColor(removeButtonTextColor);
    }

    public void setRemoveButtonTextColorRes(int removeButtonTextColorRes) {
        this.removeButtonTextColor = ContextCompat.getColor(getContext(), removeButtonTextColorRes);
        mButtonRemove.setTextColor(removeButtonTextColor);
    }

    private int dpFromPx(final float px) {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(final float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }


    private boolean isNumber(String string) {
        try {
            Float.parseFloat(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}