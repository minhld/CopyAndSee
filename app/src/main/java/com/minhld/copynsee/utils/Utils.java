package com.minhld.copynsee.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.minhld.copynsee.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * this class contains multiple general public functions
 * 
 * @author minhld
 *
 */
@SuppressLint("ShowToast")
public class Utils {
	private static DisplayMetrics dispMetrics;
	private static int floatingIconSize;
	
	private static String basePath = Constant.EMPTY;
	private static String dbPath = Constant.EMPTY;
	
	/**
	 * initialize all parameters
	 * 
	 * @param context
	 */
	public static boolean initUtils(Context context){
		// get device metrics
		dispMetrics = new DisplayMetrics();
		Display display = ((WindowManager) context.getSystemService(
					Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getRealMetrics(dispMetrics);
		
		// get base path 
		getBasePath(context);
		
		return false;
	}
	
	public static DisplayMetrics getDisplayMatrics(Context context){
		if (Utils.dispMetrics == null){
			initUtils(context);
		}
		
		return Utils.dispMetrics;
	}
	
	public static void toast(Context context, int msgId){
		Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, int msgId, int duration){
		Toast.makeText(context, msgId, duration).show();
	}
	
	public static void toast(Context context, String msgStr){
		Toast.makeText(context, msgStr, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, String msgStr, int duration){
		Toast.makeText(context, msgStr, duration).show();
	}

	public static int getFloatingIconSize() {
		return floatingIconSize;
	}
	public static void setFloatingIconSize(int floatingIconSize) {
		Utils.floatingIconSize = floatingIconSize;
	}
	
	public static String unzipFile(String zipFilePath, 
						String outputFolder) throws Exception {
		byte[] buffer = new byte[1024];

		try {
			File folder = new File(outputFolder);
			if (folder.exists()) {
				Utils.delete(folder);
			}
			folder.mkdir();

			File zipFile = new File(zipFilePath);
			ZipInputStream zis = new ZipInputStream(
							new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();
			String outputFile = Constant.EMPTY;
			while (ze != null) {
				// only process file - skip folder
				if (ze.isDirectory()) {
					ze = zis.getNextEntry();
					continue;
				}
				String fileName = ze.getName();
				outputFile = outputFolder + File.separator + fileName;
				File newFile = new File(outputFile);
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();

			String zipFileNameNoExt = zipFile.getName();
			int extOffs = zipFileNameNoExt.lastIndexOf(".");
			zipFileNameNoExt = zipFile.getName().substring(0, extOffs);

			return outputFolder;
		} catch (IOException e) {
			Log.e("Utils.unzipFile()", e.getClass() + ": " + e.getMessage());
			throw e;
		}
	}

	/**
     * delete an non-empty folder recursively
     * 
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException{
     	if(file.isDirectory()){
     		if(file.list().length == 0){
     			file.delete();
    		}else{
    			String files[] = file.list();
    			for (String temp : files){
    				File fileDelete = new File(file,temp);
    				delete(fileDelete);
    			}

    			if(file.list().length == 0){
    				file.delete();
    			}
    		}
     	}else{
    		file.delete();
    	}
    }
    
    /**
     * make a vibration in a short time
     * 
     * @param c
     * @param duration
     */
    public static void vibrate(Context c,int duration){
    	Vibrator vibrator = (Vibrator)c.getSystemService(
    					Context.VIBRATOR_SERVICE);
    	vibrator.vibrate(duration);
    }
    
    public static boolean checkBaseFolder(Context context){
		// check if full base path exists. if it really exists
		// then quit the check and return OK.
		if (!basePath.equals(Constant.EMPTY)){
			return true;
		}
		boolean isFolderCreated = false;
		
		String externalPath = Environment.getExternalStorageDirectory().
						getAbsolutePath() + "/";
		
		basePath = externalPath + context.getString(R.string.asset_cns_folder);
		File p = new File(basePath);
		if (!p.exists()){
			p.mkdir();
			isFolderCreated = true;
		}
		dbPath = basePath + "/" + context.getString(R.string.asset_dict_db_folder);
		p = new File(dbPath);
		if (!p.exists()){
			p.mkdir();
			isFolderCreated = true;
		}
		
		return !isFolderCreated;
	}
    
    /**
	 * get path of application's base folder
	 * @return
	 */
	public static String getBasePath(Context context){
		if (basePath.equals(Constant.EMPTY)){
			checkBaseFolder(context);
		}
		return basePath;
	}

	/**
	 * get dictionary database path
	 * 
	 * @param context
	 * @return
	 */
	public static String getDbPath(Context context){
		if (basePath.equals(Constant.EMPTY)){
			checkBaseFolder(context);
		}
		return dbPath;
	}
	
	/**
	 * sleep for a duration of time<br>
	 * this only works in thread to wait for a while
	 * 
	 * @param duration
	 */
	public static void sleep(int duration){
		try{
			Thread.sleep(duration);
		}catch (Exception e) { }
	}
}
