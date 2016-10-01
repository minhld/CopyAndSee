package com.minhld.copynsee.business;

import java.util.List;

import com.minhld.copynsee.R;
import com.minhld.copynsee.data.Word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

public class WordAdapter extends ArrayAdapter<Word> {
	Context context;
	List<Word> wordList;
	LayoutInflater mLayoutInflatter;
	
	public WordAdapter(Context context, List<Word> objects){
		super(context, R.layout.item_suggest_word, objects);
		this.context = context;
		this.wordList = objects;
		mLayoutInflatter = LayoutInflater.from(this.context);
	}
	
	@Override
	public Word getItem(int position) {
		return this.wordList.get(position);
	}
	
	@Override
	public int getCount() {
		return this.wordList.size();
	}
	
	@Override
	public int getPosition(Word item) {
		return this.wordList.indexOf(item);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		Word word = this.wordList.get(position);
		
		if (convertView == null || convertView.getTag() == null){
			convertView = mLayoutInflatter.inflate(R.layout.item_suggest_word, parent, false);
			
			vh = new ViewHolder();
			vh.mWord = (TextView)convertView.findViewById(R.id.word);
		}else{
			vh = (ViewHolder)convertView.getTag();
		}
		
		vh.mWord.setOnClickListener(new WordClickListener(word));
		vh.mWord.setText(word.getWord());
		
		return convertView;
	}
	
	class WordClickListener implements View.OnClickListener {
		Word word;
		public WordClickListener(Word word){
			this.word = word;
		}
		
		@Override
		public void onClick(View v) {
			
		}
		
	}
	
	class ViewHolder{
		TextView mWord;
	}
}
