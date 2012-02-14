/*
 * Copyright (C) 2012 The Serval Project
 *
 * This file is part of the Serval Maps Software
 *
 * Serval Maps Software is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.servalproject.maps;

import java.util.TimeZone;

import org.servalproject.maps.provider.MapItemsContract;

import android.app.Activity;
import android.content.ContentValues;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * an activity to solicit information about a POI
 */
public class NewPoiActivity extends Activity implements OnClickListener{
	
	/*
	 * private class level constants
	 */
	private final boolean V_LOG = true;
	private final String  TAG = "NewPoiActivity";
	
	/*
	 * private class level variables
	 */
	private final int MAX_DESCRIPTION_CHARACTERS = 250;
	private final int MAX_TITLE_CHARACTERS = 50;
	
	TextView txtCharacters;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_poi);
        
        // inform user of the character limit
        txtCharacters = (TextView) findViewById(R.id.new_poi_ui_txt_characters);
        txtCharacters.setText(Integer.toString(MAX_DESCRIPTION_CHARACTERS));
        
        // watch for changes in the text of the description
        TextView mTextView = (TextView) findViewById(R.id.new_poi_ui_txt_description);
        mTextView.addTextChangedListener(descriptionWatcher);
        
        // listen for button presses
        Button mButton = (Button) findViewById(R.id.new_poi_ui_btn_save);
        mButton.setOnClickListener(this);
    }
    
    // keep track of the number of characters remaining in the description
    private final TextWatcher descriptionWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
			int mCharsRemaining = MAX_DESCRIPTION_CHARACTERS - s.length();
			txtCharacters.setText(Integer.toString(mCharsRemaining));
		}
    	
    };

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View v) {
		
		String mMessage;
		
		// work out which element was clicked
		switch(v.getId()) {
		case R.id.new_poi_ui_btn_save:
			// save the new POI
			if(V_LOG) {
				Log.v(TAG, "save new poi button touched");
			}
			
			// validate the title
			TextView mView = (TextView) findViewById(R.id.new_poi_ui_txt_title);
			
			if(TextUtils.isEmpty(mView.getText()) == true) {
				Toast.makeText(this, R.string.new_poi_toast_title_missing, Toast.LENGTH_SHORT).show();
				mView.requestFocus();
				return;
			}
			
			if(mView.getText().length() > MAX_TITLE_CHARACTERS) {
				mMessage = getString(R.string.new_poi_toast_title_too_long);
				Toast.makeText(this, String.format(mMessage, MAX_TITLE_CHARACTERS), Toast.LENGTH_SHORT).show();
				mView.requestFocus();
				return;
			}
			
			String mTitle = mView.getText().toString();
			
			// validate the description
			mView = (TextView) findViewById(R.id.new_poi_ui_txt_description);
			
			if(TextUtils.isEmpty(mView.getText()) == true) {
				Toast.makeText(this, R.string.new_poi_toast_description_missing, Toast.LENGTH_SHORT).show();
				mView.requestFocus();
				return;
			}
			
			if(mView.getText().length() > MAX_DESCRIPTION_CHARACTERS) {
				mMessage = getString(R.string.new_poi_toast_description_too_long);
				Toast.makeText(this, String.format(mMessage, MAX_DESCRIPTION_CHARACTERS), Toast.LENGTH_SHORT).show();
				mView.requestFocus();
				return;
			}
			
			String mDescription = mView.getText().toString();
			
			// add the new POI
			addNewPoi(mTitle, mDescription);
			
			// return to calling activity
			finish();
		}
	}
	
	// add the new POI to the database
	private void addNewPoi(String title, String description) {
		
		// add the new POI to the database
		ContentValues mValues = new ContentValues();
		
		//TODO update to use actual values from Serval Mesh
		mValues.put(MapItemsContract.PointsOfInterest.Table.PHONE_NUMBER, "myphonenumber");
		mValues.put(MapItemsContract.PointsOfInterest.Table.SUBSCRIBER_ID, "mysid");
		mValues.put(MapItemsContract.PointsOfInterest.Table.LATITUDE, 0); //TODO use real latitude
		mValues.put(MapItemsContract.PointsOfInterest.Table.LONGITUDE, 0); // TODO use real longitude
		mValues.put(MapItemsContract.PointsOfInterest.Table.TIMESTAMP, System.currentTimeMillis());
		mValues.put(MapItemsContract.PointsOfInterest.Table.TIMEZONE, TimeZone.getDefault().getID());
		mValues.put(MapItemsContract.PointsOfInterest.Table.TITLE, title);
		mValues.put(MapItemsContract.PointsOfInterest.Table.DESCRIPTION, description);
		
		try {
			Uri newRecord = getContentResolver().insert(MapItemsContract.PointsOfInterest.CONTENT_URI, mValues);
			if(V_LOG) {
				Log.v(TAG, "new POI record created with id: " + newRecord.getLastPathSegment());
			}
		}catch (SQLException e) {
			Log.e(TAG, "unable to add new POI record", e);
			Toast.makeText(this, R.string.new_poi_toast_save_error, Toast.LENGTH_SHORT).show();
		}
	}

}