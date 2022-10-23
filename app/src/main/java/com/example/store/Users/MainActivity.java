package com.example.store.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.store.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button registerButton, loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = (Button) findViewById(R.id.main_register_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey == "" && UserPasswordKey == "") {
            return;
        }

        if (TextUtils.isEmpty(UserPhoneKey) && TextUtils.isEmpty(UserPasswordKey)) {
            return;
        }

        loadingBar.setTitle("Entering account");
        loadingBar.setMessage("Please, wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        ValidateUser(UserPhoneKey, UserPasswordKey);
        loadingBar.dismiss();
    }

    private void ValidateUser(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("Users").child(phone).exists()) {
                    Toast.makeText(MainActivity.this, "Account with" + phone + "number doesn't exist", Toast.LENGTH_SHORT).show();

                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                    return;
                }

                Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);

                if (!usersData.getPhone().equals(phone)) {
                    Toast.makeText(MainActivity.this, "Phone number changed. Login again, please", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!usersData.getPassword().equals(password)) {
                    Toast.makeText(MainActivity.this, "Password changed. Login again, please", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}