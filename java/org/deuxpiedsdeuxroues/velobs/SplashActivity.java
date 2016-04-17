package org.deuxpiedsdeuxroues.velobs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while(_active && (waited < _splashTime)) {
                        sleep(100);
                        if(_active) {
                            waited += 100;
                        }
                    }
                } catch(InterruptedException e) {
 
                } finally {

                    SplashActivity.this.finish();
                    Intent myIntent = new Intent(SplashActivity.this,	MainActivity.class);
                    SplashActivity.this.startActivity(myIntent);

                }

            }
        };
        splashTread.start();
    }

    protected boolean _active = true;
    protected int _splashTime = 2000;


    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }



}
