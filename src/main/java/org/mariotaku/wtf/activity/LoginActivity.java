package org.mariotaku.wtf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bluelinelabs.logansquare.LoganSquare;

import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.TwidereConstants;
import org.mariotaku.twidere.model.ParcelableCredentials;
import org.mariotaku.twidere.model.UserKey;
import org.mariotaku.wtf.Constants;
import org.mariotaku.wtf.R;

import java.io.IOException;

/**
 * Created by mariotaku on 16/4/28.
 */
public class LoginActivity extends AppCompatActivity implements Constants, View.OnClickListener {
    private static final int REQUEST_SELECT_ACCOUNT = 101;
    private static final int REQUEST_REQUEST_PERMISSION = 102;
    private View mLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginButton.setOnClickListener(this);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mLoginButton = findViewById(R.id.login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_ACCOUNT: {
                if (resultCode == RESULT_OK) {
                    UserKey accountKey = data.getParcelableExtra(Twidere.EXTRA_ACCOUNT_KEY);
                    ParcelableCredentials credentials = Twidere.getCredentials(this, accountKey);
                    if (credentials == null) {
                        // TODO show error
                        return;
                    }
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    try {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_ACCOUNT, LoganSquare.serialize(credentials));
                        editor.apply();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } catch (IOException e) {
                        // TODO show error
                        return;
                    }
                }
                break;
            }
            case REQUEST_REQUEST_PERMISSION: {
                if (resultCode == RESULT_OK) {
                    selectAccount();
                } else {
                    browserLogin();
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void selectAccount() {
        Intent intent = new Intent(TwidereConstants.INTENT_ACTION_SELECT_ACCOUNT);
        intent.setPackage(TwidereConstants.TWIDERE_PACKAGE_NAME);
        intent.putExtra(TwidereConstants.EXTRA_SINGLE_SELECTION, true);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
        } else {
            browserLogin();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login: {
                if (Twidere.isPermissionGranted(this) == Twidere.Permission.GRANTED) {
                    selectAccount();
                } else {
                    final Intent intent = new Intent(Twidere.INTENT_ACTION_REQUEST_PERMISSIONS);
                    intent.setPackage(Twidere.TWIDERE_PACKAGE_NAME);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_REQUEST_PERMISSION);
                    } else {
                        browserLogin();
                    }
                }
                break;
            }
        }
    }

    private void browserLogin() {

    }
}
