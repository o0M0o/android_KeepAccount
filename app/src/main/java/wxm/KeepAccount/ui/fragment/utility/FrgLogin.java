package wxm.KeepAccount.ui.fragment.utility;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import cn.wxm.andriodutillib.util.WRMsgHandler;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.data.UsrItem;
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

    // for msg handler
    private LocalMsgHandler mMHHandler;

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
        mMHHandler = new LocalMsgHandler(this);

        // Set up the login form.
        //populateAutoComplete();
        mETPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBTEmailSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mBTEmailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewAccount();
            }
        });

        mBTDefUsrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(UsrItem.FIELD_NAME, GlobalDef.DEF_USR_NAME);
                data.putExtra(UsrItem.FIELD_PWD, GlobalDef.DEF_USR_PWD);

                Message m = Message.obtain(ContextUtil.getMsgHandler(),
                        GlobalDef.MSG_USR_LOGIN);
                m.obj = new Object[]{data, mMHHandler};
                m.sendToTarget();
            }
        });
    }

    @Override
    protected void initUiInfo() {

    }

    /// PRIVATE BEGIN
    /**
     * 注册新帐户
     */
    private void registerNewAccount() {
        Intent intent = new Intent(getActivity(), ACAddUsr.class);
        startActivityForResult(intent, 1);
    }

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
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }


    /**
     * 切换到工作主activity
     */
    private void SwitchToWorkActivity() {
        Intent intent = new Intent(getActivity(), ACWelcome.class);
        startActivityForResult(intent, 1);
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


    /**
     * 执行完登陆动作后辅助函数
     *
     * @param loginret 登陆结果
     */
    private void afterLoginExecute(final boolean loginret) {
        mAuthTask = null;
        showProgress(false);

        if (loginret) {
            SwitchToWorkActivity();
        } else {
            mETPassword.setError(mHSErrorPassword);
            mETPassword.requestFocus();
        }
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
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }

            Intent data = new Intent();
            data.putExtra(UsrItem.FIELD_NAME, mEmail);
            data.putExtra(UsrItem.FIELD_PWD, mPassword);

            Message m = Message.obtain(ContextUtil.getMsgHandler(),
                    GlobalDef.MSG_USR_LOGIN);
            m.obj = new Object[]{data, mMHHandler};
            m.sendToTarget();
            return true;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }


    private static class LocalMsgHandler extends WRMsgHandler<FrgLogin> {
        LocalMsgHandler(FrgLogin ac) {
            super(ac);
            TAG = "LocalMsgHandler";
        }

        @Override
        protected void processMsg(Message m, FrgLogin home) {
            switch (m.what) {
                case GlobalDef.MSG_REPLY: {
                    switch (m.arg1) {
                        case GlobalDef.MSG_USR_LOGIN: {
                            boolean ret = UtilFun.cast(m.obj);
                            home.afterLoginExecute(ret);
                        }
                        break;

                        default:
                            Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
                            break;
                    }
                }
                break;

                default:
                    Log.e(TAG, String.format("msg(%s) can not process", m.toString()));
                    break;
            }
        }
    }
}
