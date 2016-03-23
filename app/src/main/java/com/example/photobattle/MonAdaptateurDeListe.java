package com.example.photobattle;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MonAdaptateurDeListe extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] itemname;
	private final Bitmap[] imgid;
	public MonAdaptateurDeListe(Activity context, String[] itemname, Bitmap[] bitmaps) {
		super(context, R.layout.image_view_layout, itemname);
		// TODO Auto-generated constructor stub
		
		this.context=context;
		this.itemname=itemname;
		this.imgid=bitmaps;
	}

	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.image_view_layout, null,true);

		ImageView imageView = (ImageView) rowView.findViewById(R.id.image_view_layout);
		TextView extratxt = (TextView) rowView.findViewById(R.id.label);
		imageView.setImageBitmap(imgid[position]);
		if( itemname[position].indexOf('.')>0) {
			itemname[position] = itemname[position].substring(0, itemname[position].indexOf('.'));
		}
		extratxt.setText(itemname[position]);

		return rowView;
		
	}
}

