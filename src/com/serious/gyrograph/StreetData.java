package com.serious.gyrograph;

public class StreetData
{
	private int color;
	private double latitude;
	private double longitude;
	
	public StreetData(int color, double latitude, double longitude)
	{
		this.color = color;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	
}
