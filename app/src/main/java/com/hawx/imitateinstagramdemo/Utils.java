package com.hawx.imitateinstagramdemo;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Utils {
    public static int getScreenHeight(Context context){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);
        int height=point.x;
        return height;
    }
}
