package org.hopto.eriksen.core;

// Generated 2015-okt-07 12:38:00 by Hibernate Tools 4.3.1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Ingredient generated by hbm2java
 */
@Entity
@Table(name = "ingredient", catalog = "recept")
public class Ingredient implements java.io.Serializable {

	private Integer ingredientId;
	private String name;
	private String comment;
	private Set<RecipeHasIngredient> recipeHasIngredients = new HashSet<RecipeHasIngredient>(
			0);

	public Ingredient() {
	}

	public Ingredient(String name, String comment,
			Set<RecipeHasIngredient> recipeHasIngredients) {
		this.name = name;
		this.comment = comment;
		this.recipeHasIngredients = recipeHasIngredients;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "ingredient_id", unique = true, nullable = false)
	public Integer getIngredientId() {
		return this.ingredientId;
	}

	public void setIngredientId(Integer ingredientId) {
		this.ingredientId = ingredientId;
	}

	@Column(name = "name", length = 45)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "comment", length = 200)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ingredient")
	public Set<RecipeHasIngredient> getRecipeHasIngredients() {
		return this.recipeHasIngredients;
	}

	public void setRecipeHasIngredients(
			Set<RecipeHasIngredient> recipeHasIngredients) {
		this.recipeHasIngredients = recipeHasIngredients;
	}

}