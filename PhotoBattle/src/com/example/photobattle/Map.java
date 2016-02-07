package com.example.photobattle;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
//a
public class Map extends Activity {
	ScrollView s;
	LinearLayout l;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);*/
		setContentView(R.layout.activity_map);
		s=(ScrollView) findViewById(R.id.scroll);
		l=(LinearLayout) this.findViewById(R.id.l);
		//Récupération des fichiers jpeg dans le dossier
		
		File f2 = new File(Environment.getExternalStorageDirectory() +
	                File.separator + "photoBattle");
		 File[] fichiers = f2.listFiles();
		 
		 int f=0;
		 Vector<File> fichierJpeg=new  Vector<File>(15);
		 for(int i=0; i<fichiers.length;i++)
		 {
			 
			String ext="";
			int k=fichiers[i].getName().length()-1;
			char p=fichiers[i].getName().charAt(k);
			while(p!='.' && k>=0)
			{
				ext+=p;
				k--;
				p=fichiers[i].getName().charAt(k);
			}
			 if(ext.equals("gpj"))
			 {
				 fichierJpeg.add(fichiers[i]);
				 f++;
				 LayoutParams lparams = new LayoutParams(
				 LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 ImageView tv=new ImageView(this);
				 tv.setLayoutParams(lparams);
				 Bitmap myBitmap = BitmapFactory.decodeFile(fichiers[i].getAbsolutePath());
			     tv.setImageBitmap(myBitmap);
				 this.l.addView(tv);
				 
			 }
			 
		 }
		 
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
