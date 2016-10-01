package com.minhld.copynsee.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;

/**
 * this will help downloading data on different thread
 * 
 * @author minhld
 *
 */
public class DataDownloader extends AsyncTask<Void,Integer,Void>{
	
	public static final int STATUS_FILE_EXISTED = -201;
	public static final int STATUS_DOWNLOAD_FAILED = -202;
	public static final int STATUS_STARTING = 2000;
	public static final int STATUS_FINISHED = 202;
	public static final int STATUS_UNZIP_FILE = 201;
	public static final int STATUS_ONGOING = 200;
	
	private Context context;
	private BookDownloadListener listener;
	
	public void setBookDownloadListener(BookDownloadListener listener){
		this.listener=listener;
	}
	
	public DataDownloader(Context ctx){
		this.context=ctx;
	}
	
	@Override
	protected void onPreExecute(){
	}
	
	@Override
	protected Void doInBackground(Void... params){
		InputStream input = null;
		OutputStream output = null;
		String basePath = Utils.getBasePath(context);
		String dbBasePath = Utils.getDbPath(context);
		String dataUrl = context.getString(R.string.data_url);
		
		String dataFileName = context.getString(R.string.data_file);
		String zipDlPath = basePath + "/" + dataFileName;
		
		String dataAVFileName = context.getString(R.string.data_av_file);
		String dbAVPath = dbBasePath + "/" + dataAVFileName;
		if (new File(dbAVPath).exists()){
			// if the files have already been downloaded, they will
			// not be downloaded again
			publishProgress(STATUS_FILE_EXISTED);
			return null;
		}

		// start downloading
		publishProgress(STATUS_STARTING);
		
		try{
			URL url = new URL(dataUrl);
			HttpsURLConnection connection = (HttpsURLConnection)
										url.openConnection();
	        connection.setDoInput(true);
	        connection.setConnectTimeout(Constant.SYNC_TIMEOUT);
	        connection.connect();
	        
	        int retCode = connection.getResponseCode();
	        if (retCode == Constant.SYNC_CODE_OK){
	        	input = connection.getInputStream();
	        }else{
	        	publishProgress(STATUS_DOWNLOAD_FAILED);
	        	return null;
	        }
	        
	        output = new FileOutputStream(zipDlPath, false);
	        
	        int totalBytesRead = 0;
	        int estimateBytes = connection.getContentLength();
	        byte[] buff = new byte[8192];
	        int bytesRead = 0;
	        while((bytesRead = input.read(buff)) != -1){
	        	output.write(buff, 0, bytesRead);
	        	totalBytesRead += bytesRead;
	        	// update progress 
	        	publishProgress((int)(((double)totalBytesRead /
	        				(double)estimateBytes) * (double)100));
	        }
	        output.flush();
	        output.close();
	        
	        // extract the ZIP file
	        publishProgress(STATUS_UNZIP_FILE);
	        Utils.unzipFile(zipDlPath, dbBasePath);
	        
	        // and delete the saved EPUB file
	        Utils.delete(new File(zipDlPath));
	        
	        publishProgress(STATUS_FINISHED);
			return null;
		}catch(Exception e){
			publishProgress(STATUS_DOWNLOAD_FAILED);
		}finally{
			try {
				if (input != null){
					input.close();
				}
			}catch(IOException e){ }
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		int progVal = values[0];
		if (progVal >= 0 && progVal <= 100){
			listener.update(STATUS_ONGOING, progVal);
		}else{
			listener.update(progVal, -1);
		}
	}
	
	public interface BookDownloadListener{
		public void update(int code, int process);
	}
	
}
