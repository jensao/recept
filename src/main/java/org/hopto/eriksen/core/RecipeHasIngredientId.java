package org.hopto.eriksen.core;

// Generated 2015-okt-07 12:38:00 by Hibernate Tools 4.3.1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * RecipeHasIngredientId generated by hbm2java
 */
@Embeddable
public class RecipeHasIngredientId implements java.io.Serializable {

	private int recipeId;
	private int ingredientId;

	public RecipeHasIngredientId() {
	}

	public RecipeHasIngredientId(int recipeId, int ingredientId) {
		this.recipeId = recipeId;
		this.ingredientId = ingredientId;
	}

	@Column(name = "recipe_id", nullable = false)
	public int getRecipeId() {
		return this.recipeId;
	}

	public void setRecipeId(int recipeId) {
		this.recipeId = recipeId;
	}

	@Column(name = "ingredient_id", nullable = false)
	public int getIngredientId() {
		return this.ingredientId;
	}

	public void setIngredientId(int ingredientId) {
		this.ingredientId = ingredientId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof RecipeHasIngredientId))
			return false;
		RecipeHasIngredientId castOther = (RecipeHasIngredientId) other;

		return (this.getRecipeId() == castOther.getRecipeId())
				&& (this.getIngredientId() == castOther.getIngredientId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getRecipeId();
		result = 37 * result + this.getIngredientId();
		return result;
	}

}