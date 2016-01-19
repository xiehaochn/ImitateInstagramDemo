package com.hawx.imitateinstagramdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2016/1/4.
 */
public class Utils implements Transformation{
    private static final int STROKE_WIDTH = 2;
    public static int getScreenHeight(Context context){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);
        int height=point.x;
        return height;
    }

    public static int getScreenWidth(Context context){
        WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display=windowManager.getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);
        int width=point.y;
        return width;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint avatarPaint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        avatarPaint.setShader(shader);

        Paint outlinePaint = new Paint();
        outlinePaint.setColor(Color.WHITE);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(STROKE_WIDTH);
        outlinePaint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, avatarPaint);
        canvas.drawCircle(r, r, r - STROKE_WIDTH / 2, outlinePaint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circleTransformation()";
    }
}
