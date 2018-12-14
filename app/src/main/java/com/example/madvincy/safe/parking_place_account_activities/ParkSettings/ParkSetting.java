package com.example.madvincy.safe.parking_place_account_activities.ParkSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.ChangePassword;
import com.example.madvincy.safe.parking_place_account_activities.CustomerPreview;
import com.example.madvincy.safe.parking_place_account_activities.ParkPlaceLocation;
import com.example.madvincy.safe.parking_place_account_activities.QrScanning;
import com.example.madvincy.safe.parking_place_account_activities.SignUpActivity;
import com.example.madvincy.safe.settings.editUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParkSetting extends AppCompatActivity implements OnMapReadyCallback {
    private TextView parkSize,fullName,parkType,mNatId,PhoneNo,mEmail,adressde,licenKey,owner,date1,date2;
    private String mName,mNatid,mphoneno,mEmaile, userID ;
    private String mProfileImageUrl,format,formate;
    private ImageView mProfileImage,mProfileImagee;
    private FirebaseAuth mAuth;
    private DatabaseReference mCusDatabase;
    private TimePicker datePicker1,datePicker2;
    private Spinner Resourcede;
    private ImageButton editProfile,changePass,changeLoc,deleteAcc;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_setting);
        fullName= findViewById(R.id.txtFullNamede);
        mNatId= findViewById(R.id.idde);
        PhoneNo= findViewById(R.id.Phonede);
        mEmail= findViewById(R.id.Emailde);
        adressde= findViewById(R.id.addressde);
//        Resourcede= findViewById(R.id.Resourcede);
        licenKey= findViewById(R.id.licensekeyde);
        parkType= findViewById(R.id.Resourcede);
        parkSize=(TextView)findViewById(R.id.ParkingSizede);
        date1= findViewById(R.id.datePicker1);
        date2= findViewById(R.id.datePicker2);
        owner= findViewById(R.id.Parkownerde);
        editProfile = (ImageButton) findViewById(R.id.btnEditProfilede);
//        datePicker1 = (TimePicker) findViewById(R.id.datePicker1);
//        datePicker2 = (TimePicker) findViewById(R.id.datePicker2);
        changePass = (ImageButton) findViewById(R.id. btnchangepass);
        deleteAcc = (ImageButton) findViewById(R.id.btndeleteAccount);
        changeLoc = (ImageButton) findViewById(R.id.btnchangeLoc);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        int min = datePicker1.getCurrentMinute();
//        int hour = datePicker1.getCurrentHour();
//        int houre = datePicker2.getCurrentHour();
//        int mine = datePicker2.getCurrentMinute();
//
//        if (hour == 0) {
//            hour += 12;
//            format = "AM";
//        } else if (hour == 12) {
//            format = "PM";
//        } else if (hour > 12) {
//            hour -= 12;
//            format = "PM";
//        } else {
//            format = "AM";
//        }
//        if (houre == 0) {
//            houre += 12;
//            formate = "AM";
//        } else if (houre == 12) {
//            format = "PM";
//        } else if (hour > 12) {
//            houre -= 12;
//            formate = "PM";
//        } else {
//            formate = "AM";
//        }


        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProfileImagee = (ImageView) findViewById(R.id.btnEditProfilePicturede);



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userID).child("user info");
       DatabaseReference mMap = FirebaseDatabase.getInstance().getReference().child("Users");
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
                Intent intent = new Intent(getApplicationContext(), editPark.class);
                startActivity(intent);
            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
            }
        });
        changeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ParkPlaceLocation.class);
                startActivity(intent);
            }
        });
        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("You will have to sign up!!!!!!!");
                builder.setTitle("Deleting Account");
                builder.setPositiveButton("Cancel ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("Delete MyAccount",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }

                    private void deleteAccount() {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ParkSetting.this, "Account deleted!" + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });

                AlertDialog alert1 = builder.create();
                alert1.show();

            }
        });




        getUserInfo();
    }




    private void getUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String emailed = user.getEmail();
        mEmail.setText("User Email: " + emailed );



        mCusDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("national id_number")!=null){
                        mNatid= map.get("national id_number").toString();
                        mNatId.setText("Nat Id: " + mNatid);
                    }
                    if(map.get("car park address")!=null){
                       String mAdress= map.get("car park address").toString();
                        adressde.setText("Address: " + mAdress);
                    }
                    if(map.get("car park owner")!=null){
                       String mOwner= map.get("car park owner").toString();
                        owner.setText("Owner: " + mOwner);
                    }
                    if(map.get("license key")!=null){
                       String mLicen= map.get("license key").toString();
                        licenKey.setText("license-key: " + mLicen);
                    }
                    if(map.get("car park type")!=null){
                        String mType= map.get("car park type").toString();
                        parkType.setText("park-type: " + mType);
                    }
                    if(map.get("car park name")!=null){
                        String mName= map.get("car park name").toString();
                        fullName.setText("park-name: " + mName);
                    }
                    if(map.get("opening-Time")!=null){
                        String mOpening= map.get("opening-Time").toString();
                        date1.setText("Opening Time: " + mOpening);
                    }
                    if(map.get("closing-Time")!=null){
                        String mClosing= map.get("closing-Time").toString();
                        date2.setText("closing Time: " + mClosing);
                    }
                    if(map.get("phone number")!=null){
                        mphoneno = map.get("phone number").toString();
                        PhoneNo.setText("Phone No: " +mphoneno);

                    }
                    if(map.get("Parking Size")!=null){
                        String mparkSize = map.get("Parking Size").toString();
                        parkSize.setText("Park Size: " +mparkSize);

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

            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("park profile_images").child(userID);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

