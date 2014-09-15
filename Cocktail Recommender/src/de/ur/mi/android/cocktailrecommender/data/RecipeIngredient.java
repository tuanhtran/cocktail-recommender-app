package de.ur.mi.android.cocktailrecommender.data;

public class RecipeIngredient extends IngredientType {
	private String quantity;

	public RecipeIngredient(int ingID, String name, String quantity) {
		super(ingID, name);
		this.quantity = quantity;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getFullIngredientString() {
		return quantity + " " + getIngName();
	}
}
