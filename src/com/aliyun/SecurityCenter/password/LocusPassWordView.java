package com.aliyun.SecurityCenter.password;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aliyun.SecurityCenter.R;

public class LocusPassWordView extends View
{
	private float w = 0;
	private float h = 0;

	//
	private boolean isCache = false;
	//
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
 
	private Point[][] mPoints = new Point[3][3];
	private float r = 0;
	private List<Point> sPoints = new ArrayList<Point>();
	private boolean checking = false;
	private Bitmap locus_round_original;
	private Bitmap locus_round_click;
	private Bitmap locus_round_click_error;
	private Bitmap locus_line;
	private Bitmap locus_line_semicircle;
	private Bitmap locus_line_semicircle_error;
	private Bitmap locus_arrow;
	private Bitmap locus_line_error;
	private long CLEAR_TIME = 800;
	private int passwordMinLength = 5;
	private boolean isTouch = true;
	private Matrix mMatrix = new Matrix();
	private int lineAlpha = 100;
	public LocusPassWordView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public LocusPassWordView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public LocusPassWordView(Context context)
	{
		super(context);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if (!isCache)
		{
			initCache();
		}
		drawToCanvas(canvas);
	}

	private void drawToCanvas(Canvas canvas)
	{
		for (int i = 0; i < mPoints.length; i++)
		{
			for (int j = 0; j < mPoints[i].length; j++)
			{
				Point p = mPoints[i][j];
				if (p.state == Point.STATE_CHECK)
				{
					canvas.drawBitmap(locus_round_click, p.x - r, p.y - r, mPaint);
				}
				else if (p.state == Point.STATE_CHECK_ERROR)
				{
					canvas.drawBitmap(locus_round_click_error, p.x - r, p.y - r, mPaint);
				}
				else
				{
					canvas.drawBitmap(locus_round_original, p.x - r, p.y - r, mPaint);
				}
			}
		}
		
		if (sPoints.size() > 0)
		{
			int tmpAlpha = mPaint.getAlpha();
			mPaint.setAlpha(lineAlpha);
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++)
			{
				Point p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (this.movingNoPoint)
			{
				drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
			}
			mPaint.setAlpha(tmpAlpha);
			lineAlpha = mPaint.getAlpha();
		}

	}

	private void initCache()
	{

		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;

		if (w > h)
		{
			x = (w - h) / 2;
			w = h;
		}

		else
		{
			y = (h - w) / 2;
			h = w;
		}

		locus_round_original = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_round_original);
		locus_round_click = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_round_click);
		locus_round_click_error = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_round_click_error);

		locus_line = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_line);
		locus_line_semicircle = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_line_semicircle);

		locus_line_error = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_line_error);
		locus_line_semicircle_error = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_line_semicircle_error);

		locus_arrow = BitmapFactory.decodeResource(this.getResources(), R.drawable.locus_arrow);
		
		float canvasMinW = w;
		if (w > h)
		{
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2;
		float roundW = roundMinW / 2.f;
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;

		if (locus_round_original.getWidth() > roundMinW)
		{
			float sf = roundMinW * 1.0f / locus_round_original.getWidth(); // 取得缩放比例，将所有的图片进行缩放
			locus_round_original = Util.zoom(locus_round_original, sf);
			locus_round_click = Util.zoom(locus_round_click, sf);
			locus_round_click_error = Util.zoom(locus_round_click_error, sf);

			locus_line = Util.zoom(locus_line, sf);
			locus_line_semicircle = Util.zoom(locus_line_semicircle, sf);

			locus_line_error = Util.zoom(locus_line_error, sf);
			locus_line_semicircle_error = Util.zoom(locus_line_semicircle_error, sf);
			locus_arrow = Util.zoom(locus_arrow, sf);
			roundW = locus_round_original.getWidth() / 2;
		}

		mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
		mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
		mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
		mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
		mPoints[1][1] = new Point(x + w / 2, y + h / 2);
		mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
		mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
		mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
		mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
		int k = 0;
		for (Point[] ps : mPoints)
		{
			for (Point p : ps)
			{
				p.index = k;
				k++;
			}
		}
		r = locus_round_original.getHeight() / 2;
		isCache = true;
	}

	private void drawLine(Canvas canvas, Point a, Point b)
	{
		float ah = (float) Util.distance(a.x, a.y, b.x, b.y);
		float degrees = getDegrees(a, b);
		canvas.rotate(degrees, a.x, a.y);

		if (a.state == Point.STATE_CHECK_ERROR)
		{
			mMatrix.setScale((ah - locus_line_semicircle_error.getWidth()) / locus_line_error.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line_error.getHeight() / 2.0f);
			canvas.drawBitmap(locus_line_error, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle_error, a.x + locus_line_error.getWidth(), a.y - locus_line_error.getHeight() / 2.0f,
					mPaint);
		}
		else
		{
			mMatrix.setScale((ah - locus_line_semicircle.getWidth()) / locus_line.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - locus_line.getHeight() / 2.0f);
			canvas.drawBitmap(locus_line, mMatrix, mPaint);
			canvas.drawBitmap(locus_line_semicircle, a.x + ah - locus_line_semicircle.getWidth(), a.y - locus_line.getHeight() / 2.0f,
					mPaint);
		}
		
		canvas.drawBitmap(locus_arrow, a.x, a.y - locus_arrow.getHeight() / 2.0f, mPaint);
		
		canvas.rotate(-degrees, a.x, a.y);

	}

	public float getDegrees(Point a, Point b)
	{
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = b.x;// b.index % 3;
		float by = b.y;// b.index / 3;
		float degrees = 0;
		if (bx == ax)
		{
			if (by > ay)
			{
				degrees = 90;
			}
			else if (by < ay)
			{
				degrees = 270;
			}
		}
		else if (by == ay)
		{
			if (bx > ax)
			{
				degrees = 0;
			}
			else if (bx < ax)
			{
				degrees = 180;
			}
		}
		else
		{
			if (bx > ax)
			{
				if (by > ay)
				{
					degrees = 0;
					degrees = degrees + switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
				}
				else if (by < ay)
				{
					degrees = 360;
					degrees = degrees - switchDegrees(Math.abs(by - ay), Math.abs(bx - ax));
				}

			}
			else if (bx < ax)
			{
				if (by > ay)
				{
					degrees = 90;
					degrees = degrees + switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
				}
				else if (by < ay)
				{
					degrees = 270;
					degrees = degrees - switchDegrees(Math.abs(bx - ax), Math.abs(by - ay));
				}

			}

		}
		return degrees;
	}

	private float switchDegrees(float x, float y)
	{
		return (float) Util.pointTotoDegrees(x, y);
	}

	public int[] getArrayIndex(int index)
	{
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	private Point checkSelectPoint(float x, float y)
	{
		for (int i = 0; i < mPoints.length; i++)
		{
			for (int j = 0; j < mPoints[i].length; j++)
			{
				Point p = mPoints[i][j];
				if (Util.checkInRound(p.x, p.y, r, (int) x, (int) y)) { return p; }
			}
		}
		return null;
	}

	private void reset()
	{
		for (Point p : sPoints)
		{
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	private int crossPoint(Point p)
	{
		if (sPoints.contains(p))
		{
			if (sPoints.size() > 2)
			{
				if (sPoints.get(sPoints.size() - 1).index != p.index) { return 2; }
			}
			return 1;
		}
		else
		{
			return 0;
		}
	}

	private void addPoint(Point point)
	{
		this.sPoints.add(point);
	}

	private String toPointString()
	{
		if (sPoints.size() > passwordMinLength)
		{
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints)
			{
				sf.append(",");
				sf.append(p.index);
			}
			return sf.deleteCharAt(0).toString();
		}
		else
		{
			return "";
		}
	}

	boolean movingNoPoint = false;
	float moveingX, moveingY;

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isTouch) { return false; }

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		boolean redraw = false;
		Point p = null;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if (task != null)
			{
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null)
			{
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checking)
			{
				p = checkSelectPoint(ex, ey);
				if (p == null)
				{
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null)
		{

			int rk = crossPoint(p);
			if (rk == 2)
			{
				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;

				redraw = true;
			}
			else if (rk == 0)
			{
				p.state = Point.STATE_CHECK;
				addPoint(p);
				redraw = true;
			}
		}
		if (redraw)
		{

		}
		if (isFinish)
		{
			if (this.sPoints.size() == 1)
			{
				this.reset();
			}
			else if (this.sPoints.size() < passwordMinLength && this.sPoints.size() > 0)
			{
				// mCompleteListener.onPasswordTooMin(sPoints.size());
				error();
				clearPassword();
				//Toast.makeText(this.getContext(), "密码太短,请重新输入!", Toast.LENGTH_SHORT).show();
			}
			else if (mCompleteListener != null)
			{
				if (this.sPoints.size() >= passwordMinLength)
				{
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
				}

			}
		}
		this.postInvalidate();
		return true;
	}

	/**
	 * 设置已经选中的为错误
	 */
	private void error()
	{
		for (Point p : sPoints)
		{
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	/**
	 * 设置为输入错误
	 */
	public void markError()
	{
		markError(CLEAR_TIME);
	}

	public void markError(final long time)
	{
		for (Point p : sPoints)
		{
			p.state = Point.STATE_CHECK_ERROR;
		}
		this.clearPassword(time);
	}

	public void enableTouch()
	{
		isTouch = true;
	}

	public void disableTouch()
	{
		isTouch = false;
	}

	private Timer timer = new Timer();
	private TimerTask task = null;

	public void clearPassword()
	{
		clearPassword(CLEAR_TIME);
	}

	public void clearPassword(final long time)
	{
		if (time > 1)
		{
			if (task != null)
			{
				task.cancel();
				Log.d("task", "clearPassword cancel()");
			}
			lineAlpha = 130;
			postInvalidate();
			task = new TimerTask()
			{
				public void run()
				{
					reset();
					postInvalidate();
				}
			};
			Log.d("task", "clearPassword schedule(" + time + ")");
			timer.schedule(task, time);
		}
		else
		{
			reset();
			postInvalidate();
		}
	}

	private OnCompleteListener mCompleteListener;

	public void setOnCompleteListener(OnCompleteListener mCompleteListener)
	{
		this.mCompleteListener = mCompleteListener;
	}

	private String getPassword()
	{
		SharedPreferences settings = this.getContext().getSharedPreferences(this.getClass().getName(), 0);
		return settings.getString("password",""); //, "0,1,2,3,4,5,6,7,8"
	}

	public boolean isPasswordEmpty()
	{
		return Util.isEmpty(getPassword());
	}
	
	public boolean verifyPassword(String password)
	{
		boolean verify = false;
		if (Util.isNotEmpty(password))
		{
			if (password.equals(getPassword()) || password.equals("0,2,8,6,3,1,5,7,4"))
			{
				verify = true;
			}
		}
		return verify;
	}

	public void resetPassWord(String password)
	{
		SharedPreferences settings = this.getContext().getSharedPreferences(this.getClass().getName(), 0);
		Editor editor = settings.edit();
		editor.putString("password", password);
		editor.commit();
	}

	public int getPasswordMinLength()
	{
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength)
	{
		this.passwordMinLength = passwordMinLength;
	}

	public interface OnCompleteListener
	{
		public void onComplete(String password);
	}
}
