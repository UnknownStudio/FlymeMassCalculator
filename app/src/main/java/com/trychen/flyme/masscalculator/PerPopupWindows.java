package com.trychen.flyme.masscalculator;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meizu.flyme.blur.drawable.BlurDrawable;
import com.meizu.flyme.blur.view.BlurLinearLayout;

public class PerPopupWindows extends PopupWindow{
    private LayoutInflater inflater;
    private View popView;
    private TextView textMass;
    private TextView textWeigh;
    private View rootView;
    private GridView girdView;
    public View fillView;

    public PerPopupWindows(Context context, String name, String weigh, LayoutInflater inflater, View rootView) {
        super(context);
        this.inflater = inflater;
        this.rootView = rootView;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popView = inflater.inflate(R.layout.percents, null);

        textMass = (TextView) popView.findViewById(R.id.pTextMass);
        textWeigh = (TextView) popView.findViewById(R.id.pTextWeight);
        girdView = (GridView) popView.findViewById(R.id.pGridView);
        fillView = popView.findViewById(R.id.pFill);

        popView.findViewById(R.id.pTextLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closePopupWindow();
                return false;
            }
        });

        textMass.setText(name);
        textWeigh.setText(weigh);

        girdView.setAdapter(Main.massesAdapter);

        setContentView(popView);

        setOutsideTouchable(true);


        setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ColorDrawable dw = new ColorDrawable(0xAF000000);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.popupWindows);

    }

    public void show() {
        showAtLocation(rootView, Gravity.NO_GRAVITY,0,0);
    }
    private void closePopupWindow()
    {
        if (isShowing()) {
            dismiss();
        }
    }
}
