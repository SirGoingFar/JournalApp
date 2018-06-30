package com.eemf.sirgoingfar.journalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.database.AppExecutors;
import com.eemf.sirgoingfar.journalapp.service.RetrieveUserDataService;
import com.eemf.sirgoingfar.journalapp.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    private ImageView andelaLogo;
    private ImageView googleLogo;
    private ImageView udacityLogo;

    private Intent intent;
    public static String ACTION_LOGIN = "login";

    //Firebase Authentication object
    private FirebaseAuth mAuth;

    public static final String SERVICE_ACTION = "start_catalog_activity";
    public static final String USER_ID = "user_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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

                            if(mAuth.getCurrentUser() == null)
                            startActivity(intent);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
        });

        //instantiate Firebase objects
        mAuth = FirebaseAuth.getInstance();
    }

    private void fetchUserDocumentFromFirebase(String userId) {
        if(!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, getString(R.string.poor_connectivitiy),Toast.LENGTH_LONG).show();
            return;
        }

        //Todo: Fetch user data and populate the db using Service
        Intent intent = new Intent(this, RetrieveUserDataService.class);
        intent.setAction(SERVICE_ACTION);
        intent.putExtra(USER_ID, userId);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(currentUser == null)
                            //no user is logged in
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        else
                            //a user is currently logged in
                            fetchUserDocumentFromFirebase(currentUser.getUid());
                    }
                });
            }
        },5000);
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
