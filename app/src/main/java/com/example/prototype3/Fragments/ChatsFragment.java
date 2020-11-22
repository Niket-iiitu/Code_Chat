package com.example.prototype3.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prototype3.Adapter.PermissionAdapter;
import com.example.prototype3.Adapter.UserAdapter;
import com.example.prototype3.Model.Chatlist;
import com.example.prototype3.Model.Friend;
import com.example.prototype3.Model.Users;
import com.example.prototype3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        
    }

    private PermissionAdapter userAdapter;
    private List<Users> mUser;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<Friend>usersList;

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//                //Looping for all users:
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
//                    usersList.add(chatlist);
//                }
//                chatList();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        reference = FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                Map<String, Object> td = (HashMap<String,Object>) snapshot.getValue();
                for(Map.Entry mapElement:td.entrySet()){
                    Friend friend = new Friend(mapElement.getKey().toString(),mapElement.getValue().toString());
                    usersList.add(friend);
                }
                userAdapter = new PermissionAdapter(getContext(),usersList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

//    private void chatList(){
//        //Getting all recent chats:
//        mUser = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("MyUsers");
//        reference.addValueEventListener(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUser.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Users user = snapshot.getValue(Users.class);
//                    for(Chatlist chatlist: usersList){
//                        if(user.getId().equals(chatlist.getId())){
//                            mUser.add(user);
//                        }
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(), mUser, true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}