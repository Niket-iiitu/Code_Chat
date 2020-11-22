package com.example.prototype3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationPage extends AppCompatActivity {
    String PhoneNumberVerified;

    EditText userET,passET,emailET,phoneET;
    Button registerBtn,phoneBtn;

    //----------------------------------------------------------------------------------------------Firebase Authentication
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        //------------------------------------------------------------------------------------------Initialise wedigits
        userET = findViewById(R.id.userName);
        passET = findViewById(R.id.Password);
        emailET = findViewById(R.id.Email);
        phoneET = findViewById(R.id.phoneNumber);
        registerBtn = findViewById(R.id.RegisterButton);
        phoneBtn = findViewById(R.id.RegistrationVerifyButton);

        PhoneNumberVerified="ZZ";

        //------------------------------------------------------------------------------------------Firebase
        auth=FirebaseAuth.getInstance();

        //------------------------------------------------------------------------------------------Adding event listener
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String username=userET.getText().toString();
                String phoneNumber = phoneET.getText().toString();
                String password = passET.getText().toString();
                String email = emailET.getText().toString();

                if(PhoneNumberVerified.equals("OK")){
                    phoneBtn.setTextColor(Color.BLUE);
                    if(TextUtils.isEmpty(username)||TextUtils.isEmpty(email)||TextUtils.isEmpty(phoneNumber)||TextUtils.isEmpty(password)){
                        Toast.makeText(RegistrationPage.this,"Please fill all details.",Toast.LENGTH_SHORT).show();
                    }else if(phoneNumber.length()!=10){
                        Toast.makeText(RegistrationPage.this,"Enter a correct phone number",Toast.LENGTH_SHORT).show();
                    }else{
                        RegisterNow(username,email,password,phoneNumber);
                    }
                }else if(PhoneNumberVerified.equals("ZZ")){
                    Toast.makeText(RegistrationPage.this,"Please Verify Phone Number",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegistrationPage.this,"Please Enter Correct Phone Number",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //------------------------------------------------------------------------------------------Verify Phone Number
        try {
            Intent verificationIntent = getIntent();
            PhoneNumberVerified = verificationIntent.getStringExtra("RESULT");
            userET.setText(verificationIntent.getStringExtra("NAME"));
            passET.setText(verificationIntent.getStringExtra("PASSWORD"));
            emailET.setText(verificationIntent.getStringExtra("EMAIL"));
            phoneET.setText(verificationIntent.getStringExtra("PHONE_NUMBER"));
            //Toast.makeText(RegistrationPage.this,PhoneNumberVerified,Toast.LENGTH_SHORT).show();
            Log.i("Result",PhoneNumberVerified+"-------------------------------------------------------------------");
        }catch (Exception e){
            /////
        }


        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("PHONE_NUMBER",phoneET.getText().toString());
                bundle.putString("NAME",userET.getText().toString());
                bundle.putString("PASSWORD",passET.getText().toString());
                bundle.putString("EMAIL",emailET.getText().toString());
                Intent phoneIntent = new Intent(RegistrationPage.this,PhoneNumberVerify.class).putExtras(bundle);
                startActivity(phoneIntent);
            }
        });
    }

    private void RegisterNow(final String username,String email,String password,String phoneNumber){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

                            //----------------------------------------------------------------------Adding user data
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("phoneNumber",phoneNumber);
                            hashMap.put("status","online");
                            hashMap.put("imageURL","Default");

                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegistrationPage.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegistrationPage.this,"Invalid Email or Password",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}