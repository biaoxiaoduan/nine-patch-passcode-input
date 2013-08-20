package com.aliyun.SecurityCenter.password;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class Util
{
	public static Bitmap zoom(Bitmap bitmap, float zf)
	{
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	public static Bitmap zoom(Bitmap bitmap, float wf,float hf)
	{
		Matrix matrix = new Matrix();
		matrix.postScale(wf,hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	
	public static Bitmap getRCB(Bitmap bitmap, float roundPX)
	{
		// RCB means
		// Rounded
		// Corner Bitmap
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbmp);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return dstbmp;
	}
	
	public static double distance(double x1,double y1,double x2,double y2)
	{
		return Math.sqrt(Math.abs(x1-x2)*Math.abs(x1-x2)+Math.abs(y1-y2)*Math.abs(y1-y2));
	}

	public static double pointTotoDegrees(double x,double y)
	{
		return Math.toDegrees(Math.atan2(x,y));
	}
	
	public static boolean checkInRound(float sx, float sy, float r, float x, float y)
	{
		return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
	}
	
	public static boolean isNotEmpty(String s)
	{
		return s != null && !"".equals(s.trim());
	}

	public static boolean isEmpty(String s)
	{
		return s == null || "".equals(s.trim());
	}
	
	public static String format(String src,Object... objects)
	{
		int k = 0;
		for(Object obj : objects)
		{
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}
}
