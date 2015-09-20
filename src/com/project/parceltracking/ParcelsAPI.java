package com.project.parceltracking;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ParcelsAPI {


	private static final String LOG_TAG = "PorteApp";
	private static final String API_BASE = "https://porter.0x10.info/api";
	private static final String Resource = "/parcel";

	public JSONObject getParcelDetails() {

		GetParcelTask g = new GetParcelTask();
		try {

			JSONObject x = g.execute("list_parcel").get();

			return x;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	
	public JSONObject getAPIHits() {

		GetParcelTask g = new GetParcelTask();
		try {

			JSONObject x = g.execute("api_hits").get();

			return x;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	private class GetParcelTask extends AsyncTask<String, Integer, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(JSONObject result) {

			super.onPostExecute(result);

		}

		@Override
		protected JSONObject doInBackground(String... query) {

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder(API_BASE + Resource);

				sb.append("?type=json");
				sb.append("&query=" + query[0]);

				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");

				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, "Error processing Porter API URL", e);
				return null;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error connecting to porter parcel API", e);
				return null;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());

				Log.d("Result", jsonObj.toString());

				return jsonObj;
			} catch (JSONException e) {
				Log.e(LOG_TAG, "Cannot process JSON results", e);
			}

			return null;
			// TODO Auto-generated method stub

		}

	};

}
