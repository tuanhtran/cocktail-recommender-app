package com.example.data;

import android.util.SparseArray;

public class Ingredients {

	private static SparseArray<String> alcohol;
	private static SparseArray<String> juices;
	private static SparseArray<String> other;
	
	//Arbitrary numbers to get clear ingredient ID separation 
	public static final int ALCOHOL_END = 99;
	public static final int JUICES_END = 199;
	public static final int OTHER_START = 200;
	
	//Static initialization for the SparseArrays
	static {
		alcohol = new SparseArray<String>();
		juices = new SparseArray<String>();
		other = new SparseArray<String>();

		// Enter the ingredients into the following methods
		initAlcoholValues();
		initJuiceValues();
		initOtherValues();
	}

	private static void initAlcoholValues() {
		// alcohol.append(1, "Rum");

	}

	private static void initJuiceValues() {
		// juices.append(100, "Orangensaft");

	}

	private static void initOtherValues() {
		// other.append(200, "Olive");

	}

	// SparseArray keys are ordered, the SparseArray indices correspond to the
	// keys - index 0 corresponds to the smallest key
	public static int getAlcoholKey(String alcoholString) {
		for (int i = 0; i < alcohol.size(); i++) {
			if (alcohol.valueAt(i).equals(alcoholString))
				return alcohol.keyAt(i);
		}
		return -1;
	}

	public static int getJuiceKey(String juiceString) {
		for (int i = 0; i < juices.size(); i++) {
			if (juices.valueAt(i).equals(juiceString))
				return juices.keyAt(i);
		}
		return -1;
	}

	public static int getOtherKey(String otherString) {
		for (int i = 0; i < other.size(); i++) {
			if (other.valueAt(i).equals(otherString))
				return other.keyAt(i);
		}
		return -1;
	}

	public static String getAlcoholString(int alcoholKey) {
		return (String) alcohol.get(alcoholKey);
	}

	public static String getJuiceString(int juiceKey) {
		return (String) juices.get(juiceKey);
	}

	public static String getOtherString(int otherKey) {
		return (String) other.get(otherKey);
	}
	
}
