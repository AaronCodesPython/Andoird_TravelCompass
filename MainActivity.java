package com.example.travelcompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView latTextView ;
    TextView longTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView AddressTextView;
    TextView infoText;

    LocationManager locationManager;
    LocationListener locationListener;

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void updateLocatioInfo(Location location){
        latTextView.setText("Latitude: "+ Double.toString(round(location.getLatitude(),4)));
        longTextView.setText("Longitude: "+ Double.toString(round(location.getLongitude(),4)));
        accuracyTextView.setText("Accuracy:  "+ Double.toString(round(location.getAccuracy(),2))+" meter");
        altitudeTextView.setText("Altitude: "+ Double.toString(round(location.getAltitude(),4)));
        infoText.setVisibility(View.GONE);

        String address= "Addresse konnte nicht gefunden werden!";
        Geocoder geocoder = new Geocoder(this, Locale.GERMAN);
        try{
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(addressList!= null && addressList.size() >0){
                address="Address: \n";
                if(addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare()+"\n";
                }
                if(addressList.get(0).getLocality() != null){
                    address += addressList.get(0).getLocality()+"\n";
                }
                if(addressList.get(0).getPostalCode() != null){
                    address += addressList.get(0).getPostalCode()+"\n";
                }
                if(addressList.get(0).getAdminArea() != null){
                    address += addressList.get(0).getAdminArea()+"\n";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        AddressTextView.setText(address);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.lattextView);
        longTextView = findViewById(R.id.longtextView);
        accuracyTextView = findViewById(R.id.accTextview);
        altitudeTextView = findViewById(R.id.altitudetextView);
        AddressTextView = findViewById(R.id.addresstextView);
        infoText = findViewById(R.id.textView9);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocatioInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0 , locationListener);
            Location lastknowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastknowLocation!=null){
                updateLocatioInfo(lastknowLocation);
            }
        }
    }
}