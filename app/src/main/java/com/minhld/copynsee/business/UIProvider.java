package com.minhld.copynsee.business;

import java.util.ArrayList;
import java.util.List;

import com.minhld.copynsee.ui.FloatService;
import com.minhld.copynsee.R;
import com.minhld.copynsee.data.Word;
import com.minhld.copynsee.ui.FloatService;
import com.minhld.copynsee.utils.Constant;
import com.minhld.copynsee.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * This class will provide basic and commonly use function
 * for the UI.
 * 
 * @author minhld
 *
 */
@SuppressLint("InflateParams")
public class UIProvider {
	private static int ID_NOTIFICATION = 2018;

	private static AutoCompleteTextView searchText;
	private static ImageView searchBtn;
	private static TextView suggestText;
	
	private static PopupWindow popupWindow = null;
	
	private static Drawable popupBackground = null;
	
	/**
	 * initialize the floating service with icon
	 * 
	 * @param context
	 */
	public static void initiateFloatingService(Context context){
		// initialize background service
		// to draw background floating icon & listen to clip-board
		context.startService(new Intent(context, FloatService.class));
	}
	
	public static void setClipboard(ClipData clipData){
		String clipboardText = Constant.EMPTY;
		for (int i = 0; i < clipData.getItemCount(); i++){
			clipboardText += clipData.getItemAt(i).getText();
		}
		searchText.setText(clipboardText);
	}
	
	/**
	 * toggle the floating pop-up window
	 * 
	 * @param context
	 * @param anchor
	 * @param forceOpenPopup
	 * 
	 */
	public static void togglePopupWindow(Context context, View anchor,
										boolean forceOpenPopup){
		if (popupWindow == null){
			initiatePopupWindow(context, anchor);
		}else{
			if (popupWindow.isShowing() && !forceOpenPopup){
				popupWindow.dismiss();
			}else if (!popupWindow.isShowing()){
				popupWindow.showAsDropDown(anchor, 0, 1);
			}
		}
	}

	/**
	 * initialize the pop-up window attaching to the floating icon
	 * 
	 * @param context
	 * @param anchor
	 */
	private static void initiatePopupWindow(final Context context, View anchor) {
		try {
			LayoutInflater popupInflater = (LayoutInflater)context.getSystemService(
	        								Context.LAYOUT_INFLATER_SERVICE);
	        View popupView = popupInflater.inflate(R.layout.popup_words, null);
	        
	        int popupWidth = Utils.getFloatingIconSize() * 5;
	        int popupHeight = (int)(popupWidth * 1.2f);
	        popupWindow = new PopupWindow(popupView, popupWidth, popupHeight, true);
	        popupWindow.setOutsideTouchable(true);
			popupBackground = context.getResources().getDrawable(R.drawable.ic_blank);
			popupWindow.setBackgroundDrawable(popupBackground);
	        popupWindow.showAsDropDown(anchor, 0, 1);
	        
	        // search text editor
	        searchText = (AutoCompleteTextView)popupView.findViewById(R.id.searchText);
	        
	        // search button
	        searchBtn = (ImageView)popupView.findViewById(R.id.searchBtn);
	        searchBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					String searchStr = searchText.getText().toString();
					lookupWord(context, searchStr);
				}
	        });
			
//	        suggestText = (TextView)popupView.findViewById(R.id.suggestSpan);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * this will place an icon to the tray of android system
	 * 
	 * @param context
	 */
	public static void createNotification(Context context){
		// create the pending intent, so that it will wake up the 
		// floating icon when user click on its item on waiting bar
		Intent notificationIntent = new Intent(context, FloatService.class);
		PendingIntent pendingIntent = PendingIntent.getService(
								context, 0, notificationIntent, 0);

		String notifClickToStart = context.getString(R.string.notif_click_to_start);
		Notification notification = new Notification.Builder(context).
					    				setContentText(notifClickToStart).
					    				setSmallIcon(R.drawable.ic_launcher).
					    				setWhen(System.currentTimeMillis()).
					    				setContentIntent(pendingIntent).
					    				build();
		notification.flags = Notification.FLAG_AUTO_CANCEL | 
							Notification.FLAG_ONGOING_EVENT;

		NotificationManager notificationManager = (NotificationManager)
						context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ID_NOTIFICATION,notification);
		
		// close floating window if it is available
		if (popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
		}
	}

	/**
	 * this function will analyze the multiple words from user
	 * and look up in dictionary
	 * 
	 * @param context
	 * @param words
	 */
	private static void lookupWord(final Context context, String words){
		new WordsSearchTask(words, new WordsSearchTask.WordsSearchListener() {
			
			@Override
			public void wordsSearchDone(boolean searchResult,
										List<Word> wordList) {
				WordAdapter adapter = new WordAdapter(context, wordList);
				searchText.setAdapter(adapter);
				searchText.showDropDown();
			}
		}).execute();
	}

}
