package com.example.madvincy.safe.settings;

import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madvincy.safe.R;

import java.util.HashMap;
import java.util.Map;

public class editCar extends AppCompatActivity {
    private EditText mMakeField,mColorField,mRegnoField,mModelField,fullName;
    private String mColor,mMake,mModel,mRegno,mCala ,mMak, mMode , mReg ;
    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;
    private Button btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);
        mColorField = (EditText) findViewById(R.id.color);
        mRegnoField= (EditText) findViewById(R.id.regno);
        mModelField = (EditText) findViewById(R.id.model);
        mMakeField = (EditText) findViewById(R.id.make);

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

                }
                if(map.get("Registration number")!=null){
                    mRegno = map.get("Registration number").toString();
                    mRegnoField.setText(mRegno);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




    }
    public void updateProfile() {
        mCala = mColorField.getText().toString();
        mMak = mMakeField.getText().toString();
        mMode =  mModelField.getText().toString();
        mReg = mRegnoField.getText().toString();




        Map carInfo = new HashMap();
        carInfo.put("color", mCala);
        carInfo.put("make", mMak);
        carInfo.put("model", mMode);
        carInfo.put("registration number", mReg);
        mDriverDatabase.updateChildren(carInfo);


    }
}
