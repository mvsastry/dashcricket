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
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Settings for DashCricket.
 */
public class DashCricketSettingsActivity extends PreferenceActivity {
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

		// Set the initial summary when the activity is created.
		SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String liveScoreSummary = mSharedPrefs.getString(DashCricket.PREF_LIVE_SCORE_SUMMARY,
				getString(R.string.none));
		LiveScoreIdPreference mLiveScoreIdPref = (LiveScoreIdPreference) findPreference(
				DashCricket.PREF_LIVE_SCORE_ID);
		mLiveScoreIdPref.setSummary(liveScoreSummary);
		mLiveScoreIdPref.setSharedPrefs(mSharedPrefs);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
