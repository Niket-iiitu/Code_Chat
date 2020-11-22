package com.example.prototype3.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prototype3.Dialogs.PasswordDialog;
import com.example.prototype3.Dialogs.ProfileDataDialog;
import com.example.prototype3.Model.Users;
import com.example.prototype3.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    String UserCredentials;
    //----------------------------------------------------------------------------------------------Widigits
    TextView username,PhoneNumber,ProfileID,ChangePassword;
    ImageView imageView;

    //----------------------------------------------------------------------------------------------Firebase
    DatabaseReference reference;
    FirebaseUser fuser;

    //----------------------------------------------------------------------------------------------Profile Image
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.profile_image2);
        username = view.findViewById(R.id.UserName);
        PhoneNumber = view.findViewById(R.id.ProfilePhoneNumber);
        ProfileID = view.findViewById(R.id.ProfileID);
        ChangePassword = view.findViewById(R.id.ChangePassword);
        //------------------------------------------------------------------------------------------Profile image referance in storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                UserCredentials=user.getId();
                username.setText(user.getUsername());
                PhoneNumber.setText("Phone: "+user.getPhoneNumber());
                ProfileID.setText("ID: "+user.getId());
                if(user.getImageURL().equals("Default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getContext()).load(user.getImageURL()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        username.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UpdateUserName();
                return true;
            }
        });

        PhoneNumber.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UpdatePhoneNumber();
                return true;
            }
        });

        ChangePassword.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                OpenResetPasswordDialog();
                return true;
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                SelectImage();
                return true;
            }
        });

        return view;
    }

    private void SelectImage(){
        Intent intent = new Intent();
        intent.setType("image/*"); //Opening galery to pick up image.
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadMyImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if(imageUri!=null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtention(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imageURL",mUri);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(getContext(),"Image Uploading Failed!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else{
            Toast.makeText(getContext(),"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            if(uploadTask!=null && uploadTask.isInProgress()){
                Toast.makeText(getContext(),"Uploading in progress...",Toast.LENGTH_SHORT).show();
            }else{
                UploadMyImage();
            }
        }
    }

    public void OpenResetPasswordDialog(){
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.setUser(fuser);
        passwordDialog.setContext(getContext());
        passwordDialog.show(getActivity().getSupportFragmentManager(),"Password Reset Dialog");
        //String result=null;
        //while(result==null)  passwordDialog.getResult();
        //Log.i("ProfileFragment",result+"-----------------------------------------------------------");
        //Toast.makeText(getContext(),result,Toast.LENGTH_SHORT).show();
    }


    public  void UpdatePhoneNumber(){
        ProfileDataDialog profileDataDialog = new ProfileDataDialog();
        profileDataDialog.setContent("phone number");
        profileDataDialog.setUser(UserCredentials);
        profileDataDialog.setContext(getContext());
        profileDataDialog.show(getActivity().getSupportFragmentManager(),"User Phone Number Reset Dialog");
    }

    public  void UpdateUserName(){
        ProfileDataDialog profileDataDialog = new ProfileDataDialog();
        profileDataDialog.setContent("name");
        profileDataDialog.setUser(UserCredentials);
        profileDataDialog.setContext(getContext());
        profileDataDialog.show(getActivity().getSupportFragmentManager(),"User Name Reset Dialog");
    }
}