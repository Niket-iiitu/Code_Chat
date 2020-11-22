package com.example.prototype3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.prototype3.Adapter.MessageAdapter;
import com.example.prototype3.Model.Chat;
import com.example.prototype3.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    public static final int REQUEST_CALL=1;
    //----------------------------------------------------------------------------------------------Widgets
    TextView username;
    ImageView imageView;
    RecyclerView recyclerViewy;
    EditText msg_editText;
    ImageButton sendBtn;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    String userid;
    String phoneNumber;
    String MyImage;

    ValueEventListener seenListener;

    //----------------------------------------------------------------------------------------------Firebase
    FirebaseUser fuser;
    DatabaseReference reference,userRef;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //------------------------------------------------------------------------------------------Remove action Bar
        //getActionBar().hide();
        /*
        android:name=".MessageActivity"
        to
        android:name=".MessageActivity" android:theme="@style/Theme.Prototype3.NoActionBar"

        and

        created style.xml
         */

        //------------------------------------------------------------------------------------------Initialise Widgets
        imageView=findViewById(R.id.imageView);
        username=findViewById(R.id.textViewMessege);
        sendBtn=findViewById(R.id.btn_send);
        msg_editText=findViewById(R.id.text_send);
        recyclerViewy =findViewById(R.id.recycler_view);
        
        //------------------------------------------------------------------------------------------Recycler View
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //------------------------------------------------------------------------------------------ToolBar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        intent=getIntent();
        userid = intent.getStringExtra("userid");

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        userRef.addValueEventListener(new ValueEventListener() { //////////
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users me = snapshot.getValue(Users.class);
                if(me.getImageURL().equals("Default")){
                    MyImage="Default";
                }else{
                    MyImage=me.getImageURL();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        reference= FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                phoneNumber = user.getPhoneNumber();
                username.setText(" " +user.getUsername());
                if(user.getImageURL().equals("Default")){
                    imageView.setImageResource(android.R.drawable.sym_def_app_icon);
                }else{
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(imageView);
                }
                readMessages(fuser.getUid(),userid,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_SHORT).show(); //////
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String msg=msg_editText.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(),userid,msg);
                }else{
                    Toast.makeText(MessageActivity.this,"Message is Empty",Toast.LENGTH_SHORT).show();
                }
                msg_editText.setText("");
            }
        });
        SeenMessage(userid);
        //SeenMessage(fuser.getUid());
        username.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });


    }

    private void SeenMessage(String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                        Log.i("MessageActivity","Read------------------------------------------");
                    }else{
                        Log.i("MessageActivity","NotRead------------------------------------------");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //----------------------------------------------------------------------------------------------Sending Message to Firebase
    private void sendMessage(String sender,String receiver,String  message){
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        reference.child("Chats").push().setValue(hashMap);

        //------------------------------------------------------------------------------------------Adding User to chat fragment: Latest chat with contacts
        final DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid()).child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(!datasnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readMessages(String myid,String userid,String imageUrl){
        mChat=new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if((chat.getReceiver().equals(myid) && chat.getSender().equals(userid)) || (chat.getReceiver().equals(userid) && chat.getSender().equals(myid))){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageUrl,MyImage);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CheckStatus(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        CheckStatus("offline");
    }

    //----------------------------------------------------------------------------------------------Phone Call
    private void makePhoneCall(){
        if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MessageActivity.this, new String[] {Manifest.permission.CALL_PHONE},REQUEST_CALL);
            makePhoneCall();
        }else{
            new AlertDialog.Builder(MessageActivity.this)
                    .setTitle("Phone Call")
                    .setMessage("Do you want to call "+ username.getText().toString()+"?")
                    .setPositiveButton("Yes", (dialog,which)->{startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumber)));})
                    .setNegativeButton("No",null)
                    .show();
        }
    }
}