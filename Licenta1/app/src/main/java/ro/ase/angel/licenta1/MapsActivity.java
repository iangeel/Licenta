package ro.ase.angel.licenta1;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

import ro.ase.angel.licenta1.Utils.Constants;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;

    private double[] myCoordinates;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        myCoordinates = getIntent().getDoubleArrayExtra(Constants.MAP_ARRAY);
        if(myCoordinates != null && myCoordinates.length > 0) {
            for (int i = 0; i < myCoordinates.length; i++) {
                Toast.makeText(getApplicationContext(), String.valueOf(myCoordinates[i]), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng startPoint = new LatLng(myCoordinates[0], myCoordinates[1]);
//        LatLng finishPoint = new LatLng(myCoordinates[myCoordinates.length - 1], myCoordinates[myCoordinates.length]);

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.clickable(true);

        LatLng mCoord;

        for(int i = 0 ; i<myCoordinates.length;i++) {
          if(i % 2 == 0) {
              mCoord = new LatLng(myCoordinates[i], myCoordinates[i+1]);
              polylineOptions.add(mCoord);
          }
        }

        polylineOptions.width(5).color(Color.RED).geodesic(true);
        googleMap.addPolyline(polylineOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,13));
        //googleMap.setOnPolylineClickListener(this);

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }
}
