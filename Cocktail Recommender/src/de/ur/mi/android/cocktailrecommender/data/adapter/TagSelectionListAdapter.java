package de.ur.mi.android.cocktailrecommender.data.adapter;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.ur.mi.android.cocktailrecommender.R;
import de.ur.mi.android.cocktailrecommender.data.CocktailRecommenderValues;
import de.ur.mi.android.cocktailrecommender.data.Tag;

public class TagSelectionListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Tag> tags;
	private Toast toast;
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
			
			tagIcon.setImageResource(CocktailRecommenderValues.getCorrectTagImageResource(tagIcon, tag));
		}

		view.setBackgroundColor(view.getResources().getColor(
				getBGColor(tag.isSelected())));

		view.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				tag.toggleSelection();
				v.setBackgroundColor(v.getResources().getColor(
						getBGColor(tag.isSelected())));
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				if (toast == null){
					toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
				}
				if (tag.isSelected()){					
					toast.setText(context.getResources().getString(R.string.toast_tag_select));
					toast.show();
				}	
				else
					toast.setText(context.getResources().getString(R.string.toast_tag_remove));
					toast.show();
			}
		});
		return view;
	}
	
	
	//Icons from icons8.com, remember to link to website
	private void setCorrectTagIcon(ImageView tagIcon, Tag tag) {
		
		switch (tag.getTagID()){
			case CocktailRecommenderValues.TAG_APERITIF:
				tagIcon.setImageResource(R.drawable.ic_tag_aperitif);
				break;
			case CocktailRecommenderValues.TAG_HIGHBALL:
				tagIcon.setImageResource(R.drawable.ic_tag_highball);
				break;
			case CocktailRecommenderValues.TAG_SHOT:
				tagIcon.setImageResource(R.drawable.ic_tag_shooter);
				break;
			case CocktailRecommenderValues.TAG_DIGESTIF:
				tagIcon.setImageResource(R.drawable.ic_tag_digestif);
				break;
			case CocktailRecommenderValues.TAG_KLASSISCH:
				tagIcon.setImageResource(R.drawable.ic_tag_klassisch);
				break;
			case CocktailRecommenderValues.TAG_SOUR:
				tagIcon.setImageResource(R.drawable.ic_tag_sour);
				break;
			case CocktailRecommenderValues.TAG_FIZZES:
				tagIcon.setImageResource(R.drawable.ic_tag_fizz);
				break;
			case CocktailRecommenderValues.TAG_SHOOTER:
				tagIcon.setImageResource(R.drawable.ic_tag_shooter);
				break;
			case CocktailRecommenderValues.TAG_STRONG:
				tagIcon.setImageResource(R.drawable.ic_tag_strong);
				break;
			default:
				tagIcon.setImageResource(R.drawable.tag_icon_placeholder);
				break;
		}
	}

	private int getBGColor(boolean isSelected) {
		if (isSelected) {
			
			return R.color.background_selected_dark_blue;
		} else {
			
			return R.color.background_black;
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
