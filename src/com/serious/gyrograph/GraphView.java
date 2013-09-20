package com.serious.gyrograph;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class GraphView extends View implements SensorEventListener
{
	private float sensitivity = 135;
	private float rmsLength = 250;

	List<Float> readings = new ArrayList<Float>();
	int currentRMS;
	boolean tmp = false;
	boolean recording = false;
	
	private SensorManager sensorManager;
	private Sensor gyro;
	
	Paint graphPaint = new Paint();
	Paint rectBgPaint = new Paint();
	Paint rectPaint = new Paint();
	Paint recPaint = new Paint();
	
	public GraphView(Context context)
	{
		super(context);
		
		this.setBackgroundColor(Color.BLACK);
		
		this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		this.gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		this.sensorManager.registerListener(this, this.gyro, SensorManager.SENSOR_DELAY_GAME);
		
		graphPaint.setColor(Color.WHITE);
		graphPaint.setStrokeWidth(3f);
		
		rectBgPaint.setColor(Color.DKGRAY);
		
		recPaint.setColor(Color.RED);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		/* Graph */
		
		int length = readings.size();
		float x = getWidth() + 2;
		float last_y = 0;
		float center_y = getHeight() - 115;
		
		for(int i = length - 1; i >= 0; i--)
		{
			canvas.drawLine(x, center_y - last_y, 
							x, center_y - readings.get(i), graphPaint);
			
			last_y = readings.get(i);
			x -= 2;
		}
		
		/* Color rectangle */
		
		canvas.drawRect(50, 50, getWidth() - 50, (getHeight() / 100) * 35, rectBgPaint);
		
		if(length >= rmsLength)
		{
			float tmp = readings.get(length - 1);
			float rms = 0;
			
			for(int i = length - 2; i > length - rmsLength; i--)
			{
				rms += Math.abs(tmp - readings.get(i));
				tmp = readings.get(i);
			}
			
			rms -= 9;
			if(rms < 0) rms = 0;
			
			float color = rms / (float) rmsLength;
			color *= sensitivity;
			
			if(color < 0)
				color = 0;
			else if(color > 0xFF)
				color = 0xFF;
			
			currentRMS = (int) color;
			
			//Log.d("srs", "rms: " + rms + "  | color: " + color);
			rectPaint.setColor(Color.rgb((int)color, 0xFF - (int)color, 0));
			
			canvas.drawRect(50, 50, (((float) (getWidth() - 100) / 0xFF) * color) + 50, (getHeight() / 100) * 35, rectPaint);
		}
		
		/* Recording icon */
		
		if(recording)
			canvas.drawCircle(1010, 637, 15, recPaint);
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		
    }

    public void onSensorChanged(SensorEvent event)
    {
    	if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
    	{
    		float mixed = 0;
    		
    		mixed += Math.abs(event.values[0]);
    		mixed += Math.abs(event.values[1]);
    		mixed += Math.abs(event.values[2]);
    		
    		readings.add(mixed *= 40);
    		
    		if(readings.size() > 0xFFF)
    			readings.remove(0);
    		
    		//Log.d("srs", "" + mixed);
    		
    		invalidate();
    	}
    }
    
    public float getSensivity()
    {
		return sensitivity;
	}

	public void setSensivity(float sensivity)
	{
		this.sensitivity = sensivity;
	}

	public float getRMSLength()
	{
		return rmsLength;
	}

	public void setRMSLength(float rmsLength)
	{
		this.rmsLength = rmsLength;
	}

	public void setRecordingIcon(boolean recording)
	{
		this.recording = recording;
	}

	public int getCurrentRMS()
	{
		return currentRMS;
	}
	
	
}
