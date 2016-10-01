package com.minhld.copynsee.business;

import java.util.List;

import com.minhld.copynsee.data.DataProvider;
import com.minhld.copynsee.data.Word;
import com.minhld.copynsee.utils.Constant;

import android.os.AsyncTask;

public class WordsSearchTask extends AsyncTask<Void, Integer, Void>{
	final int MAX_NUMBER_OF_WORDS = 5;
	
	WordsSearchListener searchListener;
	List<Word> wordList;
	String words = Constant.EMPTY;
	
	
	public WordsSearchTask(String words, WordsSearchListener searchListener){
		this.words = words;
		this.searchListener = searchListener;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try{
			wordList = DataProvider.searchWordList(words, MAX_NUMBER_OF_WORDS);
			publishProgress(0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if (searchListener != null){
			searchListener.wordsSearchDone(wordList.size() > 0, wordList);
		}
	}

	public interface WordsSearchListener {
		public void wordsSearchDone(boolean searchResult, List<Word> wordList);
	}
}
