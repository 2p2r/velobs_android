package org.deuxpiedsdeuxroues.velobs;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapActivity extends FragmentActivity implements GoogleMap.OnMapClickListener {


    private GoogleMap mMap;
    private Marker leMarker = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();

        int height = 0 ;

        height = display.getHeight();



        if ((height>0)&&(height<801)) {
            setContentView(R.layout.activity_map_small);

        } else {

            setContentView(R.layout.activity_map);
        }

        setUpMapIfNeeded();

        final Context ct = this ;

        Button ok = (Button) findViewById(R.id.okMapButton);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (leMarker != null) {
                    VelobsSingleton.getInstance().lati = String.valueOf(leMarker.getPosition().latitude);
                    VelobsSingleton.getInstance().longi = String.valueOf(leMarker.getPosition().longitude);
                } else {
                    VelobsSingleton.getInstance().lati = null;
                    VelobsSingleton.getInstance().longi = null;
                    Toast.makeText(ct, "Aucun lieu n'a été choisi", Toast.LENGTH_LONG).show();
                }

                finish();

            }
        });


    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
        if (leMarker!=null) {
            leMarker.remove();
        }
        leMarker = mMap.addMarker(new MarkerOptions()
                .position(arg0)
                .draggable(false));
    }


    private void setUpMapIfNeeded() {

        if (mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        if (mMap != null) {

            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mMap.setOnMapClickListener(this);

            if ((VelobsSingleton.getInstance().lati!=null)&&(VelobsSingleton.getInstance().longi!=null)) {
                CameraPosition positionInit =
                        new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(VelobsSingleton.getInstance().lati),
                                        Double.parseDouble(VelobsSingleton.getInstance().longi)))
                                .zoom(15F)
                                .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(positionInit));
                leMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(VelobsSingleton.getInstance().lati),
                                Double.parseDouble(VelobsSingleton.getInstance().longi)))
                        .draggable(false));
            } else {

                CameraPosition positionInit =
                        new CameraPosition.Builder()
                                .target(new LatLng(43.6045, 1.4440))
                                .zoom(12F)
                                .build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(positionInit));
            }

        }

    }



}
