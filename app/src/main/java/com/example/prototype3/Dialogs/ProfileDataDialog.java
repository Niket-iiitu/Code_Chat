package com.example.prototype3.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileDataDialog extends AppCompatDialogFragment {
    String Content;
    String user;
    EditText userContent;
    DatabaseReference databaseInstance;
    Context toastContext;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        userContent = new EditText(getActivity()); ///
        if(Content=="name") userContent.setHint("New User Name");
        else userContent.setHint("New Phone Number");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Change "+Content)
                .setView(userContent)
                .setPositiveButton("Change",(dialog,which)->{
                    if(Content=="name"){ //---------------------------------------------------------Change username
                        databaseInstance=FirebaseDatabase.getInstance().getReference("MyUsers").child(user).child("username");
                    }else{ //-----------------------------------------------------------------------Change phone number
                        databaseInstance=FirebaseDatabase.getInstance().getReference("MyUsers").child(user).child("phoneNumber");
                    }
                    databaseInstance.setValue(userContent.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                showToastMethod("Changes made Successfully",toastContext);
                            }else{
                                showToastMethod("Failed to conduct changes.",toastContext);
                            }
                        }
                    });
                })
                .setNeutralButton("Cancel",null);

        return builder.create();
    }

    public void setContent(String content){
        Content=content;
    }

    public void setUser(String userid){
        user=userid;
    }

    public void setContext(Context context){
        toastContext=context;
    }


    public void showToastMethod(String result,Context context) {
        Toast.makeText(context, result , Toast.LENGTH_SHORT).show();
    }
}
