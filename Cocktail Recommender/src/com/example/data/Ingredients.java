package com.example.data;

import android.util.SparseArray;

public class Ingredients {

	private static SparseArray alcohol;
	private static SparseArray juices;
	private static SparseArray other;

	public Ingredients() {
		alcohol = new SparseArray();
		juices = new SparseArray();
		other = new SparseArray();

		// Enter the ingredients into the following methods
		initAlcoholValues();
		initJuiceValues();
		initOtherValues();
	}

	private void initAlcoholValues() {
		// alcohol.append(1, "Rum");

	}

	private void initJuiceValues() {
		// juices.append(100, "Orangensaft");

	}

	private void initOtherValues() {
		// other.append(200, "Olive");

	}

	// SparseArray keys are ordered, the SparseArray indices correspond to the
	// keys - index 0 corresponds to the smallest key
	public int getAlcoholKey(String alcoholString) {
		for (int i = 0; i < alcohol.size(); i++) {
			if (alcohol.valueAt(i).equals(alcoholString))
				return alcohol.keyAt(i);
		}
		return -1;
	}

	public int getJuiceKey(String juiceString) {
		for (int i = 0; i < juices.size(); i++) {
			if (juices.valueAt(i).equals(juiceString))
				return juices.keyAt(i);
		}
		return -1;
	}

	public int getOtherKey(String otherString) {
		for (int i = 0; i < other.size(); i++) {
			if (other.valueAt(i).equals(otherString))
				return other.keyAt(i);
		}
		return -1;
	}

	public String getAlcoholString(int alcoholKey) {
		return (String) alcohol.get(alcoholKey);
	}

	public String getJuiceString(int juiceKey) {
		return (String) juices.get(juiceKey);
	}

	public String getOtherString(int otherKey) {
		return (String) other.get(otherKey);
	}

}
