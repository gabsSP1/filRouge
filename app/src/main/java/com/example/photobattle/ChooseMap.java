package com.example.photobattle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

//a
public class ChooseMap extends Activity {
	ViewFlipper viewFlipper;
	ArrayList<File> listPhoto;
	Button bDelete;
	Button importPicture;
	Button takePicture;
	Button edit;
	Button play;
	Button backButton;
	private float lastX;
	ProgressBar loading;
	int nbMap;
	String pictureName;
	static int currentPosition;
	final static String CURRENT_FILE = "selcted_file";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		onWindowFocusChanged(true);
		setContentView(R.layout.activity_map);
		listPhoto = new ArrayList<File>();
		FileManager.initialyzeTreeFile();
		initComponent();
		//Récupération des fichiers jpeg dans le dossier
		setNbMap(0);

		loadList();
        currentPosition=viewFlipper.getDisplayedChild();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.layout.activity_menu_app, menu);
		return true;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case 89:

				if (data != null && resultCode == Activity.RESULT_OK) {

					try {
						Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
						FileManager.saveBitmap(bm, FileManager.PICTURE_PATH, pictureName);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				break;

			case 55:

				try {

					Bitmap bm = BitmapFactory.decodeFile(FileManager.PICTURE_PATH + File.separator + pictureName);
				} catch (Exception e) {
				}
				break;

			default:
		}


	}

	//Load the list of the picture
	void loadList() {
		File f2 = new File(FileManager.PICTURE_PATH);
		File[] fichiers = f2.listFiles();
		listPhoto.clear();
        viewFlipper.removeAllViews();
		for (int i = 0; i < fichiers.length; i++) {
			String ext = "";
			int k = fichiers[i].getName().length() - 1;
			char p = fichiers[i].getName().charAt(k);
			while (p != '.' && k > 0) {
				ext += p;
				k--;
				p = fichiers[i].getName().charAt(k);
			}
			if (ext.equals("gpj")) {
				if (fichiers[i].getTotalSpace() > 20) {
					listPhoto.add(fichiers[i]);
					boolean isnumber = true;
					boolean change = false;
					int m=4;
					while (m < fichiers[i].getName().length() && isnumber) {
						try {
							if (Integer.parseInt(fichiers[i].getName().substring(3, m)) >= nbMap) {
								nbMap = Integer.parseInt(fichiers[i].getName().substring(3, m));
								change = true;
							}
						} catch (Exception e) {
							isnumber = false;
						}
						m++;
					}
					if (change) {
						nbMap++;
					}
					setNbMap(nbMap);

				} else {
					(new File(FileManager.THRESHOLD_PATH + File.separator + fichiers[i].getName())).delete();
					fichiers[i].delete();

				}
			}
		}
        loadView();
	}

	public void initComponent() {
		bDelete = (Button) findViewById(R.id.button_delete);
		//imageSelected = (ImageView) findViewById(R.id.imageSelected);
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		/*viewFlipper.setInAnimation(this, android.R.anim.fade_in);
		viewFlipper.setOutAnimation(this, android.R.anim.fade_out);*/
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent touchevent){


                switch (touchevent.getAction())
                {
                    // when user first touches the screen to swap
                    case MotionEvent.ACTION_DOWN:
                    {
                        lastX = touchevent.getX();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        float currentX = touchevent.getX();

                        // if left to right swipe on screen
                        if (lastX < currentX)
                        {
                            // If no more View/Child to flip
                            if (viewFlipper.getDisplayedChild() == 0)
                                break;

                            // set the required Animation type to ViewFlipper
                            // The Next screen will come in form Left and current Screen will go OUT

                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                            // Show the next Screen
                            viewFlipper.showPrevious();
                        }

                        // if right to left swipe on screen
                        if (lastX > currentX)
                        {
                            if (viewFlipper.getDisplayedChild()==viewFlipper.getChildCount()-1)//viewFlipper.getDisplayedChild() == 1 ||(viewFlipper.getDisplayedChild() == 0) && viewFlipper.getChildCount()==1)
                                break;
                            // set the required Animation type to ViewFlipper
                            // The Next screen will come in form Right and current Screen will go OUT

                            viewFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                            viewFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                            // Show The Previous Screen
                            viewFlipper.showNext();
                        }
                        break;
                    }
                }
                currentPosition=viewFlipper.getDisplayedChild();
                return false;

            }

        });
		bDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listPhoto.size()!=0) {
					(new File(FileManager.THRESHOLD_PATH + File.separator + listPhoto.get(viewFlipper.getDisplayedChild()).getName())).delete();
					listPhoto.get(viewFlipper.getDisplayedChild()).delete();
					loadList();
                    currentPosition--;
                    viewFlipper.setDisplayedChild(currentPosition);
				}
			}

		});
		backButton=(Button)findViewById(R.id.back_chooseMap);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChooseMap.this.finish();
				ChooseMap.this.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
			}
		});
		play = (Button) findViewById(R.id.button_start);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(listPhoto.size()!=0) {
                    Intent intentMyAccount = new Intent(getApplicationContext(), Game.class);
                    intentMyAccount.putExtra("selected_file", listPhoto.get(viewFlipper.getDisplayedChild()).getName());
                    startActivity(intentMyAccount);
                }
			}
		});

		edit = (Button) findViewById(R.id.button_edit);
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listPhoto.size()!=0) {
					Intent intentMyAccount = new Intent(getApplicationContext(), EditActivity.class);
					intentMyAccount.putExtra("selected_file", FileManager.THRESHOLD_PATH + File.separator + listPhoto.get(viewFlipper.getDisplayedChild()).getName());
					startActivity(intentMyAccount);
				}
			}

		});

		importPicture = (Button) findViewById(R.id.import_picture);
		importPicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 89);
            }

        });

		takePicture = (Button) findViewById(R.id.take_picture);
		takePicture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = new File(FileManager.PICTURE_PATH + File.separator + pictureName);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, 55);
                }
            }
        });
	}

	void setNbMap(int i) {
		nbMap = i;
		pictureName = "map" + Integer.toString(i) + ".jpg";

	}

    public void loadView()
    {
        if(listPhoto.size()==0)
        {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View view = inflater.inflate(R.layout.image_view_layout, null);
            TextView t=new TextView((this));
            t.setText("No map found,\r\n Press the import or take picture Button");
            t.setGravity(Gravity.CENTER);
            viewFlipper.addView(t);
        }
        for(int i=0; i<listPhoto.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.image_view_layout, null);
            Bitmap bm = BitmapFactory.decodeFile(listPhoto.get(i).getPath());
            ImageView image = (ImageView) view.findViewById(R.id.selected_image);
            image.setImageBitmap(bm);
            TextView name = (TextView) view.findViewById(R.id.label);
            name.setText(listPhoto.get(i).getName().substring(0, listPhoto.get(i).getName().indexOf(".")));
            viewFlipper.addView(view,i);
        }
    }

	public void onRestart() {

        super.onRestart();
        if ((new File(FileManager.PICTURE_PATH, pictureName).exists())){
        this.setContentView(R.layout.layout_loading);
        loading = (ProgressBar) findViewById(R.id.progressBar);
            Loading l = new Loading();
            l.execute();
            viewFlipper.setDisplayedChild(viewFlipper.getChildCount()-1);
        }
		else {

            initComponent();
            loadList();
            viewFlipper.setDisplayedChild(currentPosition);
        }
        super.onRestart();

	}



	private class Loading extends AsyncTask<Void, Integer, Void>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			super.onProgressUpdate(values);
			// Mise à jour de la ProgressBar
			loading.setProgress(0);
			loading.setProgress(values[0]);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);

			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;

			BitmapFactory.decodeFile(FileManager.PICTURE_PATH+File.separator+pictureName, bmOptions);

			int scaleFactor = Math.min(bmOptions.outWidth/size.x, bmOptions.outHeight/size.y);
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;

			Bitmap bm = BitmapFactory.decodeFile(FileManager.PICTURE_PATH+File.separator+pictureName, bmOptions);

			publishProgress(10);
			bm=PhotoFilter.grayScale(bm);
			publishProgress(20);
			bm=PhotoFilter.chgtoBandW(bm);
			publishProgress(90);
			OutputStream outStream = null;
			FileManager.saveBitmap(bm, FileManager.THRESHOLD_PATH, pictureName);
			publishProgress(100);
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			setContentView(R.layout.activity_map);
            initComponent();
			loadList();
            viewFlipper.setDisplayedChild(viewFlipper.getChildCount() - 1);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			final View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}

	public void loadImage(String path)
	{
		Bitmap bm= BitmapFactory.decodeFile(FileManager.PICTURE_PATH+File.separator+pictureName);
	}
}




