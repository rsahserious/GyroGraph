package com.serious.gyrograph;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecBrowserDialog extends Dialog implements OnItemClickListener
{
	GPSMap GPSMapInstance;
	String[] files;
	
	public RecBrowserDialog(Context context, GPSMap parentInstance)
	{
		super(context);
		
		this.GPSMapInstance = parentInstance;
		
		this.setTitle("Select the recording");
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.rec_browser, null, false);
		this.setContentView(view);
		this.setCancelable(true);

		files = new File(GyroGraph.RECORDINGS_DIR).list();
		
		ListView list = (ListView) this.findViewById(R.id.rec_browser_list);
		list.setOnItemClickListener(this);
		list.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, files));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		GPSMapInstance.loadRecording(files[position]);
		this.cancel();
	}
}
