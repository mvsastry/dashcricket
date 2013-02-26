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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import com.google.android.apps.dashclock.api.ExtensionData;

/**
 * A class representing a live cricket score. It is filled from a {@link JSONObject}.
 */
public class LiveScore {
	private static final String EMPTY = "";

	public final String id;
	public final String teamOneName;
	public final String teamOneAbbreviation;
	public final String teamTwoName;
	public final String teamTwoAbbreviation;
	public final String matchTitle;
	public final String townName;
	public final String liveScorecardText;
	public final String liveScorecardLink;
	public final String result;

	public LiveScore(JSONObject json) {
		id = json.optString("object_id", EMPTY);
		teamOneName = json.optString("team1_name", EMPTY);
		teamOneAbbreviation = json.optString("team1_abbrevation", EMPTY);
		teamTwoName = json.optString("team2_name", EMPTY);
		teamTwoAbbreviation = json.optString("team2_abbrevation", EMPTY);
		matchTitle = json.optString("cms_match_title", EMPTY);
		townName = json.optString("town_name", EMPTY);
		liveScorecardText = StringEscapeUtils.unescapeHtml4(
				json.optString("live_scorecard_text", EMPTY));
		liveScorecardLink = json.optString("live_scorecard_link", EMPTY);
		result = json.optString("result", EMPTY);
	}

	/**
	 * @return The string for {@link ExtensionData#status(String)}
	 */
	public String getStatus() {
		return liveScorecardText.replace(teamOneName, teamOneAbbreviation)
				.replace(teamTwoName, teamTwoAbbreviation);
	}

	/**
	 * @return The string for {@link ExtensionData#expandedTitle(String)}
	 */
	public String getExpandedTitle() {
		return liveScorecardText;
	}

	/**
	 * @return The string for {@link ExtensionData#expandedBody(String)}
	 */
	public String getExpandedBody() {
		return matchTitle + ", " + townName;
	}

	/**
	 * @return The summary string for {@link LiveScoreIdPreference}
	 */
	public String getPreferenceEntry() {
		return teamOneAbbreviation + " v " + teamTwoAbbreviation + ", " + matchTitle;
	}
}
