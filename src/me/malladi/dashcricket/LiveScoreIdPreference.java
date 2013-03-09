/*
 * Copyright 2013 Venkat Malladi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.malladi.dashcricket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * A preference for saving a live score id. Subclasses {@link ListPreference} to fetch the matches
 * in progress upon click.
 */
public class LiveScoreIdPreference extends ListPreference {
	private static SharedPreferences mSharedPrefs;

	public LiveScoreIdPreference(Context context) {
		super(context);
	}

	public LiveScoreIdPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSharedPrefs(SharedPreferences sharedPrefs) {
		mSharedPrefs = sharedPrefs;
	}

	/**
	 * Override the superclass method to fetch live scores upon click.
	 */
	@Override
	protected void onClick() {
		setSummary(R.string.fetching_matches);
		new FetchLiveScoresTask().execute();
	}

	/**
	 * Callback for {link {@link FetchLiveScoresTask}
	 */
	private void onLiveScoresFetched() {
		if (getEntry() != null) {
			setSummary(getEntry().toString());
		} else {
			resetPreference();
		}
		super.onClick();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (getEntry() != null) {
			String summary = getEntry().toString();
			setSummaryAndSaveToSharedPrefs(summary);
		}
	}

	/**
	 * Show the summary in the UI and save it in the {@link SharedPreferences}
	 * 
	 * @param summary The summary to show.
	 */
	public void setSummaryAndSaveToSharedPrefs(String summary) {
		setSummary(summary);
		if (mSharedPrefs != null) {
			mSharedPrefs.edit().putString(DashCricket.PREF_LIVE_SCORE_SUMMARY, summary).commit();			
		}
	}

	/**
	 * Reset the preference.
	 */
	public void resetPreference() {
		setValue(DashCricket.NO_LIVE_SCORE_ID);
		setSummaryAndSaveToSharedPrefs(getContext().getString(R.string.none));
	}

	/**
	 * An {@link AsyncTask} to fetch live scores.
	 */
	class FetchLiveScoresTask extends AsyncTask<Void, Void, LiveScore[]> {
		@Override
		protected LiveScore[] doInBackground(Void... arg0) {
			return Util.getLiveScores();
		}
		
		@Override
		protected void onPostExecute(LiveScore[] liveScores) {
			if (liveScores == null) {
				setSummary(R.string.error_fetch);
			} else if (liveScores.length == 0) {
				resetPreference();
				setSummary(R.string.error_no_matches);
			} else {
				CharSequence[] entries = new CharSequence[liveScores.length];
				CharSequence[] entryValues = new CharSequence[liveScores.length];

				for (int i = 0; i < liveScores.length; ++i) {
					entries[i] = liveScores[i].getPreferenceEntry();
					entryValues[i] = liveScores[i].id;
				}
				setEntries(entries);
				setEntryValues(entryValues);
				onLiveScoresFetched();
			}
		}
	}
}
