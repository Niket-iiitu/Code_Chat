package com.example.prototype3.Adapter;

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
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
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

            //Remove Friend

            return false;
        }
    }
}
