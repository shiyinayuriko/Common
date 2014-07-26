package com.common.imageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageLoader {
    private static HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();    
    File imageCacheDir;    
    String imageCacheDirName;
    private boolean isWorking ;

	/**
     * 
     * @param imageCacheDir
     *imageCacheDir = new File("/mnt/sdcard/test/");    
     *    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     */
    public ImageLoader(String imageCacheDir)    
    {    
        imageCacheDirName=imageCacheDir;
        isWorking = false;
        this.imageCacheDir = new File(imageCacheDir);
        if(!this.imageCacheDir.exists()) {    
        	this.imageCacheDir.mkdirs();    
        }    
        
    }    
    
    public Bitmap loadLocalBitmap(String imageURL){
    	
		if (imageCache.containsKey(imageURL)) {
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			Bitmap bitmap = reference.get();
			if (bitmap != null) {
				return bitmap;
			}
		}else {    
            String bitmapName = getBitmapName(imageURL);    
            File[] cacheFiles =(imageCacheDir.listFiles());    
			if (cacheFiles != null) {
				for (File f:cacheFiles) {
					if (bitmapName.equals(f.getName())) {
						return BitmapFactory.decodeFile(f.getAbsolutePath());
					}
				}
			}
        }
		return null;    
	}
    
    //TODO
    public static String getBitmapName(String imageURL){
        String bitmapName = imageURL.substring(imageURL.lastIndexOf("/") + 1);  
        return bitmapName;
    }
    
    public Bitmap downloadBitmap(String imageURL) throws IOException{
    	isWorking = true;
		URL url = new URL(imageURL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream bitmapIs = connection.getInputStream();
		Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);    
		
        imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap)); 
        
        File bitmapFile = new File(imageCacheDirName+"/"+getBitmapName(imageURL));    
        if(!bitmapFile.exists()) bitmapFile.createNewFile();    
      
        FileOutputStream fos = new FileOutputStream(bitmapFile);    
        bitmap.compress(Bitmap.CompressFormat.PNG,100, fos);    
        fos.close();
        
    	isWorking = false;
		return bitmap;    
	}

    public boolean isWorking() {
		return isWorking;
	}
    
	public Bitmap setBitmap(final ImageView iv,final String url,final Handler handler2){
		Bitmap bitmap = loadLocalBitmap(url);
		if(bitmap!=null) {
			iv.setImageBitmap(bitmap);
			handler2.sendEmptyMessage(0);
			return bitmap;
		}
		
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				iv.setImageBitmap((Bitmap)msg.obj);   
				handler2.sendEmptyMessage(1);
			}
		};
		
		new Thread(){
			@Override
			public void run() {
				try {
					Bitmap bitmap = downloadBitmap(url);
					Message msg = handler.obtainMessage(0, bitmap);
					handler.sendMessage(msg);
				} catch (IOException e) {}		
			}
		}.start();
		return null;
	}
	public Bitmap setBitmap(ImageView iv,String url){
		return setBitmap(iv,url,new Handler());
	}
}
