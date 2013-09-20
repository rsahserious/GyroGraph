package com.serious.gyrograph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class GPSMap extends FragmentActivity implements OnClickListener
{
	public static final float DEFAULT_CONTRAST = 1.0f;
	
	GoogleMap map;
	List<StreetData> streetData = new ArrayList<StreetData>();
	float contrast = DEFAULT_CONTRAST;
	
	private enum ViewMode {
		LIVE, RECORDED
	}
	private ViewMode viewMode = ViewMode.LIVE;
	
	Button contrastIncButton;
	Button contrastDecButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);

		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		contrastIncButton = (Button) findViewById(R.id.buttonContrastInc);
		contrastDecButton = (Button) findViewById(R.id.buttonContrastDec);
		contrastIncButton.setOnClickListener(this);
		contrastDecButton.setOnClickListener(this);

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
		viewMode = ViewMode.RECORDED;
		
		streetData.clear();
		
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
		
		updateCurrentRecordingLines();

		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(new LatLng(streetData.get(0).getLatitude(), streetData.get(0).getLongitude()))
	    .zoom(11)
	    .bearing(0)
	    .tilt(0)
	    .build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private void updateCurrentRecordingLines()
	{
		map.clear();
		
		for(int i = 0; i < streetData.size() - 1; i++)
		{
			addLine(streetData.get(i), streetData.get(i + 1));
		}
	}
	
	public void addLine(StreetData startPoint, StreetData endPoint)
	{
		// Calculate the color from start-end point average
		int color = (startPoint.getColor() + endPoint.getColor()) / 2;
		
		map.addPolyline((new PolylineOptions())
	        .add( 	new LatLng(startPoint.getLatitude(), startPoint.getLongitude()),
	        		new LatLng(endPoint.getLatitude(), endPoint.getLongitude())	)
	        .width(5)
	        .color(getColorForLine(color))
	        .geodesic(true));
	}

	private int getColorForLine(int color)
	{
		color *= contrast;
		
		if(color > 0xFF)
			color = 0xFF;
		else if(color < 0)
			color = 0;
		
		return Color.rgb(color, 0xFF - color, 0);
	}

	@Override
	public void onClick(View arg0)
	{
		switch(arg0.getId())
		{
			case R.id.buttonContrastInc:
			{
				if(contrast < 20.0f)
					contrast += 0.1f;
			
				updateCurrentRecordingLines();
				
				DecimalFormat df = new DecimalFormat("#.#");
				Toast.makeText(this, "Contrast: " + df.format(contrast), Toast.LENGTH_SHORT).show();
				
				break;
			}
			
			case R.id.buttonContrastDec:
			{
				if(contrast > 0.1f)
					contrast -= 0.1f;
				
				updateCurrentRecordingLines();
				
				DecimalFormat df = new DecimalFormat("#.#");
				Toast.makeText(this, "Contrast: " + df.format(contrast), Toast.LENGTH_SHORT).show();
				
				break;
			}
		}
	}
}