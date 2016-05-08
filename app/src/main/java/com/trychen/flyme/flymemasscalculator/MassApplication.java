package com.trychen.flyme.flymemasscalculator;

import android.util.Log;

import com.meizu.common.renderer.effect.GLRenderManager;

public class MassApplication extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Debug","initialize GLRenderManager");
        GLRenderManager.getInstance().initialize(this);
    }

    @Override
    public void onTrimMemory(int level) {

        super.onTrimMemory(level);
        GLRenderManager.getInstance().trimMemory(level);
    }
}
