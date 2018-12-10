package com.example.madvincy.safe.findparking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.madvincy.safe.CustomerActivities.ActiveParkingActivity;
import com.example.madvincy.safe.Dashboard;
import com.example.madvincy.safe.MainActivity;
import com.example.madvincy.safe.R;
import com.example.madvincy.safe.booking.Booking;
import com.example.madvincy.safe.parkinghistory.CustomerParkingHistory;
import com.example.madvincy.safe.promotions.InviteFriend;
import com.example.madvincy.safe.qrscanner.GenerateQr;
import com.example.madvincy.safe.settings.CarSettings;
import com.example.madvincy.safe.settings.CustomerSettings;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class map extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, RoutingListener {

        DrawerLayout drawer;
        NavigationView navigationView;
        Toolbar toolbar=null;
        private GoogleMap mMap;
        Location mLastLocation;
        LocationRequest mLocationRequest;
        private DatabaseReference mCustomerDatabase, mCustomercarDatabase;
        private String mProfileImageUrl;
        private ImageView mProfileImage;

        private FusedLocationProviderClient mFusedLocationClient;

        private Button  mRequest;
        Spinner spinnerType;

        private LatLng pickupLocation;
        private String emaile;

        private Boolean requestBol = false;

        private Marker pickupMarker;

        private SupportMapFragment mapFragment;

        private String destination, requestService;

        private LatLng destinationLatLng;

        private LinearLayout mParkingPlaceInfo;

        private ImageView mParkingPlaceProfileImage;

        private TextView email,mParkingPlaceName, mParkingPlacePhone, mParkingPlaceCapacity;

        private RadioGroup mRadioGroup;
        private FirebaseAuth mAuth;
        private String userID;

        private RatingBar mRatingBar;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_map);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);





                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(map.this, GenerateQr.class));
                    }
                });

                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                navigationView = (NavigationView) findViewById(R.id.nav_view);
                email = (TextView) findViewById(R.id.useremail);





                navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);


                spinnerType=(Spinner) findViewById(R.id.Resource);

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

                destinationLatLng = new LatLng(0.0,0.0);

                mParkingPlaceInfo = (LinearLayout) findViewById(R.id.driverInfo);

                mParkingPlaceProfileImage = (ImageView) findViewById(R.id.driverProfileImage);

                mParkingPlaceName = (TextView) findViewById(R.id.driverName);
                mParkingPlacePhone = (TextView) findViewById(R.id.driverPhone);
                mParkingPlaceCapacity = (TextView) findViewById(R.id.driverCar);

                mRatingBar = (RatingBar) findViewById(R.id.ratingBar);



                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("user info");
                mCustomercarDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("car info");


                mRequest = (Button) findViewById(R.id.request);






                mRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                if (requestBol){
                                        endParkingRequest();


                                }else{

                                        requestService = spinnerType.getSelectedItem().toString();

                                        requestBol = true;

                                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                                        GeoFire geoFire = new GeoFire(ref);
                                        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                        pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("My location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                                        mRequest.setText("Getting you a parking place..");

                                        getClosestParkingPlace();
                                }
                        }
                });




                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                                // TODO: Get info about the selected place.
                                destination = place.getName().toString();
                                destinationLatLng = place.getLatLng();
                        }
                        @Override
                        public void onError(Status status) {
                                // TODO: Handle the error.
                        }
                });


        }
        private int radius = 1;
        private Boolean parkingplaceFound = false;
        private String parkingplaceFoundID;

        GeoQuery geoQuery;
        private void getClosestParkingPlace(){
                DatabaseReference parkingplaceLocation = FirebaseDatabase.getInstance().getReference().child("parking Locations");

                GeoFire geoFire = new GeoFire(parkingplaceLocation);
                geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
                geoQuery.removeAllListeners();

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                                if (!parkingplaceFound && requestBol){
                                        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(key).child("user info");
                                        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                                                Map<String, Object> parkingplaceMap = (Map<String, Object>) dataSnapshot.getValue();
                                                                if (parkingplaceFound){
                                                                        return;
                                                                }

                                                                if(parkingplaceMap.get("service").equals(requestService)){
                                                                        parkingplaceFound = true;
                                                                        parkingplaceFoundID = dataSnapshot.getKey();

                                                                        DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("Users").child("parkingplaces").child(parkingplaceFoundID).child("customerRequest");
                                                                        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                                        HashMap map = new HashMap();
                                                                        map.put("customerRideId", customerId);
//
                                                                        parkingRef.updateChildren(map);

                                                                        getParkingPlaceLocation();
                                                                        getParkingPlaceInfo();
                                                                        getHasRideEnded();
                                                                        mRequest.setText("Looking for Parking Location....");
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
                                if (!parkingplaceFound)
                                {
                                        radius++;
                                        getClosestParkingPlace();
                                }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                });
        }

        private Marker mParkingPlaceMarker;
        private DatabaseReference parkingplaceLocationRef;
        private ValueEventListener parkingplaceLocationRefListener;
        private void getParkingPlaceLocation(){
                parkingplaceLocationRef = FirebaseDatabase.getInstance().getReference().child("parkingPlacesOpen").child(parkingplaceFoundID).child("l");
                parkingplaceLocationRefListener = parkingplaceLocationRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists() && requestBol){
                                        List<Object> map = (List<Object>) dataSnapshot.getValue();
                                        double locationLat = 0;
                                        double locationLng = 0;
                                        if(map.get(0) != null){
                                                locationLat = Double.parseDouble(map.get(0).toString());
                                        }
                                        if(map.get(1) != null){
                                                locationLng = Double.parseDouble(map.get(1).toString());
                                        }
                                        LatLng parkingplaceLatLng = new LatLng(locationLat,locationLng);
                                        if(mParkingPlaceMarker != null){
                                                mParkingPlaceMarker.remove();
                                        }
                                        Location loc1 = new Location("");
                                        loc1.setLatitude(pickupLocation.latitude);
                                        loc1.setLongitude(pickupLocation.longitude);

                                        Location loc2 = new Location("");
                                        loc2.setLatitude(parkingplaceLatLng.latitude);
                                        loc2.setLongitude(parkingplaceLatLng.longitude);

                                        float distance = loc1.distanceTo(loc2);

                                        if (distance<100){
                                                mRequest.setText("You have reached the Parking Place");
                                        }else{
                                                mRequest.setText("Parking PlaceFound: " + String.valueOf(distance));
                                        }



                                        mParkingPlaceMarker = mMap.addMarker(new MarkerOptions().position(parkingplaceLatLng).title("Assigned Parking").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking)));
                                    getRouteToMarker(parkingplaceLatLng);
                                }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                });

        }

    private void getRouteToMarker(LatLng parkingplaceLatLng) {
        if (parkingplaceLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), parkingplaceLatLng)
                    .build();
            routing.execute();
        }
    }

        /*-------------------------------------------- getDriverInfo -----
        |  Function(s) getDriverInfo
        |
        |  Purpose:  Get all the user information that we can get from 0the user's database.
        |
        |  Note: --
        |
        *-------------------------------------------------------------------*/
        private void getParkingPlaceInfo(){
                mParkingPlaceInfo.setVisibility(View.VISIBLE);
                DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingplaceFoundID).child("user info");
                mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                        if(dataSnapshot.child("car park name")!=null){
                                                mParkingPlaceName.setText(dataSnapshot.child("car park name").getValue().toString());
                                        }
                                        if(dataSnapshot.child("phone number")!=null){
                                                mParkingPlacePhone.setText(dataSnapshot.child("phone number").getValue().toString());
                                        }
                                        if(dataSnapshot.child("Parking Size")!=null){
                                                mParkingPlaceCapacity.setText(dataSnapshot.child("Parking Size").getValue().toString());
                                        }
                                        if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                                                Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mParkingPlaceProfileImage);
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

        private DatabaseReference parkingplaceHasEndedRequestRef;
        private ValueEventListener parkingplaceHasEndedRequestRefListener;
        private void getHasRideEnded(){
            parkingplaceHasEndedRequestRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingplaceFoundID).child("customerRequest").child("customerRideId");
            parkingplaceHasEndedRequestRefListener = parkingplaceHasEndedRequestRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){

                                }else{
                                        endParkingRequest();
                                }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                });
        }

        private void endParkingRequest(){
                requestBol = false;
                geoQuery.removeAllListeners();
                parkingplaceLocationRef.removeEventListener(parkingplaceLocationRefListener);
                parkingplaceHasEndedRequestRef.removeEventListener( parkingplaceHasEndedRequestRefListener);

                if (parkingplaceFoundID != null){
                        DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkingplaceFoundID).child("customerRequest");
                        parkingRef.removeValue();
                        parkingplaceFoundID = null;

                }
                parkingplaceFound = false;
                radius = 1;
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userId);

                if(pickupMarker != null){
                        pickupMarker.remove();
                }
                if (mParkingPlaceMarker != null){
                        mParkingPlaceMarker.remove();
                }
                mRequest.setText("Find Parking Place");

                mParkingPlaceInfo.setVisibility(View.GONE);
                mParkingPlaceName.setText("");
                mParkingPlacePhone.setText("");
                mParkingPlaceCapacity.setText("Destination: --");
                mParkingPlaceProfileImage.setImageResource(R.mipmap.ic_default_user);
        }

        /*-------------------------------------------- Map specific functions -----
        |  Function(s) onMapReady, buildGoogleApiClient, onLocationChanged, onConnected
        |
        |  Purpose:  Find and update user's location.
        |
        |  Note:
        |	   The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
        |      If you're having trouble with battery draining too fast then change these to lower values
        |
        |
        *-------------------------------------------------------------------*/
        @Override
        public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        }else{
                                checkLocationPermission();
                        }
                }

                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
        }

        LocationCallback mLocationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                        for(Location location : locationResult.getLocations()){
                                if(getApplicationContext()!=null){
                                        mLastLocation = location;

                                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                                        if(!getParkingPlacesAroundStarted)
                                                getParkingPlacesAround();
                                }
                        }
                }
        };

        /*-------------------------------------------- onRequestPermissionsResult -----
        |  Function onRequestPermissionsResult
        |
        |  Purpose:  Get permissions for our app if they didn't previously exist.
        |
        |  Note:
        |	requestCode: the nubmer assigned to the request that we've made. Each
        |                request has it's own unique request code.
        |
        *-------------------------------------------------------------------*/
        private void checkLocationPermission() {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                                new android.app.AlertDialog.Builder(this)
                                        .setTitle("give permission")
                                        .setMessage("give permission message")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                        ActivityCompat.requestPermissions(map.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                                }
                                        })
                                        .create()
                                        .show();
                        }
                        else{
                                ActivityCompat.requestPermissions(map.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                switch(requestCode){
                        case 1:{
                                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                                                mMap.setMyLocationEnabled(true);
                                        }
                                } else{
                                        Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                }
        }




        boolean getParkingPlacesAroundStarted = false;
        List<Marker> markers = new ArrayList<Marker>();
        private void getParkingPlacesAround(){
                getParkingPlacesAroundStarted = true;
                DatabaseReference ParkingPlaceLocation = FirebaseDatabase.getInstance().getReference().child("parkingplacesAvailable");

                GeoFire geoFire = new GeoFire(ParkingPlaceLocation);
                GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {

                                for(Marker markerIt : markers){
                                        if(markerIt.getTag().equals(key))
                                                return;
                                }

                                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title("Other Parkingplaces").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking)));
                                mDriverMarker.setTag(key);

                                markers.add(mDriverMarker);


                        }

                        @Override
                        public void onKeyExited(String key) {
                                for(Marker markerIt : markers){
                                        if(markerIt.getTag().equals(key)){
                                                markerIt.remove();
                                        }
                                }
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                                for(Marker markerIt : markers){
                                        if(markerIt.getTag().equals(key)){
                                                markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                                        }
                                }
                        }

                        @Override
                        public void onGeoQueryReady() {
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                });
        }








        @Override
        public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                        super.onBackPressed();
                }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.user_home, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                        return true;
                }

                return super.onOptionsItemSelected(item);
        }


        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                //here is the main place where we need to work on.
                int id=item.getItemId();
                switch (id){


                        case R.id.nav_home:
                                Intent b= new Intent(map.this, com.example.madvincy.safe.CustomerActivities.Dashboard.class);
                                startActivity(b);
                                break;
                        case R.id.nav_parking:
                                Intent r= new Intent(map.this,ActiveParkingActivity.class);
                                r.putExtra("customerOrParkingplace", "Customers");
                                startActivity(r);
                                break;

                        case R.id.nav_slots:
                                Intent h= new Intent(map.this,map.class);
                                startActivity(h);
                                break;
                        case R.id.nav_booking:
                                Intent g= new Intent(map.this,Booking.class);
                                startActivity(g);
                                break;
                        case R.id.nav_invite:
                                Intent s= new Intent(map.this,InviteFriend.class);
                                startActivity(s);
                                break;

                        case R.id.nav_history:
                                Intent q= new Intent(map.this,CustomerParkingHistory.class);
                                q.putExtra("customerOrParkingplace", "Customers");
                                startActivity(q);
                                break;
                        case R.id.nav_settings:
                                Intent a= new Intent(map.this,CustomerSettings.class);
                                startActivity(a);
                                break;
                        case R.id.nav_settings2:
                                Intent ab= new Intent(map.this,CarSettings.class);
                                startActivity(ab);
                                break;
                        case R.id.nav_logout:
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(map.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                        // this is done, now let us go and intialise the home page.
                        // after this lets start copying the above.
                        // FOLLOW MEEEEE>>>
                }



                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }


        private FirebaseAuth auth;


        @SuppressLint("SetTextI18n")
        private void setDataToView(FirebaseUser user) {

                email.setText("User Email: " + user.getEmail());


        }

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null) {
                                // user auth state is changed - user is null
                                // launch login activity
                                finish();
                        } else {
                                setDataToView(user);

                        }
                }


        };



        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {

        }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onRoutingCancelled() {
    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }


}
