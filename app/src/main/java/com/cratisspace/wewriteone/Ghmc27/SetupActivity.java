package com.cratisspace.wewriteone.Ghmc27;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText UserName,FullName;
    private TextView AreaName;
    private Button SaveInformationButton;
    private CircleImageView ProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    String currentUserId;
    private ProgressDialog loadingbar;
    String[] listItemstransport;
    boolean[] checkedItemstransport;
    ArrayList<Integer> mUserItems = new ArrayList<>();


    private int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        listItemstransport = getResources().getStringArray(R.array.Select_item);
        checkedItemstransport = new boolean[listItemstransport.length];


        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        currentUserId =mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");


        loadingbar = new ProgressDialog(this);


        UserName = (EditText)findViewById(R.id.setup_username);
        FullName = (EditText)findViewById(R.id.setup_full_name);
        AreaName = (TextView)findViewById(R.id.setup_area_name);





        ProfileImage = (CircleImageView)findViewById(R.id.setup_profile_image);
        SaveInformationButton = (Button)findViewById(R.id.setup_information_button);

        SaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();

            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent  = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,PICK_IMAGE_REQUEST);

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("profileImages")){
                        String image = dataSnapshot.child("profileImages").getValue().toString();
                        Picasso.with(SetupActivity.this).load(image).into(ProfileImage);
                    }
                    else {
                        Toast.makeText(SetupActivity.this, "please  select profile image first", Toast.LENGTH_SHORT).show();
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AreaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDilogfortranspo();
            }
        });

    }



    private void SaveAccountSetupInformation() {

        String username = UserName.getText().toString();
        String fullname = FullName.getText().toString();
        String area = (String) AreaName.getText();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        if (TextUtils.isEmpty(fullname)){
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        if (TextUtils.isEmpty(area)){
            Toast.makeText(this, "Please enter your area", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else {



            loadingbar.setTitle("Saveing Info");
            loadingbar.setMessage("Please wait,while your account is Account ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            HashMap userMap = new HashMap();

            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("area",area);
            userMap.put("status","hey i am useing GHMC");
            userMap.put("gender","none");
            userMap.put("dob","none");
            userMap.put("relationshipstatus","none");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) 
                {
                    if (task.isSuccessful())
                    {
                        loadingbar.dismiss();
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your Account is created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            loadingbar.dismiss();
                            String messge = task.getException().toString();
                            Toast.makeText(SetupActivity.this, "error"+ messge, Toast.LENGTH_SHORT).show();
                        }
                }
            });



        }



    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(SetupActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            loadingbar.setTitle("Profile image uploading");
            loadingbar.setMessage("Please wait,while your profile image  is updateing ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            final Uri uri = data.getData();


            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                final StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String image = uri.toString();

                                UsersRef.child("profileImages").setValue(image)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){

                                                    Toast.makeText(SetupActivity.this, "image stored to database", Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(SetupActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();
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
    }

    private void showDilogfortranspo() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SetupActivity.this);
        mBuilder.setTitle("Select Area");
        mBuilder.setMultiChoiceItems(listItemstransport, checkedItemstransport, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                if(isChecked){
                    mUserItems.add(position);
                }else{
                    mUserItems.remove((Integer.valueOf(position)));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                for (int i = 0; i < mUserItems.size(); i++) {
                    item = item + listItemstransport[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                AreaName.setText(item);



            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItemstransport.length; i++) {
                    checkedItemstransport[i] = false;
                    mUserItems.clear();
                    AreaName.setText("");
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }
}
