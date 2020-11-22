package com.example.prototype3.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.prototype3.Fragments.ProfileFragment;
import com.example.prototype3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordDialog extends AppCompatDialogFragment {
    private EditText id,oldPassword,newPassword;
    AuthCredential Credentials;
    FirebaseUser fuser;
    String result;
    Context toastContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        id=view.findViewById(R.id.DialogEmail);
        oldPassword=view.findViewById(R.id.DialogCurrentPassword);
        newPassword=view.findViewById(R.id.DialogNewPassword);
        builder.setView(view)
                .setTitle("Change Password")
                .setPositiveButton("Change", (dialog,which)->{
                    String NewPassword = newPassword.getText().toString();
                    Credentials= EmailAuthProvider.getCredential(id.getText().toString(),oldPassword.getText().toString());
                    //fuser = FirebaseAuth.getInstance().getCurrentUser();
                    fuser.reauthenticate(Credentials).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                                fuser.updatePassword(NewPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    result = "Password Changed Successfully";
                                                    Log.i("PasswordDialog", "Success-------------------------------------");
                                                    showToastMethod(toastContext);
                                                } else {
                                                    result = "Password not updated";
                                                    Log.i("PasswordDialog", "Process Failed---------------------------------");
                                                    showToastMethod(toastContext);
                                                }
                                            }
                                        });
                            }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            result = e.getMessage();
                            Log.i("ProgressDialog",e.getMessage());
                            showToastMethod(toastContext);
                        }
                    });
                })
                .setNegativeButton("Cancel",null);
        return builder.create();
    }

//    public String getResult(){
//
//        return result;
//    }

    public void setContext(Context context){
        toastContext=context;
    }

    public void setUser(FirebaseUser user){
        fuser=user;
    }

    public void showToastMethod(Context context) {
        Toast.makeText(context, result , Toast.LENGTH_SHORT).show();
    }
}
