package com.example.prototype3.Adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context context;
    private List<Users> mUsers;
    private boolean isChat;

    public UserAdapter(Context context, List<Users> mUsers,boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
            return new UserAdapter.ViewHolder(view);
        }else{
        View view = LayoutInflater.from(context).inflate(R.layout.no_friend, parent, false);
        return new NoFriend(view);
    }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("Default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(user.getImageURL()).into(holder.imageView);
        }

        if(isChat){
            if(user.getStatus().equals("online")){
                holder.status.setText("(Online)");
            }else{
                holder.status.setText("(Offline)");
            }
        }else{
            holder.status.setText("(Offline)");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mUsers.size() > 0) return 1;
        else return 0;
    }

    public class NoFriend extends UserAdapter.ViewHolder {
        public NoFriend(View itemView){
            super(itemView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView username,status;
        public ImageView imageView;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.textViewItem);
            imageView = itemView.findViewById(R.id.imageViewItem);
            status = itemView.findViewById(R.id.textViewStatus);

            itemView.setOnLongClickListener(this);
        }
        @Override
        public boolean onLongClick(View v){

            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Remove Friend?")
                    .setMessage("Do you want to remove "+ mUsers.get(getAdapterPosition()).getUsername()+"?")
                    .setPositiveButton("Remove",(dialog,which)->{
                        FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(mUsers.get(getAdapterPosition()).getId()).setValue(null);
                    })
                    .setNegativeButton("Cancel",null)
                    .create();
            builder.show();
            return true;
        }
    }
}
