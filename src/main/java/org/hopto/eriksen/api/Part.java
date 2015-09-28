package org.hopto.eriksen.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PART")
@XmlRootElement
public class Part {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// This must be IDENTITY for hsqldb?
	@Column(name = "PART_ID")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECIPE_ID")
	private Recipe recipe;

	private String name;

	// private List<Ingredient> ingredient;

	//	@OrderColumn(name="employeeNumber")	// Behöver denna lista detta för att behålla ordningen, behöver man i så fall byta ut String mot object?
	@ElementCollection(targetClass = String.class)
	private List<String> instructions = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinTable(
			name = "PART_INGREDIENT",
			joinColumns = { @JoinColumn(name = "PART_ID") },
			inverseJoinColumns = { @JoinColumn(name = "INGREDIENT_ID") })
	private Set<Ingredient> ingredients = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public Recipe getRecipe() {
		return recipe;
	}

	/**
	 * This will not update the relationship Recipe -> Course. So use the
	 * Recipe.addCourse() instead.
	 * 
	 * @param recipe
	 */
	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<String> instruction) {
		this.instructions = instruction;
	}

	public void addInstruction(String instruction) {
		this.instructions.add(instruction);
	}

	public Set<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public void addIngredient(Ingredient ingredient) {
		ingredients.add(ingredient);
	}

	/**
	 * Don't include the recipe reference in the to string (circular reference
	 * => StackOverflowError)
	 */
	@Override
	public String toString() {
		return "Course [" + (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "")
				+ (instructions != null ? "instructions=" + instructions + ", " : "")
				+ (ingredients != null ? "ingredients=" + ingredients : "") + "]";
	}

}
