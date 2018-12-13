package com.example.madvincy.safe.parking_place_account_activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.SignUp;
import com.example.madvincy.safe.signup2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail,license,address, inputPassword, full_name,owner,secret,parkingsize, phone_no, id_number;     //hit option + enter if you on mac , for windows hit ctrl + enter
    private Button btnSignUp;
    private TimePicker timePicker1, timePicker2;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private Uri resultUri;
    private String userID;
    private String id_no, phonenumber, fullname,secreto, licen,adres, pOwner, idno,time1,time2 ;
    private String format = "";
    private String formate = "";
    private  Spinner spinnerType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        full_name = (EditText) findViewById(R.id.carparkname);
        license = (EditText) findViewById(R.id.license);
        address= (EditText) findViewById(R.id.address);
        secret= (EditText) findViewById(R.id.secret);
        parkingsize = (EditText) findViewById(R.id.parkingsize);
        owner = (EditText) findViewById(R.id.owner);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        timePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        phone_no = (EditText) findViewById(R.id.input_phoneno);
        id_number = (EditText) findViewById(R.id.id_no);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        spinnerType=(Spinner) findViewById(R.id.Resource);


//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    saveUserInformation();
                                    startActivity(new Intent(SignUpActivity.this, ParkPlaceLocation.class));
                                    finish();
                                }
                            }
                        });


            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void saveUserInformation() {
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userID).child("user info");
        id_no = id_number.getText().toString();
        phonenumber = phone_no.getText().toString();
        fullname = full_name.getText().toString();
        adres= address.getText().toString();
        secreto = secret.getText().toString();
        licen= license.getText().toString();
        String parkingsizee =parkingsize.getText().toString();
        pOwner = owner.getText().toString();
        adres = address.getText().toString();
        String requestService = spinnerType.getSelectedItem().toString();
        time1 = timePicker1.getCurrentHour().toString();
        int min = timePicker1.getCurrentMinute();
        int hour = timePicker1.getCurrentHour();
        int houre = timePicker2.getCurrentHour();
        int mine = timePicker2.getCurrentMinute();

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


        String openingTime= hour+ ": " + min+ " " +format;
        String closingTime = houre+ " :" + mine+ " " +formate;

        Map userInfo = new HashMap();
        userInfo.put("national id_number", id_no);
        userInfo.put("phone number", phonenumber);
        userInfo.put("car park name", fullname);
        userInfo.put("car park type", requestService);
        userInfo.put("car park address", adres);
        userInfo.put("secret key", secreto);
        userInfo.put("Parking Size", parkingsizee);
        userInfo.put("license key", licen);
        userInfo.put("opening-Time", openingTime);
        userInfo.put("closing-Time", closingTime);


        mCustomerDatabase.updateChildren(userInfo);
        FirebaseAuth authe = FirebaseAuth.getInstance();
        FirebaseUser usere = auth.getCurrentUser();
        usere.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Email sent to verify your Account." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (resultUri != null) {

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
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}