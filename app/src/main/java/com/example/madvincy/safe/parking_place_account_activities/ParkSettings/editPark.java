package com.example.madvincy.safe.parking_place_account_activities.ParkSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.ChangePassword;
import com.example.madvincy.safe.parking_place_account_activities.ParkPlaceLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class editPark extends AppCompatActivity {
    private EditText fullName,parkType,mNatId,PhoneNo,mEmail,adressde,licenKey,owner,date1,date2;
    private String mName,mNatid,mphoneno,mEmaile, userID ;
    private String mProfileImageUrl,format,formate,openingTime,closingTime;
    private ImageView mProfileImage,mProfileImagee;
    private FirebaseAuth mAuth;
    private DatabaseReference mCusDatabase;
    private TimePicker datePicker1,datePicker2;
    private Spinner Resourcede;
    private Button editProfile;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_park);
        fullName= findViewById(R.id.parknemdd);
        mNatId= findViewById(R.id.ider);
        PhoneNo= findViewById(R.id.phoneer);
        adressde= findViewById(R.id.addressy);
        Resourcede= findViewById(R.id.Resourcede);
        licenKey= findViewById(R.id.leseni);
        owner= findViewById(R.id.parkownere);
        datePicker1 = (TimePicker) findViewById(R.id.datePicker11);
        datePicker2 = (TimePicker) findViewById(R.id.datePicker22);


        int min = datePicker1.getCurrentMinute();
        int hour = datePicker1.getCurrentHour();
        int houre = datePicker2.getCurrentHour();
        int mine = datePicker2.getCurrentMinute();

        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (houre == 0) {
            houre += 12;
            formate = "AM";
        } else if (houre == 12) {
            format = "PM";
        } else if (hour > 12) {
            houre -= 12;
            formate = "PM";
        } else {
            formate = "AM";
        }
        openingTime= hour+ ": " + min+ " " +format;
        closingTime = houre+ " :" + mine+ " " +formate;


        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProfileImagee = (ImageView) findViewById(R.id.btnEditProfilePicture);
        editProfile=(Button)findViewById(R.id.btnEditProfilePark);



        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("user info");

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                Intent intent = new Intent(getApplicationContext(), ParkSetting.class);
                startActivity(intent);
            }
        });



        getUserInfo();
    }
    public void getUserInfo(){
     mCusDatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                if(map.get("owner's id")!=null){
                    mNatid= map.get("owner's id").toString();
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
    public void updateProfile() {
        String  mParktyp = Resourcede.getSelectedItem().toString();
        String mJina =  fullName.getText().toString();
        String mAdress =  adressde.getText().toString();
        String mId =  mNatId.getText().toString();
        String mLicen =  licenKey.getText().toString();
        String mOwner =  owner.getText().toString();
        String mPhone =  PhoneNo.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("car park name", mJina);
        userInfo.put("national id_number", mParktyp);
        userInfo.put("car park address", mAdress);
        userInfo.put("owner's id", mId);
        userInfo.put("license key", mLicen);
        userInfo.put("car park owner", mOwner);
        userInfo.put("phone number", mPhone);
        userInfo.put("opening-Time", openingTime);
        userInfo.put("closing-Time", closingTime);

        mCusDatabase.updateChildren(userInfo);


    }
}
