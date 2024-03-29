package com.aliyun.SecurityCenter.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.aliyun.SecurityCenter.password.LocusPassWordView.OnCompleteListener;
import com.aliyun.SecurityCenter.R;

public class LoginActivity extends Activity
{
	private LocusPassWordView lpwv;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		setTitle("系统登陆");
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				//如果密码正确,则进入主页面。
				if (lpwv.verifyPassword(mPassword))
				{
					Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					// 打开新的Activity
					startActivity(intent);
					LoginActivity.this.finish();
				}
				else
				{
					Toast.makeText(LoginActivity.this, "密码输入错误,请重新输入", Toast.LENGTH_SHORT).show();
					lpwv.clearPassword();
				}
			}
		});

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// 如果密码为空,则进入设置密码的界面
		View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
		if (lpwv.isPasswordEmpty())
		{
			noSetPassword.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(LoginActivity.this, SetPasswordActivity.class);
					// 打开新的Activity
					startActivity(intent);
				}

			});
			noSetPassword.setVisibility(View.VISIBLE);
		}
		else
		{
			noSetPassword.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

}
