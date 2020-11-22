package com.example.prototype3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    //----------------------------------------------------------------------------------------------Widgets
    EditText userID,loginPassword;
    TextView forgotPassword;
    Button loginButton,registerButton;

    //----------------------------------------------------------------------------------------------Firebase
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //------------------------------------------------------------------------------------------Checking for current user
        if(firebaseUser!=null){
            Intent intent = new Intent(LoginPage.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //------------------------------------------------------------------------------------------Initialise widgets
        userID=findViewById(R.id.Username);
        loginPassword=findViewById(R.id.UserPassword);
        loginButton=findViewById(R.id.LoginButton);
        registerButton=findViewById(R.id.UserRegister);
        forgotPassword=findViewById(R.id.LoginForgotPassword);

        //------------------------------------------------------------------------------------------Firebase Auth
        auth=FirebaseAuth.getInstance();

        //------------------------------------------------------------------------------------------Login Button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=userID.getText().toString();
                String pass=loginPassword.getText().toString();

                //Checking if fields are empty
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                    Toast.makeText(LoginPage.this,"Please Fill All Fields",Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(LoginPage.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Toast.makeText(LoginPage.this,"Login Failed!!!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //------------------------------------------------------------------------------------------Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this,RegistrationPage.class);
                startActivity(intent);
                finish();
            }
        });

        //------------------------------------------------------------------------------------------Forgot Password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
                     EditText emailForgot = new EditText(LoginPage.this);
                     emailForgot.setHint("email@gmail.com");
                     builder.setTitle("Get Password Reset Email")
                             .setView(emailForgot)
                             .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int which) {
                                     auth.sendPasswordResetEmail(emailForgot.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                            Toast.makeText(LoginPage.this,"Email send successfully",Toast.LENGTH_SHORT).show();
                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                         @Override
                                         public void onFailure(@NonNull Exception e) {
                                             Toast.makeText(LoginPage.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                         }
                                     });
                                 }
                             })
                             .setNeutralButton("Cancel",null)
                             .show();
            }
        });
    }
}