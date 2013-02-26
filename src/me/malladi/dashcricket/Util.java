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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class Util {
	public static final String TAG = "DashCricket";
	public static String LIVE_SCORES_URL =
			"http://www.espncricinfo.com/ci/content/chrome/ls_jabber.json";

	/**
	 * Fetch the current live scores.
	 * 
	 * @return An array of the current live scores.
	 */
	public static LiveScore[] getLiveScores() {
		try {
			URL url = new URL(LIVE_SCORES_URL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setUseCaches(false);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			StringBuilder json = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				json.append(line);
			}

			JSONArray jsonArray = new JSONArray(json.toString());
			LiveScore[] liveScores = new LiveScore[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); ++i) {
				JSONObject liveScoreJson = (JSONObject) jsonArray.opt(i);
				liveScores[i] = new LiveScore(liveScoreJson);
			}

			return liveScores;
		} catch (Exception e) {
			Log.e(TAG, "exception while fetching live scores", e);
		}
		return null;
	}

	public static void debug(String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, msg);
		}
	}
}
