package de.ur.mi.android.cocktailrecommender.data;

import android.util.SparseArray;

public class TagsData {
	private static SparseArray<String> tags;
	
	static{
		tags = new SparseArray<String>();
		initTagValues();
	}

	private static void initTagValues() {
		// Add tags here
		
	}
	
	public String getTagString(int tagKey){
		return tags.get(tagKey);
	}
}
