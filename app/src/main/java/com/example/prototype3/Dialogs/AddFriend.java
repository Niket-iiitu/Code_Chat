package com.example.prototype3.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.example.prototype3.MessageActivity;
import com.example.prototype3.Model.Users;
import com.example.prototype3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

public class AddFriend extends AppCompatDialogFragment {
    EditText friendCredential;
    ImageView friendImage;
    TextView friendName;
    String MyCredential;
    Context toastContext;

    DatabaseReference GroupReference;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View Fview = inflater.inflate(R.layout.add_friend_dialog,null);
        friendCredential= Fview.findViewById(R.id.AddFrenfCredential);
        friendImage=Fview.findViewById(R.id.ViewFriendImage);
        friendName=Fview.findViewById(R.id.ViewFrienCredential);
        MyCredential= FirebaseAuth.getInstance().getCurrentUser().getUid();



        friendCredential.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                DatabaseReference UserReference = FirebaseDatabase.getInstance().getReference("MyUsers");
                try{
                    UserReference.child(friendCredential.getText().toString()).addValueEventListener(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()!=null){
                                Users friend = snapshot.getValue(Users.class);
                                friendName.setText(friend.getUsername());

                                if (friend.getImageURL().equals("Default")) {
                                    friendImage.setImageResource(android.R.drawable.sym_def_app_icon);
                                } else {
                                    Glide.with(getActivity()).load(friend.getImageURL()).into(friendImage);
                                }
                            }else{

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            friendImage.setImageResource(android.R.drawable.sym_def_app_icon);
                            friendName.setText("Friend");
                        }
                    });


                }catch(Exception e){
                }
            }
        });

        final AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add Friend")
                .setView(Fview)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String FriendCredential = friendCredential.getText().toString();
                        //HashMap<String,String> friend = new HashMap<>();
                        //friend.put(FriendCredential,"Null");
                        FirebaseDatabase.getInstance().getReference().child("Friends").child(MyCredential).child(FriendCredential).setValue("Null");
                        Log.i("AddFriend",FriendCredential+"-------------------------------------------------------------------");
                        Toast.makeText(toastContext,"Friend Added",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",null);
        return builder.create();
    }

    public void setFriendContext(Context context){
        toastContext=context;
    }
}
