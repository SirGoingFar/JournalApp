package com.eemf.sirgoingfar.journalapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eemf.sirgoingfar.journalapp.R;
import com.eemf.sirgoingfar.journalapp.utils.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity{

    private UserLoginTask mAuthTask = null;
    private FirebaseAuth mAuth;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private TextView mOnboardingSwitcher;

    private boolean isRegistration;
    private int MIN_PASSWORD_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.et_email);
        mPasswordView = (EditText) findViewById(R.id.et_password);
        mEmailSignInButton = findViewById(R.id.btn_sign_in_or_register);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRegistration)
                    attemptSignUp();
                else
                    attemptSignIn();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mOnboardingSwitcher = findViewById(R.id.tv_sign_in_or_register);
        mOnboardingSwitcher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegistration = !isRegistration;
                setupScreen();
            }
        });

        //instantiate the FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        isRegistration = false;
    }

    private void setupScreen() {
        //change view contents - Registration
        if(isRegistration) {
            mEmailSignInButton.setText(getString(R.string.sign_up));
            mOnboardingSwitcher.setText(R.string.or_sign_in);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(R.string.sign_up);
        }
        //change view contents - Sign in
        else {
            mEmailSignInButton.setText(getString(R.string.sign_in));
            mOnboardingSwitcher.setText(R.string.or_sign_up);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setTitle(R.string.sign_in);
        }
    }

    /**
     * Attempts to sign into the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignIn() {
        //implement for sign in
    }

    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        //check if there's an internet connection
        if(!NetworkUtils.isOnline(this)){
            Snackbar.make(mPasswordView, R.string.poor_connectivitiy, Snackbar.LENGTH_LONG).show();
            return;
        }
        //check if there's an incomplete task for signing in
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        if(isRegistration)
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        else
            return true;
    }

    private boolean isPasswordValid(String password) {
        if(isRegistration)
        return password.length() >= MIN_PASSWORD_LENGTH;
        else
            //this is for signing in - expand
            return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private String mResponse = null;
        private boolean isSuccessful;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mAuth.createUserWithEmailAndPassword(this.mEmail, this.mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.getException().getMessage() != null)
                    mResponse = task.getException().getMessage();

                    if(task.isSuccessful())
                        isSuccessful = true;
                    else
                        isSuccessful = false;
                }
            });
            return isSuccessful;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //navigate to the Catalog Activity
                Intent intent = new Intent(LoginActivity.this, CatalogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Snackbar.make(mPasswordView,mResponse.concat(getString(R.string.pls_retry)),Snackbar.LENGTH_LONG);
                mLoginFormView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * on @value KEYCODE_BACK is pressed, @link onKeyDown(keyCode, event) is called
     * @param keyCode is the key code for the key pressed
     * @param event is the key event
     * @return true or @return the event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent minimize = new Intent(Intent.ACTION_MAIN);
                minimize.addCategory(Intent.CATEGORY_HOME);
                minimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(minimize);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

