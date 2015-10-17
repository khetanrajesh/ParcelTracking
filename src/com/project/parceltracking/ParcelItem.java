package com.project.parceltracking;

import java.util.Comparator;

import com.google.android.gms.maps.model.LatLng;

public class ParcelItem implements Comparable<ParcelItem> {

	private String parcelImage;

	private float parcelWeight;

	private LatLng parcelLatlng;

	private String parcelPrice, parcelName, parcelType, parcelPhone,
			parcelDate, parcelQuantity, parcelColor;

	private int parcelId;

	public ParcelItem(String parcelImage, float parcelWeight,
			LatLng parcelLatlng, String parcelPrice, String parcelName,
			String parcelType, String parcelPhone, String parcelDate,
			String parcelQuantity, int parcelId, String parcelColor) {
		super();
		this.parcelImage = parcelImage;
		this.parcelWeight = parcelWeight;
		this.parcelLatlng = parcelLatlng;
		this.parcelPrice = parcelPrice;
		this.parcelName = parcelName;
		this.parcelType = parcelType;
		this.parcelPhone = parcelPhone;
		this.parcelDate = parcelDate;
		this.parcelQuantity = parcelQuantity;
		this.parcelColor = parcelColor;

		this.parcelId = parcelId;
	}

	public String getParcelColor() {
		return parcelColor;
	}

	public void setParcelColor(String parcelColor) {
		this.parcelColor = parcelColor;
	}

	public LatLng getParcelLatlng() {
		return parcelLatlng;
	}

	public void setParcelLatlng(LatLng parcelLatlng) {
		this.parcelLatlng = parcelLatlng;
	}

	public String getParcelType() {
		return parcelType;
	}

	public void setParcelType(String parcelType) {
		this.parcelType = parcelType;
	}

	public String getParcelPhone() {
		return parcelPhone;
	}

	public void setParcelPhone(String parcelPhone) {
		this.parcelPhone = parcelPhone;
	}

	public String getParcelDate() {
		return parcelDate;
	}

	public void setParcelDate(String parcelDate) {
		this.parcelDate = parcelDate;
	}

	public String getParcelQuantity() {
		return parcelQuantity;
	}

	public void setParcelQuantity(String parcelQuantity) {
		this.parcelQuantity = parcelQuantity;
	}

	public float getParcelWeight() {
		return parcelWeight;
	}

	public void setParcelWeight(float parcelWeight) {
		this.parcelWeight = parcelWeight;
	}

	public String getParcelName() {
		return parcelName;
	}

	public void setParcelName(String parcelName) {
		this.parcelName = parcelName;
	}

	public String getParcelImage() {
		return parcelImage;
	}

	public void setParcelImage(String parcelImage) {
		this.parcelImage = parcelImage;
	}

	public String getParcelPrice() {
		return parcelPrice;
	}

	public void setParcelPrice(String parcelPrice) {
		this.parcelPrice = parcelPrice;
	}

	public int getParcelId() {
		return parcelId;
	}

	public void setParcelId(int parcelId) {
		this.parcelId = parcelId;
	}

	public int compareTo(ParcelItem compareParcel) {

		String compareParcelName = ((ParcelItem) compareParcel).getParcelName();

		// ascending order
		return this.parcelName.compareTo(compareParcelName);

		// descending order
		// return compareQuantity - this.quantity;

	}

	public static Comparator<ParcelItem> ParcelValueComparator = new Comparator<ParcelItem>() {

		@Override
		public int compare(ParcelItem item1, ParcelItem item2) {

			return (int) (Integer
					.parseInt((item1.parcelPrice).replace(",", "")) - Integer
					.parseInt((item2.parcelPrice).replace(",", "")));

		}

	};

	public static Comparator<ParcelItem> ParcelWeightComparator = new Comparator<ParcelItem>() {

		@Override
		public int compare(ParcelItem item1, ParcelItem item2) {

			return (int) (item1.parcelWeight - item2.parcelWeight);

		}

	};

}
