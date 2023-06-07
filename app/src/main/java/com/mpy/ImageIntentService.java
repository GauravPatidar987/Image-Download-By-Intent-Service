package com.mpy;
import android.app.*;
import android.content.*;
import java.io.*;
import java.net.*;
import android.util.*;
import android.os.*;
import android.graphics.*;
import android.widget.*;

public class ImageIntentService extends IntentService
{
	public static final String TRANSACTION_DONE = 
	"com.mpy.TRANSACTION_DONE";

	Context cc;
	private String cacheDir;
	public ImageIntentService()
	{
		super("IIS");

		cacheDir = Environment.getExternalStorageDirectory().toString() + "/" + MainActivity.CACHE_FOLDER;
	}
	private void notifyFinished(String location, String remoteUrl)
	{
		Intent i = new Intent(TRANSACTION_DONE);
		i.putExtra("location", location);
		i.putExtra("url", remoteUrl);
		ImageIntentService.this.sendBroadcast(i);
	}
	@Override
	protected void onHandleIntent(Intent intent)
	{
		//Toast.makeText(getApplicationContext(),"sc",Toast.LENGTH_LONG).show();
		String remoteUrl = intent.getExtras().getString("url");

		String location;
		String filename = remoteUrl.substring(
			remoteUrl.lastIndexOf("/") + 1);

		File tmp = new File(
			cacheDir + "/" + filename);
		//Toast.makeText(getApplicationContext(),":"+tmp.getAbsolutePath(),Toast.LENGTH_LONG).show();
		if (tmp.exists())
		{
			Toast.makeText(getApplicationContext(), "exists", Toast.LENGTH_LONG).show();
			location = tmp.getAbsolutePath();
			notifyFinished(location, remoteUrl);
			stopSelf();
			return;
		}
		else
		{
			try
			{
				if (tmp.createNewFile())
				{
					Toast.makeText(getApplicationContext(), "file created", Toast.LENGTH_LONG).show();
				}}
			catch (IOException i)
			{		
				Toast.makeText(getBaseContext(), i.getMessage(), Toast.LENGTH_LONG).show();
			}}
		try
		{
			URL url = new URL(remoteUrl);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			if (httpCon.getResponseCode() != 200)
			{
				Toast.makeText(getBaseContext(), httpCon.getResponseMessage(), Toast.LENGTH_LONG).show();

				throw new Exception("Failed to connect");
			}
			InputStream is = httpCon.getInputStream();
			FileOutputStream fos = new FileOutputStream(tmp);
			writeStream(is, fos);
			fos.flush();
			fos.close();
			is.close();
			location = tmp.getAbsolutePath();
			notifyFinished(location, remoteUrl);
		}
		catch (Exception e)
		{
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void writeStream(InputStream is, FileOutputStream fos)
	{
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
	}
}
