package com.aliyun.SecurityCenter.password;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.aliyun.SecurityCenter.R;
import com.aliyun.SecurityCenter.password.LocusPassWordView.OnCompleteListener;

public class SetPasswordActivity extends Activity
{
	private LocusPassWordView lpwv;
	private String password;
	private boolean needverify = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword_activity);
		setTitle("��������");
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				password = mPassword;
				if (needverify)
				{
					if (lpwv.verifyPassword(mPassword))
					{
						showDialog("����������ȷ,������������!");
						needverify = false;
					}
					else
					{
						showDialog("���������,����������!");
						password = "";
					}
				}
			}
		});

		OnClickListener mOnClickListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.tvSave:
					if (Util.isNotEmpty(password))
					{
						lpwv.resetPassWord(password);
						showDialog("�����޸ĳɹ�,���ס����.");
					}
					else
					{
						showDialog("���벻��Ϊ��,����������.");
					}
					break;
				case R.id.tvReset:
					lpwv.clearPassword();
					break;
				}
			}
		};
		TextView buttonSave = (TextView) this.findViewById(R.id.tvSave);
		buttonSave.setOnClickListener(mOnClickListener);
		TextView tvReset = (TextView) this.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(mOnClickListener);
		// �������Ϊ��,ֱ����������
		if (lpwv.isPasswordEmpty())
		{
			this.needverify = false;
			Toast.makeText(SetPasswordActivity.this, "����������", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	private void showDialog(String title)
	{
		Toast.makeText(SetPasswordActivity.this, title, Toast.LENGTH_SHORT).show();
	}
}
