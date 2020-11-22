package com.example.prototype3.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prototype3.MessageActivity;
import com.example.prototype3.Model.Friend;
import com.example.prototype3.Model.Users;
import com.example.prototype3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.ViewHolder>{
    private Context context;
    private List<Friend> mFriends;

    private String friendCredential,friendPermission;
    private String friendName,friendImage;

    public PermissionAdapter(Context context,List<Friend> mFriends){
        this.mFriends=mFriends;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.user_permit, parent, false);
            return new PermissionAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.no_friend, parent, false);
            return new NoFriend(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionAdapter.ViewHolder holder, int position) {
        Friend friend = mFriends.get(position);
        friendCredential=friend.getFriendCredential();
        friendPermission=friend.getPermissions();
        Log.i("PermissionAdapter",friendCredential+"---------------------------------------");


        FirebaseDatabase.getInstance().getReference("MyUsers").child(friendCredential).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                friendName = user.getUsername();
                holder.friedName.setText(friendName);
                friendImage = user.getImageURL();
                if (friendImage.equals("Default")) {
                    holder.friendPic.setImageResource(R.mipmap.ic_launcher_round);
                } else {
                    Glide.with(context).load(friendImage).into(holder.friendPic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        switch(friendPermission){
            case "Null":
                break;
            case "Call":
                holder.callBtn.setImageResource(R.drawable.ic_receieve);
                break;
            case "Group":
                holder.groupBtn.setImageResource(R.drawable.ic_group);
                break;
            case "All":
                holder.callBtn.setImageResource(R.drawable.ic_receieve);
                holder.groupBtn.setImageResource(R.drawable.ic_group);
                break;
            default:
                Toast.makeText(context,"Error: Incorrect permission code",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mFriends.size() > 0) return 1;
        else return 0;
    }

    public Context getContext() {
        return context;
    }

    public class NoFriend extends ViewHolder {
        public NoFriend(View itemView){
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView friedName;
        ImageView friendPic,groupBtn,callBtn;

        public ViewHolder (@NonNull View itemView){
            super(itemView);

            friedName = itemView.findViewById(R.id.friendname);
            friendPic = itemView.findViewById(R.id.frienpic);
            groupBtn = itemView.findViewById(R.id.GroupFriend);
            callBtn = itemView.findViewById(R.id.CallFriend);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Friend fri = mFriends.get(getAdapterPosition());

        }


    }

}

/*
groupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(friendPermission){
                        case "Null":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Group");
                            break;
                        case "Call":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("All");
                            break;
                        case "Group":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Null");
                            break;
                        case "All":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Call");
                            break;
                        default:
                            Toast.makeText(context,"Error: Incorrect permission code",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(friendPermission){
                        case "Null":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Call");
                            break;
                        case "Call":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Null");
                            break;
                        case "Group":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("All");
                            break;
                        case "All":
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue("Group");
                            break;
                        default:
                            Toast.makeText(context,"Error: Incorrect permission code",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).getRef().removeValue();
                }
            });
 */
