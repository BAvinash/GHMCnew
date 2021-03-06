package com.cratisspace.wewriteone.Ghmc27;

import android.content.Intent;

import androidx.annotation.NonNull;


import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private Toolbar mtoolbar;
    private CardView event_de;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, UserEvents;

    ///header

    private CircleImageView NavProfileImage;
    private ImageView img;
    private TextView NavProfileUserName, event_name, event_type;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserEvents = FirebaseDatabase.getInstance().getReference().child("usersevents");

        mtoolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_closer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        img = (ImageView) findViewById(R.id.event_display_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);

        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild("fullname")) {
                    String fullName = dataSnapshot.child("fullname").getValue().toString();
                    NavProfileUserName.setText(fullName);
                }
                if (dataSnapshot.hasChild("profileImages")) {
                    String profileImage = dataSnapshot.child("profileImages").getValue().toString();
                    Picasso.with(MainActivity.this).load(profileImage).into(NavProfileImage);

                } else {
                    Toast.makeText(MainActivity.this, "Profile not updated!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        event_de = (CardView) findViewById(R.id.event_de);

        event_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FullViewEvent.class);
                startActivity(intent);
            }
        });

        event_name = (TextView) findViewById(R.id.event_name);
        event_type = (TextView) findViewById(R.id.event_type);

        UserEvents.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild("event")) {
                    event_de.setVisibility(View.VISIBLE);
                    String fullName = dataSnapshot.child("event").getValue().toString();
                    event_name.setText(fullName);
                }
                if (dataSnapshot.hasChild("dece")) {
                    String ame = dataSnapshot.child("dece").getValue().toString();
                    event_type.setText(ame);
                }
                if (dataSnapshot.hasChild("Event Images")) {
                    String profileImage = dataSnapshot.child("Event Images").getValue().toString();
                    Picasso.with(MainActivity.this).load(profileImage).into(img);
                } else {
                    Toast.makeText(MainActivity.this, "Profile not updated!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);
                return false;
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {

        Intent setupintent = new Intent(MainActivity.this, SetupActivity.class);
        setupintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupintent);
        finish();
    }

    private void SendUserToLoginActivity() {

        Intent loginintent = new Intent(MainActivity.this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_post:

                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);


                break;

            case R.id.nav_profile:
                Intent profile = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(profile);
                Toast.makeText(this, "profile Activity", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "home Activity", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Intent intent1 = new Intent(MainActivity.this,ConnectActivity.class);
                startActivity(intent1);
                Toast.makeText(this, "Connect with local officer", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "find friends Activity", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_messages:

                Intent complaint = new Intent(MainActivity.this, ComplaintActivity.class);
                startActivity(complaint);
                Toast.makeText(this, " complaint Activity", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "settings Activity", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;


        }
    }


}
