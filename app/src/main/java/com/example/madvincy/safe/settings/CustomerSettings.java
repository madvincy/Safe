package com.example.madvincy.safe.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.MainActivity;
import com.example.madvincy.safe.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.paypal.android.sdk.payments.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettings extends AppCompatActivity {
    private TextView fullName,mNatId,PhoneNo,mEmail;
    private String mName,mNatid,mphoneno,mEmaile, userID ;
    private String mProfileImageUrl;
    private ImageView mProfileImage,mProfileImagee;
    private FirebaseAuth mAuth;
    private DatabaseReference mCusDatabase;
    private ImageButton editProfile;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerr_settings);
        fullName= (TextView) findViewById(R.id.txtFullName);
        mNatId= (TextView) findViewById(R.id.id);
        PhoneNo= (TextView) findViewById(R.id.Phone);
        mEmail= (TextView) findViewById(R.id.Email);
        editProfile = (ImageButton) findViewById(R.id.btnEditProfile);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProfileImagee = (ImageView) findViewById(R.id.btnEditProfilePicture);



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("user info");
        mProfileImagee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                saveUserInformation();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), editUser.class);
                startActivity(intent);
            }
        });


        getUserInfo();
    }




    private void getUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String emailed = user.getEmail();
            mEmail.setText(emailed );



        mCusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("full names")!=null){
                        mName = map.get("full names").toString();
                        fullName.setText("Full Name: " +mName);

                    }
                    if(map.get("national id_number")!=null){
                        mNatid= map.get("national id_number").toString();
                        mNatId.setText("Nat Id: " + mNatid);
                    }
                    if(map.get("phone number")!=null){
                        mphoneno = map.get("phone number").toString();
                        PhoneNo.setText("Phone No: " +mphoneno);

                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void saveUserInformation() {


        if(resultUri != null) {

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("user profile_images").child(userID);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mCusDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }

    }
}
