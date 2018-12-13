package com.example.madvincy.safe.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.madvincy.safe.findparking.map;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.EditText;

import com.example.madvincy.safe.R;

import java.util.HashMap;
import java.util.Map;

public class editUser extends AppCompatActivity {
  private  EditText mFullnames,mNatid,mPhoneNo;
  private String mFull,mNatIde,mPhon,mSimu,mJina,userID,mKipande;
  private  FirebaseAuth mAuth;
  private Button btnUpdateProfile;
  private DatabaseReference mDriverDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        mFullnames = (EditText) findViewById(R.id.fullnamee);
        mNatid= (EditText) findViewById(R.id.ide);
        mPhoneNo = (EditText) findViewById(R.id.phonee);


        btnUpdateProfile = (Button) findViewById(R.id.btnSaveProfile);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("car info");

        loadProfileInfo();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();

            }
        });
    }

    public void loadProfileInfo() {
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                if(map.get("full names")!=null){
                    mFull = map.get("full names").toString();
                    mFullnames.setText(mFull);

                }
                if(map.get("national id_number")!=null){
                    mNatIde = map.get("national id_number").toString();
                    mNatid.setText(mNatIde);
                }
                if(map.get("phone number")!=null){
                    mPhon = map.get("phone number").toString();
                    mPhoneNo.setText(mPhon);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }
    public void updateProfile() {
        mSimu = mPhoneNo.getText().toString();
        mJina =  mFullnames.getText().toString();
        mKipande =  mNatid.getText().toString();
        Map userInfo = new HashMap();
        userInfo.put("full names", mJina);
        userInfo.put("national id_number", mKipande);
        userInfo.put("phone number", mSimu);

        mDriverDatabase.updateChildren(userInfo);


    }
}
