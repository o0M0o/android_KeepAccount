package wxm.KeepAccount.ui.fragment.utility;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACWelcome;
import wxm.KeepAccount.ui.acutility.ACAddUsr;

/**
 * for login
 * Created by ookoo on 2016/11/29.
 */
public class FrgLogin extends FrgUtilityBase {
    // for ui
    @BindView(R.id.login_progress)
    ProgressBar mPBLoginProgress;

    @BindView(R.id.email)
    AutoCompleteTextView mETEmail;

    @BindView(R.id.password)
    EditText mETPassword;

    @BindView(R.id.email_sign_in_button)
    Button mBTEmailSignIn;

    @BindView(R.id.email_register_button)
    Button mBTEmailRegister;

    @BindView(R.id.bt_def_usr_login)
    Button mBTDefUsrLogin;

    @BindView(R.id.ll_login)
    LinearLayout mLLLogin;

    // for res
    @BindString(R.string.error_incorrect_password)
    String  mHSErrorPassword;

    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgLogin";
        View rootView = inflater.inflate(R.layout.vw_login, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        /*
        // Set up the login form.
        //populateAutoComplete();
        mETPassword.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
        */

        mBTEmailSignIn.setOnClickListener(view1 -> attemptLogin());

        mBTEmailRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ACAddUsr.class);
            startActivityForResult(intent, 1);
        });

        mBTDefUsrLogin.setOnClickListener(v -> {
            mAuthTask = new UserLoginTask(GlobalDef.DEF_USR_NAME, GlobalDef.DEF_USR_PWD);
            mAuthTask.execute((Void) null);
        });
    }

    @Override
    protected void initUiInfo() {
    }

    /// PRIVATE BEGIN
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid mETEmail, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mETEmail.setError(null);
        mETPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mETEmail.getText().toString();
        String password = mETPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mETPassword.setError(getString(R.string.error_invalid_password));
            focusView = mETPassword;
            cancel = true;
        }

        // Check for a valid mETEmail address.
        if (TextUtils.isEmpty(email)) {
            mETEmail.setError(getString(R.string.error_field_required));
            focusView = mETEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                                .getInteger(android.R.integer.config_shortAnimTime);

        mLLLogin.setVisibility(show ? View.GONE : View.VISIBLE);
        mLLLogin.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLLLogin.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoginProgress.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
    /// PRIVATE END


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute()   {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ContextUtil.getUsrUtility().loginByUsr(mEmail, mPassword);
        }


        @Override
        protected void onPostExecute(Boolean bret) {
            super.onPostExecute(bret);

            mAuthTask = null;
            showProgress(false);

            if (bret) {
                Intent intent = new Intent(getActivity(), ACWelcome.class);
                startActivityForResult(intent, 1);
            } else {
                mETPassword.setError(mHSErrorPassword);
                mETPassword.requestFocus();
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
