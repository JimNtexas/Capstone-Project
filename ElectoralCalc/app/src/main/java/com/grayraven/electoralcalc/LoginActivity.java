package com.grayraven.electoralcalc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A login screen that offers login via email/password or Google Oauth.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Login";
    private FirebaseAuth mAuth;
    private final int mMinPasswordLength = 6;
    private GoogleApiClient mGoogleApiClient = null;
    private static final int RC_SIGN_IN = 9001; //Magic number for Google sign in

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        // Set up the login form
        // todo: implement auto complete
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mProgress = ProgressDialog.show(LoginActivity.this, "",
                getString(R.string.wait_for_login), true);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                mProgress.dismiss();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    gotoMainActivity(user);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //configure google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Google sign in button
        SignInButton btnGoogle = (SignInButton)findViewById(R.id.login_with_google);
        btnGoogle.setSize(SignInButton.SIZE_WIDE);
        btnGoogle.setScopes(gso.getScopeArray());
        btnGoogle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignIn();
            }
        });

        boolean signout = getIntent().getBooleanExtra(MainActivity.SIGN_OUT, false);
        Log.d(TAG, String.valueOf(signout));
        if(signout) {
            if(mAuth != null) {
                mAuth.signOut();
            }
            googleSignOut();
        }

    } // end onCreate


    private void gotoMainActivity(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // firebase password reset
    @OnClick(R.id.pw_reset_btn)
    protected void requestPwReset() {
        String email = mEmailView.getText().toString();
        if(!isEmailValid(email)) {
            showDismissableSnackbar(getString(R.string.new_user_enter_email), false);
            return;
        }
        Log.d(TAG, "sending pw reset request for: " + email);

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "Reset message success");
                        showDismissableSnackbar(getApplicationContext().getString(R.string.pw_reset_snack_msg), true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showDismissableSnackbar(getApplicationContext().getString(R.string.pw_reset_fail_msg), true);
                Log.d(TAG, "Reset message success");
            }

        });
    }


    private void showDismissableSnackbar(String msg, boolean indef) {
        final Snackbar bar = Snackbar.make(findViewById(R.id.login_activity), msg, indef ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG);
        bar.setAction(R.string.dismiss, new OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    // create new firebase user
    //TODO:  implement email verification -  http://andreasmcdermott.com/web/2014/02/05/Email-verification-with-Firebase/
    //Note:  Feature is pending from Firebase: https://console.firebase.google.com/project/project-57952108922096486/authentication/emails
    @OnClick(R.id.new_user_button)
    protected void createNewUser() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!isEmailValid(email)) {
            showDismissableSnackbar(getString(R.string.new_user_enter_email), false);
            mEmailView.requestFocus();
            return;
        }
        if(!isPasswordValid(password)){
            String errorFormat = this.getString(R.string.error_invalid_password_fmt);
            String errorMsg = String.format(errorFormat, mMinPasswordLength);
            showDismissableSnackbar(errorMsg, false);
            return;

        }

        if(mAuth != null) {
            //Firebase
            mAuth.signOut();
        }

        mProgress.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            showDismissableSnackbar(getString(R.string.user_creation_success), false);
                        } else {
                            mProgress.dismiss();
                            showDismissableSnackbar(getString(R.string.user_creation_failed), false);
                        }

                        // ...
                    }
                });
    }

    private void googleSignOut() {
        if(mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "Google logout status: " + status.getStatusMessage());
                        }
                    });
        }
    }

    @OnClick(R.id.btn_revoke_google)
    protected void revokeGoogleAccess() {
        mProgress.setTitle(getString(R.string.revoke_google));
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "Google revoke status: " + status.isSuccess());
                        mProgress.dismiss();
                        if(status.isSuccess()) {
                            showDismissableSnackbar(getString(R.string.revocation_success), false);
                        } else {
                            showDismissableSnackbar(getString(R.string.revocation_failure), false);
                        }
                    }
                });
    }

    @OnClick(R.id.email_sign_in_button)
    protected void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            String errorFormat = this.getString(R.string.error_invalid_password_fmt);
            String errorMsg = String.format(errorFormat, mMinPasswordLength);
            mPasswordView.setError(errorMsg);
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
            // Firebase email login
            mProgress.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                                mProgress.dismiss();
                                showDismissableSnackbar("Unable to log in with these credentials", false);
                            }
                        }
                    });
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isEmailValid(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isPasswordValid(String password) {
        return password.length() > mMinPasswordLength;
    }

    private void GoogleSignIn() {

        mProgress.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                mProgress.dismiss();
                Log.e(TAG, "google sign in failed in onActivityResult");
                showDismissableSnackbar(getString(R.string.unable_google), false);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

