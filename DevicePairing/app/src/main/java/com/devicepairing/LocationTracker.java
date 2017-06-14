package com.devicepairing;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import com.google.firebase.database.DatabaseReference;

public class LocationTracker extends Service implements LocationListener {

	private final Context mContext;


	boolean isGPSEnabled = false;

	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	Location location;
	double networkLatitude;
	double networkLongitude;

	double GPSLatitude;
	double GPSLongitude;

	double latitude;
	double longitude;
	boolean pairLoc;

	// Declaring a Location Manager
	protected LocationManager locationManager;
	DatabaseReference mDatabase;

	public LocationTracker(Context context, String button, DatabaseReference mDatabaseIn) {
		this.mContext = context;
		pairLoc= button.equals("Pair");
		mDatabase = mDatabaseIn;
		getLocation();
	}


	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							0,
							0, this);
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							networkLatitude= location.getLatitude();
							networkLongitude = location.getLongitude();
						}
					}
				}
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								0,
								0, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								GPSLatitude = location.getLatitude();
								GPSLongitude = location.getLongitude();
							}
						}
					}
				}
				if(networkLatitude == GPSLatitude && networkLongitude == GPSLongitude){
					latitude=GPSLatitude;
					longitude = GPSLongitude;
				}else{
					latitude = (networkLatitude+GPSLongitude) /2;
					longitude = (networkLongitude+GPSLongitude) /2;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}


	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(LocationTracker.this);
		}
	}


	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}


	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}

		return longitude;
	}


	public boolean canGetLocation() {
		return this.canGetLocation;
	}



	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		alertDialog.setTitle("GPS is settings");

		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {

		if(pairLoc){
			mDatabase.child("pairLat").setValue(location.getLatitude());
			mDatabase.child("pairLong").setValue( location.getLongitude());
		}else if(!pairLoc){
			mDatabase.child("acceptLat").setValue(location.getLatitude());
			mDatabase.child("acceptLong").setValue( location.getLongitude());
		}



		//Toast.makeText(mContext, "New Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
