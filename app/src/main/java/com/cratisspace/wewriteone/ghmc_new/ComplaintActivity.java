package com.cratisspace.wewriteone.ghmc_new;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ComplaintActivity extends AppCompatActivity {

    private EditText category,Location,landmark;
    private Button submit;
    private ImageView imageView;
    private DatabaseReference UsersComplaint;
    private FirebaseAuth mAuth;
    String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        mAuth =FirebaseAuth.getInstance();
        currentUserId =mAuth.getCurrentUser().getUid();
        UsersComplaint = FirebaseDatabase.getInstance().getReference().child("userscomplaints").child(currentUserId);

        category = (EditText)findViewById(R.id.category);

        Location = (EditText)findViewById(R.id.location_event);

        landmark = (EditText)findViewById(R.id.landmark);



        submit = (Button) findViewById(R.id.submit_event);

        imageView = (ImageView)findViewById(R.id.event_image);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToFirebase();
            }
        });





    }

    private void sendDataToFirebase() {

        String event = category.getText().toString();
        String title = Location.getText().toString();
        String dese = landmark.getText().toString();




        if (TextUtils.isEmpty(event)){

            Toast.makeText(this, "please enter Type of event", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(title)){

            Toast.makeText(this, "please enter Type of Title", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(dese)){

            Toast.makeText(this, "please enter Type of dece", Toast.LENGTH_SHORT).show();
        }


        else {



            HashMap hashMap = new HashMap();


            hashMap.put("category",event);
            hashMap.put("location",title);
            hashMap.put("landmark",dese);


            UsersComplaint.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {

                        Intent intent = new Intent(ComplaintActivity.this,MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(ComplaintActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        String messge = task.getException().toString();
                        Toast.makeText(ComplaintActivity.this, "error"+ messge, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }













    }
}
