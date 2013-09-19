package com.serious.gyrograph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class GPSMap extends FragmentActivity
{
	GoogleMap map;
	List<StreetData> streetData = new ArrayList<StreetData>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch(item.getItemId())
	    {
	        case R.id.load_rec:
	        {
	        	RecBrowserDialog dialog = new RecBrowserDialog(this, this);
	    		dialog.show();
	        	
	            return true;
	        }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void loadRecording(String szFile)
	{
		streetData.clear();
		map.clear();
		
		DataInputStream inputStream;
		BufferedReader reader = null;
		String buffer = "";
		
		try {
			
			inputStream = new DataInputStream(new FileInputStream(GyroGraph.RECORDINGS_DIR + szFile));
			reader = new BufferedReader(new InputStreamReader(inputStream));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			
			while((buffer = reader.readLine()) != null)
			{
				Scanner scanner = new Scanner(buffer);
				Log.d("srs", "scanner: |" + buffer + "|");
				scanner.useDelimiter(" ");
				streetData.add(new StreetData( Integer.parseInt(scanner.next()), Double.parseDouble(scanner.next()), Double.parseDouble(scanner.next()) ));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Toast.makeText(this, "Loading successed (" + streetData.size() + " points)", Toast.LENGTH_SHORT).show();
		
		for(int i = 0; i < streetData.size() - 1; i++)
		{
			map.addPolyline((new PolylineOptions())
		        .add( 	new LatLng(streetData.get(i).getLatitude(), streetData.get(i).getLongitude()),
		        		new LatLng(streetData.get(i + 1).getLatitude(), streetData.get(i + 1).getLongitude())	)
		        .width(5)
		        .color(Color.rgb(streetData.get(i).getColor(), 0xFF - streetData.get(i).getColor(), 0))
		        .geodesic(true));
		}

		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(new LatLng(streetData.get(0).getLatitude(), streetData.get(0).getLongitude()))
	    .zoom(8)
	    .bearing(0)
	    .tilt(0)
	    .build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
}