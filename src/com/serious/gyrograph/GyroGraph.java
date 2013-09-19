package com.serious.gyrograph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GyroGraph extends Activity implements OnClickListener, LocationListener
{
	static final int RECORDING_RATE = 2000;
	static final int RECORDING_DISTANCE_OFFSET = 5;
	
	static final String RECORDINGS_DIR = "mnt/sdcard/_recordings/";
	
	static GyroGraph instance = null;
	GraphView graphView = null;
	
	SoundPool soundPool;
	
	Button incSensButton;
	Button decSensButton;
	Button incRMSButton;
	Button decRMSButton;
	Button mapButton;
	Button recordingButton;
	
	GPSLocation location;
	
	boolean recording = false;
	
	List<StreetData> recordingReadings = new ArrayList<StreetData>();
	
	private GyroGraph()
	{
		instance = this;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		graphView = new GraphView(this);

		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View buttonsView = inflater.inflate(R.layout.activity_gyro_graph, null);
		
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(graphView);
		layout.addView(buttonsView);
		
		setContentView(layout);
		
		incSensButton = (Button) findViewById(R.id.buttonSensInc);
		decSensButton = (Button) findViewById(R.id.buttonSensDec);
		incRMSButton = (Button) findViewById(R.id.buttonRMSInc);
		decRMSButton = (Button) findViewById(R.id.buttonRMSDec);
		mapButton = (Button) findViewById(R.id.buttonMap);
		recordingButton = (Button) findViewById(R.id.buttonRecording);
		
		incSensButton.setOnClickListener(this);
		decSensButton.setOnClickListener(this);
		incRMSButton.setOnClickListener(this);
		decRMSButton.setOnClickListener(this);
		mapButton.setOnClickListener(this);
		recordingButton.setOnClickListener(this);
		
		location = new GPSLocation(getApplicationContext());
		
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS)
			Toast.makeText(this, "Google Play Services DON'T AVAILABLE !!!", Toast.LENGTH_LONG).show();
		
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		soundPool.load(this, R.raw.update_beep, 1);
	}

	@Override
	public void onClick(View arg0)
	{
		switch(arg0.getId())
		{
			case R.id.buttonSensInc:
			{
				graphView.setSensivity(graphView.getSensivity() + 5);
				Toast.makeText(this, "Sensivity: " + graphView.getSensivity(), Toast.LENGTH_SHORT).show();
				
				break;
			}
			
			case R.id.buttonSensDec:
			{
				if(graphView.getSensivity() > 5)
				{
					graphView.setSensivity(graphView.getSensivity() - 5);
					Toast.makeText(this, "Sensivity: " + graphView.getSensivity(), Toast.LENGTH_SHORT).show();
				}
				
				break;
			}
			
			case R.id.buttonRMSInc:
			{
				graphView.setRMSLength(graphView.getRMSLength() + 25);
				Toast.makeText(this, "RMS length: " + graphView.getRMSLength(), Toast.LENGTH_SHORT).show();
				
				break;
			}
			
			case R.id.buttonRMSDec:
			{
				if(graphView.getRMSLength() > 25)
				{
					graphView.setRMSLength(graphView.getRMSLength() - 25);
					Toast.makeText(this, "RMS length: " + graphView.getRMSLength(), Toast.LENGTH_SHORT).show();
				}
				
				break;
			}
			
			case R.id.buttonMap:
			{
				Intent intent = new Intent(this, GPSMap.class);
				startActivity(intent);
				
				break;
			}
			
			case R.id.buttonRecording:
			{
				if(!recording)
				{
					startRecording();
					recordingButton.setText("Stop recording");
					graphView.setRecordingIcon(true);
				}
				else
				{
					stopRecording();
					recordingButton.setText("Start recording");
					graphView.setRecordingIcon(false);
				}
				
				break;
			}
		}
	}
	
	private void startRecording()
	{
		recordingReadings.clear();
		recording = true;
		
		Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
	}

	private void stopRecording()
	{
		recording = false;
		
		if(recordingReadings.size() >= 3)
		{
			try {
				Date date = new Date();
				
				String filePath = String.format("rec_%ty-%tm-%td_%tH-%tM-%tS.txt",
						date, date, date, date, date, date);

				FileOutputStream recFile = new FileOutputStream(RECORDINGS_DIR + filePath);
				
				for(StreetData data : recordingReadings)
					recFile.write((data.getColor() + " " + data.getLatitude() + " " + data.getLongitude() + "\n").getBytes());
				
				recFile.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, "Recording stopped (not saved)", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		if(recording)
		{
			recordingReadings.add( new StreetData(graphView.getCurrentRMS(), location.getLatitude(), location.getLongitude()) );
			soundPool.play(R.raw.update_beep, 1.0f, 1.0f, 1, 0, 1.0f);
		}
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}
	
	public static GyroGraph getInstance()
	{
		return instance;
	}
}
