package us.eiyou.magnetic_map;

import android.os.CountDownTimer;

public class TimeCount extends CountDownTimer{

	public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onTick(long millisUntilFinished) {
	}

	@Override
	public void onFinish() {
	}
	
}
