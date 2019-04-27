package com.cratisspace.wewriteone.Ghmc27;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FullViewEvent extends AppCompatActivity {

    ImageView img;
    TextView txt1,txt2,txt3,txt4,txt5,txt6,txt7;
    private FirebaseAuth mAuth;
    private DatabaseReference  UserEvents;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view_event);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserEvents = FirebaseDatabase.getInstance().getReference().child("usersevents");

        img = (ImageView)findViewById(R.id.full_image_view);
        txt1 =  (TextView)findViewById(R.id.full_event);
        txt2 =  (TextView)findViewById(R.id.full_dece);
        txt3 =  (TextView)findViewById(R.id.full_land);
        txt4 =  (TextView)findViewById(R.id.full_loco);
        txt5 =  (TextView)findViewById(R.id.full_title);
        txt6 =  (TextView)findViewById(R.id.full_vol);
        txt7 =  (TextView)findViewById(R.id.full_date);

        UserEvents.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild("event")) {
                    String full_event = dataSnapshot.child("event").getValue().toString();
                    txt1.setText(full_event);
                }
                if (dataSnapshot.hasChild("dece")) {
                    String full_dece = dataSnapshot.child("dece").getValue().toString();
                    txt2.setText(full_dece);
                }
                if (dataSnapshot.hasChild("dece")) {
                    String full_land = dataSnapshot.child("dece").getValue().toString();
                    txt3.setText(full_land);
                }
                if (dataSnapshot.hasChild("location")) {
                    String full_loco = dataSnapshot.child("location").getValue().toString();
                    txt4.setText(full_loco);
                }
                if (dataSnapshot.hasChild("title")) {
                    String full_title = dataSnapshot.child("title").getValue().toString();
                    txt5.setText(full_title);
                }
                if (dataSnapshot.hasChild("volunter")) {
                    String full_vol = dataSnapshot.child("volunter").getValue().toString();
                    txt6.setText(full_vol);
                }
                if (dataSnapshot.hasChild("time")) {
                    String full_date = dataSnapshot.child("time").getValue().toString();
                    txt7.setText(full_date);
                }
                if (dataSnapshot.hasChild("Event Images")) {
                    String profileImage = dataSnapshot.child("Event Images").getValue().toString();
                    Picasso.with(FullViewEvent.this).load(profileImage).into(img);
                } else {
                    Toast.makeText(FullViewEvent.this, "Profile not updated!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
}
