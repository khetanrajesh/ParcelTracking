package com.project.parceltracking;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ParcelListAdapter extends ArrayAdapter<ParcelItem> {

	private ArrayList<ParcelItem> objects;

	ImageDownloader imagedownloader = new ImageDownloader();

	CacheImage ci = CacheImage.getInstance();

	public ParcelListAdapter(Context context, int textViewResourceId,
			ArrayList<ParcelItem> objects) {

		super(context, textViewResourceId, objects);
		this.objects = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		// assign the view we are converting to a local variable
		View v = convertView;

		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.parcel_list_item, null);
		}

		/*
		 * Recall that the variable position is sent in as an argument to this
		 * method. The variable simply refers to the position of the current
		 * object in the list. (The ArrayAdapter iterates through the list we
		 * sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		ParcelItem i = objects.get(position);

		if (i != null) {

			// This is how you obtain a reference to the TextViews.
			// These TextViews are created in the XML files we defined.

			TextView parcelName = (TextView) v.findViewById(R.id.parcelName);
			ImageView parcelImage = (ImageView) v
					.findViewById(R.id.parcelImage);
			TextView parcelPrice = (TextView) v.findViewById(R.id.parcelPrice);

			// check to see if each individual textview is null.
			// if not, assign some text!
			if (parcelName != null) {
				parcelName.setText(i.getParcelName());
			}
			if (parcelPrice != null) {
				parcelPrice.setText("₹" + i.getParcelPrice());
			}
			if (parcelImage != null) {

				final Bitmap bitmap = ci.getBitmapFromMemCache(i.getParcelId()
						+ "");
				if (bitmap != null) {
					
					Log.d("ParcelListAdapter", "Getting from cache image " + i.getParcelId());
					parcelImage.setImageBitmap(bitmap);
				} else {

					imagedownloader.download(i.getParcelImage(), parcelImage,
							i.getParcelId() + "");

				}
			}
		}

		// the view must be returned to our activity
		return v;

	}

}
