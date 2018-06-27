package com.eemf.sirgoingfar.journalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.eemf.sirgoingfar.journalapp.R;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    //    @BindView(R.id.iv_andela_logo)
    private ImageView andelaLogo;

    //    @BindView(R.id.iv_google_logo)
    private ImageView googleLogo;

    //    @BindView(R.id.iv_udacity_logo)
    private ImageView udacityLogo;
    private Intent intent;
    public static String ACTION_LOGIN = "login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        ButterKnife.bind(this);

        andelaLogo = findViewById(R.id.iv_andela_logo);
        googleLogo = findViewById(R.id.iv_google_logo);
        udacityLogo = findViewById(R.id.iv_udacity_logo);

        //set the visibility of the logos to GONE for animation effect
        andelaLogo.setVisibility(View.GONE);
        googleLogo.setVisibility(View.GONE);
        udacityLogo.setVisibility(View.GONE);

        //initialize login activity intent (intent declaration will change based on mAuth state)
        intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, ACTION_LOGIN);

        //perform animation
        handlerExecutor(new Thread[]{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                andelaLogo.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }),
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    googleLogo.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }),
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    udacityLogo.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }),
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(2000);
                            //start the login activity after the last thread execution
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
        });
    }

    private void handlerExecutor(final Thread[] tasks){
        Handler animationHandler = new Handler();

        for(int counter = 0; counter < tasks.length; counter++) {
            final int finalCounter = counter;
            animationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tasks[finalCounter].start();
                }
            }, 3000);
        }
    }
}
