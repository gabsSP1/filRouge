package com.example.photobattle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
//a
public class Map extends Activity {
	ListView s;
	HashMap<Integer, Bitmap> fichierJpeg;
	ActionMode mActionMode;
	int f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		f=0;
		super.onCreate(savedInstanceState);
		/*getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);*/
		setContentView(R.layout.activity_map);
		s=(ListView) findViewById(R.id.scroll);
		/*s.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		s.setMultiChoiceModeListener(new MultiChoiceModeListener() {

		    @Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                          long id, boolean checked) {
		        // Here you can do something when items are selected/de-selected,
		        // such as update the title in the CAB
		    }

		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        // Respond to clicks on the actions in the CAB
		        
		                return false;
		        
		    }

		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate the menu for the CAB
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.menu_app, menu);
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        // Here you can make any necessary updates to the activity when
		        // the CAB is removed. By default, selected items are deselected/unchecked.
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        // Here you can perform updates to the CAB due to
		        // an invalidate() request
		        return false;
		    }
		});*/
		
		
		
		//Récupération des fichiers jpeg dans le dossier
		
		File f2 = new File(Environment.getExternalStorageDirectory() +
	                File.separator + "photoBattle");
		 File[] fichiers = f2.listFiles();
		 
		 fichierJpeg=new  HashMap<Integer, Bitmap>();
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
				 f++;
				 LayoutParams lparams = new LayoutParams(
				 LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				 ImageView tv=new ImageView(this);
				 tv.setLayoutParams(lparams);
				 Bitmap myBitmap = BitmapFactory.decodeFile(fichiers[i].getAbsolutePath());
				 fichierJpeg.put(f,myBitmap);
				 //this.s.addView(tv);
				 
			 }
			 
		 }
		 LayoutParams lparams = new LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		 TextView t=new TextView(this);
		 t.setLayoutParams(lparams);
		 t.setText(Integer.toString(f));
		// s.addView(t);
		 Bitmap list2[] = new Bitmap[fichierJpeg.size()];
		 ArrayAdapterItem adapter =new ArrayAdapterItem(this, R.layout.image_view_layout, fichierJpeg.values().toArray(list2));
		 s.setAdapter(adapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
        case R.id.Delete:
        	if (mActionMode != null) {
                return false;
            }

            // Start the CAB using the ActionMode.Callback defined above
            mActionMode = this.startActionMode(mActionModeCallback);
            s.setSelected(true);

        	break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.menu_action_bar, menu);
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	                return false;
	        
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
}
