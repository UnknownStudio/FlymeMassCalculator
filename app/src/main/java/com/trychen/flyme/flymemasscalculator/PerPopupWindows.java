package com.trychen.flyme.flymemasscalculator;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class PerPopupWindows extends PopupWindow{
    private View popView;
    private TextView textMass;
    private TextView textWeigh;
    private View rootView;
    private GridView girdView;
    public View fillView;
    private boolean massed = false;
    private String oMass;
    private String oWeigh;
    private Context context;
    private ScrollView scrollView;

    public PerPopupWindows(Context context, String name, String weigh, LayoutInflater inflater, View rootView) {
        super(context);
        this.rootView = rootView;
        this.context=context;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popView = inflater.inflate(R.layout.percents, null);

        textMass = (TextView) popView.findViewById(R.id.pTextMass);
        textWeigh = (TextView) popView.findViewById(R.id.pTextWeight);
        girdView = (GridView) popView.findViewById(R.id.pGridView);
        fillView = popView.findViewById(R.id.pFill);
        scrollView = (ScrollView) popView.findViewById(R.id.scrollView);
        oMass = name;
        oWeigh = weigh;

        popView.findViewById(R.id.pTextLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!massed) closePopupWindow();
                else {
                    changeMass(oMass,oWeigh,false);
                    massed = false;
                }
                return false;
            }
        });

        fillView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopupWindow();
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

    public void changeMass(final String text , final String weigh , boolean massed){
        this.massed = massed;
        Main.goneAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textMass.setText(text);
                textWeigh.setText(weigh);
                textMass.startAnimation(Main.showAnimation);
                textWeigh.startAnimation(Main.showAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textMass.startAnimation(Main.goneAnimation);
        textWeigh.startAnimation(Main.goneAnimation);
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
