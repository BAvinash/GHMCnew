package com.cratisspace.wewriteone.Ghmc27;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

public class EventActivity extends AppCompatActivity {

    private EditText type_of_event,title_of_event,dese_of_event,volunter_event,location_event,landmark_event,time_and_date;
    private Button submit;
    private ImageView imageView;
    private DatabaseReference UsersEvent;
    private StorageReference UserEventImageRef;
    private FirebaseAuth mAuth;
    String currentUserId;
    private Toolbar mToolbar;

    private int PICK_IMAGE_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAuth =FirebaseAuth.getInstance();
        currentUserId =mAuth.getCurrentUser().getUid();
        UsersEvent = FirebaseDatabase.getInstance().getReference().child("usersevents").child(currentUserId);
        UserEventImageRef = FirebaseStorage.getInstance().getReference().child("Event Images");

        mToolbar = (Toolbar)findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Event");



        type_of_event = (EditText)findViewById(R.id.type_of_event);
        title_of_event = (EditText)findViewById(R.id.title_of_event);
        dese_of_event = (EditText)findViewById(R.id.dese_of_event);
        volunter_event = (EditText)findViewById(R.id.volunter_event);
        location_event = (EditText)findViewById(R.id.location_event);
        landmark_event = (EditText)findViewById(R.id.landmark_event);
        time_and_date = (EditText)findViewById(R.id.time_and_date);
        submit = (Button) findViewById(R.id.submit_event);
        imageView = (ImageView)findViewById(R.id.event_image);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToFirebase();
            }
        });


         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 goToGall();
             }
         });


    }

    private void goToGall() {

        Intent galleryIntent  = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST);
    }


    private void sendDataToFirebase() {

        String event = type_of_event.getText().toString();
        String title = title_of_event.getText().toString();
        String dese = dese_of_event.getText().toString();
        String volunter = volunter_event.getText().toString();
        String locaation = location_event.getText().toString();
        String landmark = landmark_event.getText().toString();
        String time = time_and_date.getText().toString();





        if (TextUtils.isEmpty(event)){

            Toast.makeText(this, "please enter Type of event", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(title)){

            Toast.makeText(this, "please enter Type of Title", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(dese)){

            Toast.makeText(this, "please enter Type of dece", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(volunter)){
            Toast.makeText(this, "please enter Type of vole", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(locaation)){
            Toast.makeText(this, "please enter Type of loca", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(landmark)){

            Toast.makeText(this, "please enter Type of landmard", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(time)){
            Toast.makeText(this, "please enter Type of time", Toast.LENGTH_SHORT).show();
        }

        else {



            HashMap hashMap = new HashMap();


            hashMap.put("event",event);
            hashMap.put("title",title);
            hashMap.put("dece",dese);
            hashMap.put("volunter",volunter);
            hashMap.put("location",landmark);
            hashMap.put("time",time);
            hashMap.put("points","null");

            UsersEvent.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {

                        Intent intent = new Intent(EventActivity.this,MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(EventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        String messge = task.getException().toString();
                        Toast.makeText(EventActivity.this, "error"+ messge, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }













    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id  ==  android.R.id.home){
            sendUserToMainActivity();
        }


        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent loginintent = new Intent(EventActivity.this,MainActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            final Uri uri = data.getData();


            try {
                final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                final StorageReference filepath = UserEventImageRef.child(currentUserId + ".jpg");
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String image = uri.toString();
                                imageView.setImageBitmap(bitmap);

                                UsersEvent.child("Event Images").setValue(image)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){

                                                    Toast.makeText(EventActivity.this, "image stored to database", Toast.LENGTH_SHORT).show();

                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(EventActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();

                                                }
                                            }


                                        });




                            }
                        });
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
