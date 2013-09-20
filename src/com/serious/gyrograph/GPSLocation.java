package com.serious.gyrograph;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class GPSLocation extends Location
{
	public GPSLocation(Context context)
	{
		this("GPSLocationProvider");
		
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//String locationProvider = locationManager.getBestProvider(new Criteria(), false);
		locationManager.requestLocationUpdates("gps", GyroGraph.RECORDING_RATE, GyroGraph.RECORDING_DISTANCE_OFFSET, GyroGraph.getInstance());
	}

	public GPSLocation(String provider)
	{
		super(provider);
	}
}

