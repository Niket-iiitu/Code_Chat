package com.example.prototype3.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.prototype3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PermissionDialog extends AppCompatDialogFragment {

    CheckBox call,group;
    String friendCredential,result,permit;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_permit,null);
        call=view.findViewById(R.id.checkBoxCall);
        group=view.findViewById(R.id.checkBoxGroup);

        switch(permit){
            case "Null":
                call.setChecked(false);
                group.setChecked(false);
                break;
            case "Call":
                call.setChecked(true);
                group.setChecked(false);
                break;
            case "Group":
                call.setChecked(false);
                group.setChecked(true);
                break;
            case "All":
                call.setChecked(true);
                group.setChecked(true);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Change Permit")
                .setPositiveButton("Change", (dialog,which)->{
                    result="Null";
                    if(call.isChecked() && group.isChecked())  result="All";
                    if(call.isChecked() && !group.isChecked()) result="Call";
                    if(!call.isChecked() && group.isChecked()) result="Group";
                    FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid()).child(friendCredential).setValue(result);
                })
                .setNeutralButton("Cancel",null);
        return builder.create();
    }

    public void setFriendCredential(String friendCredential) {
        this.friendCredential = friendCredential;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }
}
