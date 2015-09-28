package org.hopto.eriksen.db;

import org.hopto.eriksen.api.Ingredient;


public interface IngredientPersister {

	void save(Ingredient ingredient);
	
	Ingredient findById(Long id);
}
