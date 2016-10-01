package com.minhld.copynsee.data;

import java.util.ArrayList;
import java.util.List;

import com.minhld.copynsee.R;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

public class DataProvider {
	static SQLiteDatabase dbConnector = null;
	static DbListener listener;
	
	public static void setDbListener(DbListener listener){
		DataProvider.listener = listener;
	}
	
	/**
	 * open the DB connection in synchronization mode
	 * 
	 * @param context
	 */
	public static void openDbSync(final Context context){
		new AsyncTask<Void,Integer,Void>(){
			boolean loadDbValue = false;
			@Override
			protected Void doInBackground(Void... params) {
				loadDbValue = DataProvider.openDb(context);
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				if (DataProvider.listener != null){
					DataProvider.listener.dbLoaded(values[0], loadDbValue);
				}
			}
			
		}.execute();
		
	}
	
	public static void searchWordListSync(final String word, final int numOfWords){
		
		
		new AsyncTask<Void,Integer,Void>(){
			List<Word> wordList = null;
			
			@Override
			protected Void doInBackground(Void... params) {
				wordList = searchWordList(word, numOfWords);
				return null;
			}
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				if (DataProvider.listener != null){
					DataProvider.listener.dbLoaded(values[0], wordList);
				}
			}
			
		}.execute();
	}
	
	public static List<Word> searchWordList(String word, int numOfWords){
		final String SELECT_WORDS = "SELECT * FROM word_tbl WHERE word LIKE '" + word +"%' LIMIT " + numOfWords;
		
		List<Word> wordList = new ArrayList<Word>();
		Cursor result = dbConnector.rawQuery(SELECT_WORDS, null);
		
		if (result != null){
			Word pWord;
			result.moveToFirst();
			while (!result.isAfterLast()){
				pWord = new Word();
				pWord.setWord(result.getString(0) != null ? result.getString(0) : Constant.EMPTY);
				pWord.setEvMean(result.getBlob(1) != null ? new String(result.getBlob(1)) : Constant.EMPTY);
				pWord.setEvProfMean(result.getBlob(2) != null ? new String(result.getBlob(2)) : Constant.EMPTY);
				pWord.setSameWord(result.getBlob(3) != null ? new String(result.getBlob(3)) : Constant.EMPTY);
				pWord.setEeMean(result.getBlob(4) != null ? new String(result.getBlob(4)) : Constant.EMPTY);
				pWord.setMean(result.getString(5) != null ? result.getString(5) : Constant.EMPTY);
				wordList.add(pWord);
				result.moveToNext();
			}
			
			result.close();
		}		
		return wordList;
	}
	
	/**
	 * this will open a database 
	 * the connector will then be hold for later usage
	 * 
	 * @param context
	 * @return
	 */
	public static boolean openDb(Context context){
		String evFileName = context.getString(R.string.data_av_file);
		String evPath = Utils.getDbPath(context) + "/" + evFileName;
		
		try{
			dbConnector = SQLiteDatabase.openDatabase(
						evPath, null, SQLiteDatabase.OPEN_READWRITE);
			return true;
		}catch(SQLiteException e){
			return false;
		}
	}
	
	/**
	 * this will close the database 
	 * 
	 * @return
	 */
	public static boolean closeDb(){
		if (dbConnector != null){
			dbConnector.close();
			return true;
		}
		return false;
	}
	
	public interface DbListener{
		public void dbLoaded(int status, Object... data);
	}
}
