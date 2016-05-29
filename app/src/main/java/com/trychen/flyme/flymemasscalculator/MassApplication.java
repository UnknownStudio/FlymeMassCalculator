package com.trychen.flyme.flymemasscalculator;

import android.app.Activity;
import android.util.Log;

import com.meizu.common.renderer.effect.GLRenderManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MassApplication extends android.app.Application{
    private boolean[] option = {false,false};
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean isSimpleMode() {
        return option[0];
    }

    public boolean isFractionMode(){
        return option[1];
    }

    public void setSimpleMode(boolean open){
        option[0] = open;
    }
    public void setFractionMode(boolean open){
        option[1] = open;
    }


    public void save() {
        try {
            Properties prop = new Properties();
            prop.put("version","1");
            prop.put("application.isSimpleMode()", isSimpleMode()?"1":"0");
            prop.put("application.isFractionMode()", isFractionMode()?"1":"0");
            FileOutputStream outputStream = openFileOutput("config.dat",
                    Activity.MODE_PRIVATE);
            prop.store(outputStream,"");
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        try {
            FileInputStream inputStream = this.openFileInput("config.dat");
            Properties properties = new Properties();
            properties.load(inputStream);
            setSimpleMode(properties.get("application.isSimpleMode()").equals("1")?true:false);
            setFractionMode(properties.get("application.isFractionMode()").equals("1")?true:false);
            inputStream.close();
        } catch (FileNotFoundException e) {
            save();
        } catch (IOException e) {
            save();
            e.printStackTrace();
        } catch (NullPointerException e){
            save();
            e.printStackTrace();
        }
    }
}
