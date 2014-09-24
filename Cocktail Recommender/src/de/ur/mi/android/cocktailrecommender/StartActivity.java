package de.ur.mi.android.cocktailrecommender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CRDatabase;

public class StartActivity extends ActionBarActivity {
	private CRDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		initData();
		startNextActivity();
	}

	private void initData() {
		db = CRDatabase.getInstance(this);
		db.open();
	}

	private void startNextActivity() {
		Intent intent = new Intent(StartActivity.this, MenuActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		db.close();
		super.onDestroy();
	}
}
