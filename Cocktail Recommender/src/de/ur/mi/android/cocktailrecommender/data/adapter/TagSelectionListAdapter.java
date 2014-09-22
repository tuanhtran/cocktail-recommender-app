package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.IngredientType;
import de.ur.mi.android.cocktailrecommender.data.Tag;
import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class TagSelectionListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Tag> tags;

	public TagSelectionListAdapter(Context context, ArrayList<Tag> tags) {
		this.context = context;
		this.tags = tags;
	}

	public int getCount() {
		return tags.size();
	}

	// create a new ImageView for each item referenced by the Adapter

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listitem_tag_selection, null);
		}

		final Tag tag = tags.get(position);

		if (tag != null) {
			TextView tagName = (TextView) view
					.findViewById(R.id.selection_tag_name);
			ImageView tagIcon = (ImageView) view
					.findViewById(R.id.selection_tag_icon);

			tagName.setText(tag.getTagName());
			tagIcon.setBackgroundResource(R.drawable.tag_icon_placeholder);
		}

		view.setBackgroundColor(view.getResources().getColor(
				getBGColor(tag.isSelected())));

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tag.toggleSelection();
				v.setBackgroundColor(v.getResources().getColor(
						getBGColor(tag.isSelected())));
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
			}
		});
		return view;
	}

	private int getBGColor(boolean isSelected) {
		if (isSelected) {
			return R.color.test_background_red;
		} else {
			return R.color.test_background_white;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void notifyDataSetChanged() {
		Collections.sort(tags);
		super.notifyDataSetChanged();
	}
}
