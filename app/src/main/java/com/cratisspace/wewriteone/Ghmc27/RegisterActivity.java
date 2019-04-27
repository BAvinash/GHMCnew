package com.cratisspace.wewriteone.Ghmc27;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserEmail,UserPassword,UserConformPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);

        mAuth= FirebaseAuth.getInstance();


        loadingbar = new ProgressDialog(this);

        UserEmail = (EditText)findViewById(R.id.register_email);
        UserPassword = (EditText)findViewById(R.id.register_password);
        UserConformPassword = (EditText)findViewById(R.id.register_conform_password);
        CreateAccountButton = (Button)findViewById(R.id.register_create_account);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
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

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(RegisterActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String conformpassword = UserConformPassword.getText().toString();


        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email.....", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please enter password.....", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else if(TextUtils.isEmpty(conformpassword)) {

            Toast.makeText(this, "Please enter Conform Password", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else if (!password.equals(conformpassword)){
            Toast.makeText(this, "Your password do not  match !", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else {

            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Please wait,while your account is creating");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()){

                                SendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this, "your account is created successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                Log.d("avinash",message);
                                loadingbar.dismiss();

                            }

                        }
                    });

        }
    }

    private void SendUserToSetupActivity() {
        Intent setupintent = new Intent(RegisterActivity.this,SetupActivity.class);
        setupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupintent);
        finish();
    }

}
