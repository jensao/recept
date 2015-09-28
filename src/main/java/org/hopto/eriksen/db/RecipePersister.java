package org.hopto.eriksen.db;

import java.util.List;

import org.hopto.eriksen.api.Recipe;

public interface RecipePersister {

	void save(Recipe recipe);

	Recipe findById(Long id);

	List<Recipe> getAll(int pageNumber, int pageSize);
	
	void delete(Recipe recipe);
}
