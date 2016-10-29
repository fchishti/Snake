package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.postDelayed(mUpdateTimeTask, 2000);

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
