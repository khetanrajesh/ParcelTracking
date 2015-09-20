package com.project.parceltracking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	Toolbar toolbar;

	AutoCompleteTextView searchBox;

	ListView parcelList;

	ArrayList<ParcelItem> parcelArrayLocal = new ArrayList<ParcelItem>();

	ImageView clearButton;

	TextView apiHits, totalParcels;

	JSONObject object = null;

	CardView sortByValue, sortByWeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initialize() {
		// TODO Auto-generated method stub

		searchBox = (AutoCompleteTextView) findViewById(R.id.searchParcel);

		clearButton = (ImageView) findViewById(R.id.clear);

		setUpSearchBar();

		totalParcels = (TextView) findViewById(R.id.totalParcels);

		apiHits = (TextView) findViewById(R.id.apiHits);

		setUpApiHits();

		parcelList = (ListView) findViewById(R.id.parcelList);

		setUpParcelList();

		setListAdapter(ParcelArrayList.parcelArray);

		parcelList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> adapterView,
							View view, int position, long id) {

						Intent intent = new Intent(MainActivity.this,
								ParcelDetailsActivity.class);
						
						
						ParcelItem item = (ParcelItem) adapterView.getItemAtPosition(position);

						intent.putExtra("parcelId", item.getParcelId());
						startActivity(intent);

					}
				});

		sortByValue = (CardView) findViewById(R.id.card_view6);
		sortByWeight = (CardView) findViewById(R.id.card_view7);

		sortByValue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Collections.sort(parcelArrayLocal,
						ParcelItem.ParcelValueComparator);

				setListAdapter(parcelArrayLocal);

			}
		});

		sortByWeight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Collections.sort(parcelArrayLocal,
						ParcelItem.ParcelWeightComparator);

				setListAdapter(parcelArrayLocal);

			}
		});

	}

	private void setUpApiHits() {
		// TODO Auto-generated method stub

		ParcelsAPI parcelsAPI = new ParcelsAPI();

		try {

			object = parcelsAPI.getAPIHits();

			apiHits.setText("API Hits: " + (String) (object.get("api_hits")));

		} catch (Exception E) {

			Log.e("Stack Trace", "Error Parsing JSON", E);

		}

	}

	private void setUpParcelList() {
		// TODO Auto-generated method stub

		ParcelsAPI parcelsAPI = new ParcelsAPI();
		JSONArray parcels = null;
		String parcelPrice, parcelName, parcelType, parcelPhone, parcelDate, parcelQuantity;
		String parcelImage;
		float parcelWeight;
		LatLng parcelLatlng;

		try {

			object = parcelsAPI.getParcelDetails();

			parcels = object.getJSONArray("parcels");

			Log.d("No of parcels ", " " + parcels.length());

			totalParcels.setText("Total Parcels: " + parcels.length());

			for (int i = 0; i < parcels.length(); i++) {

				parcelName = (String) ((JSONObject) parcels.get(i)).get("name");

				parcelPrice = (String) ((JSONObject) parcels.get(i))
						.get("price");
				parcelImage = (String) ((JSONObject) parcels.get(i))
						.get("image");

				parcelType = (String) ((JSONObject) parcels.get(i)).get("type");

				parcelPhone = (String) ((JSONObject) parcels.get(i))
						.get("phone");

				parcelDate = (String) ((JSONObject) parcels.get(i)).get("date");

				parcelQuantity = (String) ((JSONObject) parcels.get(i))
						.get("quantity");

				parcelLatlng = new LatLng(
						(double) ((JSONObject) ((JSONObject) parcels.get(i))
								.get("live_location")).getDouble("latitude"),
						(double) ((JSONObject) ((JSONObject) parcels.get(i))
								.get("live_location")).getDouble("longitude"));

				String weight = (String) ((JSONObject) parcels.get(i))
						.get("weight");

				parcelWeight = Float.parseFloat(weight.substring(0,
						weight.length() - 2));

				ParcelArrayList.parcelArray
						.add(new ParcelItem(parcelImage, parcelWeight,
								parcelLatlng, parcelPrice, parcelName,
								parcelType, parcelPhone, parcelDate,
								parcelQuantity, i));
			}

			parcelArrayLocal = ParcelArrayList.parcelArray;

		} catch (Exception E) {

			Log.e("Stack Trace", "Error Parsing JSON", E);

		}

	}

	private void setUpSearchBar() {
		// TODO Auto-generated method stub

		searchBox.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {

					clearButton.setVisibility(View.GONE);

				}

				if (hasFocus) {

					clearButton.setVisibility(View.VISIBLE);

					clearButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							searchBox.setText("");

						}
					});

				}
			}
		});

		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				
				ArrayList<ParcelItem> parcelArrayfiltered = new ArrayList<ParcelItem>();

				for (ParcelItem item : ParcelArrayList.parcelArray) {
					if (item.getParcelName() != null
							&& (item.getParcelName().toUpperCase(Locale
									.getDefault())).contains(s.toString()
									.toUpperCase(Locale.getDefault()))) {

						parcelArrayfiltered.add(item);

					}
					// something here
				}

				setListAdapter(parcelArrayfiltered);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void setListAdapter(ArrayList<ParcelItem> parcelArray) {
		// TODO Auto-generated method stub
		ParcelListAdapter adapter = new ParcelListAdapter(MainActivity.this,
				R.layout.parcel_list_item, parcelArray);

		parcelList.setAdapter(adapter);
	}

	/* class BitmapDownloaderTask, see below */
}
