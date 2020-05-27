package com.example.vmac.WatBot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class Register extends AppCompatActivity {
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    EditText etName,etMail,etPassword,etReEnter,etnumber;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        etName=findViewById(R.id.etName);
        etMail=findViewById(R.id.etMail);
        etPassword=findViewById(R.id.etPassword);
        etnumber = findViewById(R.id.etnumber);
        etReEnter=findViewById(R.id.etReEnter);
        btnRegister=findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().trim().isEmpty() ||etMail.getText().toString().trim().isEmpty() || etPassword.getText().toString().trim().isEmpty()
                        ||etnumber.getText().toString().trim().isEmpty() || etReEnter.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(Register.this,"Please Enter All Fields",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(etnumber.getText().toString().trim().length()!=10)
                    {
                        Toast.makeText(Register.this,"please enter a valid mobile number!",Toast.LENGTH_SHORT).show();
                    }
                    if(etPassword.getText().toString().trim().equals(etReEnter.getText().toString().trim()))
                    {
                        showProgress(true);
                        String name=etName.getText().toString().trim();
                        String mail=etMail.getText().toString().trim();
                        String password=etPassword.getText().toString().trim();
                        String number = etnumber.getText().toString().trim();
                        BackendlessUser user=new BackendlessUser();
                        user.setEmail(mail);
                        user.setPassword(password);
                        user.setProperty("name",name);
                        user.setProperty("phoneNumber",number);
                        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser response) {
                                ApplicationClass.user= response;
                                showProgress(false);
                                Toast.makeText(Register.this,"Registration Successfull",Toast.LENGTH_LONG).show();
                                Register.this.finish();
                            }
                            @Override
                            public void handleFault(BackendlessFault fault) {
                                showProgress(false);
                                Toast.makeText(Register.this,"Error" +fault.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(Register.this,"Please Enter passwords Correctly!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
        }
    }
}

