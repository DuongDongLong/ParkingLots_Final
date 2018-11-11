package com.vmh.manhhung.parkinglotsone;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,OnMapReadyCallback
        ,GoogleApiClient.OnConnectionFailedListener {
    CircleImageView imgPhoto;
    TextView txtFullName, txtEmail;
    private DatabaseReference mData;
    FirebaseAuth mAuth;


    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));
    private static final float DEFAULT_ZOOM = 17f;
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleMap mMap;
    private AutoCompleteTextView mSearchText;
    private LinearLayout find;
    private ImageButton seach;
    private ImageView gps;
    private DrawerLayout drawerLayout;
    private Button toggleButton;
    private GoogleApiClient mGoogleApiClient;
    ArrayList<LatLng> listPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        init();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Drawerlayout
        listPoints = new ArrayList<>();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //dua thong tin len headView
        View headView=navigationView.getHeaderView(0);
        txtEmail=(TextView)headView.findViewById(R.id.txtEmail);
        txtFullName=(TextView)headView.findViewById(R.id.txtFullName);
        imgPhoto=(CircleImageView) headView.findViewById(R.id.imgPhoto);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        mData.child("ThongTin").child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtEmail.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(user.getUid()).child("uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).into(imgPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mData.child("ThongTin").child(user.getUid()).child("fullname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue().toString()!="")
                    {
                        txtFullName.setText(dataSnapshot.getValue().toString());
                        imgPhoto.setImageResource(R.mipmap.ic_launcher);
                    }
                    else
                    {
                        txtFullName.setText("Name");
                        imgPhoto.setImageResource(R.mipmap.ic_launcher);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

    }


   private void init() {
        drawerLayout=findViewById(R.id.drawer_layout);
       toggleButton=findViewById(R.id.btnToggle);
      toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        mSearchText=findViewById(R.id.input_search);
        gps=findViewById(R.id.ic_gps);
        find=findViewById(R.id.test);
        seach=findViewById(R.id.ic_magnify);
        seach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find.setVisibility(View.VISIBLE);
                hideSoftKeyboard();
                geoLocate();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();


            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        placeAutocompleteAdapter =new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS,null);
        mSearchText.setAdapter(placeAutocompleteAdapter);


        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_SEARCH
                        || actionId==EditorInfo.IME_ACTION_DONE
                        || event.getAction()==event.ACTION_DOWN
                        || event.getAction()==event.KEYCODE_ENTER)
                    geoLocate();
                return false;
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
        getMenuInflater().inflate(R.menu.main, menu);
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

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            FirebaseAuth.getInstance().signOut();

        } else if (id == R.id.nav_slideshow) {
            Intent intent=new Intent(MainActivity.this,ManageAccountActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
   private void hideSoftKeyboard() {
       getWindow().setSoftInputMode(
               WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
       );

   }
    public void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // check pprovider is enable
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude =location.getLatitude();
                    double longitude =location.getLongitude();
                    LatLng latLng=new LatLng(latitude,longitude);
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList= geocoder.getFromLocation(latitude, longitude, 1);
                        String str= addressList.get(0).getLocality();
                        str+=addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                        String str = addressList.get(0).getLocality();
                        str += addressList.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(str));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng danang = new LatLng(16.047079, 108.206230);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(danang,15f));
    /*    mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2) {
                    //Create the URL to get request from first marker to second marker
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        });*/
    }
   
    
    public void geoLocate(){
        String searchText=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MainActivity.this);
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchText,1 );
        }catch (Exception e){

        }
        if(list.size()>0){
            Address address=list.get(0);
            Log.d(TAG,"tim thay vi tri:"+address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideSoftKeyboard();

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
