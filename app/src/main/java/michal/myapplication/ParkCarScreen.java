package michal.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.DrawRoute;

import Framework.MapHelpers.MapRotator;
import Framework.MapHelpers.Utils;

public class ParkCarScreen extends AppCompatActivity  implements OnMapReadyCallback{

    public static final String TAG = ParkCarScreen.class.getSimpleName();



    //UI ELEMENTS
    private EditText    desiredDurationEdit;
    private EditText    notesEdit;
    private Button      updateLocationButton;
    private CheckBox    openDayModeCheckbox;
    private Button parkCarButton;
    private Button      drawRouteButton;

    private GoogleMap map;
    private GpsTag              currentLocation;
    private GpsTag              parkingLocation;
    private LocationManager     locationManager;
    private MapRotator          mapRotator;
    final Handler h = new Handler();




    public void updateMarker(GpsTag location){
        map.clear();
        LatLng currentPosition = Utils.toLatLng(location);

        map.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }



    public void updateLocation(){

        // wait for the gpsManager to be connected
         if(locationManager.isReady()) {

             //adding a name because of the framework specification
             GpsTag newLocation = locationManager.getCurrentLocation("parkedCarLocation");

             //only update location if it's different to the one we've already got
             if(!GpsTag.isSameLocation(currentLocation,newLocation)){
                 currentLocation = newLocation;
                 mapRotator.updateDeclination(currentLocation);
                 updateMarker(currentLocation);
             }


        }else{
            Log.i(TAG,"GPSmanager is still not connected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_car_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //WIRE UI ELEMENTS
        desiredDurationEdit =   (EditText)  findViewById(R.id.desDurEdit);
        notesEdit =             (EditText)  findViewById(R.id.notesEdit);
        openDayModeCheckbox =   (CheckBox)  findViewById(R.id.openDayCheckbox);
        parkCarButton =      (Button)       findViewById(R.id.parkCarButton);


        //initialise GPSManager - start listening for location
        locationManager = LocationManager.getInstance(this);

        //hardcoded parking location for testing purposes
        parkingLocation = new GpsTag("parkingLocation",52.623247,1.241826,29);



        parkCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parkCar();
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    public void parkCar(){
        //get info from forms
        //TODO: input-check
        int desiredDuration  = Integer.parseInt(desiredDurationEdit.getText().toString());
        String notes = notesEdit.getText().toString();
        boolean openDayMode = openDayModeCheckbox.isChecked();

        //set timeParked to current time
        GregorianCalendar timeParked = new GregorianCalendar();


        ParkedCar parkedCar = ParkedCar.getInstance();

        parkedCar.setDesiredDuration(desiredDuration);
        parkedCar.setOpenDayMode(openDayMode);
        parkedCar.setNotes(notes);
        parkedCar.setParkTime(timeParked);

        locationManager.storeGpsLocation("parkedCarLocation", parkingLocation);

        Intent intent = new Intent(this, OverviewScreen.class);
        Bundle b = new Bundle();
        b.putSerializable("parkedCar", parkedCar);
        intent.putExtras(b);

        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.select_map_type) {
            if(map!=null) {
                Utils.getMapTypeSelectorDialog(map, ParkCarScreen.this).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMyLocationEnabled(true);

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);


        final int delay = 2000; //milliseconds

        // hack - we know it takes a bit of time
        // for the gpsManager to be ready
        // wait 2 seconds and then try to updateLocation
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                updateLocation();
                h.postDelayed(this, delay);

            }
        }, delay);

    }


}
