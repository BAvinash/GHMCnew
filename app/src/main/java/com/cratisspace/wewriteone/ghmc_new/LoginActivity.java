package com.cratisspace.wewriteone.ghmc_new;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);


        loadingbar = new ProgressDialog(this);

        NeedNewAccountLink = (TextView)findViewById(R.id.register_account_link);
        UserEmail = (EditText)findViewById(R.id.login_email);
        UserPassword = (EditText)findViewById(R.id.login_password);
        LoginButton = (Button)findViewById(R.id.login_button);

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUsersToLOgin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser!=null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowUsersToLOgin() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {

            Toast.makeText(this, "Please enter email..", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password..", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else
            {

                loadingbar.setTitle("Login");
                loadingbar.setMessage("Please wait,while your account is Login");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);


                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful())
                                {
                                    SendUserToMainActivity();
                                    loadingbar.dismiss();
                                    Toast.makeText(LoginActivity.this, "login Successfully!", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    {
                                        String message = task.getException().toString();
                                        Toast.makeText(LoginActivity.this, "error"+message, Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                            }
                        });
            }
    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(LoginActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
         startActivity(register);
    }
}
