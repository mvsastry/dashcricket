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

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Settings for DashCricket.
 */
public class DashCricketSettingsActivity extends PreferenceActivity
		implements OnPreferenceChangeListener {
	SharedPreferences mSharedPrefs;
	ListPreference mCountryPref;
	LiveScoreIdPreference mLiveScoreIdPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Get the saved preferences.
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String country = mSharedPrefs.getString(DashCricket.PREF_COUNTRY,
				DashCricket.ALL_COUNTRIES);
		String liveScoreSummary = mSharedPrefs.getString(DashCricket.PREF_LIVE_SCORE_SUMMARY,
				getString(R.string.none));

		// Listen for changes in the country preference.
		mCountryPref = (ListPreference) findPreference(DashCricket.PREF_COUNTRY);
		mCountryPref.setOnPreferenceChangeListener(this);

		// Set the initial match summary when the activity is created.
		mLiveScoreIdPref = (LiveScoreIdPreference) findPreference(DashCricket.PREF_LIVE_SCORE_ID);
		mLiveScoreIdPref.setSummary(liveScoreSummary);
		mLiveScoreIdPref.setSharedPrefs(mSharedPrefs);

		if (!country.equals(DashCricket.ALL_COUNTRIES)) {
			mLiveScoreIdPref.setEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
		if (preference.getKey().equals(DashCricket.PREF_COUNTRY)) {
			String valueString = value.toString();
			int index = mCountryPref.findIndexOfValue(valueString);
			if ((index >= 0) &&
					(mCountryPref.getEntries() != null) &&
					(index < mCountryPref.getEntries().length)) {
				mCountryPref.setSummary(mCountryPref.getEntries()[index]);
			}
			if (!valueString.equals(DashCricket.ALL_COUNTRIES)) {
				mLiveScoreIdPref.setEnabled(false);
			} else {
				mLiveScoreIdPref.setEnabled(true);
			}
			mLiveScoreIdPref.setSummary(R.string.fetching_matches);
			new FetchLiveScoresTask().execute();
		}
		return true;
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
				mLiveScoreIdPref.setSummary(R.string.error_fetch);
				return;
			} else {
				String countryId = mSharedPrefs.getString(
						DashCricket.PREF_COUNTRY, DashCricket.ALL_COUNTRIES);
				if (!countryId.equals(DashCricket.ALL_COUNTRIES)) {
					for (LiveScore liveScore : liveScores) {
						if (countryId.equals(liveScore.teamOneId) ||
								countryId.equals(liveScore.teamTwoId)) {
							mLiveScoreIdPref.setSummaryAndSaveToSharedPrefs(
									liveScore.getPreferenceEntry());
							mLiveScoreIdPref.setValue(liveScore.id);
							return;
						}
					}
					mLiveScoreIdPref.resetPreference();
					mLiveScoreIdPref.setSummary(R.string.error_no_matches);
				} else {
					String liveScoreId = mSharedPrefs.getString(
							DashCricket.PREF_LIVE_SCORE_ID, DashCricket.NO_LIVE_SCORE_ID);
					for (LiveScore liveScore : liveScores) {
						if (liveScoreId.equals(liveScore.id)) {
							mLiveScoreIdPref.setSummaryAndSaveToSharedPrefs(
									liveScore.getPreferenceEntry());
							mLiveScoreIdPref.setValue(liveScore.id);
							return;
						}
					}
					mLiveScoreIdPref.resetPreference();					
				}
			}
		}
	}
}
