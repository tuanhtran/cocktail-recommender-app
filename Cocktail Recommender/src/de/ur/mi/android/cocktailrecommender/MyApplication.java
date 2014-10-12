package de.ur.mi.android.cocktailrecommender;

import de.ur.mi.android.cocktailrecommender.data.CRDatabase;
import android.app.Application;

public class MyApplication extends Application {
	
	@Override
	public void onCreate() {
		CRDatabase.getInstance(this).open();
		super.onCreate();
	}
}
