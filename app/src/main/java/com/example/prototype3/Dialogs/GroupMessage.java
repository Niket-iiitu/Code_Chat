package com.example.prototype3.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupMessage extends AppCompatDialogFragment {

    String myCredential,permit;
    EditText message;
    List<String> groupMates;
    DatabaseReference database;
    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        message = new EditText(context);
        message.setHint("Enter the message");
        myCredential= FirebaseAuth.getInstance().getUid();
        database=FirebaseDatabase.getInstance().getReference().child("Chats");

        groupMates=new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference("Friends").child(myCredential).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    permit=data.getValue(String.class);
                    if(permit.equals("Group") || permit.equals("All")){
                        groupMates.add(data.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Group Message")
                .setView(message)
                .setPositiveButton("Send",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(String friend:groupMates){
                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("sender",myCredential);
                            hashMap.put("receiver",friend);
                            hashMap.put("message",message.getText().toString());
                            hashMap.put("isseen",false);
                            database.push().setValue(hashMap);
                            Toast.makeText(context, "Message Send", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNeutralButton("Cancel",null);
        return builder.create();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
