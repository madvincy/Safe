package com.example.madvincy.safe.booking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.HelperUtils.HelperUtilities;
import com.example.madvincy.safe.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Booking extends AppCompatActivity {
    private Button btnRoundReturnDatePicker,mRequest;
    //date picker dialog
    private DatePickerDialog datePickerDialog1;
    private DatePickerDialog datePickerDialog2;
    private DatePickerDialog datePickerDialog3;
    //current date
    private int year;
    private int month;
    private TimePicker timePicker1;
    private int day;
    private String oneWayDepartureDate;
    private int tempYear;
    private int tempMonth;
    private int tempDay;
    private String bookingarea,requestService;
    private LatLng bookingareaLatLng;
    Spinner spinnerType;
    private String format = "";
    private Boolean requestBol = false;
    private TextView mParkName, mParkPhone;
    private ImageView mParkProfileImage;
    private LinearLayout mParkInfo;
    private RatingBar mRatingBar;


    private final int ONE_WAY_RETURN_DATE_PICKER = R.id.DatePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        spinnerType=(Spinner) findViewById(R.id.Resource);
        requestService = spinnerType.getSelectedItem().toString();
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);

        btnRoundReturnDatePicker = (Button) findViewById(R.id.DatePicker);
        mRequest= (Button) findViewById(R.id.btnSearch);
        year = HelperUtilities.currentYear();
        month = HelperUtilities.currentMonth();
        bookingareaLatLng = new LatLng(0.0,0.0);
        day = HelperUtilities.currentDay();
        mParkInfo = (LinearLayout) findViewById(R.id.driverInfo);

        mParkProfileImage = (ImageView) findViewById(R.id.driverProfileImage);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        mParkName = (TextView) findViewById(R.id.driverName);
        mParkPhone = (TextView) findViewById(R.id.driverPhone);
        btnRoundReturnDatePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                datePickerDialog(ONE_WAY_RETURN_DATE_PICKER).show();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                bookingarea = place.getName().toString();
                bookingareaLatLng = place.getLatLng();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol){
                    endSearch();


                }else{



                    requestBol = true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    mRequest.setText("Booking You Parking Place....");

                    getClosestParkingToBook();
                }
            }
        });
    }

    public DatePickerDialog datePickerDialog(int datePickerId) {

        switch (datePickerId) {
            case ONE_WAY_RETURN_DATE_PICKER:

                if (datePickerDialog1 == null) {
                    datePickerDialog1 = new DatePickerDialog(this, getOneWayDepartureDatePickerListener(), year, month, day);
                }
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return datePickerDialog1;

        }
        return null;
    }
    public DatePickerDialog.OnDateSetListener getOneWayDepartureDatePickerListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int startYear, int startMonth, int startDay) {

                //get one way departure date here

                oneWayDepartureDate = startYear + "-" + (startMonth + 1) + "-" + startDay;
                btnRoundReturnDatePicker.setText(HelperUtilities.formatDate(startYear, startMonth, startDay));

            }
        };
    }
    private int radius = 1;
    private Boolean ParkingFound = false;
    private String parkingFoundID;

    GeoQuery geoQuery;
    private void getClosestParkingToBook(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("parking Locations");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(bookingareaLatLng.latitude, bookingareaLatLng.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, GeoLocation location) {
                if (!ParkingFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(key).child("user info");
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (ParkingFound){
                                    return;
                                }

                                if(driverMap.get("car park type").equals(requestService)){
                                    ParkingFound = true;
                                    parkingFoundID = key;
                                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    DatabaseReference parkRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingFoundID).child("Booking");
                                    DatabaseReference cusRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("Booking");
                                    DatabaseReference parkbookRef = FirebaseDatabase.getInstance().getReference().child("Booking");
                                    String requestId = parkbookRef.push().getKey();
                                    parkRef.child(requestId).setValue(true);
                                    cusRef.child(requestId).setValue(true);
                                    int min = timePicker1.getCurrentMinute();
                                    int hour = timePicker1.getCurrentHour();
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


                                    HashMap map = new HashMap();
                                    map.put("Parkingplace", parkingFoundID);
                                    map.put("customer", customerId);
                                    map.put("date booked", getCurrentTimestamp());
                                    map.put("booking date", oneWayDepartureDate);
                                    map.put("booking hour", hour);
                                    map.put("booking time format", format);
                                    map.put("booking minute", min);
                                    parkbookRef.child(requestId).updateChildren(map);

                                    getParkInfo();
                                    mRequest.setText("Looking for Resource Location....");
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if (!ParkingFound)
                {

                    radius++;


                    if(radius<100) {
                        getClosestParkingToBook();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No Parking place found", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());

                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }

    private void endSearch(){
        requestBol = false;
        geoQuery.removeAllListeners();

        if (parkingFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingFoundID).child("Booking");
            driverRef.removeValue();
            parkingFoundID = null;

        }
        ParkingFound = false;
        radius = 1;
    }
    private void getParkInfo(){
        mParkInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingFoundID).child("user info");
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("car park name")!=null){
                        mParkName.setText(dataSnapshot.child("car park name").getValue().toString());
                    }
                    if(dataSnapshot.child("phone number")!=null){
                        mParkPhone.setText(dataSnapshot.child("phone number").getValue().toString());
                    }

                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mParkProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
