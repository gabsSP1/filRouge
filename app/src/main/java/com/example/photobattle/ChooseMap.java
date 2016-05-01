package com.example.photobattle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photobattle.R;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Mat.*;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ChooseMap extends BaseActivity {
	public static final String EXTRA_IMAGE = "extra_image";
	static List<String> filesName;
	private ImagePagerAdapter mAdapter;
	private ViewPager mPager;
	int nbMap;
	Button bDelete;
	Button importPicture;
	Button takePicture;
	Button edit;
	Button play;
	Button backButton;
	TextView mTextView;
	String pictureName;
	Dialog dialog;
	int position;
	boolean editP;
	Button onLine;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    //Log.i(TAG, "OpenCV loaded successfully");
                    // Create and set View
                    //setContentView(R.layout.);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

	// A static dataset to back the ViewPager adapter

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		FullScreencall();
		setContentView(R.layout.activity_map);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
            //Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
		editP=false;
		setNbMap(0);
		loadList();
		initComponent();
		Sound.resumeMusic();
	}

	public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
		private final int mSize;

		public ImagePagerAdapter(android.support.v4.app.FragmentManager fm, int size) {
			super(fm);
			mSize = size;
		}

		@Override
		public int getCount() {
			return mSize;
		}

		@Override
		public Fragment getItem(int position) {
			return ImageDetailFragment.newInstance(position);
		}
	}

	public void loadBitmap(String name, ImageView imageView, TextView textView) {
		BitmapWorkerTask task = new BitmapWorkerTask(imageView, textView);
		task.execute(name);
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private final WeakReference<TextView> textViewReferences;
		private String data = "";

		public BitmapWorkerTask(ImageView imageView, TextView textView) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			textViewReferences = new WeakReference<TextView>(textView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			data = params[0];
            /*Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);*/
			System.out.println("load " + data);
			return BazarStatic.decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+data, 650);//decodeSampledBitmapFromResource(FileManager.PICTURE_PATH+File.separator+data,1,500);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final TextView textView = textViewReferences.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
					textView.setText(data.substring(0, data.indexOf('.')));
				}

			}
		}
	}

	void loadList() {
		if(mPager!=null)
			mPager.removeAllViews();
		filesName=new ArrayList<>();
		File f2 = new File(FileManager.PICTURE_PATH);
		File[] fichiers = f2.listFiles();
		mTextView=(TextView) findViewById(R.id.no_picture);
		if(fichiers.length != 0)
		{
			mTextView.setVisibility(View.INVISIBLE);
			Arrays.sort(fichiers, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
				}
			});
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
						boolean isnumber = true;
						boolean change = false;
						filesName.add(fichiers[i].getName());
						int m = 4;
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
						;


					} else {
						(new File(FileManager.THRESHOLD_PATH + File.separator + fichiers[i].getName())).delete();
						fichiers[i].delete();

					}
				}
			}
		}

        else
        {
            mTextView.setVisibility(View.VISIBLE);
        }
	}


	public void initComponent() {

		bDelete = (Button) findViewById(R.id.button_delete);
		//imageSelected = (ImageView) findViewById(R.id.imageSelected);

		bDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (filesName.size()!=0) {

					showDialog();

				}
				else
				{
					Toast.makeText(ChooseMap.this, "No file to delete", Toast.LENGTH_SHORT).show();
				}
			}

		});
		onLine =(Button) findViewById(R.id.button_online);
		onLine.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(filesName.size()!=0) {
					Intent intentMyAccount = new Intent(getApplicationContext(), Connect_activity.class);
					intentMyAccount.putExtra("selected_file", filesName.get(mPager.getCurrentItem()));
					BazarStatic.nomMap = filesName.get(mPager.getCurrentItem());
					startActivity(intentMyAccount);

				}
			}
		});
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), filesName.size());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mPager.setCurrentItem(position);
		backButton=(Button)findViewById(R.id.back_chooseMap);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChooseMap.this.finish();
				ChooseMap.this.overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
			}
		});
		play = (Button) findViewById(R.id.button_start);
		play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(filesName.size()!=0) {
					BazarStatic.nomMap = filesName.get(mPager.getCurrentItem());
					Intent intentMyAccount = new Intent(getApplicationContext(), Game.class);
                    intentMyAccount.putExtra("selected_file", filesName.get(mPager.getCurrentItem()));
					startActivity(intentMyAccount);
					ChooseMap.this.finish();
				}
			}
		});

		edit = (Button) findViewById(R.id.button_edit);
		edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (filesName.size()!=0) {
					editP=true;
					position=mPager.getCurrentItem();
					Intent intentMyAccount = new Intent(getApplicationContext(), EditActivity.class);
					intentMyAccount.putExtra("selected_file", FileManager.THRESHOLD_PATH + File.separator + filesName.get(mPager.getCurrentItem()));
					startActivity(intentMyAccount);
				}
			}

		});

		importPicture = (Button) findViewById(R.id.import_picture);
		importPicture.setOnClickListener(new View.OnClickListener() {

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
		takePicture.setOnClickListener(new View.OnClickListener() {

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 89://appel depuis import

                if (data != null && resultCode == Activity.RESULT_OK) {

                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        FileManager.saveBitmap(bm, FileManager.PICTURE_PATH, pictureName);
                        bm = bm.copy(Bitmap.Config.ARGB_8888, true);
                        Mat mImg = new Mat();
                        Utils.bitmapToMat(bm, mImg);
                        Mat edges = new Mat();
                        Imgproc.Canny(mImg,edges,100,100);
                        bm = Bitmap.createBitmap(mImg.cols(), mImg.rows(),Bitmap.Config.ARGB_8888);
                        Mat invertcolormatrix = new Mat(edges.rows(),edges.cols(), edges.type(), new Scalar(255,255,255));
                        Core.subtract(invertcolormatrix, edges, edges);
                        Utils.matToBitmap(edges, bm);
                        FileManager.saveBitmap(bm, FileManager.THRESHOLD_PATH, pictureName);
						bm.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case 55://appel depuis appareil

                try {

                    Bitmap bm = BitmapFactory.decodeFile(FileManager.PICTURE_PATH + File.separator + pictureName);
                    bm = bm.copy(Bitmap.Config.ARGB_8888, true);
                    Mat mImg = new Mat();
                    Utils.bitmapToMat(bm, mImg);
                    Mat edges = new Mat();
                    Imgproc.Canny(mImg,edges,100,100);
                    bm = Bitmap.createBitmap(mImg.cols(), mImg.rows(),Bitmap.Config.ARGB_8888);
                    Mat invertcolormatrix = new Mat(edges.rows(),edges.cols(), edges.type(), new Scalar(255,255,255));
                    Core.subtract(invertcolormatrix, edges, edges);
                    Utils.matToBitmap(edges, bm);
                    FileManager.saveBitmap(bm, FileManager.THRESHOLD_PATH, pictureName);
					bm.recycle();
                } catch (Exception e) {
                }
                break;

            default:
        }
		loadList();
		initComponent();
	}


	private void showDialog() throws Resources.NotFoundException {
		new AlertDialog.Builder(this)
				.setTitle("Delete Map")
				.setMessage("Do you really want to delete this map?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						position=mPager.getCurrentItem()-1;
						(new File(FileManager.THRESHOLD_PATH + File.separator + filesName.get(mPager.getCurrentItem()))).delete();
						(new File(FileManager.PICTURE_PATH + File.separator + filesName.get(mPager.getCurrentItem()))).delete();
						loadList();
						initComponent();
					}
				})
				.setNegativeButton(android.R.string.no, null).show();
	}

	public void onResume() {
		super.onResume();
		loadList();
		initComponent();
		if(editP) {
			mPager.setCurrentItem(position);
			editP=false;
		}

	}
	public void FullScreencall() {
		if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if(Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}
}




