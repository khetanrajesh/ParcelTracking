package com.project.parceltracking;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ParcelDetailsActivity extends AppCompatActivity {

	Toolbar toolbar;

	int parcelId;

	ImageView parcelImage, refreshLocation;
	TextView parcelName, parcelPrice, parcelType, parcelWeight, parcelPhone,
			parcelDate, parcelQuantity, parcelColor;

	LatLng parcelLatLng;

	CacheImage ci = CacheImage.getInstance();

	ImageDownloader imagedownloader = new ImageDownloader();

	private GoogleMap googlemap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parcel_detail);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			parcelId = extras.getInt("parcelId");
		}

		initialize();

	}

	private void initialize() {
		// TODO Auto-generated method stub

		ParcelItem pItem = null;

		for (ParcelItem item : ParcelArrayList.parcelArray) {

			if (item.getParcelId() == parcelId) {

				pItem = item;

				break;
			}

		}

		final ParcelItem p = pItem;

		parcelImage = (ImageView) findViewById(R.id.parcelImage);

		final Bitmap bitmap = ci.getBitmapFromMemCache(p.getParcelId() + "");
		if (bitmap != null) {
			parcelImage.setImageBitmap(bitmap);
		} else {

			imagedownloader.download(p.getParcelImage(), parcelImage,
					p.getParcelId() + "");

		}

		parcelName = (TextView) findViewById(R.id.parcelName);
		parcelName.setText(p.getParcelName());

		parcelPrice = (TextView) findViewById(R.id.parcelValue);
		parcelPrice.setText(p.getParcelPrice());

		parcelType = (TextView) findViewById(R.id.parcelType);
		parcelType.setText(p.getParcelType());

		parcelWeight = (TextView) findViewById(R.id.parcelWeight);
		parcelWeight.setText("" + p.getParcelWeight());

		parcelPhone = (TextView) findViewById(R.id.parcelPhone);
		parcelPhone.setText(p.getParcelPhone());

		parcelQuantity = (TextView) findViewById(R.id.parcelQuantity);
		parcelQuantity.setText(p.getParcelQuantity());

		parcelColor = (TextView) findViewById(R.id.parcelColor);
		parcelColor.setBackgroundColor(Color.parseColor(p.getParcelColor()));

		refreshLocation = (ImageView) findViewById(R.id.refreshLocation);

		refreshLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ParcelsAPI parcelsAPI = new ParcelsAPI();
				JSONArray parcels = null;
				String parcelName;
				LatLng parcelLatlng;
				JSONObject object = null;

				try {

					object = parcelsAPI.getParcelDetails();

					parcels = object.getJSONArray("parcels");

					for (int i = 0; i < parcels.length(); i++) {

						parcelName = (String) ((JSONObject) parcels.get(i))
								.get("name");

						if (parcelName.equalsIgnoreCase(p.getParcelName())) {

							parcelLatlng = new LatLng(
									(double) ((JSONObject) ((JSONObject) parcels
											.get(i)).get("live_location"))
											.getDouble("latitude"),
									(double) ((JSONObject) ((JSONObject) parcels
											.get(i)).get("live_location"))
											.getDouble("longitude"));

							displayMarkerOnMap(parcelLatlng);

							break;

						}

					}

				} catch (Exception E) {

					Log.e("Stack Trace", "Error Parsing JSON", E);

				}

			}
		});

		if (isGooglePlay()) {

			MapFragment mapFragment = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);

			// initialize google map . To perform ui operations on map .
			googlemap = mapFragment.getMap();

			displayMarkerOnMap(p.getParcelLatlng());

		}

	}

	private void displayMarkerOnMap(LatLng l) {
		// TODO Auto-generated method stub

		googlemap.clear();
		Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

		// try to get the address from the location and display
		// the marker
		try {
			String display = "";
			List<Address> address = geocoder.getFromLocation(l.latitude,
					l.longitude, 2);

			if (address.size() > 0) {

				for (int i = 0; i < address.get(0).getMaxAddressLineIndex(); i++) {

					display += address.get(0).getAddressLine(i) + "\n";
				}

			}

			DisplayMarker(l, display, "You Parcel is Here");

			googlemap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 15));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean isGooglePlay() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status == ConnectionResult.SUCCESS) {

			return (true);

		} else {
			GooglePlayServicesUtil.getErrorDialog(status, this, 10).show();
			Toast.makeText(this, "google play services is not available",
					Toast.LENGTH_SHORT).show();

		}

		return (false);

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

	private void DisplayMarker(LatLng l, String Address, String Title) {
		// TODO Auto-generated method stub
		googlemap.addMarker(new MarkerOptions()
				.position(l)
				.title(Title)
				.snippet(Address)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	}

}
