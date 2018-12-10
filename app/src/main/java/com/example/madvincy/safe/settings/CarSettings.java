package com.example.madvincy.safe.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CarSettings extends AppCompatActivity {
    private TextView mMakeField,mColorField,mRegnoField,mModelField,fullName;
    private String mColor,mMake,mModel,mRegno;
    private String mProfileImageUrl,userID;
    private ImageView mProfileImage,mProfileImagee;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private ImageButton editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_settings);
        mMakeField = (TextView) findViewById(R.id.cmake);
        mModelField = (TextView) findViewById(R.id.cmodel);
        mColorField = (TextView) findViewById(R.id.cColor);
        mRegnoField = (TextView) findViewById(R.id.Regno);
        fullName = (TextView) findViewById(R.id.txtFullName);
        editProfile = (ImageButton) findViewById(R.id.btnEditProfile);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProfileImagee = (ImageView) findViewById(R.id.btnEditProfilePicture);



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("car info");
        mProfileImagee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), editCar.class);
                startActivity(intent);
            }
        });


        getUserInfo();
    }
    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("color")!=null){
                        mColor = map.get("color").toString();
                        mColorField.setText(mColor);

                    }
                    if(map.get("make")!=null){
                        mMake = map.get("make").toString();
                        mMakeField.setText(mMake);
                    }
                    if(map.get("model")!=null){
                        mModel = map.get("car").toString();
                        mModelField.setText(mModel);
                        fullName.setText(map.get("color").toString()+ " " + map.get("make").toString()+" "+map.get("Registration number"));
                    }
                    if(map.get("Registration number")!=null){
                        mRegno = map.get("Registration number").toString();
                        mRegnoField.setText(mRegno);

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

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("car profile_images").child(userID);
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
                    mDriverDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }

    }
}
