package com.mpy;

import android.app.*;
import android.os.*;
import android.content.*;
import java.io.*;
import android.text.*;
import android.widget.*;
import android.graphics.*;
import android.*;
import android.content.pm.*;

public class MainActivity extends Activity 
{
	ProgressDialog pd;
	int REQ_EXT_PERMIT=7;
	public static String CACHE_FOLDER="Cache_Folder";
	String url="https://cdn.pixabay.com/photo/2014/09/20/23/44/website-454460_1280.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		getPermit();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ImageIntentService.TRANSACTION_DONE);
		registerReceiver(imageReceiver, intentFilter);

		String tmpLocation = Environment.getExternalStorageDirectory().toString() + 
			"/" + CACHE_FOLDER;
		File cacheDir = new File(tmpLocation);
		if (!cacheDir.exists())
		{
			cacheDir.mkdir();
		}

		Intent i = new Intent(this, ImageIntentService.class);
		i.putExtra("url", url);
		startService(i);

		pd = new ProgressDialog(this);
		pd.setTitle("Fetching Image");
		pd.setMessage("Go intent service go!");
		pd.show();

	}
	private BroadcastReceiver imageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String location = intent.getExtras().getString("location");
			if (TextUtils.isEmpty(location))
			{
				String failedString = "Failed to download image";
				Toast.makeText(context, failedString , Toast.LENGTH_LONG).show();
			}
			File imageFile = new File(location);
			if (!imageFile.exists())
			{
				pd.dismiss();
				String downloadFail = "Unable to Download file :-(";
				Toast.makeText(context, downloadFail, Toast.LENGTH_LONG);
				return;
			}
			Bitmap b = BitmapFactory.decodeFile(location);
			ImageView iv = (ImageView)findViewById(R.id.remote_image);
			iv.setImageBitmap(b);
			pd.dismiss();
		}
	};

    public void getPermit()
	{
		requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXT_PERMIT);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		// TODO: Implement this method
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQ_EXT_PERMIT && grantResults.length > 0)
		{
			if (checkSelfPermission(
					Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{		
				Toast.makeText(this, "please allow Storage permission", Toast.LENGTH_LONG).show();
			}
		}
	}

}
