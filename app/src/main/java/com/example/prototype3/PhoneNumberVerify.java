package com.example.prototype3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PhoneNumberVerify extends AppCompatActivity {
    public static final int REQUEST_SMS=1;
    Intent intent;

    EditText otpField;
    Button otpVerify,otpResend;

    String PhoneNumber;
    String otpCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verify);

        otpField=findViewById(R.id.otp);
        otpVerify=findViewById(R.id.otpSubmitButton);
        otpResend=findViewById(R.id.otpResetButton);

        //------------------------------------------------------------------------------------------SMS Permission
        if(ContextCompat.checkSelfPermission(PhoneNumberVerify.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(PhoneNumberVerify.this, new String[] {Manifest.permission.SEND_SMS},REQUEST_SMS);}

        //------------------------------------------------------------------------------------------Get Phone Number
        intent = getIntent();
        PhoneNumber=intent.getStringExtra("PHONE_NUMBER");

        //------------------------------------------------------------------------------------------OTP
        generateOTP();
        sendOTP();
        otpVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });

        otpResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTP();
                sendOTP();
            }
        });
    }

    //----------------------------------------------------------------------------------------------Generate OTP
    public void generateOTP(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        otpCode=String.format("%06d", number);
    }
    
    //----------------------------------------------------------------------------------------------Send OTP
    public void sendOTP(){
//        Intent intent=new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("smato:"+PhoneNumber));
//        generateOTP();
//        intent.putExtra("sms_body","Your OTP is "+otpCode);
//        startActivity(intent);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(PhoneNumber, null, otpCode, null, null);
            Toast.makeText(getApplicationContext(), "Your Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            if(!ex.getMessage().equals("")) Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //----------------------------------------------------------------------------------------------Verify OTP
    public void verifyOTP(){
        String result;
        if(otpField.getText().toString().equals(otpCode)){
            result="OK";
        }else{
            result="NO";
        }
        Log.i("Result",otpField.getText().toString().equals(otpCode)+"-"+otpCode+"-"+otpField.getText().toString()+"----------------------------------------------");
        Bundle bundle=new Bundle();
        bundle.putString("RESULT",result); //----------------------------------------------------------Passing Result to user
        bundle.putString("NAME",intent.getStringExtra("NAME"));
        bundle.putString("PASSWORD",intent.getStringExtra("PASSWORD"));
        bundle.putString("EMAIL",intent.getStringExtra("EMAIL"));
        bundle.putString("PHONE_NUMBER",intent.getStringExtra("PHONE_NUMBER"));

        Intent Nintent=new Intent(this,RegistrationPage.class);
        Nintent.putExtras(bundle);
        startActivity(Nintent);
    }
}