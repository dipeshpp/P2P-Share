package com.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Preference reportBugs = (Preference) findPreference("reportBugs");
		reportBugs
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						Intent emailIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						String[] recipients = new String[] {
								"dipeshpp@gmail.com", "", };
						emailIntent.setType("message/rfc822");
						emailIntent.putExtra(
								android.content.Intent.EXTRA_EMAIL, recipients);
						emailIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT,
								"Feedback");
						// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"This is email's message");
						startActivity(Intent.createChooser(emailIntent,
								"Send mail..."));
						// finish();
						return true;
					}

				});
		Preference about = (Preference) findPreference("about");
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				// new
				// AlertDialog.Builder(preference.getContext()).setMessage("About \nP2P Share v1.0").show();
				AlertDialog.Builder alert = new AlertDialog.Builder(preference
						.getContext());

				alert.setTitle("About");
				alert.setMessage("P2P Share v1.0 \nKJSCE");

				alert.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// cancel
							}

						});

				alert.show();

				/*
				 * SharedPreferences customSharedPreference =
				 * getSharedPreferences( "myCustomSharedPrefs",
				 * Activity.MODE_PRIVATE); SharedPreferences.Editor editor =
				 * customSharedPreference .edit();
				 * editor.putString("myCustomPref",
				 * "The preference has been clicked"); editor.commit();
				 */
				return true;
			}

		});

	}

}