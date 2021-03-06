package cabsapp.intern.cyberteam.cabsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private TextView signUp;
    private ProgressDialog mProgressDialog;
    //private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    private final String KEY_NAME = "name";
    private final String KEY_USERNAME = "username";
    private final String KEY_PHONE = "phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toast.makeText(LoginActivity.this, "Login to proceed!", Toast.LENGTH_SHORT).show();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        inputEmail = (EditText) findViewById(R.id.email_edittext);////////////////////////////////////////////////////
        inputPassword = (EditText) findViewById(R.id.password_edittext);//////////////////////////////////////////////
        signUp = (TextView) findViewById(R.id.sign_up);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.login_button);
        //btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginAction = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(loginAction);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);
                mProgressDialog.setMessage("Checking Login...");
                mProgressDialog.show();

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                //progressBar.setVisibility(View.GONE);
                                mProgressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Logged in Successfully!", Toast.LENGTH_LONG).show();
                                    getUserProfile();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void getUserProfile() {
        String name = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_NAME, null);
        String username = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_USERNAME, null);
        String phone = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(KEY_PHONE, null);

        Toast.makeText(this, "Name : " + name +
                        "\nUsername : " + username +
                        "\nPhone Number : " + phone,
                Toast.LENGTH_SHORT).show();
    }
}
