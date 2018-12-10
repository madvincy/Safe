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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateEmployees extends AppCompatActivity {

    private EditText inputEmail,employee_id, inputPassword, full_name, phone_no, id_number;     //hit option + enter if you on mac , for windows hit ctrl + enter
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private Uri resultUri;
    private String userID;
    private String id_no, emp_id,phonenumber, fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_employees);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        full_name = (EditText) findViewById(R.id.input_name);
        phone_no = (EditText) findViewById(R.id.input_phoneno);
        id_number = (EditText) findViewById(R.id.id_no);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        employee_id = (EditText) findViewById(R.id.emp_id);


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
                        .addOnCompleteListener(CreateEmployees.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(CreateEmployees.this, "create employee WithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(CreateEmployees.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    saveUserInformation();
                                    startActivity(new Intent(CreateEmployees.this, ParkPlaceLocation.class));
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
        emp_id= employee_id.getText().toString();
        String email = inputEmail.getText().toString().trim();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Employees").child(email).child("Employer");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userID).child("Employees");
        DatabaseReference Employment = FirebaseDatabase.getInstance().getReference().child("Employees");
        String employmentId = Employment.push().getKey();
        driverRef.child(employmentId ).setValue(true);
        customerRef.child(employmentId ).setValue(true);
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Employees").child(email).child("user info");
        id_no = id_number.getText().toString();
        phonenumber = phone_no.getText().toString();
        fullname = full_name.getText().toString();


        Map userInfo = new HashMap();
        userInfo.put("national id_number", id_no);
        userInfo.put("phone number", phonenumber);
        userInfo.put("full names", fullname);
        userInfo.put("job employee id", emp_id);

        mCustomerDatabase.updateChildren(userInfo);


        Map EmploymentInfo = new HashMap();
        userInfo.put("Employee", email);
        userInfo.put("Employer", userID);


        Employment.updateChildren(userInfo);

        if (resultUri != null) {

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
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
